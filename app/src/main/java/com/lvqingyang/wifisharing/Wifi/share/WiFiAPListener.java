package com.lvqingyang.wifisharing.Wifi.share;

/**
 * 　　┏┓　　  ┏┓+ +
 * 　┏┛┻━ ━ ━┛┻┓ + +
 * 　┃　　　　　　  ┃
 * 　┃　　　━　　    ┃ ++ + + +
 * ████━████     ┃+
 * 　┃　　　　　　  ┃ +
 * 　┃　　　┻　　  ┃
 * 　┃　　　　　　  ┃ + +
 * 　┗━┓　　　┏━┛
 * 　　　┃　　　┃
 * 　　　┃　　　┃ + + + +
 * 　　　┃　　　┃
 * 　　　┃　　　┃ +  神兽保佑
 * 　　　┃　　　┃    代码无bug！
 * 　　　┃　　　┃　　+
 * 　　　┃　 　　┗━━━┓ + +
 * 　　　┃ 　　　　　　　┣┓
 * 　　　┃ 　　　　　　　┏┛
 * 　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　┃┫┫　┃┫┫
 * 　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/3/20
 * Email：biloba12345@gamil.com
 * Info：
 */

public interface WiFiAPListener {

    int WIFI_AP_CLOSEING 		= 10;  //wifi hot is closeing
    int WIFI_AP_CLOSE_SUCCESS 	= 11;  //wifi hot close success
    int WIFI_AP_OPENING 		= 12;  //WiFi hot is opening
    int WIFI_AP_OPEN_SUCCESS 	= 13;  //WiFi hot open success

    /**
     * 热点状态改变
     * @param state
     */
    void stateChanged(int state) ;
}
