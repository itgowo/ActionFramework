package com.itgowo.actionframework.base;

import com.itgowo.actionframework.ServerManager;
import com.itgowo.actionframework.classutils.ClassEntry;
import com.itgowo.actionframework.classutils.ClassUtils;
import com.itgowo.actionframework.utils.Utils;
import com.itgowo.actionframework.utils.WatchFileService;
import com.itgowo.servercore.http.HttpServerHandler;
import com.itgowo.servercore.onServerListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lujianchao
 * 请求事件处理类
 */
public class Dispatcher implements onServerListener<HttpServerHandler>, WatchFileService.onWatchFileListener {
    public static float tps;
    public static AtomicLong tpsTime = new AtomicLong();
    public static AtomicLong tpsCount = new AtomicLong();
    public static ScheduledFuture scheduledFuture;
    private HashMap<String, ConcurrentHashMap<String, ActionRequest>> actionList = new HashMap();

    private HashMap<String, String> actionPath = new HashMap<>();
    private onDispatcherListener dispatcherListener;
    private WatchFileService watchFileService;
    private boolean isValidSign = true;
    private volatile AtomicBoolean isHotLoading = new AtomicBoolean(false);
    private StringBuilder stringBuilder;
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

    public void startAnalysisTps() {
        AnalysisTps();
    }

    public void stopAnalysisTps() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

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

    public Dispatcher setJsonConvertListener(onJsonConvertListener jsonConvertListener) {
        ServerManager.setOnJsonConvertListener(jsonConvertListener);
        return this;
    }

    public Dispatcher setValidParameter(boolean validParameter) {
        isValidParameter = validParameter;
        return this;
    }

    /**
     * 停止外部目录变更监听，默认关闭动态更新
     *
     * @return
     */
    public Dispatcher stopWatchAction() {
        if (watchFileService != null) {
            watchFileService.stopWatch();
            watchFileService = null;
        }
        return this;
    }

    /**
     * 开始外部目录变更监听，默认关闭动态更新
     *
     * @return
     */
    public Dispatcher startWatchAction() {
        if (watchFileService == null) {
            watchFileService = new WatchFileService(BaseConfig.getServerDynamicActionDir());
            watchFileService.startWatch(this);
        }
        return this;
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
            if (BaseConfig.getServerAutoWatchAction()) {
                watchFileService = new WatchFileService(dynamicDir.getAbsolutePath());
                watchFileService.startWatch(this);
            }
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
                Object action = Utils.getFinalFieldValueByName("ACTION", c);
                Object method = Utils.getFinalFieldValueByName("METHOD", c);
                if (action != null && method != null && action instanceof String && method instanceof String) {
                    String[] actions = ((String) action).split(";");
                    String[] methods = ((String) method).split(";");
                    if (methods != null && actions != null) {
                        for (int i = 0; i < methods.length; i++) {
                            for (int j = 0; j < actions.length; j++) {
                                registerAction(methods[i], actions[j], (ActionRequest) o, classEntry.getFilePath());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ServerManager.getLogger().warning(e.getLocalizedMessage());
        }
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
    public Dispatcher registerAction(String method, String action, ActionRequest actionRequest, String filePath) {
        ConcurrentHashMap<String, ActionRequest> actionHashMap = actionList.get(method);
        if (actionHashMap == null) {
            actionHashMap = new ConcurrentHashMap<>();
            actionList.put(method, actionHashMap);
        }

        actionHashMap.put(action, actionRequest);
        actionPath.put(filePath, action);
        stringBuilder.append("HttpMethod:" + method + "  Action:" + action + " \t Class:" + actionRequest.getClass().getName() + "\r\n");
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
            ConcurrentHashMap concurrentHashMap = actionList.get(s);
            if (concurrentHashMap != null) {
                concurrentHashMap.remove(key);
                ServerManager.getLogger().info("Dispatcher.unRegisterAction " + key);
            }
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
        ConcurrentHashMap concurrentHashMap = actionList.get(method);
        if (concurrentHashMap != null) {
            concurrentHashMap.clear();
        }
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
            onDispatch(handler);
            if (scheduledFuture != null) tpsCount.addAndGet(1);
        }
    }

    private void AnalysisTps() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduledFuture = ServerManager.getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long time;
                if (tpsTime.get() == 0) {
                    tpsTime.set(System.nanoTime());
                } else {
                    time = System.nanoTime() - tpsTime.get();
                    tps = ((tpsCount.get() * 1000000000f / time));
                    tpsTime.set(System.nanoTime());
                    tpsCount.set(0);
                }
            }
        }, 2, 5, TimeUnit.SECONDS);
    }

    /**
     * 热加载状态下所有新请求会暂停处理，已在处理中的请求不会干预，直到允许处理，默认入口预置循环
     *
     * @param isHotLoading
     */
    private void setLoadActionStatus(boolean isHotLoading) {
        this.isHotLoading.set(isHotLoading);
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
            HttpMethod method = handler.getHttpRequest().method();
            if (method == HttpMethod.POST) {
                doPost(handler);
            } else {
                doOther(handler);
            }
        } else {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求无法处理，数据错误"), true);
            } catch (UnsupportedEncodingException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
            return;
        }
    }

    private void doOther(HttpServerHandler handler) {
        while (isHotLoading.get()) {
            try {
                this.wait(5);
            } catch (InterruptedException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
        }
        String path = handler.getPath();
        ConcurrentHashMap<String, ActionRequest> concurrentHashMap = actionList.get(handler.getHttpRequest().method().name());
        if (concurrentHashMap == null) {
            try {
                if (!dispatcherListener.onNotFoundAction(handler, path)) {
                    handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("未找到对应接口功能，请检查版本，如有问题请联系管理员"), true);
                }
                return;
            } catch (Exception e) {
                ServerManager.getLogger().severe(e.getLocalizedMessage());
            }
        }
        ActionRequest actionRequest = concurrentHashMap.get(path);
        try {
            if (actionRequest == null) {
                if (!dispatcherListener.onNotFoundAction(handler, path)) {
                    handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("未找到对应接口功能，请检查版本，如有问题请联系管理员"), true);
                }
                return;
            }
            actionRequest.doAction(handler, null);
        } catch (Exception e) {
            ServerManager.getLogger().severe(e.getLocalizedMessage());
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("业务处理异常").setError(e.getMessage()), true);
            } catch (UnsupportedEncodingException e1) {
                ServerManager.getLogger().warning(e1.getLocalizedMessage());
            }
        }
    }

    /**
     * 主要是针对post方式处理业务的，所以单独提出来，做了很多处理
     *
     * @param handler
     */
    private void doPost(HttpServerHandler handler) {
        BaseRequest baseRequest = null;
        if (dispatcherListener != null) {
            try {
                baseRequest = ServerManager.getOnJsonConvertListener().parseJson(handler.getBody(Charset.forName("utf-8")));
            } catch (Exception e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
        }
        if (baseRequest == null) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("请求解析失败"), true);
            } catch (UnsupportedEncodingException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
            return;
        }
        if (isValidTimeDifference && !baseRequest.validTimeDifference(serverClientTimeDifference)) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("时间校验失败"), true);
            } catch (UnsupportedEncodingException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
            return;
        }
        if (isValidParameter && !baseRequest.validParameter()) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("参数校验失败"), true);
            } catch (UnsupportedEncodingException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
            return;
        }
        if (isValidSign && !baseRequest.validSign()) {
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("签名校验失败"), true);
            } catch (UnsupportedEncodingException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
            return;
        }
        while (isHotLoading.get()) {
            try {
                this.wait(5);
            } catch (InterruptedException e) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
        }
        ConcurrentHashMap<String, ActionRequest> concurrentHashMap = actionList.get(HttpMethod.POST.name());
        if (concurrentHashMap == null) {
            try {
                if (!dispatcherListener.onNotFoundAction(handler, baseRequest.getAction())) {
                    handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("未找到对应接口功能，请检查版本，如有问题请联系管理员"), true);
                }
                return;
            } catch (Exception e) {
                ServerManager.getLogger().severe(e.getLocalizedMessage());
            }
        }
        ActionRequest actionRequest = concurrentHashMap.get(baseRequest.getAction());
        try {
            if (actionRequest == null) {
                if (!dispatcherListener.onNotFoundAction(handler, baseRequest.getAction())) {
                    handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("未找到对应接口功能，请检查版本，如有问题请联系管理员"), true);
                }
                return;
            }
            actionRequest.doAction(handler, baseRequest);
        } catch (Exception e) {
            ServerManager.getLogger().severe(e.getLocalizedMessage());
            try {
                handler.sendData(new ServerJsonEntity().setCode(ServerJsonEntity.Fail).setMsg("业务处理异常").setError(e.getMessage()), true);
            } catch (UnsupportedEncodingException e1) {
                ServerManager.getLogger().warning(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onCreateFile(String dir, String fileName) {
        ServerManager.getLogger().info("HotLoad:------registerAction");
        File file = new File(dir + File.separator + fileName);
        List<String> stringList = BaseConfig.getServerActionPackageList();
        List<ClassEntry> classEntries = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            classEntries.addAll(loadClass(file, stringList));
        }
        setLoadActionStatus(true);
        for (int i = 0; i < classEntries.size(); i++) {
            checkAndRegisterAction(classEntries.get(i));
        }
        setLoadActionStatus(false);
        ServerManager.getLogger().info("HotLoad:------\r\n");
    }

    @Override
    public void onModifyFile(String dir, String fileName) {
        File file = new File(dir + File.separator + fileName);
        List<String> stringList = BaseConfig.getServerActionPackageList();
        List<ClassEntry> classEntries = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            classEntries.addAll(loadClass(file, stringList));
        }
        ServerManager.getLogger().info("HotLoad:------registerAction");
        setLoadActionStatus(true);
        for (int i = 0; i < classEntries.size(); i++) {
            checkAndRegisterAction(classEntries.get(i));
        }
        setLoadActionStatus(false);
        ServerManager.getLogger().info("HotLoad:------\r\n");
    }

    @Override
    public void onDeleteFile(String dir, String fileName) {
        String f = actionPath.get(dir + File.separator + fileName);
        if (f != null) {
            ServerManager.getLogger().info("HotLoad:------unRegisterAction");
            setLoadActionStatus(true);
            unRegisterAction(f);
            setLoadActionStatus(false);
            ServerManager.getLogger().info("HotLoad:------\r\n");
        }
    }

    public interface onDispatcherListener {

        void onDispatcherError(Throwable throwable);

        /**
         * 请求中断拦截，true则拦截不再传递下去
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
