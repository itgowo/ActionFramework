package com.itgowo.actionframework.action;

import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.mybatisframework.MybatisManager;
import com.itgowo.servercore.http.HttpServerHandler;

import java.util.List;

public class ActionServerReloadMybatisMapper extends ActionRequest {
    public static final String ACTION = "ReloadMapper";
    public static final String METHOD = "POST";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        boolean result = MybatisManager.reloadMapper();
        if (result) {
            handler.sendData(entity, true);
        } else {
            handler.sendData(entity.setCode(ServerJsonEntity.Fail).setMsg("重新加载Mapper失败，请检查日志"), true);
        }
    }

    @Override
    public void getFilter(List<Filter> filterList) {
        filterList.add(new Filter(METHOD, ACTION, ""));
    }

}
