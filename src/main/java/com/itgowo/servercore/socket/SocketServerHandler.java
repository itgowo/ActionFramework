package com.itgowo.servercore.socket;

import com.itgowo.servercore.ServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
        ctx.writeAndFlush(Unpooled.wrappedBuffer(data));
    }
    public void sendData(ByteBuf data) {
        ctx.writeAndFlush(data);
    }
    public void sendData(ByteBuffer data) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer(data.readableBytesArray()));
    }
}
