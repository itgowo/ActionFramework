package com.itgowo.baseServer.base;

import com.itgowo.SimpleServerCore.Http.HttpServerHandler;
import io.netty.handler.codec.http.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Dispatcher implements HttpServerHandler.onReceiveHandlerListener {
    private HashMap<String, ActionRequest> actionTasks = new HashMap<>();
    private onServerListener serverListener;
    private boolean isValidSign=true;

    public Dispatcher setValidSign(boolean validSign) {
        isValidSign = validSign;
        return this;
    }

    public void registerAction(String key, ActionRequest actionRequest) {
        actionTasks.put(key, actionRequest);
    }

    public void registerActions(HashMap<String, ActionRequest> actionTasks) {
        this.actionTasks.putAll(actionTasks);
    }

    public void unRegisterAction(String key) {
        actionTasks.remove(key);
    }

    public void clearAction() {
        actionTasks.clear();
    }

    @Override
    public void onReceiveHandler(HttpServerHandler handler) {
        if (handler != null) {
            onDispatch(handler);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (serverListener != null) serverListener.onError(throwable);
    }

    public Dispatcher setServerListener(Dispatcher.onServerListener serverListener) {
        this.serverListener = serverListener;
        return this;
    }

    public void onDispatch(HttpServerHandler handler) {
        if (handler.getHttpRequest() != null && handler.getHttpRequest().method() != null) {
            if (handler.getHttpRequest().method() != HttpMethod.POST) {
                if (serverListener != null) {
                    serverListener.doRequestOtherMethod(handler, actionTasks.get(handler.getHttpRequest().method().name()));
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
        BaseRequest baseRequest = BaseRequest.parse(handler);
        if (baseRequest == null) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求解析失败"), true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return;
        }
        if (!baseRequest.isValid(isValidSign)) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("参数校验失败"), true);
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

    public interface onServerListener {
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
    }
}
