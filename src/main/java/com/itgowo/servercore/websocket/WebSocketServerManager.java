package com.itgowo.servercore.websocket;

import com.itgowo.actionserver.ServerManager;
import com.itgowo.actionserver.base.Dispatcher;
import com.itgowo.servercore.onServerListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * Created by lujianchao on 2017/5/25.
 */
public class WebSocketServerManager {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private onServerListener onServerListener;
    private boolean isRunning = false;
    private String websocketPath = "/";
    private String serverName = "HttpServer";


    public WebSocketServerManager setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getWebsocketPath() {
        return websocketPath;
    }

    public WebSocketServerManager setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
        return this;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 先设置回调后start
     *
     * @param onServerListener
     */
    public WebSocketServerManager setOnReceiveHandleListener(onServerListener onServerListener) {
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
    public WebSocketServerManager setThreadConfig(int bossThreadNum, int workerThreadNum) {
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
    public void start(int port, boolean isSSL) throws Exception {
        if (onServerListener == null) {
            throw new Exception("请先设置接收信息回调");
        }
        if (bossGroup == null || workerGroup == null || serverBootstrap == null) {
            throw new Exception("请先设置线程池");
        }
        try {
            final SslContext sslCtx;
            if (isSSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }
            ServerManager.getLogger().info("The " + serverName + " is Starting....");
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            if (sslCtx != null) {
                                pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
                            }
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath, null, true));
//                            pipeline.addLast(new WebSocketIndexPageHandler(websocketPath));
                            pipeline.addLast(new WebSocketFrameHandler(onServerListener));
                        }
                    });
            ChannelFuture f = serverBootstrap.bind(port).sync();
            if (onServerListener != null) {
                onServerListener.onServerStarted(port);
            }
            ServerManager.getLogger().info("The " + serverName + " is started....");
            isRunning = true;
            f.channel().closeFuture().sync();
            if (onServerListener != null) {
                onServerListener.onServerStop();
            }
            ServerManager.getLogger().info("The " + serverName + " is stopped");
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
