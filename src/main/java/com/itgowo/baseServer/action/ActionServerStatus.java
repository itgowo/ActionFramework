package com.itgowo.baseServer.action;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.baseServer.base.ActionRequest;
import com.itgowo.baseServer.base.BaseRequest;
import com.itgowo.baseServer.base.ServerJsonEntity;
import com.itgowo.baseServer.utils.SystemInfo;

public class ActionServerStatus implements ActionRequest {
    public static final String ACTION = "ServerStatus";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        entity.setData(new SystemInfo());
        handler.sendData(entity, true);
    }
}
