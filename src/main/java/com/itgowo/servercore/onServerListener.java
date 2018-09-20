package com.itgowo.servercore;

import io.netty.channel.ChannelHandlerContext;

public interface onServerListener<Handler extends ServerHandler> {
    void onChannelActive(ChannelHandlerContext ctx);

    void onChannelInactive(ChannelHandlerContext ctx);

    void onReceiveHandler(Handler handler) throws Exception;

    void onError(Throwable throwable);

    void onServerStarted(int serverPort);

    void onServerStop();
}
