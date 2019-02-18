package com.itgowo.mybatisframework.demo;

import java.io.Serializable;

/**
 * stzb_hero2
 *
 * @author
 */
public class DemoEntity implements Serializable {

    private Integer id;

    public Integer getId() {
        return id;
    }

    public DemoEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * 武将名
     */
    private String name;

    /**
     * 阵营国家
     */
    private String contory;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContory() {
        return contory;
    }

    public void setContory(String contory) {
        this.contory = contory;
    }


}