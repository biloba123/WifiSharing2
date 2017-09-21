package com.lvqingyang.wifisharing.Wifi.share;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lvqingyang.wifisharing.bean.ConnectDevice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
 * Info：WiFi热点工具
 */

public class WifiHotUtil {

    private static final String TAG = "WifiHotUtil";
    public final static boolean DEBUG = true;


    //监听wifi热点的状态变化
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static int WIFI_AP_STATE_DISABLING = 10;
    public static int WIFI_AP_STATE_DISABLED = 11;
    public static int WIFI_AP_STATE_ENABLING = 12;
    public static int WIFI_AP_STATE_ENABLED = 13;
    public static int WIFI_AP_STATE_FAILED = 14;
    //默认wifi秘密
    private static final String DEFAULT_AP_PASSWORD = "12345678";
    private static WifiHotUtil sInstance;
    private static Context mContext;
    private WifiManager mWifiManager;

    public enum WifiSecurityType {
        WIFICIPHER_NOPASS, WIFICIPHER_WPA, WIFICIPHER_WEP, WIFICIPHER_INVALID, WIFICIPHER_WPA2
    }

    private WifiHotUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiHotUtil getInstance(Context c) {
        if (null == sInstance)
            sInstance = new WifiHotUtil(c);
        return sInstance;
    }

    public boolean turnOnWifiAp(String str, String password, WifiSecurityType Type) {
        String ssid = str;
        //配置热点信息。
        WifiConfiguration wcfg = new WifiConfiguration();
        wcfg.SSID = new String(ssid);
        wcfg.networkId = 1;
        wcfg.allowedAuthAlgorithms.clear();
        wcfg.allowedGroupCiphers.clear();
        wcfg.allowedKeyManagement.clear();
        wcfg.allowedPairwiseCiphers.clear();
        wcfg.allowedProtocols.clear();

        if(Type == WifiSecurityType.WIFICIPHER_NOPASS) {
            if(DEBUG) Log.d(TAG, "wifi ap----no password");
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN, true);
            wcfg.wepKeys[0] = "";
            wcfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wcfg.wepTxKeyIndex = 0;
        } else if(Type == WifiSecurityType.WIFICIPHER_WPA) {
            if(DEBUG) Log.d(TAG, "wifi ap----wpa");
            //密码至少8位，否则使用默认密码
            if(null != password && password.length() >= 8){
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
            }
            wcfg.hiddenSSID = false;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        } else if(Type == WifiSecurityType.WIFICIPHER_WPA2) {
            if(DEBUG) Log.d(TAG, "wifi ap---- wpa2");
            //密码至少8位，否则使用默认密码
            if(null != password && password.length() >= 8){
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_AP_PASSWORD;
            }
            wcfg.hiddenSSID = true;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(4);
            //wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        }
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration",
                    wcfg.getClass());
            Boolean rt = (Boolean)method.invoke(mWifiManager, wcfg);
            if(DEBUG) Log.d(TAG, " rt = " + rt);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(TAG, e.getMessage());
        }
        return setWifiApEnabled();
    }

    //获取热点状态
    public int getWifiAPState() {
        int state = -1;
        try {
            Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if(DEBUG) Log.i("WifiAP", "getWifiAPState.state " + state);
        return state;
    }

    private boolean setWifiApEnabled() {
        //开启wifi热点需要关闭wifi
        while(mWifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLED){
            mWifiManager.setWifiEnabled(false);
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }
        // 确保wifi 热点关闭
        while(getWifiAPState() != WIFI_AP_STATE_DISABLED){
            try {
                Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                method1.invoke(mWifiManager, null, false);

                Thread.sleep(200);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        }

        //开启wifi热点
        try {
            Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method1.invoke(mWifiManager, null, true);
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //关闭WiFi热点
    public void closeWifiAp() {
        if (getWifiAPState() != WIFI_AP_STATE_DISABLED) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //获取热点ssid
    public String getValidApSsid() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration)method.invoke(mWifiManager);
            return configuration.SSID;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    //获取热点密码
    public String getValidPassword(){
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration)method.invoke(mWifiManager);
            return configuration.preSharedKey;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    //获取热点安全类型
    public int getValidSecurity(){
        WifiConfiguration configuration;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            configuration = (WifiConfiguration)method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
        }

        if(DEBUG) Log.i(TAG,"getSecurity security="+configuration.allowedKeyManagement);
        if(configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
            return WifiSecurityType.WIFICIPHER_NOPASS.ordinal();
        }else if(configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WifiSecurityType.WIFICIPHER_WPA.ordinal();
        }else if(configuration.allowedKeyManagement.get(4)) { //4 means WPA2_PSK
            return WifiSecurityType.WIFICIPHER_WPA2.ordinal();
        }
        return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
    }

    //获取连接热点的设备
    public ArrayList<ConnectDevice> getConnectedDevices() {
        ArrayList<ConnectDevice> connectDevices = new ArrayList<>();

        if (getWifiAPState() == WIFI_AP_STATE_ENABLED) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader("/proc/net/arp"));
                String line = reader.readLine();
                //读取第一行信息，就是IP address HW type Flags HW address Mask Device
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("[ ]+");
                    if (tokens.length < 6) {
                        continue;
                    }
                    String ip = tokens[0]; //ip
                    String mac = tokens[3];  //mac 地址
//                String flag = tokens[2];//表示连接状态
                    connectDevices.add(new ConnectDevice(ip, mac));
                }
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return connectDevices;
    }

//    public static String getWifiName(Context context, String ssid){
//        String special=context.getString(R.string.wifi_special);
//        return ssid.endsWith(special)?ssid.replace(special,""):ssid;
//    }
}
