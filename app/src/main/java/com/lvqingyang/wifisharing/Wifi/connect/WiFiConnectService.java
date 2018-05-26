package com.lvqingyang.wifisharing.Wifi.connect;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lvqingyang.wifisharing.BuildConfig;

import java.util.ArrayList;
import java.util.List;

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
 * Info：wifi状态监听service
 */

public class WiFiConnectService extends Service {

    private static String TAG = "WiFiConnectService";

    public static String ACTION_START_SERVICE = "action_start_service";
    public static String ACTION_STOP_SERVICE = "action_stop_service";
    private static List<WifiConnectListener> sWifiConnectListener=new ArrayList<>();


    /**
     * static method to start service
     * @param context
     */
    public static void startService(Context context) {
        Intent intent = new Intent(context, WiFiConnectService.class);
        intent.setAction(ACTION_START_SERVICE);
        context.startService(intent);
    }

    /**
     * static method to stop service
     * @param context
     */
    public static void stopService(Context context) {
        Intent intent = new Intent(context, WiFiConnectService.class);
        intent.setAction(ACTION_STOP_SERVICE);
        context.stopService(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "intent.getAction()="+intent.getAction());
        // option based on action
        if (intent.getAction().equals(ACTION_START_SERVICE) == true) {
            //startService();
        } else if (intent.getAction().equals(ACTION_STOP_SERVICE) == true) {
            stopSelf();
        }
        //重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        //注册接收器
        IntentFilter filter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.EXTRA_RESULTS_UPDATED);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
        super.onCreate();
    }



    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        sWifiConnectListener.clear();
        super.onDestroy();
    }

    public static void addWiFiConnectListener(WifiConnectListener listener) {
        if (!sWifiConnectListener.contains(listener)) {
            sWifiConnectListener.add(listener);
        }
    }


    public static void removeWiFiConnectListener(WifiConnectListener listener) {
        if (sWifiConnectListener.contains(listener)) {
            sWifiConnectListener.remove(listener);
        }
    }


    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();

            if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
                Log.i(TAG, "wifi信号强度变化");
                for (WifiConnectListener wifiConnectListener : sWifiConnectListener) {
                    wifiConnectListener.onWifiSignChange();
                }
            }

            //监听wifi打开关闭
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)) {
                    case WifiManager.WIFI_STATE_DISABLED: {
                        if (BuildConfig.DEBUG) Log.d(TAG, "onReceive: Wifi关闭");
                        for (WifiConnectListener wifiConnectListener : sWifiConnectListener) {
                            wifiConnectListener.onWifiDisable();
                        }
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLED: {
                        if (BuildConfig.DEBUG) Log.d(TAG, "onReceive: Wifi打开");
                        for (WifiConnectListener wifiConnectListener : sWifiConnectListener) {
                            wifiConnectListener.onWifiEnable();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }

            //扫描结果出来
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (BuildConfig.DEBUG) Log.d(TAG, "onReceive: 扫描结果");
                for (WifiConnectListener wifiConnectListener : sWifiConnectListener) {
                    wifiConnectListener.onScanResultAvailable();
                }

            }
            
            if (action.equals(WifiManager.EXTRA_RESULTS_UPDATED)){
                if (BuildConfig.DEBUG) Log.d(TAG, "onReceive: EXTRA_RESULTS_UPDATED");
            }

            //连接上WIFI
            if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                Parcelable parcelable=intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (parcelable != null) {
                    NetworkInfo networkInfo= (NetworkInfo) parcelable;
                    NetworkInfo.State state=networkInfo.getState();
                    if (state== NetworkInfo.State.CONNECTED) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "onReceive: wifi连接上");
                        for (WifiConnectListener wifiConnectListener : sWifiConnectListener) {
                            wifiConnectListener.onWifiConnected();
                        }
                    }else if (state.equals(NetworkInfo.State.DISCONNECTED)) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "onReceive: wifi断开");
                        for (WifiConnectListener wifiConnectListener : sWifiConnectListener) {
                            wifiConnectListener.onWifiDisconnected();
                        }
                    }
                }

            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
