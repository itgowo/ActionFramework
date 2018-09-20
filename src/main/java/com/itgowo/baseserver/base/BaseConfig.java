package com.itgowo.baseserver.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
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
    public static final String CONFIG_SERVER_ANALYSIS_TPS = "ServerAnalysisTps";

    public static List<String> ServerActionPackage = null;

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
        return getProperty(CONFIG_SERVER_PORT, 1661);
    }

    public static String getServerMySQLUrl() {
        return getProperty(CONFIG_SERVER_MYSQL_URL, "jdbc:mysql://localhost:3306/dbname?characterEncoding=utf-8&amp;useSSL=false");
    }

    public static String getServerMySQLUser() {
        return getProperty(CONFIG_SERVER_MYSQL_USER, "数据库用户名");
    }

    public static String getServerMySQLPassword() {
        return getProperty(CONFIG_SERVER_MYSQL_PASSWORD, "数据库密码");
    }

    public static String getServerRedisUrl() {
        return getProperty(CONFIG_SERVER_REDIS_URL, "localhost");
    }

    public static String getServerRedisAuth() {
        return getProperty(CONFIG_SERVER_REDIS_AUTH, "redis密码");
    }

    public static int getThreadCoreNum() {
        return getProperty(CONFIG_SERVER_THREAD_CORE_NUM, 10);
    }

    public static int getThreadMaxNum() {
        return getProperty(CONFIG_SERVER_THREAD_MAX_NUM, 200);
    }

    public static int getThreadCacheTime() {
        return getProperty(CONFIG_SERVER_THREAD_CACHE_TIME, 180);
    }

    public static int getNettyBossGroupThreadNum() {
        return getProperty(CONFIG_SERVER_NETTY_BOSSGROUP_THREAD_NUM, 3);
    }

    public static int getNettyWorkerGroupThreadNum() {
        return getProperty(CONFIG_SERVER_NETTY_WORKER_THREAD_NUM, 6);
    }

    public static boolean getServerIsValidSign() {
        return getProperty(CONFIG_SERVER_IS_VALID_SIGN, true);
    }

    public static boolean getServerIsValidTimeDifference() {
        return getProperty(CONFIG_SERVER_IS_VALID_TIME_DIFFERENCE, true);
    }

    public static boolean getServerIsValidParameter() {
        return getProperty(CONFIG_SERVER_IS_VALID_PARAMETER, true);
    }

    public static long getServerActionTimeDifference() {
        return getProperty(CONFIG_SERVER_ACTION_TIME_DIFFERENCE, 60000l);
    }

    public static String getServerDynamicActionDir() {
        return getProperty(CONFIG_SERVER_DYNAMIC_ACTION_DIR, null);
    }

    /**
     * 不设置是在所有路径查找，设置了则只在指定路径搜索
     *
     * @return
     */
    public static List<String> getServerActionPackageList() {
        String s = getProperty(CONFIG_SERVER_ACTION_PACKAGE, null);
        String[] split = s.split(";");
        ServerActionPackage = Arrays.asList(split);
        return ServerActionPackage;
    }

    /**
     * 如果想用界面化方式启动服务，需要设置该值,不设置为只支持传统启动服务
     *
     * @return
     */
    public static String getServerMainClass() {
        return getProperty(CONFIG_SERVER_MAIN_CLASS, null);
    }

    /**
     * 设置是否热更新动态接口
     *
     * @return
     */
    public static boolean getServerAutoWatchAction() {
        return getProperty(CONFIG_SERVER_AUTO_WATCH_ACTION, false);
    }

    /**
     * 设置是否可以打开服务控制界面，接口可以添加打开，或者jar文件打开
     *
     * @return
     */
    public static boolean isShowServerWindow() {
        return getProperty(CONFIG_SERVER_SHOW_SERVER_WINDOW, false);
    }

    /**
     * 是否打开TPS分析开关，默认关
     * @return
     */
    public static boolean isAnalysisTps() {
        return getProperty(CONFIG_SERVER_ANALYSIS_TPS, false);
    }
}
