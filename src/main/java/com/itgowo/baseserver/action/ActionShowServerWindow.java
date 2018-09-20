package com.itgowo.baseserver.action;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.baseserver.view.ServerConfig;
import com.itgowo.baseserver.base.ActionRequest;
import com.itgowo.baseserver.base.BaseConfig;
import com.itgowo.baseserver.base.BaseRequest;
import com.itgowo.baseserver.base.ServerJsonEntity;

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
