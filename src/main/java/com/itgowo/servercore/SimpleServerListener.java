package com.itgowo.servercore;

import io.netty.channel.ChannelHandlerContext;

public class SimpleServerListener<T> implements onServerListener {
    @Override
    public void onChannelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void onChannelInactive(ChannelHandlerContext ctx) {

    }

    @Override
    public void onReceiveHandler(ServerHandler handler) throws Exception {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onServerStarted(int serverPort) {

    }

    @Override
    public void onServerStop() {

    }
}
