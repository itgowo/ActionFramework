package com.itgowo.actionframework.base;

import com.itgowo.servercore.http.HttpServerHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件分发处理，将实现该接口的类添加到处理器中就会被自动事件调用
 */
public abstract class ActionRequest<T extends BaseRequest> {
    //public static final String ACTION = "getHeroDetailList";
    //public static final String METHOD = "POST";
    public List<Filter> filterList = new ArrayList<>();

    public ActionRequest() {
        getFilter(filterList);
    }

    /**
     * 只处理POST请求，只有匹配对应action才会触发
     *
     * @param handler
     * @param request
     * @throws Exception
     */
    public abstract void doAction(HttpServerHandler handler, T request) throws Exception;

    public abstract void getFilter(List<Filter> filterList);

    /**
     * 如果是文件上传，建议值为/upload，method为POST，postAction为null
     */
    public class Filter {
        public Filter(String method, String postAction, String path) {
            this.method = method;
            this.postAction = postAction;
            this.path = path;
        }

        /**
         * 允许多个method值用英文分号;隔开
         */
        public String method = "POST";
        /**
         * 允许多个postAction值用英文分号;隔开，postAction只在POST请求中有效
         */
        public String postAction;
        /**
         * 允许多个path值用英文分号;隔开，path优先匹配
         */
        public String path;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Filter{");
            sb.append("method='").append(method).append('\'');
            sb.append(", postAction='").append(postAction).append('\'');
            sb.append(", path='").append(path).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
