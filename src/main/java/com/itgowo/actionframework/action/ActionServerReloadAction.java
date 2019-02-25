package com.itgowo.actionframework.action;

import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.Dispatcher;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.servercore.onServerListener;

import java.util.List;

public class ActionServerReloadAction extends ActionRequest<BaseRequest> {
    public static final String ACTION = "ReloadAction";
    public static final String METHOD = "POST";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        if (ServerManager.getHttpServerManager() != null) {
            onServerListener onServerListener = ServerManager.getHttpServerManager().getOnServerListener();
            if (onServerListener instanceof Dispatcher) {
                Dispatcher dispatcher = (Dispatcher) onServerListener;
                dispatcher.actionScanner(ActionServerReloadAction.class);
                handler.sendData(entity, true);
            } else {
                handler.sendData(entity.setCode(ServerJsonEntity.Fail).setMsg("框架中未使用默认或继承自Dispatcher，无法reload！"), true);
            }
        } else {
            handler.sendData(entity.setCode(ServerJsonEntity.Fail).setMsg("框架中未使用默认或设置HttpServer，无法reload！"), true);
        }
    }

    @Override
    public void getFilter(List<Filter> filterList) {
        filterList.add(new Filter(METHOD,ACTION,""));
    }

}
