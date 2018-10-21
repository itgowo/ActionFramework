# BaseServer

## 概述
QQ：1264957104

Web：http://itgowo.com

本人较懒，以后再写各种逻辑和流程图。

这是一个来自android开发写java的经验框架，支持热更新等，主要是自用库，现在发出来，很多第三方框架未集成，需要单独实现。默认接口都是POST形式，自动解析Body，也可以复写各种校验参数来达到过滤恶意攻击。

此框架依赖netty部分库，具体使用时按需打包,库内有demo文件，支持接口触发或者直接运行Jar打开配置界面。

    implementation 'com.itgowo:BaseServer:0.0.33'

## 配置文件（config.properties）
默认会创建一个配置文件在运行目录，包含基础配置信息和拓展信息，只有部分是必须的。
新增swing界面配置管理工具，注意：都按String处理，内容类型需要注意下，要求是数值型的必须输入数字。

    调用configWindow.showConfigWindow();

![图形化配置界面](https://github.com/itgowo/BaseServer/blob/master/image/config.jpg)
    
    #Server Config
    #Thu Aug 16 20:51:30 CST 2018
    ServerPort=1666 //必须自己控制
    ServerActionTimeDifference=60000 //接口校验时间差单位毫秒
    ServerRedisAuth=test123 //redis暂时未集成
    ServerRedisUrl=localhost //redis地址
    ServerMySQLUser=test //数据库未集成
    ServerActionPackage=com.itgowo.actionframework.action;com.itgowo.actionframework.demo;com.game //设置Action接口包名，过滤，支持数组
    ServerNettyWorkerThreadNum=6 //netty WorkerThread线程数
    ServerDynamicActionDir=/Users/lujianchao/111/action //设置动态加载替换接口目录
    ServerIsValidParameter=true //是否校验参数
    ServerNettyBossGroupThreadNum=3 //netty bossGroup线程数
    ServerMySQLPassword=test123 //数据库未集成
    ServerIsValidTimeDifference=true //是否校验时间差
    ServerAutoWatchAction=false //是否自动监测指定目录接口更新，自动加载覆盖或添加删除接口
    ServerMySQLUrl=jdbc\:mysql\://localhost\:3306/test?characterEncoding\=utf-8&amp&useSSL\=false ////数据库未集成
    ServerIsValidSign=false //是否校验签名
    ServerAnalysisTps=false //是否开启tps统计，Dispatcher.tps获取静态变量，float类型，每隔5秒计算一次
    ServerMainClass=com.itgowo.actionframework.demo.DemoMainClassForWindow    //设置服务器配置启动类，使用界面控制必须实现，代码控制不要求，最好使用ServerManager.getHttpServerManager获取单例，这样打开页面也可以对服务控制
    ServerShowServerWindow=true

## 服务控制界面

监控为进程监控，tps也是静态变量，不受一个项目启动服务数限制，启动多个server后，TPS为总处理量，tps计算节点在向用户发送数据时，所以比较有意义。


    调用ServerConfig.showServerWindow();

![图形化控制界面](https://github.com/itgowo/BaseServer/blob/master/image/server.png)


## 配置request，ACTION必须全工程唯一，非post请求必须path路径全匹配，不能模糊匹配

    public class ActionShowServerWindow implements ActionRequest {
        public static final String ACTION = "ShowServerWindow";
    
        @Override
        public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
            if (BaseConfig.isShowServerWindow()) {
                ServerConfig.showServerWindow();
            }
            handler.sendData(new ServerJsonEntity(), true);
        }
    }
    

    public class DemoActionRequestGet implements ActionRequestGet {
        public static final String ACTION = "/aa/helloword";
    
        @Override
        public void doAction(HttpServerHandler handler, BaseRequest baseRequest) throws Exception {
            handler.sendData(ACTION,false);
        }
    }

## 配置DispatcherListener

    public class MyDispatcherListener implements Dispatcher.onDispatcherListener {
        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public boolean interrupt(HttpServerHandler httpServerHandler) {
            try {
                System.out.println("\r\n^^^^^^^^^^^^^^");
                System.out.println(httpServerHandler.getUri());
                System.out.println(httpServerHandler.getBody(Charset.forName("utf-8")));
                System.out.println("\r\n--------------");
            } catch (Exception e) {
            }
            if (!httpServerHandler.getPath().startsWith(GameServer.ROOTPATH)) {
                return true;
            }
            return false;
        }

         @Override
        public boolean onNotFoundAction(HttpServerHandler handler, String action) throws Exception {
            if (handler.getPath()!=null&&handler.getPath().startsWith("GameSTZB")){
                String html1 = readToString("config/html_index");
                handler.sendData(html1, false);
            }else {
                handler.sendData(HttpResponseStatus.NOT_FOUND,"not found Action",false);
            }
            return true;
        }
        
        @Override
        public BaseRequest parseJson(String s) throws Exception {
            return JSON.parseObject(s,ClientRequest.class);
        }

        @Override
        public String toJson(Object o) throws Exception {
            return JSON.toJSONString(o);
        }

    }

## 配置请求解析类

    public class ClientRequest extends BaseRequest {
        private String flag = "GameSTZB";
        private String token;
        private Integer pageIndex;
        private Integer pageSize;
        private Integer serverVersion;
        private Integer appVersion;
        private String data;
        private String sign;

        public Integer getServerVersion() {
            return serverVersion;
        }

        private String fixSign() {
            //此算法是自用服务的简化版，部分代码未贴出
            JSONObject paramsJsonObj = (JSONObject) JSON.toJSON(this);
            Map<String, String> signParamsMap = new TreeMap<>();
            for (Map.Entry<String, Object> param : paramsJsonObj.entrySet()) {
                // 排除参数名为sign。
                if (param != null && param.getValue() != null && !"sign".equals(param.getKey())) {
                    signParamsMap.put(param.getKey(), param.getValue().toString());
                }
            }
            Set<Map.Entry<String, String>> entrys = signParamsMap.entrySet();
            // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
            StringBuilder basestring = new StringBuilder();
            for (Map.Entry<String, String> param : entrys) {
                basestring.append(param.getKey()).append("=").append(param.getValue());
            }
            // 使用MD5对待签名串求签
            byte[] byteBuffer = null;
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                try {
                    byteBuffer = md5.digest(basestring.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } catch (GeneralSecurityException ex) {
    //          throw new IOException(ex);
            }
            // 将MD5输出的二进制结果转换为小写的十六进制
            StringBuilder sign1 = new StringBuilder();
            for (int i = 0; i < byteBuffer.length; i++) {
                String hex = Integer.toHexString(byteBuffer[i] & 0xFF);
                if (hex.length() == 1) {
                    sign1.append("0");
                }
                sign1.append(hex);
            }
            return sign1.toString();
        }

        @Override
        public boolean validParameter() {
            if (action == null || action.trim().length() < 1) {
                return false;
            }
            if (token == null || token.trim().length() < 16) {
                return false;
            }
            if (flag == null || flag.trim().length() < 1) {
                return false;
            }
            return true;
        }

        @Override
        public boolean validSign() {
            if (sign == null || sign.trim().length() < 16) {
                return false;
            }
            return sign.equals(fixSign());
        }

        @Override
        public boolean validTimeDifference(long l) {
            return System.currentTimeMillis() - timeStamp > l;
        }

        public String getSign() {
            return sign;
        }

        public ClientRequest setSign(String sign) {
            this.sign = sign;
            return this;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public ClientRequest setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public ClientRequest setServerVersion(Integer serverVersion) {
            this.serverVersion = serverVersion;
            return this;
        }

        public int getAppVersion() {
            return appVersion == null ? 0 : appVersion;
        }

        public ClientRequest setAppVersion(Integer appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public String getFlag() {
            return flag;
        }

        public ClientRequest setFlag(String flag) {
            this.flag = flag;
            return this;
        }

        public String getToken() {
            return token;
        }

        public ClientRequest setToken(String token) {
            this.token = token;
            return this;
        }

        public Integer getPageIndex() {
            return pageIndex;
        }

        public ClientRequest setPageIndex(Integer pageIndex) {
            this.pageIndex = pageIndex;
            return this;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public ClientRequest setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public String getData() {
            return data;
        }


        public ClientRequest setData(String data) {
            this.data = data;
            return this;
        }

        @Override
        public <T> T getData(Class<T> classObject) {
            return JSON.parseObject(data, classObject);
        }

        @Override
        public String initToken() {
            return null;
        }

        @Override
        public String toJsonTool() {
            return JSON.toJSONString(this);
        }
    }


## 配置启动参数，适合一个项目多个server

        public static void main(String[] args) {
        HttpServerManager mHttpServer = new HttpServerManager();
        Dispatcher dispatcher = new Dispatcher();
        int portint = BaseConfig.getServerPort();
        mHttpServer.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());//设置处理线程数，4，2基本满足小型服务需求

        dispatcher.setValidSign(BaseConfig.getServerIsValidSign());//是否开启签名校验，需要实现BaseRequest.validSign()方法
        dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());//是否开启时间差校验，，需要实现BaseRequest.validTimeDifference()方法
        dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());//设置时间差阈值
        dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());//是否开启参数校验，需要实现BaseRequest.validParameter()方法
        dispatcher.startWatchAction();//打开动态更新接口功能
        //dispatcher.stopWatchAction();//关闭动态更新接口功能
        dispatcher.actionScanner(Main.class);//必须传，根据class查找到工作jar或者目录
        dispatcher.setDispatcherListener(new MyDispatcherListener());//服务状态和公共处理接口，包含拦截和Json解析
        mHttpServer.setOnReceiveHandleListener(dispatcher);//向基础网络封装库注册事件分发器

        int finalPortint = portint;
        Thread mGameThread = new Thread(() -> {
            try {
                Thread.currentThread().setName("GameMainThread");
                mHttpServer.start(finalPortint);
            } catch (Exception mEm) {
                mEm.printStackTrace();
            }
        });
        mGameThread.start();
    }
    
    
    
## 推荐这种方式配置，单例server,图形化和代码控制台都是一个server

    public class Main implements HttpServerInitCallback{


        public static void main(String[] args) {
            ServerManager.searchMainClass();
            ServerManager.initServer();
        }
        
         @Override
        public void onServerConfigPrepare(HttpServerManager serverManager) {
            Dispatcher dispatcher = new Dispatcher();
            serverManager.setThreadConfig(BaseConfig.getNettyBossGroupThreadNum(), BaseConfig.getNettyWorkerGroupThreadNum());
            dispatcher.setValidSign(BaseConfig.getServerIsValidSign());
            dispatcher.setValidTimeDifference(BaseConfig.getServerIsValidTimeDifference());
            dispatcher.setServerClientTimeDifference(BaseConfig.getServerActionTimeDifference());
            dispatcher.setValidParameter(BaseConfig.getServerIsValidParameter());
            if (BaseConfig.isAnalysisTps())dispatcher.startAnalysisTps();
    //        dispatcher.startWatchAction();
            dispatcher.actionScanner(DemoMainClassForWindow.class);
            dispatcher.setDispatcherListener(new DemoDispatcher());
            serverManager.setOnReceiveHandleListener(dispatcher);
        }
    
        @Override
        public void onError(Exception e) {
            e.printStackTrace();
            ServerManager.onError(e);
        }
    }

## 附上一个项目的阿里云压测（双核4gb，高效云盘，压测时内存不高于500MB）
mysql、redis、gogs和其他服务都部署在同一服务器上。
三个接口只有读数据库操作处理了业务逻辑，只做参考，redis不给力，包含数据库写入和缓存的测试去掉了
测试结果是cpu满载，内存用的不多，之前有数据库写入测试TPS达到500了，没写入操作最高670.
![压测](https://github.com/itgowo/BaseServer/blob/master/image/test.png)