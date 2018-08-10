package com.itgowo.baseServer.base;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import com.itgowo.baseServer.utils.Utils;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

/**
 * @author lujianchao
 * 请求事件处理类
 */
public class Dispatcher implements HttpServerHandler.onReceiveHandlerListener {
    private HashMap<String, ActionRequest> actionTasks = new HashMap<>();
    private onDispatcherListener dispatcherListener;
    private boolean isValidSign = true;
    /**
     * 是否校验时差,BaseRequest中复写对应实现方法
     */
    private boolean isValidTimeDifference = true;
    /**
     * 是否触发参数校验方法，BaseRequest中复写对应实现方法
     */
    private boolean isValidParameter = true;
    /**
     * 服务端与客户端时间差，BaseRequest中复写对应实现方法
     */
    private long serverClientTimeDifference = 60000;

    /**
     * 设置是否校验签名
     *
     * @param validSign
     * @return
     */
    public Dispatcher setValidSign(boolean validSign) {
        isValidSign = validSign;
        return this;
    }

    public Dispatcher setValidParameter(boolean validParameter) {
        isValidParameter = validParameter;
        return this;
    }

    /**
     * 手动调用扫描功能，自动检查指定包路径并添加到dispatch中
     *
     * @param packagePath
     */
    public void actionScanner(File file, String packagePath) {
        packagePath = packagePath.replace(".", "/");
        List<Class> classes = null;
        if (file.isFile()) {
            classes = Utils.getClasssFromJarFile(file.getAbsolutePath(), packagePath);
        } else {
            classes = Utils.getClassNameByFile(file.getAbsolutePath(), true, file.getAbsolutePath(), packagePath);
        }
        System.out.println("找到如下Action处理器：\r\n");
        for (int i = 0; i < classes.size(); i++) {
            Class c = classes.get(i);
            System.out.println(c.getName());
            try {
                Object o = c.newInstance();
                if (o instanceof ActionRequest) {
                    Object f = Utils.getFinalFieldValueByName("ACTION", c);
                    if (f != null && f instanceof String) {
                        registerAction((String) f, (ActionRequest) o);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("-----------------\r\n");
    }

    /**
     * 设置校验时差间隔，如果服务端和客户端时差较大，则会失败
     *
     * @param serverClientTimeDifference
     * @return
     */
    public Dispatcher setServerClientTimeDifference(long serverClientTimeDifference) {
        this.serverClientTimeDifference = serverClientTimeDifference;
        return this;
    }

    /**
     * 设置是否校验时差，如果服务端和客户端时差较大，则会失败
     *
     * @param validTimeDifference
     * @return
     */
    public Dispatcher setValidTimeDifference(boolean validTimeDifference) {
        isValidTimeDifference = validTimeDifference;
        return this;
    }

    /**
     * 注册Action
     *
     * @param actionRequest
     * @return
     */
    public Dispatcher registerAction(String key, ActionRequest actionRequest) {
        actionTasks.put(key, actionRequest);
        return this;
    }

    /**
     * 注册Action
     *
     * @param actionTasks
     * @return
     */
    public Dispatcher registerActions(HashMap<String, ActionRequest> actionTasks) {
        this.actionTasks.putAll(actionTasks);
        return this;
    }

    /**
     * 取消注册某个Action
     *
     * @param key
     * @return
     */
    public Dispatcher unRegisterAction(String key) {
        actionTasks.remove(key);
        return this;
    }

    /**
     * 清除所有Action
     *
     * @return
     */
    public Dispatcher clearAction() {
        actionTasks.clear();
        return this;
    }

    @Override
    public void onReceiveHandler(HttpServerHandler handler) {
        if (handler != null) {
            onDispatch(handler);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (dispatcherListener != null) dispatcherListener.onError(throwable);
    }

    /**
     * 设置处理函数，部分处理需要单独处理或者第三方处理
     *
     * @param dispatcherListener
     * @return
     */
    public Dispatcher setDispatcherListener(Dispatcher.onDispatcherListener dispatcherListener) {
        this.dispatcherListener = dispatcherListener;
        return this;
    }

    public void onDispatch(HttpServerHandler handler) {
        if (handler.getHttpRequest() != null && handler.getHttpRequest().method() != null) {
            if (dispatcherListener != null && dispatcherListener.interrupt(handler)) {
                try {
                    handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求被拦截"), true);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (handler.getHttpRequest().method() != HttpMethod.POST) {
                if (dispatcherListener != null) {
                    dispatcherListener.doRequestOtherMethod(handler, actionTasks.get(handler.getHttpRequest().method().name()));
                }
                return;
            }
        } else {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求无法处理，数据错误"), true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
        BaseRequest baseRequest = null;
        if (dispatcherListener != null) {
            try {
                baseRequest = dispatcherListener.parseJson(handler.getBody(Charset.forName("utf-8")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (baseRequest == null) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求解析失败"), true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
        if (isValidTimeDifference && !baseRequest.validTimeDifference(serverClientTimeDifference)) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("时间校验失败"), true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
        if (isValidParameter && !baseRequest.validParameter()) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("参数校验失败"), true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
        if (isValidSign && !baseRequest.validSign()) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("签名校验失败"), true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
        ActionRequest actionRequest = actionTasks.get(baseRequest.getAction());
        try {
            if (actionRequest == null) {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("未找到对应接口功能，请检查版本，如有问题请联系管理员"), true);
                return;
            }
            actionRequest.doAction(handler, baseRequest);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("业务处理异常").setError(e.getMessage()), true);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
    }

    public interface onDispatcherListener<Request extends BaseRequest> {
        void onError(Throwable throwable);

        /**
         * 其他请求，例如GET请求可以像Spring框架一样单独处理参数并匹配（单独实现，不自动实现RESTful风格）
         * 需要提前设置action为GET（PUT、DELETE）等处理类，会自动将非POST请求分发到对应类型的处理类。
         *
         * @param handler
         * @param actionRequest 没找到为null
         * @throws Exception
         */
        public void doRequestOtherMethod(HttpServerHandler handler, ActionRequest actionRequest);

        /**
         * 请求中断拦截，true则拦截不再传递下去
         *
         * @param handler
         * @return
         */
        public boolean interrupt(HttpServerHandler handler);

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
         * @param o
         * @return
         * @throws Exception
         */
        public String toJson(Object o) throws Exception;
    }
}
