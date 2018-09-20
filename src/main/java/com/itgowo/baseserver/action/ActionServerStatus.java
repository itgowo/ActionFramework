package com.itgowo.baseserver.action;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.baseserver.base.ActionRequest;
import com.itgowo.baseserver.base.BaseRequest;
import com.itgowo.baseserver.base.ServerJsonEntity;
import com.itgowo.baseserver.utils.SystemInfo;

public class ActionServerStatus implements ActionRequest {
    public static final String ACTION = "ServerStatus";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        entity.setData(new SystemInfo());
        handler.sendData(entity, true);
    }
}
