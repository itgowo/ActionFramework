package com.itgowo.mybatisframework.demo;

import org.apache.ibatis.annotations.Mapper;

/**
 * StzbHero2Dao继承基类
 */
@Mapper
public interface StzbHero2Dao extends MyBatisBaseDao<StzbHero2, StzbHero2Key > {
    StzbHero2 select(Integer keyid);
}