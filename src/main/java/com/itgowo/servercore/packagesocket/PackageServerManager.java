package com.itgowo.servercore.packagesocket;

import com.itgowo.servercore.BaseServerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.SocketChannel;

public class PackageServerManager extends BaseServerManager {

    public PackageServerManager(String serverName) {
        super(serverName);
    }

    @Override
    protected void prepareServerConfig(ServerBootstrap serverBootstrap, SocketChannel ch) throws Exception {
        ch.pipeline().addLast(PackageMessageDecoder.class.getSimpleName(),new PackageMessageDecoder());
        ch.pipeline().addLast(PackageMessageHandlerAdapter.class.getSimpleName(),new PackageMessageHandlerAdapter(onServerListener));
        ch.pipeline().addLast(PackageMessageEncoder.class.getSimpleName(),new PackageMessageEncoder());
    }
}
