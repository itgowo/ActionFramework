package com.itgowo.baseServer.view1;

import com.itgowo.baseServer.ServerManager;
import com.itgowo.baseServer.base.Dispatcher;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerConfig {
    public JPanel rootPanel;
    public JButton server_start_stop;
    public JPanel server_info_rootLayout;
    public JScrollPane server_thread_layout;
    public JButton server_gc_button;
    public JButton server_config_btn;
    public JButton server_show_log;
    public JCheckBox server_monitor_swich;
    public JTable server_thread_table;

    private JFrame frame;
    private String[] columnNames = new String[]{"id", "名称", "状态", "WaitedTime/Count", "BlockedTime/Count", "锁名/锁拥有者/BlockedClass"};
    private ScheduledFuture scheduledFuture;


    private String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    public ServerConfig(JFrame frame) {
        this.frame = frame;
        initSystemInfo();
        server_start_stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (server_start_stop.getText().equals("启动")) {
                    ServerManager.initServer();
                    server_start_stop.setText("停止");
                } else {
                    ServerManager.stop();
                    server_start_stop.setText("启动");
                }
            }
        });
        server_gc_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManagementFactory.getMemoryMXBean().gc();
            }
        });
        server_config_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigWindow.showConfigWindow();
            }
        });
        server_show_log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogWindow.showLogWindow();
            }
        });
        server_monitor_swich.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (server_monitor_swich.isSelected()) {
                    startMonitor();
                } else {
                    if (scheduledFuture != null) {
                        scheduledFuture.cancel(true);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        showServerWindow();
    }

    private void startMonitor() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduledFuture = ServerManager.getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshSystemInfo();
                        refreshThreadTable();
                    }
                });
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    public static void showServerWindow() {
        JFrame frame = new JFrame("服务管理  By 卢建超");
        frame.setContentPane(new ServerConfig(frame).rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void refreshSystemInfo() {
        List<SystemInfoItemBean> list = new ArrayList<>();
        list.add(new SystemInfoItemBean("PID:", pid));
        list.add(new SystemInfoItemBean("线程数:", String.valueOf(ManagementFactory.getThreadMXBean().getThreadCount())));
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        list.add(new SystemInfoItemBean("使用内存:", String.valueOf(usedMemory / 1024) + " KB"));
        list.add(new SystemInfoItemBean("可用内存:", String.valueOf(Runtime.getRuntime().freeMemory() / 1024 + " KB")));
        list.add(new SystemInfoItemBean("总可用内存:", String.valueOf(Runtime.getRuntime().totalMemory() / 1024 + " KB")));
        list.add(new SystemInfoItemBean("最大可用内存:", String.valueOf(Runtime.getRuntime().maxMemory() / 1024 + " KB")));
        list.add(new SystemInfoItemBean("类加载数:", String.valueOf(ManagementFactory.getClassLoadingMXBean().getLoadedClassCount())));
        list.add(new SystemInfoItemBean("TPS处理量:", String.valueOf(Dispatcher.tps)));
        ViewCache viewCache = new ViewCache(server_info_rootLayout, new ViewCache.onViewListener() {
            @Override
            public JComponent onAddView() {
                return new JLabel();
            }

            @Override
            public void onBindView(int position, Component view) {
                JLabel label = (JLabel) view;
                if (position % 2 == 0) {
                    label.setText(list.get(position / 2).getKey());
                } else {
                    label.setText(list.get(position / 2).getValue());
                }
            }
        });
        viewCache.onRefresh(list.size() * 2);
    }

    private void initSystemInfo() {
        server_info_rootLayout.setLayout(new GridLayout(0, 2));
        server_start_stop.setEnabled(ServerManager.searchMainClass());

    }

    public void refreshThreadTable() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] ids = threadMXBean.getAllThreadIds();

        Object[][] rowData = new String[ids.length][columnNames.length];
        for (int i = 0; i < ids.length; i++) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(ids[i]);
            rowData[i][0] = String.valueOf(threadInfo.getThreadId());
            rowData[i][1] = threadInfo.getThreadName();
            rowData[i][2] = threadInfo.getThreadState().name();
            rowData[i][3] = threadInfo.getWaitedTime() + "/" + threadInfo.getWaitedCount();
            rowData[i][4] = threadInfo.getBlockedTime() + "/" + threadInfo.getBlockedCount();
            String msg = threadInfo.getLockName() + "/" + threadInfo.getLockOwnerName() + "(" + threadInfo.getLockOwnerId() + ")/";
            if (threadInfo.getLockInfo() == null) {
                rowData[i][5] = msg + "null";
            } else {
                rowData[i][5] = msg + threadInfo.getLockInfo().getClassName();
            }
        }
        if (server_thread_table == null) {
            server_thread_table = new JTable();
            server_thread_table.setModel(new DefaultTableModel(rowData, columnNames));
            server_thread_layout.setViewportView(server_thread_table);
            server_thread_table.sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            DefaultTableModel defaultTableModel = (DefaultTableModel) server_thread_table.getModel();
            defaultTableModel.setDataVector(rowData, columnNames);
            server_thread_table.sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        rootPanel.add(panel1, BorderLayout.NORTH);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel1.add(panel2, BorderLayout.CENTER);
        panel2.setBorder(BorderFactory.createTitledBorder("服务控制"));
        server_start_stop = new JButton();
        server_start_stop.setText("启动");
        panel2.add(server_start_stop);
        server_gc_button = new JButton();
        server_gc_button.setText("垃圾回收");
        panel2.add(server_gc_button);
        server_config_btn = new JButton();
        server_config_btn.setText("服务配置");
        panel2.add(server_config_btn);
        server_show_log = new JButton();
        server_show_log.setEnabled(false);
        server_show_log.setText("Log");
        panel2.add(server_show_log);
        server_monitor_swich = new JCheckBox();
        server_monitor_swich.setSelected(false);
        server_monitor_swich.setText("监控");
        panel2.add(server_monitor_swich);
        server_thread_layout = new JScrollPane();
        rootPanel.add(server_thread_layout, BorderLayout.CENTER);
        server_thread_layout.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "线程信息", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        server_info_rootLayout = new JPanel();
        server_info_rootLayout.setLayout(new GridBagLayout());
        rootPanel.add(server_info_rootLayout, BorderLayout.WEST);
        server_info_rootLayout.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "服务器状态", TitledBorder.LEFT, TitledBorder.TOP));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
