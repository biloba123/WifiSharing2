package com.lvqingyang.wifisharing.Wifi.connect;

/**
 * Author：LvQingYang
 * Date：2017/9/12
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public interface WifiConnectListener {
    void onWifiEnable();
    void onWifiDisable();
    void onScanResultAvailable();
    void onWifiConnected();
    void onWifiDisconnected();
    void onWifiSignChange();
}
