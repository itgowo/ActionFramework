package com.itgowo.actionframework.action;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.actionframework.utils.SystemInfo;

public class ActionServerStatus implements ActionRequest {
    public static final String ACTION = "ServerStatus";
    public static final String METHOD = "POST";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        entity.setData(new SystemInfo());
        handler.sendData(entity, true);
    }
}
