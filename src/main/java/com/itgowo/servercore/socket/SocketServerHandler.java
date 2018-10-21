package com.itgowo.servercore.socket;

import com.itgowo.servercore.ServerHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by lujianchao on 2018/5/17.
 */
public class SocketServerHandler implements ServerHandler {
    private ChannelHandlerContext ctx;
    private ByteBuffer byteBuffer;

    public SocketServerHandler(ChannelHandlerContext ctx, ByteBuffer bytes) {
        this.ctx = ctx;
        this.byteBuffer = bytes;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public SocketServerHandler setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        return this;
    }

    @Override
    public String toString() {
        return new String(byteBuffer.readableBytesArray());
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void sendData(byte[] data) {
        ctx.writeAndFlush(data);
    }

}
