package com.itgowo.actionframework.demo;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;

public class DemoActionRequest implements ActionRequest {
    public static final String ACTION = "helloword;/helloword;/help";
    public static final String METHOD = "POST;GET;PUT";
    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        handler.sendData("helloword!this is demo interface",false);
    }
}
