package com.itgowo.actionframework.action;

import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.mybatisframework.MybatisManager;
import com.itgowo.mybatisframework.demo.DemoDao;
import com.itgowo.servercore.http.HttpServerHandler;

import java.util.List;

public class ActionServerReloadMybatisMapperTest extends ActionRequest<BaseRequest> {
    public static final String ACTION = "ReloadMapperTest";
    public static final String METHOD = "POST";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        DemoDao dao = MybatisManager.getDao(DemoDao.class);
        handler.sendData(entity.setData(dao.toString()), true);
    }

    @Override
    public void getFilter(List<Filter> filterList) {
        filterList.add(new Filter(METHOD, ACTION, ""));
    }

}
