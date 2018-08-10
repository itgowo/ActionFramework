package com.itgowo.SimpleServerCore.Socket;

import com.itgowo.SimpleServerCore.Utils.LogU;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by lujianchao on 2017/3/31.
 */
public class SocketServerManager {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private Logger log = LogU.getLogU(getClass().getName(), Level.ALL);
    private SocketServerHandler.onReceiveHandlerListener onReceiveHandlerListener;

    /**
     * 先设置回调后start
     *
     * @param onReceiveHandlerListener
     */
    public void setOnReceiveHandleListener(SocketServerHandler.onReceiveHandlerListener onReceiveHandlerListener) {
        this.onReceiveHandlerListener = onReceiveHandlerListener;
    }

    /**
     * 设置线程池线程数
     *
     * @param bossThreadNum   百万级QPS设置为2或3即可
     * @param workerThreadNum 百万级QPS设置为4到6即可
     * @return
     */
    public SocketServerManager setThreadConfig(int bossThreadNum, int workerThreadNum) {
        bossGroup = new NioEventLoopGroup(bossThreadNum, new DefaultThreadFactory("BossThread"));
        workerGroup = new NioEventLoopGroup(workerThreadNum, new DefaultThreadFactory("WorkerThread"));
        serverBootstrap = new ServerBootstrap();
        return this;
    }

    public void start(int port) throws Exception {
        if (onReceiveHandlerListener == null) {
            throw new Exception("请先设置接收信息回调");
        }
        if (bossGroup == null || workerGroup == null || serverBootstrap == null) {
            throw new Exception("请先设置线程池");
        }
        try {
            log.info("The Socket Server is Starting....");
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ByteArrayEncoder());
                            ch.pipeline().addLast(new ByteArrayDecoder());
                            ch.pipeline().addLast(new SocketServerInboundHandlerAdapter().setReceiveHandlerListener(onReceiveHandlerListener));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 4096)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture f = serverBootstrap.bind(port).sync();
            log.info("The Socket is Started....");
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void stop() {
        System.out.println("The SocketServer is Stopping");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
