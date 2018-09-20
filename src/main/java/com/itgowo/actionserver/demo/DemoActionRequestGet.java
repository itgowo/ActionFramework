package com.itgowo.actionserver.demo;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionserver.action.ActionRequestGet;
import com.itgowo.actionserver.base.BaseRequest;

public class DemoActionRequestGet implements ActionRequestGet {
    public static final String ACTION = "/aa/helloword;/cc/bb";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        handler.sendData(ACTION,false);
    }
}
