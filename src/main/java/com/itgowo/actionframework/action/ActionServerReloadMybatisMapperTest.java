package com.itgowo.actionframework.action;

import com.itgowo.actionframework.base.ActionRequest;
import com.itgowo.actionframework.base.BaseRequest;
import com.itgowo.actionframework.base.ServerJsonEntity;
import com.itgowo.mybatisframework.MybatisManager;
import com.itgowo.mybatisframework.demo.StzbHero2;
import com.itgowo.mybatisframework.demo.StzbHero2Dao;
import com.itgowo.servercore.http.HttpServerHandler;

import java.util.List;

public class ActionServerReloadMybatisMapperTest extends ActionRequest {
    public static final String ACTION = "ReloadMapperTest";
    public static final String METHOD = "POST";

    @Override
    public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
        ServerJsonEntity entity = new ServerJsonEntity();
        StzbHero2Dao dao = MybatisManager.getDao(StzbHero2Dao.class);
        StzbHero2 d = dao.select(111);
        System.out.println(d.getId() + "  " + dao);
        handler.sendData(entity.setData(d), true);

    }

    @Override
    public void getFilter(List<Filter> filterList) {
        filterList.add(new Filter(METHOD, ACTION, ""));
    }

}
