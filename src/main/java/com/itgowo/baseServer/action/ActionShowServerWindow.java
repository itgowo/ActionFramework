package com.itgowo.baseServer.action;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.baseServer.view.ServerConfig;
import com.itgowo.baseServer.base.ActionRequest;
import com.itgowo.baseServer.base.BaseConfig;
import com.itgowo.baseServer.base.BaseRequest;
import com.itgowo.baseServer.base.ServerJsonEntity;

public class ActionShowServerWindow implements ActionRequest {
    public static final String ACTION = "ShowServerWindow";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        if (BaseConfig.getServerShowServerWindow()) {
            ServerConfig.showServerWindow();
        }
        handler.sendData(new ServerJsonEntity(), true);
    }
}
