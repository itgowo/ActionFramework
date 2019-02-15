package com.itgowo.mybatisframework;

import com.itgowo.mybatisframework.demo.StzbHero2;
import com.itgowo.mybatisframework.demo.StzbHero2Dao;

public class Test {
    public static void main(String[] args) {
        StzbHero2Dao dao = MybatisManager.getDao(StzbHero2Dao.class);
        StzbHero2 d = dao.select(111);
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
                StzbHero2 d = dao.select(111);
                System.out.println(d.getId()+"  "+dao);
            }
        }).start();
    }
}
