package com.itgowo.mybatisframework.demo;

import java.io.Serializable;

/**
 * stzb_hero2
 *
 * @author
 */
public class DemoEntity   implements Serializable {
    private Integer keyid;

    /**
     * 数据库索引，非武将id
     */
    private Integer id;


    public Integer getKeyid() {
        return keyid;
    }

    public DemoEntity setKeyid(Integer keyid) {
        this.keyid = keyid;
        return this;
    }

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

    /**
     * 星级
     */
    private Integer quality;

    /**
     * 代价，占位大小
     */
    private Double cost;

    /**
     * 兵种
     */
    private String type;

    /**
     * 攻击距离
     */
    private Integer distance;

    /**
     * 攻击初始值
     */
    private Integer attack;

    /**
     * 攻击成长
     */
    private Double attGrow;

    /**
     * 防御初始值
     */
    private Integer def;

    /**
     * 防御增长值
     */
    private Double defGrow;

    /**
     * 谋略初始值
     */
    private Integer ruse;

    /**
     * 谋略成长值
     */
    private Double ruseGrow;

    /**
     * 攻城初始值
     */
    private Integer siege;

    /**
     * 攻城成长值
     */
    private Double siegeGrow;

    /**
     * 速度初始值
     */
    private Integer speed;

    /**
     * 速度成长值
     */
    private Double speedGrow;

    /**
     * 性别
     */
    private String sex;

    /**
     * 头像
     */
    private String icon;

    /**
     * 组合名
     */
    private String groupName;

    /**
     * 天赋技能id
     */
    private Integer methodId;

    /**
     * 天赋技能名
     */
    private String methodName;

    /**
     * 拆解技能id
     */
    private Integer methodId1;

    /**
     * 拆解技能名
     */
    private String methodName1;

    /**
     * 拆解技能2id
     */
    private Integer methodId2;

    /**
     * 拆解技能2名
     */
    private String methodName2;

    /**
     * 唯一标示名称
     */
    private String uniqueName;

    /**
     * 随机武将级别，越高越不容易随机到，例如军士级别高，部分情况随机不到
     */
    private Integer normal;

    private String uuid;
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

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Double getAttGrow() {
        return attGrow;
    }

    public void setAttGrow(Double attGrow) {
        this.attGrow = attGrow;
    }

    public Integer getDef() {
        return def;
    }

    public void setDef(Integer def) {
        this.def = def;
    }

    public Double getDefGrow() {
        return defGrow;
    }

    public void setDefGrow(Double defGrow) {
        this.defGrow = defGrow;
    }

    public Integer getRuse() {
        return ruse;
    }

    public void setRuse(Integer ruse) {
        this.ruse = ruse;
    }

    public Double getRuseGrow() {
        return ruseGrow;
    }

    public void setRuseGrow(Double ruseGrow) {
        this.ruseGrow = ruseGrow;
    }

    public Integer getSiege() {
        return siege;
    }

    public void setSiege(Integer siege) {
        this.siege = siege;
    }

    public Double getSiegeGrow() {
        return siegeGrow;
    }

    public void setSiegeGrow(Double siegeGrow) {
        this.siegeGrow = siegeGrow;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Double getSpeedGrow() {
        return speedGrow;
    }

    public void setSpeedGrow(Double speedGrow) {
        this.speedGrow = speedGrow;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getMethodId1() {
        return methodId1;
    }

    public void setMethodId1(Integer methodId1) {
        this.methodId1 = methodId1;
    }

    public String getMethodName1() {
        return methodName1;
    }

    public void setMethodName1(String methodName1) {
        this.methodName1 = methodName1;
    }

    public Integer getMethodId2() {
        return methodId2;
    }

    public void setMethodId2(Integer methodId2) {
        this.methodId2 = methodId2;
    }

    public String getMethodName2() {
        return methodName2;
    }

    public void setMethodName2(String methodName2) {
        this.methodName2 = methodName2;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public Integer getNormal() {
        return normal;
    }

    public void setNormal(Integer normal) {
        this.normal = normal;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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