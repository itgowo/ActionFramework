package com.itgowo.packageframework;

import com.itgowo.servercore.onServerListener;
import com.itgowo.servercore.packagesocket.PackageServerHandler;

public interface onPackageFrameworkListener extends onServerListener {
    void onHeartMessage(PackageServerHandler handler);

    void onReceivedMessage(PackageServerHandler handler);

}
