package com.itgowo.actionserver.action;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionserver.base.ActionRequest;
import com.itgowo.actionserver.base.BaseRequest;
import com.itgowo.actionserver.base.ServerJsonEntity;
import com.itgowo.actionserver.utils.SystemInfo;

public class ActionServerStatus implements ActionRequest {
    public static final String ACTION = "ServerStatus";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        entity.setData(new SystemInfo());
        handler.sendData(entity, true);
    }
}
