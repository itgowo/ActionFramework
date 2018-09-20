/*
 * Copyright (c) 2017. lujianchao
 * @晴空的一滴雨
 * WebSite:http://lujianchao.com.
 * GitHub:https://github.com/hnsugar
 * CSDN:http://blog.csdn.net/hnsugar
 *
 */

package com.itgowo.servercore.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hnvfh on 2017/3/28.
 */
public class Utils {
    /**
     * 从指定数组的copy一个子数组并返回
     *
     * @param org of type byte[] 原数组
     * @param to  合并一个byte[]
     * @return 合并的数据
     */

    public static byte[] append(byte[] org, byte[] to) {
        byte[] newByte = new byte[org.length + to.length];
        System.arraycopy(org, 0, newByte, 0, org.length);
        System.arraycopy(to, 0, newByte, org.length, to.length);
        return newByte;

    }

    /**
     * 获取第二天凌晨date  2017-03-30 11:22:54:100     2017-03-31 00:00:00:000
     *
     * @return
     */
    public static Date time_getNextDayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String time_getTime(Date mDate, String mFormat) {
        if (mFormat == null || mFormat.equals("")) {
            mFormat = "yyyy-MM-dd HH:mm:ss:SSS";
        }
        if (mDate == null) {
            mDate = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(mFormat);
        String date = sdf.format(mDate);
        return date;
    }

    public static String getFileSizeString(long mL) {
        if (mL < 1024) {
            return mL + "Byte";
        }
        BigDecimal temp = new BigDecimal(mL);
        temp = temp.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
        if (temp.longValue() < 1024) {
            return temp + "KB";
        }
        temp = temp.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
        if (temp.longValue() < 1024) {
            return temp + "MB";
        }
        temp = temp.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
        if (temp.longValue() < 1024) {
            return temp + "GB";
        }
        temp = temp.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
        if (temp.longValue() < 1024) {
            return temp + "TB";
        }
        return "null";
    }

    public static String getInfo() {
        Runtime runtime = Runtime.getRuntime();
        StringBuilder builder = new StringBuilder("UsedMemory:");
        builder.append(Utils.getFileSizeString(runtime.totalMemory() - runtime.freeMemory()));
        builder.append("   TotalMemory:");
        builder.append(Utils.getFileSizeString(runtime.totalMemory()));
        builder.append("   MaxMemory:");
        builder.append(Utils.getFileSizeString(runtime.maxMemory()));
        builder.append("   FreeMemory:");
        builder.append(Utils.getFileSizeString(runtime.freeMemory()) + "\r\n");
        return builder.toString();
    }

    /**
     * 获取线程信息
     *
     * @return
     */
    public synchronized static String printThreadList() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while (group.getParent() != null) {
            group = group.getParent();
        }
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        StringBuilder buf = new StringBuilder();
        for (Thread thread : threads) {
            if (thread == null) {
                continue;
            }
            try {
                ThreadGroup tgroup = thread.getThreadGroup();
                String groupName = tgroup == null ? "null" : tgroup.getName();
                buf.append("ThreadGroup:").append(groupName).append(", ");
                buf.append("Id:").append(thread.getId()).append(", ");
                buf.append("Name:").append(thread.getName()).append(", ");
                buf.append("isDaemon:").append(thread.isDaemon()).append(", ");
                buf.append("isAlive:").append(thread.isAlive()).append(", ");
                buf.append("Priority:").append(thread.getPriority());
                buf.append("\r\n");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return buf.toString();
    }
}
