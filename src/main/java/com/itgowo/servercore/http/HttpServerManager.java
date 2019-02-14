package com.itgowo.servercore.http;

import com.itgowo.servercore.BaseServerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by lujianchao on 2017/5/25.
 */
public class HttpServerManager extends BaseServerManager {
    private String webRootDir;

    public HttpServerManager(String webRootDir) {
        this.webRootDir = webRootDir;
    }

    public HttpServerManager setWebRootDir(String webRootDir) {
        this.webRootDir = webRootDir;
        return this;
    }

    @Override
    protected void prepareServerConfig(ServerBootstrap serverBootstrap, SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpRequestDecoder());
        ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        ch.pipeline().addLast(new ChunkedWriteHandler());
        ch.pipeline().addLast(new HttpServerInboundHandlerAdapter(webRootDir,onServerListener));
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 4096)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false);
    }
}
