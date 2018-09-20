package com.itgowo.baseserver.demo;

import com.alibaba.fastjson.JSON;
import com.itgowo.baseserver.ServerManager;
import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.baseserver.base.Dispatcher;

public class DemoDispatcher implements Dispatcher.onDispatcherListener<DemoClientRequest> {
    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public boolean interrupt(HttpServerHandler handler) {
        return false;
    }

    @Override
    public boolean onNotFoundAction(HttpServerHandler handler, String action) throws Exception {
        handler.sendData("404,not found action",false);
        return true;
    }


    @Override
    public DemoClientRequest parseJson(String string) throws Exception {
        return JSON.parseObject(string, DemoClientRequest.class);
    }

    @Override
    public String toJson(Object o) throws Exception {
        return JSON.toJSONString(o);
    }

    @Override
    public void onServerStarted(int serverPort) {
        ServerManager.getLogger().info("DemoMainClassForWindow.onServerStarted  " + serverPort);
    }


    @Override
    public void onServerStop() {
        ServerManager.getLogger().info("DemoMainClassForWindow.onServerStop");
    }

}
