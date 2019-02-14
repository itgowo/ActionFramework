package com.itgowo.mybatisframework.demo;

import java.io.Serializable;

/**
 * stzb_hero2
 * @author 
 */
public class StzbHero2WithBLOBs extends StzbHero2 implements Serializable {
    /**
     * 说明简介
     */
    private String desc;

    private String groudArr;

    /**
     * 天赋技能简述
     */
    private String methodDesc;

    /**
     * 拆解技能简述
     */
    private String methodDesc1;

    /**
     * 拆解技能2简述
     */
    private String methodDesc2;

    /**
     * 组合信息
     */
    private String groups;

    private static final long serialVersionUID = 1L;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGroudArr() {
        return groudArr;
    }

    public void setGroudArr(String groudArr) {
        this.groudArr = groudArr;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public String getMethodDesc1() {
        return methodDesc1;
    }

    public void setMethodDesc1(String methodDesc1) {
        this.methodDesc1 = methodDesc1;
    }

    public String getMethodDesc2() {
        return methodDesc2;
    }

    public void setMethodDesc2(String methodDesc2) {
        this.methodDesc2 = methodDesc2;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }
}