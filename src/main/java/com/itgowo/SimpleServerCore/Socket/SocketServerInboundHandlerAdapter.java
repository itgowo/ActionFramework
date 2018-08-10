package com.itgowo.SimpleServerCore.Socket;/*
 * Copyright (c) 2017. lujianchao
 * @晴空的一滴雨
 * WebSite:http://itgowo.com.
 * GitHub:https://github.com/hnsugar
 * CSDN:http://blog.csdn.net/hnsugar
 *
 */

import com.itgowo.SimpleServerCore.Utils.Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

/**
 * Created by hnvfh on 2017/3/28.
 */
public class SocketServerInboundHandlerAdapter implements ChannelInboundHandler {
    private SocketServerHandler.onReceiveHandlerListener onReceiveHandlerListener;
    private byte[] bytes = new byte[0];

    public SocketServerInboundHandlerAdapter setReceiveHandlerListener(SocketServerHandler.onReceiveHandlerListener receiveHandlerListener) {
        onReceiveHandlerListener = receiveHandlerListener;
        return this;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (onReceiveHandlerListener != null) {
            onReceiveHandlerListener.onChannelActive(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (onReceiveHandlerListener != null) {
            onReceiveHandlerListener.onChannelInactive(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] bytes= (byte[]) msg;
        this.bytes = Utils.append(this.bytes, bytes);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        if (onReceiveHandlerListener != null) {
            onReceiveHandlerListener.onReceiveHandler(new SocketServerHandler(ctx, bytes));
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
        if (onReceiveHandlerListener != null) {
            onReceiveHandlerListener.onError(cause);
        }
    }
}
