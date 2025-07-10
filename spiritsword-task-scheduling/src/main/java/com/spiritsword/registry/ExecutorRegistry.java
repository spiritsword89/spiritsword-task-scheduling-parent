package com.spiritsword.registry;

import com.spiritsword.handler.ByteToJsonMessageDecoder;
import com.spiritsword.handler.JsonMessageToByteEncoder;
import com.spiritsword.handler.RegistryInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExecutorRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorRegistry.class);

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
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new JsonMessageToByteEncoder());
                            socketChannel.pipeline().addLast(new ByteToJsonMessageDecoder());
                            socketChannel.pipeline().addLast(new RegistryInboundHandler(new RegistryService()));
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(11111).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
