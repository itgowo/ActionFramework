package com.itgowo.servercore.packagesocket;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class PackageMessageEncoder extends MessageToMessageEncoder<PackageMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, PackageMessage msg, List<Object> out) throws Exception {
        out.add(Unpooled.wrappedBuffer(msg.encodePackageMessage().readableBytesArray()));
    }
}
