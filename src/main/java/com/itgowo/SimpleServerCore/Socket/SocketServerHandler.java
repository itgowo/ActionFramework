package com.itgowo.SimpleServerCore.Socket;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by lujianchao on 2018/5/17.
 */
public class SocketServerHandler {
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

    public interface onReceiveHandlerListener {
        void onReceiveHandler(SocketServerHandler handler);

        void onChannelActive(ChannelHandlerContext ctx);

        void onChannelInactive(ChannelHandlerContext ctx);

        void onError(Throwable throwable);
    }
}
