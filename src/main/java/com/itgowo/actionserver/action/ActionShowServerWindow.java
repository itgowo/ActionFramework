package com.itgowo.actionserver.action;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionserver.view.ServerConfig;
import com.itgowo.actionserver.base.ActionRequest;
import com.itgowo.actionserver.base.BaseConfig;
import com.itgowo.actionserver.base.BaseRequest;
import com.itgowo.actionserver.base.ServerJsonEntity;

public class ActionShowServerWindow implements ActionRequest {
    public static final String ACTION = "ShowServerWindow";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        if (BaseConfig.isShowServerWindow()) {
            ServerConfig.showServerWindow();
        }
        handler.sendData(new ServerJsonEntity(), true);
    }
}
