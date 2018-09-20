package com.itgowo.actionserver.demo;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionserver.base.ActionRequest;
import com.itgowo.actionserver.base.BaseRequest;

public class DemoActionRequest implements ActionRequest {
    public static final String ACTION = "helloword";
    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        handler.sendData("helloword!this is demo interface",false);
    }
}
