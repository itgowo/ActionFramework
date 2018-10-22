package com.itgowo.packageframework;

import com.itgowo.servercore.socket.SocketServerHandler;

public class Client {
    public static long timeOut = 30 * 60 * 1000;
    private SocketServerHandler handler;
    private String clientId;
    private long lastMsgTime = System.currentTimeMillis();

    public Client(SocketServerHandler handler) {
        this.handler = handler;
        this.clientId=handler.getCtx().channel().id().asLongText();
    }

    public boolean isTimeout() {
        if (System.currentTimeMillis() - lastMsgTime > timeOut) {
            return true;
        }
        return false;
    }

    public void refreshLastTime() {
        lastMsgTime = System.currentTimeMillis();
    }

    public SocketServerHandler getHandler() {
        return handler;
    }

    public Client setHandler(SocketServerHandler handler) {
        this.handler = handler;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public Client setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public Client setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
        return this;
    }
}
