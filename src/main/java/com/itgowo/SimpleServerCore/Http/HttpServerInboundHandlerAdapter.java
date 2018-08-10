package com.itgowo.SimpleServerCore.Http;/*
 * Copyright (c) 2017. lujianchao
 * @晴空的一滴雨
 * WebSite:http://lujianchao.com.
 * GitHub:https://github.com/hnsugar
 * CSDN:http://blog.csdn.net/hnsugar
 *
 */

import com.itgowo.SimpleServerCore.Utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * Created by lujianchao onReceiveHandlerListener 2017/3/28.
 * http服务器
 */
public class HttpServerInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    private HttpServerHandler.onReceiveHandlerListener onReceiveHandlerListener;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private byte[] bytes = new byte[0];

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
            ByteBuf buf = content.content();
            byte[] b = new byte[buf.readableBytes()];
            buf.getBytes(0, b);
            this.bytes = Utils.append(this.bytes, b);
            buf.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        if (onReceiveHandlerListener != null) {
            if (ctx != null && httpRequest != null) {
                onReceiveHandlerListener.onReceiveHandler(new HttpServerHandler(ctx, httpRequest, httpResponse, bytes));
            }
        }
        bytes = new byte[0];
    }

    public HttpServerInboundHandlerAdapter setReceiveHandlerListener(HttpServerHandler.onReceiveHandlerListener onReceiveHandlerListener) {
        this.onReceiveHandlerListener = onReceiveHandlerListener;
        return this;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (onReceiveHandlerListener != null) {
            onReceiveHandlerListener.onError(cause);
        }
        ctx.close();
    }
}
