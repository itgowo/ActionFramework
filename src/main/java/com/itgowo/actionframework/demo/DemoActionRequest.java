package com.itgowo.actionframework.demo;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;

import java.util.List;

public class DemoActionRequest extends ActionRequest<DemoClientRequest> {

    @Override
    public void doAction(HttpServerHandler handler, DemoClientRequest baseRequest) throws Exception {
        handler.sendData("helloword!this is demo interface",false);
    }

    @Override
    public void getFilter(List<Filter> filterList) {
        filterList.add(new Filter("POST","helloword",null));
        filterList.add(new Filter("GET",null,"helloword"));
    }
}
