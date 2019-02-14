package com.itgowo.actionframework.demo;

import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.base.Dispatcher;
import com.itgowo.servercore.http.HttpServerHandler;

public class DemoDispatcher implements Dispatcher.onDispatcherListener {

    @Override
    public void onDispatcherError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public boolean interrupt(HttpServerHandler handler) {
        ServerManager.getLogger().finest(handler.toString());
        //TODO 校验，发送处理结果，返回true则后续不再处理。
        return false;
    }

    @Override
    public boolean onNotFoundAction(HttpServerHandler handler, String action) throws Exception {
//        handler.sendData(HttpResponseStatus.NOT_FOUND,"404,not found action",false);
        return false;
    }

    @Override
    public void onServerStarted(int serverPort) {
    }


    @Override
    public void onServerStop() {
    }

}
