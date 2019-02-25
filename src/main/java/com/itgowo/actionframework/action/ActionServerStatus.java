package com.itgowo.actionframework.action;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.actionframework.utils.SystemInfo;

import java.util.ArrayList;
import java.util.List;

public class ActionServerStatus extends ActionRequest<BaseRequest> {
    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        entity.setData(new SystemInfo());
        handler.sendData(entity, true);
    }

    @Override
    public void getFilter(List<Filter> filterList) {
        filterList.add(new Filter("POST","ServerStatus",""));
        filterList.add(new Filter("GET","","/ServerStatus"));
    }

}
