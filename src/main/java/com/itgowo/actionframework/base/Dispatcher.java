package com.itgowo.actionframework.base;

import com.itgowo.BaseConfig;
import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.classutils.ClassEntry;
import com.itgowo.actionframework.classutils.ClassUtils;
import com.itgowo.actionframework.utils.Utils;
import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.servercore.onServerListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author lujianchao
 * 请求事件处理类
 */
public class Dispatcher implements onServerListener<HttpServerHandler> {
    public static float tps;
    public static long tpsTime;
    public static long tpsCount;
    public static ScheduledFuture scheduledFuture;
    /**
     * 外层Path，中间method，最内层PostAction
     */
    private HashMap<String, HashMap<String, HashMap<String, ActionRequest>>> actionList = new HashMap();

    private HashMap<String, String> actionPath = new HashMap<>();
    private onDispatcherListener dispatcherListener;
    private volatile boolean isHotLoading = false;
    private StringBuilder stringBuilder;
    private String rootPath = "";

    public void startAnalysisTps() {
        AnalysisTps();
    }

    public String getRootPath() {
        return rootPath;
    }

    public Dispatcher setRootPath(String rootPath) {
        if (!rootPath.startsWith("/")) {
            rootPath = "/" + rootPath;
        }
        this.rootPath = rootPath;
        return this;
    }

    public void stopAnalysisTps() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }


    /**
     * 手动调用扫描功能，自动检查指定包路径并添加到dispatch中
     *
     * @param mainClass 主项目工程内class文件，推荐main类
     */
    public void actionScanner(Class mainClass) {
        File file = ClassUtils.getJarFile(mainClass);
        List<String> packagePaths = BaseConfig.getServerActionPackageList();
        if (packagePaths == null || packagePaths.isEmpty()) {
            if (dispatcherListener != null) {
                dispatcherListener.onDispatcherError(new Throwable("ServerActionPackage配置不正确，配置格式例如com.game.stzb.action或者com/game/stzb/action,允许继续运行"));
            }
        }
        setLoadActionStatus(true);
        stringBuilder = new StringBuilder("\r\n找到如下Action处理器：\r\n");
        checkAndRegister(loadClass(file, packagePaths));
        stringBuilder.append("-----------------\r\n");
        ServerManager.getLogger().info(stringBuilder.toString());
        stringBuilder.setLength(0);
        stringBuilder = null;
        setLoadActionStatus(false);
    }

    private List<ClassEntry> loadClass(File file, List<String> packagePath) {
        if (packagePath == null || packagePath.size() == 0) {
            return new ArrayList<>();
        }
        List<ClassEntry> classes = ClassUtils.getClassList(file.getAbsolutePath(), packagePath);
        if (BaseConfig.getServerDynamicActionDir() != null && BaseConfig.getServerDynamicActionDir().trim().length() > 0) {
            File dynamicDir = new File(BaseConfig.getServerDynamicActionDir());
            List<ClassEntry> dynamicClass = ClassUtils.getClassList(dynamicDir.getAbsolutePath(), packagePath);
            classes.addAll(dynamicClass);
        }
        return classes;
    }

    /**
     * 检查并加载接口
     *
     * @param classes
     */
    private void checkAndRegister(List<ClassEntry> classes) {
        for (int i = 0; i < classes.size(); i++) {
            checkAndRegisterAction(classes.get(i));
        }
    }

    /**
     * 检查并加载接口
     *
     * @param classEntry
     */
    private void checkAndRegisterAction(ClassEntry classEntry) {
        if (classEntry == null || classEntry.getClassObject() == null) {
            return;
        }
        Class c = classEntry.getClassObject();
        try {
            if (c == null || Modifier.isInterface(c.getModifiers()) || Modifier.isAbstract(c.getModifiers()) || c.isEnum()) {
                return;
            }

            Constructor<?>[] constructors = c.getConstructors();
            if (constructors.length == 0) {
                return;
            }
            //判断是否有无参构造器
            boolean has = false;
            for (int i1 = 0; i1 < constructors.length; i1++) {
                if (constructors[i1].getParameterCount() == 0) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                return;
            }
        } catch (SecurityException e) {
            ServerManager.getLogger().warning(e.getLocalizedMessage());
            return;
        }

        Object o = null;
        try {
            o = c.newInstance();
        } catch (Exception e) {
            ServerManager.getLogger().fine(e.getLocalizedMessage());
            return;
        }
        try {
            if (o instanceof ActionRequest) {
                Object filter1 = Utils.getFieldValueByName("filterList", o);
                if (filter1 instanceof List) {
                    List<ActionRequest.Filter> filterList = (List<ActionRequest.Filter>) filter1;
                    if (filterList == null || filterList.isEmpty()) {
                        return;
                    }
                    for (int i = 0; i < filterList.size(); i++) {
                        if (filterList.get(i).method == null) {
                            continue;
                        }
                        registerAction(filterList.get(i), (ActionRequest) o, classEntry.getFilePath());
                    }

                }
            }
        } catch (Exception e) {
            ServerManager.getLogger().warning(e.getLocalizedMessage());
        }
    }


    /**
     * 注册Action
     *
     * @param actionRequest
     * @return
     */
    public Dispatcher registerAction(ActionRequest.Filter filter, ActionRequest actionRequest, String filePath) {

        if (filter.path == null) {
            filter.path = "/";
        }
        HashMap<String, HashMap<String, ActionRequest>> methodlist;
        if (actionList.containsKey(filter.path)) {
            methodlist = actionList.get(filter.path);
        } else {
            methodlist = new HashMap<>();
            if (!filter.path.startsWith("/") && filter.path.length() > 1) {
                filter.path = "/" + filter.path;
            }
            if (filter.path.endsWith("/")) {
                filter.path = filter.path.substring(0, filter.path.length() - 1);
            }
            if (rootPath != null) {
                if (rootPath.endsWith("/")) {
                    filter.path = rootPath.substring(0, rootPath.length() - 1) + filter.path;
                } else {
                    filter.path = rootPath + filter.path;
                }
            }
            actionList.put(filter.path, methodlist);
        }

        HashMap<String, ActionRequest> actions;

        if (methodlist.containsKey(filter.method)) {
            actions = methodlist.get(filter.method);
        } else {
            actions = new HashMap<>();
            methodlist.put(filter.method, actions);
        }
        if (filter.postAction == null) {
            actions.put("", actionRequest);
        } else {
            actions.put(filter.postAction, actionRequest);
        }


        actionPath.put(filePath, filter.toString());
        stringBuilder.append("Path:" + filter.path + "  HttpMethod:" + filter.method + "  Action:" + filter.postAction + " \t Class:" + actionRequest.getClass().getName() + "\r\n");
        System.out.println(actionList.toString());
        return this;
    }

    /**
     * 取消注册某个Action
     *
     * @param key
     * @return
     */
    public Dispatcher unRegisterAction(String key) {
        for (String s : actionList.keySet()) {
            ServerManager.getLogger().info("Dispatcher.unRegisterAction " + key);
        }
        return this;
    }

    /**
     * 清除所有Action
     *
     * @return
     */
    public Dispatcher clearAction() {
        actionList.clear();
        return this;
    }

    /**
     * 清除指定请求类型中的Action
     *
     * @return
     */
    public Dispatcher clearActionByMethod(String method) {
//        ConcurrentHashMap concurrentHashMap = actionList.get(method);
//        if (concurrentHashMap != null) {
//            concurrentHashMap.clear();
//        }
        return this;
    }

    @Override
    public void onChannelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void onChannelInactive(ChannelHandlerContext ctx) {

    }

    @Override
    public void onReceiveHandler(HttpServerHandler handler) {
        if (handler != null) {
            while (isHotLoading) {
                try {
                    handler.wait(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            onDispatch(handler);
            if (scheduledFuture != null) tpsCount++;
        }
    }

    @Override
    public void onUserEventTriggered(Object event) {

    }

    private void AnalysisTps() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduledFuture = ServerManager.getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long time;
                if (tpsTime == 0) {
                    tpsTime = System.nanoTime();
                } else {
                    time = System.nanoTime() - tpsTime;
                    tps = ((tpsCount * 1000000000f / time));
                    tpsTime = System.nanoTime();
                    tpsCount = 0;
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 热加载状态下所有新请求会暂停处理，已在处理中的请求不会干预，直到允许处理，默认入口预置循环
     *
     * @param isHotLoading
     */
    private void setLoadActionStatus(boolean isHotLoading) {
        this.isHotLoading = isHotLoading;
    }

    @Override
    public void onError(Throwable throwable) {
        ServerManager.getLogger().severe(throwable.getLocalizedMessage());
        if (dispatcherListener != null) dispatcherListener.onDispatcherError(throwable);
    }

    @Override
    public void onServerStarted(int serverPort) {
        if (dispatcherListener != null) dispatcherListener.onServerStarted(serverPort);
    }

    @Override
    public void onServerStop() {
        if (dispatcherListener != null) dispatcherListener.onServerStop();
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

    private ActionRequest findAction(String path, String method, String action) {
        ActionRequest actionRequest = null;
        HashMap<String, HashMap<String, ActionRequest>> pathMap = actionList.get(path);
        if (pathMap != null) {
            HashMap<String, ActionRequest> methodMap = pathMap.get(method);
            if (methodMap != null) {
                actionRequest = methodMap.get(action == null ? "" : action);
            }
        }
        return actionRequest;
    }

    public void onDispatch(HttpServerHandler handler) {
        if (handler.getHttpRequest() != null && handler.getHttpRequest().method() != null) {
            if (dispatcherListener != null && dispatcherListener.interrupt(handler)) {
                try {
                    handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求被拦截"), true);
                } catch (UnsupportedEncodingException e) {
                    ServerManager.getLogger().warning(e.getLocalizedMessage());
                }
                return;
            }
            String path = "/";
            if (!"/".equals(handler.getPath())) {
                path = handler.getPath();
            }
            String action = null;
            BaseRequest baseRequest = null;
            if (handler.getHttpRequest().method().equals(HttpMethod.POST)) {
                try {
                    String body = handler.getBody(null);
                    baseRequest = ServerManager.getOnJsonConvertListener().parseJson(body);
                    action = baseRequest.action;
                } catch (Exception e) {
                    try {
                        handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求无法处理，数据解析错误"), true);
                    } catch (UnsupportedEncodingException f) {
                        ServerManager.getLogger().warning(f.getLocalizedMessage());
                        if (dispatcherListener != null) {
                            dispatcherListener.onDispatcherError(f);
                        }
                    }
                    return;
                }
            }
            ActionRequest actionRequest = findAction(path, handler.getHttpRequest().method().name(), action);
            try {
                if (actionRequest == null) {
                    try {
                        handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求无法处理，未找到对应处理器"), true);
                    } catch (UnsupportedEncodingException f) {
                        ServerManager.getLogger().warning(f.getLocalizedMessage());
                        if (dispatcherListener != null) {
                            dispatcherListener.onDispatcherError(f);
                        }
                    }
                    return;
                }
                actionRequest.doAction(handler, baseRequest);
            } catch (Exception e) {
                try {
                    handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求无法处理，数据解析错误"), true);
                } catch (UnsupportedEncodingException f) {
                    ServerManager.getLogger().warning(f.getLocalizedMessage());
                    if (dispatcherListener != null) {
                        dispatcherListener.onDispatcherError(f);
                    }
                }
                return;
            }
        } else {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求无法处理，数据错误"), true);
            } catch (UnsupportedEncodingException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
                if (dispatcherListener != null) {
                    dispatcherListener.onDispatcherError(e);
                }
            }
            return;
        }
    }


    public interface onDispatcherListener {

        void onDispatcherError(Throwable throwable);

        /**
         * 请求中断拦截，true则拦截不再传递下去，由拦截过程处理发送结果，后续不再做任何处理
         *
         * @param handler
         * @return
         */
        public boolean interrupt(HttpServerHandler handler);

        /**
         * 是否被处理，默认处理返回false
         *
         * @param handler
         * @param action
         * @return
         */
        boolean onNotFoundAction(HttpServerHandler handler, String action) throws Exception;

        public void onServerStarted(int serverPort);

        public void onServerStop();

    }
}
