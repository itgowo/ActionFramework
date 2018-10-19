package com.itgowo.socketframework;/*
 * Copyright (c) 2017. lujianchao
 * @晴空的一滴雨
 * WebSite:http://itgowo.com.
 * GitHub:https://github.com/hnsugar
 * CSDN:http://blog.csdn.net/hnsugar
 *
 */

import com.itgowo.servercore.onServerListener;
import com.itgowo.servercore.utils.Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

/**
 * Created by hnvfh on 2017/3/28.
 */
public class SocketServerInboundHandlerAdapter implements ChannelInboundHandler {
    private onServerListener onServerListener;
    private byte[] bytes = new byte[0];

    public SocketServerInboundHandlerAdapter(com.itgowo.servercore.onServerListener onServerListener) {
        this.onServerListener = onServerListener;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.fireChannelActive();
        if (onServerListener != null) {
            onServerListener.onChannelActive(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.fireChannelInactive();
        if (onServerListener != null) {
            onServerListener.onChannelInactive(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] bytes= (byte[]) msg;
        this.bytes = Utils.append(this.bytes, bytes);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        if (onServerListener != null) {
            onServerListener.onReceiveHandler(new SocketServerHandler(ctx, bytes));
        }
        bytes = new byte[0];
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (onServerListener != null) {
            onServerListener.onError(cause);
        }
    }
}
