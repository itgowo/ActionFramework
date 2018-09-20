package com.itgowo.baseserver.view;

import javax.swing.*;
import java.awt.*;

/**
 * 开发android的习惯，哈哈
 *
 * @param <RootLayout>
 */
public class ViewCache<RootLayout extends JComponent> {
    private RootLayout rootLayout;
    private onViewListener listener;

    public ViewCache(RootLayout rootLayout, onViewListener listener) {
        this.rootLayout = rootLayout;
        this.listener = listener;
    }

    public void onRefresh(int count) {
        if (count < rootLayout.getComponentCount()) {
            int size = rootLayout.getComponentCount() - count;
            for (int i = 0; i < size; i++) {
                rootLayout.remove(rootLayout.getComponentCount() - 1);
            }
        } else if (count > rootLayout.getComponentCount()) {
            int size = count - rootLayout.getComponentCount();
            for (int i = 0; i < size; i++) {
                rootLayout.add(listener.onAddView());
            }
        }
        for (int i = 0; i < rootLayout.getComponentCount(); i++) {
            listener.onBindView(i, rootLayout.getComponent(i));
        }
        rootLayout.revalidate();
    }

    public interface onViewListener {
        JComponent onAddView();

        void onBindView(int position, Component view);
    }
}
