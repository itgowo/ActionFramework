package com.itgowo.servercore.socket;/*
 * Copyright (c) 2017. lujianchao
 * @晴空的一滴雨
 * WebSite:http://itgowo.com.
 * GitHub:https://github.com/hnsugar
 * CSDN:http://blog.csdn.net/hnsugar
 *
 */

import com.itgowo.servercore.onServerListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by lujianchao on 2017/3/28.
 */
public class SocketServerInboundHandlerAdapter extends SimpleChannelInboundHandler<ByteBuf> {
    private onServerListener onServerListener;
    private ByteBuf bytes = Unpooled.buffer();

    public SocketServerInboundHandlerAdapter(com.itgowo.servercore.onServerListener onServerListener) {
        this.onServerListener = onServerListener;
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
        this.bytes.writeBytes((ByteBuf) msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (onServerListener != null) {
            onServerListener.onReceiveHandler(new SocketServerHandler(ctx, ByteBuffer.newByteBuffer().writeBytes(bytes.array(), bytes.readableBytes())));
            bytes.release();
        }
        bytes = Unpooled.buffer();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (onServerListener != null) {
            onServerListener.onError(cause);
        }
    }
}
