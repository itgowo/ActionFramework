package com.itgowo.baseServer.demo;

import com.alibaba.fastjson.JSON;
import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.baseServer.base.ActionRequest;
import com.itgowo.baseServer.base.Dispatcher;

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
    public DemoClientRequest parseJson(String string) throws Exception {
        return JSON.parseObject(string, DemoClientRequest.class);
    }

    @Override
    public String toJson(Object o) throws Exception {
        return JSON.toJSONString(o);
    }

    @Override
    public void onServerStarted(int serverPort) {
        System.out.println("DemoMainClassForWindow.onServerStarted  " + serverPort);
    }


    @Override
    public void onServerStop() {
        System.out.println("DemoMainClassForWindow.onServerStop");
    }

}
