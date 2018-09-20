package com.itgowo.actionframework.action;

import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.actionframework.view.ServerConfig;
import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseConfig;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;

public class ActionShowServerWindow implements ActionRequest {
    public static final String ACTION = "ShowServerWindow";
    public static final String METHOD = "POST";
    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        if (BaseConfig.isShowServerWindow()) {
            ServerConfig.showServerWindow();
        }
        handler.sendData(new ServerJsonEntity(), true);
    }
}
