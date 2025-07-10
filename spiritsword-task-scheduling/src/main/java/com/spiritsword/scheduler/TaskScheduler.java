package com.spiritsword.scheduler;

import com.spiritsword.handler.ByteToJsonMessageDecoder;
import com.spiritsword.handler.JsonMessageToByteEncoder;
import com.spiritsword.handler.SchedulerClientHandler;
import com.spiritsword.handler.SchedulerServerHandler;
import com.spiritsword.repository.Repository;
import com.spiritsword.task.model.ChannelMessage;
import com.spiritsword.task.model.MessageType;
import com.spiritsword.task.model.Task;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

@Component
public class TaskScheduler {
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private Repository taskRepository;
    @Autowired
    private Executor threadExecutor;
    private List<Task> cacheTasks;

    private ScheduledFuture<?> scheduledSynchronizeRegistryFuture;
    private ScheduledFuture<?> scheduledPullTaskFuture;
    private StandaloneExecutorManager executorManager = new StandaloneExecutorManager();
    private ScheduledExecutorService  scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private Channel registryChannel;

    @PostConstruct
    public void start() {
        new Thread(this::startSchedulerServer).start();
        new Thread(this::connectRegistry).start();
    }

    private void connectRegistry() {
        NioEventLoopGroup worker = new NioEventLoopGroup(3);
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new JsonMessageToByteEncoder());
                    socketChannel.pipeline().addLast(new ByteToJsonMessageDecoder());
                    socketChannel.pipeline().addLast(new SchedulerClientHandler(TaskScheduler.this.executorManager));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 11111).addListener(future -> {
                if(future.isSuccess()){
                    ChannelFuture cf = (ChannelFuture) future;
                    this.registryChannel = cf.channel();
                    registerToRegistryCentre();
                    synchronizeExecutorsWithRegistry(1,5,TimeUnit.MINUTES);
                } else {
                    this.registryChannel = null;
                    reconnect();
                }
            });

            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException("Connection to Registry failed");
        } finally {
            worker.shutdownGracefully();
        }
    }

    public void synchronizeExecutorsWithRegistry(int initialDelay, int interval, TimeUnit timeUnit) {
        if(this.scheduledSynchronizeRegistryFuture != null) {
            this.scheduledSynchronizeRegistryFuture.cancel(false);
        }
        this.scheduledSynchronizeRegistryFuture = scheduledExecutorService.scheduleAtFixedRate(this::sendPullRequest, initialDelay, interval, timeUnit);
    }

    public void pullingTasksFromRepository(int initialDelay, int interval, TimeUnit timeUnit) {
        if(this.scheduledPullTaskFuture != null) {
            this.scheduledPullTaskFuture.cancel(false);
        }
        this.scheduledPullTaskFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            List<Task> tasksAboutDue = taskRepository.findTasksAboutDue();
        }, initialDelay, interval, timeUnit);
    }

    public void reconnect() {

    }

    public void registerToRegistryCentre() {
        ChannelMessage channelMessage = new ChannelMessage(MessageType.SCHEDULER_REGISTER);
        registryChannel.writeAndFlush(channelMessage);
    }

    public void sendPullRequest() {
        ChannelMessage channelMessage = new ChannelMessage(MessageType.PULL_REQUEST);
        this.registryChannel.writeAndFlush(channelMessage);
    }

    /**
     * Scheduler Server
     */
    private void startSchedulerServer() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup(3);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new JsonMessageToByteEncoder());
                            socketChannel.pipeline().addLast(new ByteToJsonMessageDecoder());
                            socketChannel.pipeline().addLast(new SchedulerServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(9999).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException("Server failed to start");
        }
    }
}
