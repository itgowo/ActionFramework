package com.itgowo.packageframework;

import com.itgowo.servercore.ServerHandler;
import com.itgowo.servercore.SimpleServerListener;
import com.itgowo.servercore.packagesocket.PackageMessage;
import com.itgowo.servercore.packagesocket.PackageServerHandler;
import com.itgowo.servercore.packagesocket.PackageServerManager;
import com.itgowo.servercore.socket.SocketServerHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageFrameworkManager {
    private PackageServerManager packageServerManager;
    private onPackageFrameworkListener listener;
    private HashMap<String, Client> clients = new HashMap<>();

    public void startServer(String serverName, onPackageFrameworkListener listener) {
        this.listener = listener;
        packageServerManager = new PackageServerManager();
        packageServerManager.setThreadConfig(2, 4);
        packageServerManager.setServerName(serverName);
        packageServerManager.setOnServerListener(new SimpleServerListener() {
            @Override
            public void onReceiveHandler(ServerHandler handler) throws Exception {
                PackageServerHandler packageServerHandler = (PackageServerHandler) handler;
                onFirst(packageServerHandler);
            }
        });
    }

    private void onFirst(PackageServerHandler packageServerHandler) {
        Client client = clients.get(packageServerHandler.getCtx().channel().id().asLongText());
        if (client != null) {
            client.refreshLastTime();
        }
        if (PackageMessage.DATA_TYPE_HEART == packageServerHandler.getPackageMessage().getDataType()) {
            listener.onHeartMessage(packageServerHandler);
        } else {
            listener.onReceivedMessage(packageServerHandler);
        }
    }

    public void sendHeartPackage(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(PackageMessage.getHeartPackageMessage().encodePackageMessage().readableBytesArray());
    }

    public void sendHeartPackage(SocketServerHandler handler) {
        sendHeartPackage(handler.getCtx());
    }

    public void sendPackageMessage(ChannelHandlerContext channelHandlerContext, PackageMessage packageMessage) {
        channelHandlerContext.writeAndFlush(packageMessage.encodePackageMessage().readableBytesArray());
    }

    public void sendPackageMessage(SocketServerHandler handler, PackageMessage packageMessage) {
        sendPackageMessage(handler.getCtx(), packageMessage);
    }

    public void addClientToPool(Client client) {
        clients.put(client.getClientId(), client);
    }

    public List<Client> getTimeoutClients() {
        List<Client> timeoutClients = new ArrayList<>();
        for (Map.Entry<String, Client> stringClientEntry : clients.entrySet()) {
            if (stringClientEntry.getValue().isTimeout()) {
                timeoutClients.add(stringClientEntry.getValue());
            }
        }
        return timeoutClients;
    }
}
