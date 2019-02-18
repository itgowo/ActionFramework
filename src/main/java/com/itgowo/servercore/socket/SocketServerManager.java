package com.itgowo.servercore.socket;

import com.itgowo.servercore.BaseServerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by lujianchao on 2017/3/31.
 */
public class SocketServerManager extends BaseServerManager {

    public SocketServerManager(String serverName) {
        super(serverName);
    }

    @Override
    protected void prepareServerConfig(ServerBootstrap serverBootstrap, SocketChannel ch) throws Exception {
        ch.pipeline().addLast(SocketServerInboundHandlerAdapter.class.getSimpleName(), new SocketServerInboundHandlerAdapter(onServerListener));
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 4096)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, 4096);
    }
}
