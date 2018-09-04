package com.itgowo.baseServer.demo;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.baseServer.action.ActionRequestGet;
import com.itgowo.baseServer.base.BaseRequest;

public class DemoActionRequestGet implements ActionRequestGet {
    public static final String ACTION = "/aa/helloword";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        handler.sendData(ACTION,false);
    }
}
