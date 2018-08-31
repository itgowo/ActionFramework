package com.itgowo.baseServer.demo;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.baseServer.base.ActionRequest;
import com.itgowo.baseServer.base.BaseRequest;

public class DemoActionRequest implements ActionRequest {
    public static final String ACTION = "helloword";
    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        handler.sendData("helloword!this is demo interface",false);
    }
}
