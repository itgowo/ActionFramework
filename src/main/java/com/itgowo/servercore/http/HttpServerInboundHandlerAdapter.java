package com.itgowo.servercore.http;/*
 * Copyright (c) 2017. lujianchao
 * @晴空的一滴雨
 * WebSite:http://lujianchao.com.
 * GitHub:https://github.com/hnsugar
 * CSDN:http://blog.csdn.net/hnsugar
 *
 */

import com.itgowo.servercore.onServerListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by lujianchao onServerListener 2017/3/28.
 * http服务器
 */
public class HttpServerInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    private onServerListener onServerListener;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private ByteBuf byteBuf = Unpooled.buffer();

    public HttpServerInboundHandlerAdapter(onServerListener onServerListener) {
        this.onServerListener = onServerListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            httpRequest = (HttpRequest) msg;
        }
        if (msg instanceof HttpResponse) {
            httpResponse = (HttpResponse) msg;
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            byteBuf.writeBytes(content.content());
            content.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (onServerListener != null) {
            if (ctx != null && httpRequest != null) {
                onServerListener.onReceiveHandler(new HttpServerHandler(ctx, httpRequest, httpResponse, byteBuf));
            }
        }
        byteBuf = Unpooled.buffer();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (onServerListener != null) {
            onServerListener.onError(cause);
        }
        ctx.close();
    }
}
