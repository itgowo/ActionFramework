package com.itgowo.actionframework.action;

import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.BaseConfig;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.actionframework.view.ServerConfig;
import com.itgowo.servercore.http.HttpServerHandler;

import java.util.List;

public class ActionShowServerWindow extends ActionRequest {
    public static final String ACTION = "ShowServerWindow";
    public static final String METHOD = "POST";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        if (BaseConfig.isShowServerWindow()) {
            ServerConfig.showServerWindow();
        }
        handler.sendData(new ServerJsonEntity(), true);
    }

    @Override
    public void getFilter(List<Filter> filterList) {
        filterList.add(new Filter(METHOD, ACTION, ""));
    }

}
