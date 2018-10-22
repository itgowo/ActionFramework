package com.itgowo.servercore.packagesocket;

import com.itgowo.servercore.onServerListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PackageMessageHandlerAdapter extends SimpleChannelInboundHandler<PackageMessage> {
    private onServerListener onServerListener;

    public PackageMessageHandlerAdapter(com.itgowo.servercore.onServerListener onServerListener) {
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
    protected void channelRead0(ChannelHandlerContext ctx, PackageMessage msg) throws Exception {
        onServerListener.onReceiveHandler(new PackageServerHandler(ctx, msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (onServerListener != null) {
            onServerListener.onError(cause);
        }
    }
}
