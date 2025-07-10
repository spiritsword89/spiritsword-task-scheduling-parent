package com.spiritsword.task.excutor;

import com.spiritsword.config.SpiritswordTaskHandler;
import com.spiritsword.handler.ByteToJsonMessageDecoder;
import com.spiritsword.handler.ExecutorClientHandler;
import com.spiritsword.handler.JsonMessageToByteEncoder;
import com.spiritsword.task.handler.TaskHandler;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.MessageType;
import com.spiritsword.task.model.TaskRequest;
import com.spiritsword.task.model.TaskResult;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class BaseTaskExecutor implements TaskExecutor, InitializingBean, EnvironmentAware, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(BaseTaskExecutor.class);
    public static final String EXECUTOR_ID_FIELD = "executorId";

    private ApplicationContext context;
    private String executorId;
    private Environment environment;
    private int workerSize = 3;
    private String registryHost;
    private String schedulerHost;
    private int registryPort;
    private int schedulerPort;
    private Channel registryChannel;
    private Channel schedulerChannel;
    private NioEventLoopGroup nioEventLoopGroup;;
    private Map<String, TaskHandler> handlers = new HashMap<>();
    private List<String> handlerClassNames = new ArrayList<>();
    private List<ChannelInboundHandler> customChannelInboundHandlers = new ArrayList<>();
    private List<ChannelOutboundHandler> customChannelOutboundHandlers = new ArrayList<>();
    private long lastActiveTime;

    @Override
    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    @PostConstruct
    public void connect() {
        nioEventLoopGroup = new NioEventLoopGroup(this.workerSize);
        new Thread(this::connectRegistry).start();
        new Thread(this::connectScheduler).start();
    }

    private void connectScheduler() {
        String workerSize = environment.getProperty("spiritsword.task.executor.worker");
        if(workerSize != null){
            this.workerSize = Integer.parseInt(workerSize);
        }
        String schedulerHost = environment.getProperty("spiritsword.scheduler.host");
        if(schedulerHost != null){
            this.schedulerHost = schedulerHost;
        }
        String registryPort = environment.getProperty("spiritsword.scheduler.port");
        if(registryPort != null){
            this.schedulerPort = Integer.parseInt(registryPort);
        }

        try {
            Bootstrap bootstrap = new Bootstrap();
            ExecutorClientHandler executorClientHandler = new ExecutorClientHandler(this);

            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ByteToJsonMessageDecoder());
                            socketChannel.pipeline().addLast(new JsonMessageToByteEncoder());

                            for(ChannelInboundHandler channelInboundHandler : customChannelInboundHandlers){
                                socketChannel.pipeline().addLast(channelInboundHandler);
                            }

                            for(ChannelOutboundHandler channelOutboundHandler : customChannelOutboundHandlers){
                                socketChannel.pipeline().addLast(channelOutboundHandler);
                            }

                            socketChannel.pipeline().addLast(executorClientHandler);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(this.schedulerHost, this.schedulerPort).addListener(future -> {
                if (future.isSuccess()) {
                    ChannelFuture cf = (ChannelFuture) future;
                    this.schedulerChannel = cf.channel();
                } else {
                    this.schedulerChannel = null;
                    this.handlers.clear();
                    reconnectScheduler();
                }
            });

            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    private void connectRegistry() {
        String workerSize = environment.getProperty("spiritsword.task.executor.worker");
        if(workerSize != null){
            this.workerSize = Integer.parseInt(workerSize);
        }
        String registryHost = environment.getProperty("spiritsword.registry.host");
        if(registryHost != null){
            this.registryHost = registryHost;
        }
        String registryPort = environment.getProperty("spiritsword.registry.port");
        if(registryPort != null){
            this.registryPort = Integer.parseInt(registryPort);
        }

        nioEventLoopGroup = new NioEventLoopGroup(this.workerSize);

        try {
            Bootstrap bootstrap = new Bootstrap();
            ExecutorClientHandler executorClientHandler = new ExecutorClientHandler(this);

            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ByteToJsonMessageDecoder());
                            socketChannel.pipeline().addLast(new JsonMessageToByteEncoder());
                            socketChannel.pipeline().addLast(executorClientHandler);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(this.registryHost, this.registryPort).addListener(future -> {
                if (future.isSuccess()) {
                    ChannelFuture cf = (ChannelFuture) future;
                    this.registryChannel = cf.channel();
                    this.discoverHandlers();
                } else {
                    reconnectRegistry();
                }
            });

            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    private void discoverHandlers() {
        Map<String, TaskHandler> beansOfType = context.getBeansOfType(TaskHandler.class);
        Collection<TaskHandler> values = beansOfType.values();
        for(TaskHandler taskHandler : values){
            boolean annotationPresent = taskHandler.getClass().isAnnotationPresent(SpiritswordTaskHandler.class);
            if(annotationPresent){
                SpiritswordTaskHandler annotation = taskHandler.getClass().getAnnotation(SpiritswordTaskHandler.class);
                String exeId = annotation.executorId();

                if(exeId != null && exeId.equals(this.executorId)){
                    String handlerClassName = taskHandler.getClass().getName();
                    registerHandler(handlerClassName, taskHandler);
                    handlerClassNames.add(handlerClassName);
                }
            }
        }
    }

    @Override
    public void reconnectRegistry() {
        nioEventLoopGroup.schedule(this::connectRegistry, 5, TimeUnit.SECONDS);
    }

    @Override
    public void reconnectScheduler() {
        nioEventLoopGroup.schedule(this::connectScheduler, 5, TimeUnit.SECONDS);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::connect).start();
    }

    @Override
    public boolean supports(String handlerClass) {
        for(Map.Entry<String, TaskHandler> mp : this.handlers.entrySet()) {
            TaskHandler handler = mp.getValue();
            if(handler.getClass().getName().equals(handlerClass)){
                return true;
            }
        }
        return false;
    }

    @Override
    public double getLoad() {
        return 0.7;
    }

    @Override
    public boolean isHealthy() {
        return this.registryChannel.isActive() && ((lastActiveTime + 1000 * 60) > System.currentTimeMillis());
    }

    @Override
    public void registerHandler(String handlerId, TaskHandler handler) {
        this.handlers.put(handlerId, handler);
    }

    @Override
    public void removeHandler(String handlerId) {
        this.handlers.remove(handlerId);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void execute(ChannelMessage channelMessage) {
        TaskRequest payload = (TaskRequest) channelMessage.getPayload();
        String handlerId = payload.getHandlerId();
        TaskHandler handler = this.handlers.get(handlerId);
        if(handler != null && handler.getClass().getName().equals(payload.getHandlerClass())){
            beforeExecute(channelMessage);

            TaskResult result = handler.handle(payload.getParams());
            channelMessage.setPayload(result);
            channelMessage.setMessageType(MessageType.TASK_RESPONSE);
            schedulerChannel.writeAndFlush(channelMessage);

            afterExecute(result);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public void addCustomInboundHandler(ChannelInboundHandler handler) {
        this.customChannelInboundHandlers.add(handler);
    }

    public void addCustomOutboundHandler(ChannelOutboundHandler handler) {
        this.customChannelOutboundHandlers.add(handler);
    }

    protected abstract void beforeExecute(ChannelMessage channelMessage);
    protected abstract void afterExecute(TaskResult result);
}
