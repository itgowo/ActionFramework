package com.itgowo.mybatisframework.demo;

import com.itgowo.mybatisframework.MyBatisBaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * StzbHero2Dao继承基类
 */
@Mapper
public interface DemoDao extends MyBatisBaseDao<DemoEntity, Integer > {
    DemoEntity select(Integer keyid);
}