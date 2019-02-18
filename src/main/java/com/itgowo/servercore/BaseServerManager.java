package com.itgowo.servercore;

import com.itgowo.actionframework.ServerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import org.apache.logging.log4j.Logger;

public abstract class BaseServerManager {
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected ServerBootstrap serverBootstrap;
    protected onServerListener onServerListener;
    protected boolean isRunning = false;
    protected int serverPort = 0;
    protected String serverName = this.getClass().getSimpleName();

    public BaseServerManager(String serverName) {
        this.serverName = serverName;
    }
    public Logger getLogger(){
        return ServerManager.getLogger();
    }
    public boolean isRunning() {
        return isRunning;
    }

    public onServerListener getOnServerListener() {
        return onServerListener;
    }

    /**
     * 先设置回调后start
     *
     * @param onServerListener
     */
    public BaseServerManager setOnServerListener(onServerListener onServerListener) {
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
    public BaseServerManager setThreadConfig(int bossThreadNum, int workerThreadNum) {
        bossGroup = new NioEventLoopGroup(bossThreadNum, new DefaultThreadFactory("itgowo-Netty-BossThread-" + serverName));
        workerGroup = new NioEventLoopGroup(workerThreadNum, new DefaultThreadFactory("itgowo-Netty-WorkerThread-" + serverName));
        serverBootstrap = new ServerBootstrap();
        return this;
    }

    /**
     * 先设置回调后start
     *
     * @param port
     */
    public void prepare(int port) throws Exception {
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
                            prepareServerConfig(serverBootstrap, ch);
                        }
                    });
            serverPort = port;
        } catch (Exception e) {
            isRunning = false;
            e.printStackTrace();
        }
    }

    protected abstract void prepareServerConfig(ServerBootstrap serverBootstrap, SocketChannel ch) throws Exception;

    public void start() {
        try {
            ChannelFuture f = serverBootstrap.bind(serverPort).sync();
            if (onServerListener != null) {
                onServerListener.onServerStarted(serverPort);
            }
            ServerManager.getLogger().info("The " + serverName + " is Started....");
            isRunning = true;
            if (onServerListener != null) {
                onServerListener.onServerStarted(serverPort);
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

    public Thread startAsyn() {
        Thread server = new Thread(() -> start());
        server.setName(serverName);
        server.start();
        return server;
    }

    public void stop() {
        isRunning = false;
        ServerManager.getLogger().info("The " + serverName + " is Stopping");
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
    }
}
