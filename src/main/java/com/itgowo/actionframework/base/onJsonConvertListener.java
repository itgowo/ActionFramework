package com.itgowo.actionframework.base;

public interface onJsonConvertListener<Request extends BaseRequest> {
    /**
     * 定义Json解析器
     *
     * @param string
     * @return 返回指定对象
     * @throws Exception
     */
    public Request parseJson(String string) throws Exception;

    /**
     * 生成json序列化文本
     *
     * @param o
     * @return
     * @throws Exception
     */
    public String toJson(Object o) throws Exception;
}
