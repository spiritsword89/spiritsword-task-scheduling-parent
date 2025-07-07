package com.spiritsword.task.excutor;

import com.spiritsword.handler.ByteToJsonMessageDecoder;
import com.spiritsword.handler.ExecutorClientHandler;
import com.spiritsword.handler.JsonMessageToByteEncoder;
import com.spiritsword.task.handler.TaskHandler;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.MessageType;
import com.spiritsword.task.model.TaskRequest;
import com.spiritsword.task.model.TaskResult;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTaskExecutor implements TaskExecutor, InitializingBean, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTaskExecutor.class);
    public static final String EXECUTOR_ID_FIELD = "executorId";

    private String executorId;
    private Environment environment;
    private int workerSize = 3;
    private String registryHost;
    private int port;
    private Channel channel;
    private NioEventLoopGroup nioEventLoopGroup;
    private Map<String, TaskHandler> handlers = new HashMap<>();
    private long lastActiveTime;

    @Override
    public String getExecutorId() {
        return executorId;
    }

    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    private void connect() {
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
            this.port = Integer.parseInt(registryPort);
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

            ChannelFuture channelFuture = bootstrap.connect(this.registryHost, this.port).addListener(future -> {
                if (future.isSuccess()) {
                    ChannelFuture cf = (ChannelFuture) future;
                    this.channel = cf.channel();
                }
            });

            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::connect).start();
    }

    @Override
    public Channel channel() {
        if(channel != null && channel.isActive()){
            return this.channel;
        }
        logger.info("Channel is null, may be disconnected");
        return null;
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
    public boolean isHealthy() {
        return this.channel.isActive() && ((lastActiveTime + 1000 * 60) > System.currentTimeMillis());
    }

    @Override
    public void registerHandler(TaskHandler handler) {
        this.handlers.put(handler.getHandlerId(), handler);
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
            beforeExecute();

            TaskResult result = handler.handle(payload.getParams());
            channelMessage.setPayload(result);
            channelMessage.setMessageType(MessageType.TASK_RESPONSE);
            channel().writeAndFlush(channelMessage);

            afterExecute();
        }
    }

    protected abstract void beforeExecute();
    protected abstract void afterExecute();
}
