package com.itgowo.socketframework;

import com.itgowo.actionframework.ServerManager;
import com.itgowo.servercore.onServerListener;
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

/**
 * Created by lujianchao on 2017/3/31.
 */
public class SocketServerManager {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private onServerListener onServerListener;
    private String serverName = "SocketServer";

    public SocketServerManager setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    /**
     * 先设置回调后start
     *
     * @param onReceiveHandlerListener
     */
    public void setOnReceiveHandleListener(onServerListener onReceiveHandlerListener) {
        this.onServerListener = onReceiveHandlerListener;
    }

    /**
     * 设置线程池线程数
     *
     * @param bossThreadNum   百万级QPS设置为2或3即可
     * @param workerThreadNum 百万级QPS设置为4到6即可
     * @return
     */
    public SocketServerManager setThreadConfig(int bossThreadNum, int workerThreadNum) {
        bossGroup = new NioEventLoopGroup(bossThreadNum, new DefaultThreadFactory("itgowo-Netty-BossThread-" + serverName));
        workerGroup = new NioEventLoopGroup(workerThreadNum, new DefaultThreadFactory("itgowo-Netty-WorkerThread-" + serverName));
        serverBootstrap = new ServerBootstrap();
        return this;
    }

    public void start(int port) throws Exception {
        if (onServerListener == null) {
            throw new Exception("请先设置接收信息回调");
        }
        if (bossGroup == null || workerGroup == null || serverBootstrap == null) {
            throw new Exception("请先设置线程池");
        }
        try {
            ServerManager.getLogger().info("The " + serverName + " is Starting....");
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ByteArrayEncoder());
                            ch.pipeline().addLast(new ByteArrayDecoder());
                            ch.pipeline().addLast(new SocketServerInboundHandlerAdapter(onServerListener));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 4096)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture f = serverBootstrap.bind(port).sync();
            ServerManager.getLogger().info("The " + serverName + " is Started....");
            if (onServerListener != null) {
                onServerListener.onServerStarted(port);
            }
            f.channel().closeFuture().sync();
            if (onServerListener != null) {
                onServerListener.onServerStop();
            }
            ServerManager.getLogger().info("The " + serverName + " is Stopped");
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void stop() {
        ServerManager.getLogger().info("The " + serverName + " is Stopping");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
