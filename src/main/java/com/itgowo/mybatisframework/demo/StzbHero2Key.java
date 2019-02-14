package com.itgowo.mybatisframework.demo;

import java.io.Serializable;

/**
 * stzb_hero2
 * @author 
 */
public class StzbHero2Key implements Serializable {
    private Integer keyid;

    /**
     * 数据库索引，非武将id
     */
    private Integer id;

    private static final long serialVersionUID = 1L;

    public Integer getKeyid() {
        return keyid;
    }

    public StzbHero2Key setKeyid(Integer keyid) {
        this.keyid = keyid;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public StzbHero2Key setId(Integer id) {
        this.id = id;
        return this;
    }
}