package com.spiritsword.registry;

import com.spiritsword.task.model.ExecutorInfo;
import com.spiritsword.task.model.SchedulerInfo;
import com.spiritsword.task.model.Task;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExecutorRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorRegistry.class);

    private SchedulerInfo schedulerInfo;
    private Map<String, Channel> executorChannelMap = new HashMap<>();
    private Map<String, ExecutorInfo> executorInfoMap = new HashMap<>();

    @PostConstruct
    public void initialize() {
        new Thread(this::start).start();
    }

    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                        }
                    });

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public void validateExecutors() {

    }

    public List<ExecutorInfo> getExecutorInfo(){
        validateExecutors();
        return new ArrayList<>(executorInfoMap.values());
    }

    public void registerScheduler(String id, Channel channel) {

    }

    public void registerExecutor(String id, Channel channel) {

    }

    public void dispatch(Task task) {

    }
}
