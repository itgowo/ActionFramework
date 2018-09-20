package com.itgowo.servercore.socket;

import com.itgowo.servercore.ServerHandler;
import com.itgowo.servercore.onServerListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by lujianchao on 2018/5/17.
 */
public class SocketServerHandler implements ServerHandler {
    private ChannelHandlerContext ctx;
    private byte[] bytes;

    public SocketServerHandler(ChannelHandlerContext ctx, byte[] bytes) {
        this.ctx = ctx;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public SocketServerHandler setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    @Override
    public String toString() {
        return new String(bytes);
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void sendData(byte[] data) {
        ctx.writeAndFlush(data);
    }

}
