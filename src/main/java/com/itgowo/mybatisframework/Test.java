package com.itgowo.mybatisframework;

import com.itgowo.mybatisframework.demo.DemoEntity;
import com.itgowo.mybatisframework.demo.DemoDao;

public class Test {
    public static void main(String[] args) {
        DemoDao dao = MybatisManager.getDao(DemoDao.class);
        DemoEntity d = dao.select(111);
        System.out.println(d.getId()+"  "+dao);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MybatisManager.reloadMapper();
                DemoEntity d = dao.select(111);
                System.out.println(d.getId()+"  "+dao);
            }
        }).start();
    }
}
