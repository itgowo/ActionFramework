package com.itgowo.servercore.packagesocket;

import com.itgowo.servercore.socket.ByteBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PackageMessageDecoder extends SimpleChannelInboundHandler<Object> {
    private ConcurrentHashMap<String, PackageMessage> hashMap = new ConcurrentHashMap<>();


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        hashMap.remove(ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

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
        PackageMessage packageMessage = hashMap.get(ctx.channel().id().asLongText());
        if (packageMessage == null) {
            packageMessage = PackageMessage.getPackageMessage();
            hashMap.put(ctx.channel().id().asLongText(), packageMessage);
        }
        List<PackageMessage> packageMessageList = packageMessage.packageMessage(buffer);
        for (int i = 0; i < packageMessageList.size(); i++) {
            ctx.fireChannelRead(packageMessageList.get(i));
        }
    }
}
