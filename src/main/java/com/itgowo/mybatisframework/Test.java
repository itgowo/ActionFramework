package com.itgowo.mybatisframework;

import com.itgowo.mybatisframework.demo.StzbHero2;
import com.itgowo.mybatisframework.demo.StzbHero2Dao;
import com.itgowo.mybatisframework.demo.StzbHero2Key;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSessionFactory;

public class Test {
    public static void main(String[] args) {
        SqlSessionFactory factory = MybatisManager.getSqlSessionFactory( );
        StzbHero2Dao dao = factory.openSession().getMapper(StzbHero2Dao.class);

        StzbHero2 d = dao.select(111);
        System.out.println(d);
    }
}
