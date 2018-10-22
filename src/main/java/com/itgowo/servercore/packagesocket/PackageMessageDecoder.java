package com.itgowo.servercore.packagesocket;

import com.itgowo.servercore.packagesocket.PackageMessage;
import com.itgowo.servercore.socket.ByteBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class PackageMessageDecoder extends SimpleChannelInboundHandler<Object> {
    private PackageMessage packageMessage = PackageMessage.getPackageMessage();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuffer buffer = ByteBuffer.newByteBuffer();
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            buffer.writeBytes(bytes);
        } else if (msg instanceof byte[]) {
            buffer.writeBytes((byte[]) msg);
        }
        List<PackageMessage> packageMessageList = packageMessage.packageMessage(buffer);
        for (int i = 0; i < packageMessageList.size(); i++) {
            ctx.fireChannelRead(packageMessageList.get(i));
        }
    }
}
