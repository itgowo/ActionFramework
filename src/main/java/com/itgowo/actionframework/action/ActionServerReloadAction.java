package com.itgowo.actionframework.action;

import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.servercore.http.HttpServerHandler;

public class ActionServerReloadAction implements ActionRequest {
    public static final String ACTION = "ReloadAction";
    public static final String METHOD = "POST";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        ServerManager.getHttpServerManager().getOnServerListener().actionScanner(ActionServerReloadAction.class);
        handler.sendData(entity, true);
    }
}
