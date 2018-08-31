package com.itgowo.baseServer.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 基础配置管理类，项目可以继承此类添加配置
 */
public class BaseConfig {
    private static Properties configProperties;
    private static long lastModifyTime;
    public static final String CONFIG_SERVER_PORT = "ServerPort";
    public static final String CONFIG_SERVER_IS_VALID_SIGN = "ServerIsValidSign";
    public static final String CONFIG_SERVER_IS_VALID_TIME_DIFFERENCE = "ServerIsValidTimeDifference";
    public static final String CONFIG_SERVER_ACTION_TIME_DIFFERENCE = "ServerActionTimeDifference";
    public static final String CONFIG_SERVER_IS_VALID_PARAMETER = "ServerIsValidParameter";
    public static final String CONFIG_SERVER_REDIS_URL = "ServerRedisUrl";
    public static final String CONFIG_SERVER_REDIS_AUTH = "ServerRedisAuth";
    public static final String CONFIG_SERVER_MYSQL_URL = "ServerMySQLUrl";
    public static final String CONFIG_SERVER_MYSQL_USER = "ServerMySQLUser";
    public static final String CONFIG_SERVER_MYSQL_PASSWORD = "ServerMySQLPassword";
    public static final String CONFIG_SERVER_THREAD_CORE_NUM = "ServerThreadCoreNum";
    public static final String CONFIG_SERVER_THREAD_MAX_NUM = "ServerThreadMaxNum";
    public static final String CONFIG_SERVER_THREAD_CACHE_TIME = "ServerThreadCacheTime";
    public static final String CONFIG_SERVER_NETTY_BOSSGROUP_THREAD_NUM = "ServerNettyBossGroupThreadNum";
    public static final String CONFIG_SERVER_NETTY_WORKER_THREAD_NUM = "ServerNettyWorkerThreadNum";
    public static final String CONFIG_SERVER_DYNAMIC_ACTION_DIR = "ServerDynamicActionDir";
    public static final String CONFIG_SERVER_ACTION_PACKAGE = "ServerActionPackage";
    public static final String CONFIG_SERVER_AUTO_WATCH_ACTION = "ServerAutoWatchAction";
    public static final String CONFIG_SERVER_MAIN_CLASS = "ServerMainClass";
    public static final String CONFIG_SERVER_SHOW_SERVER_WINDOW = "ServerShowServerWindow";
    public static Integer serverPort = null;
    public static Integer threadCoreNum = null;
    public static Integer threadMaxNum = null;
    public static Integer threadCacheTime = null;
    public static Integer nettyBossGroupThreadNum = null;
    public static Integer nettyWorkerGroupThreadNum = null;
    public static String serverMySQLUrl = null;
    public static String serverRedisUrl = null;
    public static Boolean ServerIsValidSign = null;
    public static Boolean ServerIsValidTimeDifference = null;
    public static Long ServerActionTimeDifference = null;
    public static Boolean ServerIsValidParameter = null;
    public static String ServerDynamicActionDir = null;
    public static String ServerActionPackage = null;
    public static String ServerMainClass = null;
    public static Boolean ServerAutoWatchAction = null;
    public static Boolean ServerShowServerWindow = null;

    public static String getProperty(String propertyName, String defaultValue) {
        try {
            if (defaultValue != null) {
                defaultValue = new String(defaultValue.getBytes(Charset.forName("utf-8")));
            } else {
                defaultValue = "";
            }
            File file = getDefaultFile();
            getProperty(file);
            String config = configProperties.getProperty(propertyName);
            if (config == null) {
                configProperties.setProperty(propertyName, defaultValue);
                configProperties.store(new FileOutputStream(file), "Server Config");
            } else {
                return config;
            }
            return defaultValue;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static File getDefaultFile() {
        File file = new File(getFilePath().getParentFile(), "config.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static Properties getProperty(File file) {
        try {
            if (configProperties == null) {
                configProperties = new Properties();
                configProperties.load(new FileInputStream(file));
                lastModifyTime = file.lastModified();
            } else {
                if (file.lastModified() != lastModifyTime) {
                    configProperties.load(new FileInputStream(file));
                    lastModifyTime = file.lastModified();
                }
            }
            return configProperties;
        } catch (Exception e) {
            e.printStackTrace();
            return configProperties;
        }
    }

    public static File getFilePath() {
        String f = BaseConfig.class.getProtectionDomain().getCodeSource().getLocation().getHost();
        return new File(f);
    }

    public static int getProperty(String propertyName, int defaultValue) {
        try {
            String result = getProperty(propertyName, String.valueOf(defaultValue));
            return Integer.valueOf(result);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static long getProperty(String propertyName, long defaultValue) {
        try {
            String result = getProperty(propertyName, String.valueOf(defaultValue));
            return Long.valueOf(result);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean getProperty(String propertyName, boolean defaultValue) {
        try {
            String result = getProperty(propertyName, String.valueOf(defaultValue));
            return Boolean.valueOf(result);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static int getServerPort() {
        if (serverPort == null) {
            serverPort = getProperty(CONFIG_SERVER_PORT, 1661);
        }
        return serverPort;
    }

    public static String getServerMySQLUrl() {
        if (serverMySQLUrl == null) {
            serverMySQLUrl = getProperty(CONFIG_SERVER_MYSQL_URL, "jdbc:mysql://localhost:3306/dbname?characterEncoding=utf-8&amp;useSSL=false");
        }
        return serverMySQLUrl;
    }

    public static String getServerMySQLUser() {
        return getProperty(CONFIG_SERVER_MYSQL_USER, "数据库用户名");
    }

    public static String getServerMySQLPassword() {
        return getProperty(CONFIG_SERVER_MYSQL_PASSWORD, "数据库密码");
    }

    public static String getServerRedisUrl() {
        if (serverRedisUrl == null) {
            serverRedisUrl = getProperty(CONFIG_SERVER_REDIS_URL, "localhost");
        }
        return serverRedisUrl;
    }

    public static String getServerRedisAuth() {
        return getProperty(CONFIG_SERVER_REDIS_AUTH, "redis密码");
    }

    public static Integer getThreadCoreNum() {
        if (threadCoreNum == null) {
            threadCoreNum = getProperty(CONFIG_SERVER_THREAD_CORE_NUM, 10);
        }
        return threadCoreNum;
    }

    public static Integer getThreadMaxNum() {
        if (threadMaxNum == null) {
            threadMaxNum = getProperty(CONFIG_SERVER_THREAD_MAX_NUM, 200);
        }
        return threadMaxNum;
    }

    public static Integer getThreadCacheTime() {
        if (threadCacheTime == null) {
            threadCacheTime = getProperty(CONFIG_SERVER_THREAD_CACHE_TIME, 180);
        }
        return threadCacheTime;
    }

    public static Integer getNettyBossGroupThreadNum() {
        if (nettyBossGroupThreadNum == null) {
            nettyBossGroupThreadNum = getProperty(CONFIG_SERVER_NETTY_BOSSGROUP_THREAD_NUM, 3);
        }
        return nettyBossGroupThreadNum;
    }

    public static Integer getNettyWorkerGroupThreadNum() {
        if (nettyWorkerGroupThreadNum == null) {
            nettyWorkerGroupThreadNum = getProperty(CONFIG_SERVER_NETTY_WORKER_THREAD_NUM, 6);
        }
        return nettyWorkerGroupThreadNum;
    }

    public static boolean getServerIsValidSign() {
        if (ServerIsValidSign == null) {
            ServerIsValidSign = getProperty(CONFIG_SERVER_IS_VALID_SIGN, true);
        }
        return ServerIsValidSign;
    }

    public static boolean getServerIsValidTimeDifference() {
        if (ServerIsValidTimeDifference == null) {
            ServerIsValidTimeDifference = getProperty(CONFIG_SERVER_IS_VALID_TIME_DIFFERENCE, true);
        }
        return ServerIsValidTimeDifference;
    }

    public static boolean getServerIsValidParameter() {
        if (ServerIsValidParameter == null) {
            ServerIsValidParameter = getProperty(CONFIG_SERVER_IS_VALID_PARAMETER, true);
        }
        return ServerIsValidParameter;
    }

    public static long getServerActionTimeDifference() {
        if (ServerActionTimeDifference == null) {
            ServerActionTimeDifference = getProperty(CONFIG_SERVER_ACTION_TIME_DIFFERENCE, 60000l);
        }
        return ServerActionTimeDifference;
    }

    public static String getServerDynamicActionDir() {
        if (ServerDynamicActionDir == null) {
            ServerDynamicActionDir = getProperty(CONFIG_SERVER_DYNAMIC_ACTION_DIR, null);
        }
        return ServerDynamicActionDir;
    }

    /**
     * 不设置是在所有路径查找，设置了则只在指定路径搜索
     *
     * @return
     */
    public static String getServerActionPackage() {
        if (ServerActionPackage == null) {
            ServerActionPackage = getProperty(CONFIG_SERVER_ACTION_PACKAGE, null);
        }
        return ServerActionPackage;
    }

    /**
     * 如果想用界面化方式启动服务，需要设置该值,不设置为只支持传统启动服务
     *
     * @return
     */
    public static String getServerMainClass() {
        if (ServerMainClass == null) {
            ServerMainClass = getProperty(CONFIG_SERVER_MAIN_CLASS, null);
        }
        return ServerMainClass;
    }

    /**
     * 设置是否热更新动态接口
     *
     * @return
     */
    public static boolean getServerAutoWatchAction() {
        if (ServerAutoWatchAction == null) {
            ServerAutoWatchAction = getProperty(CONFIG_SERVER_AUTO_WATCH_ACTION, false);
        }
        return ServerAutoWatchAction;
    }

    /**
     * 设置是否自动打开服务控制界面，只在非窗口启动下有效
     *
     * @return
     */
    public static boolean getServerShowServerWindow() {
        return getProperty(CONFIG_SERVER_SHOW_SERVER_WINDOW, false);
    }
}
