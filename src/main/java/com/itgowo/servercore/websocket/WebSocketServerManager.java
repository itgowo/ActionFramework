package com.itgowo.servercore.websocket;

import com.itgowo.servercore.BaseServerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Created by lujianchao on 2017/5/25.
 */
public class WebSocketServerManager extends BaseServerManager {
    private boolean isSSL = false;
    private String websocketPath = "";

    public WebSocketServerManager(String serverName) {
        super(serverName);
    }

    @Override
    protected void prepareServerConfig(ServerBootstrap serverBootstrap, SocketChannel ch) throws Exception {
        final SslContext sslCtx;
        if (isSSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath, null, true));
//                            pipeline.addLast(new WebSocketIndexPageHandler(websocketPath));
        pipeline.addLast(new WebSocketFrameHandler(onServerListener));
    }

    public boolean isSSL() {
        return isSSL;
    }

    public WebSocketServerManager setSSL(boolean SSL) {
        isSSL = SSL;
        return this;
    }

    public String getWebsocketPath() {
        return websocketPath;
    }

    public WebSocketServerManager setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
        return this;
    }
}
