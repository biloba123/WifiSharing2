package com.lvqingyang.wifisharing.Wifi.share;


import java.util.HashSet;

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
 * Info：热点观察者
 */

public class WiFiAPObserver implements WiFiAPListener {

    /**
     * the set to save all registed listener
     */
    private HashSet<WiFiAPListener> listenerSet = new HashSet<WiFiAPListener>();

    /**
     * add wiFiAPListener
     * @param wiFiAPListener
     */
    public void addWiFiAPListener(WiFiAPListener wiFiAPListener) {
        if (!listenerSet.contains(wiFiAPListener)) {
            listenerSet.add(wiFiAPListener);
        }
    }

    /**
     * remove the wiFiAPListener
     * @param wiFiAPListener
     */
    public void removeWiFiAPListener(WiFiAPListener wiFiAPListener) {
        if (listenerSet.contains(wiFiAPListener)) {
            listenerSet.remove(wiFiAPListener);
        }
    }

    /**
     * remove all WiFiAPListener
     */
    public void clearWiFiAPListener() {
        listenerSet.clear();
    }

    @Override
    public void stateChanged(int state) {
        //notify all Listener the state changed
        for (WiFiAPListener wiFiAPListener : listenerSet) {
            wiFiAPListener.stateChanged(state);
        }
    }

}
