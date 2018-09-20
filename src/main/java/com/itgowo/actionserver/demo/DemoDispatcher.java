package com.itgowo.actionserver.demo;

import com.itgowo.actionserver.ServerManager;
import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionserver.base.Dispatcher;
import io.netty.handler.codec.http.HttpResponseStatus;

public class DemoDispatcher implements Dispatcher.onDispatcherListener {

    @Override
    public void onDispatcherError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public boolean interrupt(HttpServerHandler handler) {
        ServerManager.getLogger().finest(handler.toString());
        return false;
    }

    @Override
    public boolean onNotFoundAction(HttpServerHandler handler, String action) throws Exception {
        handler.sendData(HttpResponseStatus.NOT_FOUND,"404,not found action",false);
        return true;
    }

    @Override
    public void onServerStarted(int serverPort) {
    }


    @Override
    public void onServerStop() {
    }

}
