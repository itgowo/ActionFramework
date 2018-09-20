package com.itgowo.baseserver.demo;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.baseserver.action.ActionRequestGet;
import com.itgowo.baseserver.base.BaseRequest;

public class DemoActionRequestGet implements ActionRequestGet {
    public static final String ACTION = "/aa/helloword";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        handler.sendData(ACTION,false);
    }
}
