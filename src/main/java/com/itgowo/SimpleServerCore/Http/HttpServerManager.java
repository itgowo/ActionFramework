package com.itgowo.SimpleServerCore.Http;

import com.itgowo.SimpleServerCore.Utils.LogU;
import com.itgowo.baseServer.base.Dispatcher;
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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by lujianchao on 2017/5/25.
 */
public class HttpServerManager {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private Logger log = LogU.getLogU(getClass().getName(), Level.ALL);
    private HttpServerHandler.onReceiveHandlerListener onReceiveHandlerListener;
    private boolean isRunning = false;

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 先设置回调后start
     *
     * @param onReceiveHandlerListener
     */
    public HttpServerManager setOnReceiveHandleListener(HttpServerHandler.onReceiveHandlerListener onReceiveHandlerListener) {
        this.onReceiveHandlerListener = onReceiveHandlerListener;
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
        bossGroup = new NioEventLoopGroup(bossThreadNum, new DefaultThreadFactory("itgowo-Netty-BossThread"));
        workerGroup = new NioEventLoopGroup(workerThreadNum, new DefaultThreadFactory("itgowo-Netty-WorkerThread"));
        serverBootstrap = new ServerBootstrap();
        return this;
    }

    /**
     * 先设置回调后start
     *
     * @param port
     */
    public void start(int port) throws Exception {
        if (onReceiveHandlerListener == null) {
            throw new Exception("请先设置接收信息回调");
        }
        if (bossGroup == null || workerGroup == null || serverBootstrap == null) {
            throw new Exception("请先设置线程池");
        }
        try {
            log.info("The HttpServer is Starting....");
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpServerInboundHandlerAdapter().setReceiveHandlerListener(onReceiveHandlerListener));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 4096).childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, false);
            ChannelFuture f = serverBootstrap.bind(port).sync();
            if (onReceiveHandlerListener != null) {
                onReceiveHandlerListener.onServerStarted(port);
            }
            log.info("The HttpServer is Started....");
            isRunning = true;
            f.channel().closeFuture().sync();
            if (onReceiveHandlerListener != null) {
                onReceiveHandlerListener.onServerStop();
            }
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
        System.out.println("The HttpServer is Stopping");
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
    }
}
