package com.itgowo.servercore.http;

import com.itgowo.actionframework.ServerManager;
import com.itgowo.servercore.onServerListener;
import com.itgowo.actionframework.base.Dispatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * Created by lujianchao on 2017/5/25.
 */
public class HttpServerManager {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private Dispatcher onServerListener;
    private boolean isRunning = false;
    private String serverName = "HttpServer";

    public boolean isRunning() {
        return isRunning;
    }

    public Dispatcher getOnServerListener() {
        return onServerListener;
    }

    public HttpServerManager setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }
    /**
     * 先设置回调后start
     *
     * @param onServerListener
     */
    public HttpServerManager setOnServerListener(Dispatcher onServerListener) {
        this.onServerListener = onServerListener;
        return this;
    }

    /**
     * 设置线程池线程数
     *
     * @param bossThreadNum   百万级QPS设置为2或3即可
     * @param workerThreadNum 百万级QPS设置为4到6即可
     * @return
     */
    public HttpServerManager setThreadConfig(int bossThreadNum, int workerThreadNum) {
        bossGroup = new NioEventLoopGroup(bossThreadNum, new DefaultThreadFactory("itgowo-Netty-BossThread-"+serverName));
        workerGroup = new NioEventLoopGroup(workerThreadNum, new DefaultThreadFactory("itgowo-Netty-WorkerThread-"+serverName));
        serverBootstrap = new ServerBootstrap();
        return this;
    }

    /**
     * 先设置回调后start
     *
     * @param port
     */
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
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpServerInboundHandlerAdapter(onServerListener) );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 4096)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, false);
            ChannelFuture f = serverBootstrap.bind(port).sync();
            if (onServerListener != null) {
                onServerListener.onServerStarted(port);
            }
            ServerManager.getLogger().info("The " + serverName + " is Started....");
            isRunning = true;
            if (onServerListener != null) {
                onServerListener.onServerStarted(port);
            }
            f.channel().closeFuture().sync();
            if (onServerListener != null) {
                onServerListener.onServerStop();
            }
            ServerManager.getLogger().info("The " + serverName + " is Stopped");
        } catch (Exception e) {
            isRunning = false;
            e.printStackTrace();
        } finally {
            if (workerGroup != null) workerGroup.shutdownGracefully();
            if (bossGroup != null) bossGroup.shutdownGracefully();
        }
    }

    public void stop() {
        isRunning = false;
        if (Dispatcher.scheduledFuture != null) {
            Dispatcher.scheduledFuture.cancel(true);
        }
        ServerManager.getLogger().info("The " + serverName + " is Stopping");
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
    }
}
