package com.lvqingyang.wifisharing.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

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
 * Date：2017/3/16
 * Email：biloba12345@gamil.com
 * Info：Wifi相关操作
 */


/**
 * ScanResult类保存扫描到Wifi信息
 * 数据成员：
 *
 * public String      BSSID                接入点地址
 * public String      SSID                   网络名字
 * public String      capabilities      网络性能（接入点支持的认证，密钥管理，加密机制...）
 * public int            frequency         接入频率-MHz
 * public int            level                    信号强度-dBm
 *
 */


/**
 * WifiConfiguration类获取网络配置
 * 6个子类：
 *
 * AuthAlgorithm        获取IEEE 802.11加密方法
 * PairwiseCipher         WPA方式的成对秘钥
 * Protocol                     加密协议
 * KeyMgmt                  密码管理体制
 * GroupCipher            组秘钥
 * Status                         当前网络状态
 */


/**
 * WifiInfo获取已连接或处于活动状态的Wifi网络状态
 * ....不想写了
 */

/**
 * WifiManager用于管理Wifi连接
 * ...
 */


public class WifiAdmin {

    //Tag
    private static final String TAG = "WifiAdmin";

    //字符串缓存
    private StringBuffer mStringBuffer=new StringBuffer();

    //保存扫描结果列表
    public List<ScanResult> mScanResultList=new ArrayList<>();

    private ScanResult mScanResult;

    //创建WifiManager对象
    private WifiManager mWifiManager;

    //创建WifiInfo对象
    private WifiInfo mWifiInfo;

    //网络连接列表
    private List<WifiConfiguration> mWifiConfigurationList=new ArrayList<>();

    //创建一个WifiLock
    private WifiManager.WifiLock mWifiLock;

    //创建连接管理器
    private ConnectivityManager mConnectivityManager;

    private Context mContext;

    //Wifi配置列表 (有密码的)
    private List<WifiConfiguration> mWifiConfigedSpecifiedList=new ArrayList<>();

    //网络状态
    private NetworkInfo.State mState;


    /**
     * 构造方法
     *
     * @param context
     */
    public WifiAdmin(Context context){
        mContext=context;
        //获取WifiManager系统服务
        mWifiManager= (WifiManager) mContext.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        //获取Wifi连接信息
        mWifiInfo=mWifiManager.getConnectionInfo();
        //调用连接管理系统服务
        mConnectivityManager= (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        againGetWifiInfo();
    }

    /**
     * 重新获取当前Wifi连接信息
     */
    public void againGetWifiInfo(){
        mWifiInfo=isNetCardFriendly()?mWifiManager.getConnectionInfo():null;
    }

    /**
     * 重新获取当前Wifi连接信息
     */
    public void againGetWifiConfigurations(){
        mWifiConfigurationList=mWifiManager.getConfiguredNetworks();
    }

    /**
     * 判断用户是否开启Wifi网卡，true为开启
     * @return
     */
    public boolean isNetCardFriendly(){
        return mWifiManager.isWifiEnabled();
    }


    /**
     * 是否在连接Wifi
     * @return
     */
    public boolean isConnecting(){
        mState = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (NetworkInfo.State.CONNECTING==mState) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 是否已连接Wifi
     * @return
     */
    public boolean isConnected(){
        mState = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (NetworkInfo.State.CONNECTED==mState) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 得到当前网络连接状态
     * @return
     */
    public NetworkInfo.State getCurrentState(){
        return (mState = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState());
    }

    /**
     * 设置配置好的网络（有密码）
     * @param ssid
     */
    public void setWifiConfigedSpecifiedList(String ssid){
        mWifiConfigedSpecifiedList.clear();
        if (mWifiConfigurationList != null) {
            for (WifiConfiguration configuration : mWifiConfigurationList) {
                //如果是指定网络
                if (configuration.SSID.equalsIgnoreCase("\""+ssid+"\"")
                        &&configuration.preSharedKey!=null) {
                    mWifiConfigedSpecifiedList.add(configuration);
                }
            }
        }
    }

    //返回Wifi设置列表
    public List<WifiConfiguration> getWifiConfigedSpecifiedList() {
        return mWifiConfigedSpecifiedList;
    }

    //打开Wifi网卡
    public void openNetCard(){
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭Wifi网卡
    public void closeNetCard(){
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //检查当前Wifi状态
    public void checkNetCardState(){
        switch (mWifiManager.getWifiState()) {
            case 0:
                Log.i(TAG, "checkNetCardState: 网卡正在关闭");
                break;
            case 1:
                Log.i(TAG, "checkNetCardState: 网卡已经关闭");
                break;
            case 2:
                Log.i(TAG, "checkNetCardState: 网卡正在打开");
                break;
            case 3:
                Log.i(TAG, "checkNetCardState: 网卡已经打开");
                break;
            default:
                Log.i(TAG, "checkNetCardState: 没有获取状态");
                break;
        }
    }

    //扫描周边网络
    public void scan(){
        //开始扫描
        mWifiManager.startScan();
        //获取扫描结果
        List<ScanResult> results=mWifiManager.getScanResults();
        //扫描配置列表
//        mWifiConfigurationList=mWifiManager.getConfiguredNetworks();
        mScanResultList.clear();
        if (results == null) {
            Log.i(TAG, "scan: 不存在无线网络");
        }else {
            Log.i(TAG, "scan: 存在无线网络    "+results.size());
            for(ScanResult result : results){
                if (result.SSID == null || result.SSID.length() == 0
                        || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                //排除重复
                boolean found = false;
                for(ScanResult item:mScanResultList){
                    if(item.SSID.equals(result.SSID)&&item.capabilities.equals(result.capabilities)){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    mScanResultList.add(result);
                }
            }
        }
    }

    //获取扫描结果
    public List<ScanResult> getScanResultList() {
        return mScanResultList;
    }

    //得到扫描结果
    public String getScanResult(){
        //每次点击扫描之前清空上次结果
        if (mStringBuffer != null) {
            mStringBuffer=new StringBuffer();
        }
//        scan();
        if (mScanResultList != null) {
            for (int i = 0; i < mScanResultList.size(); i++) {
                mScanResult=mScanResultList.get(i);
                mStringBuffer=mStringBuffer.append("No.").append(i+1)
                        .append(":").append(mScanResult.SSID).append("->")
                        .append(mScanResult.BSSID).append("->")
                        .append(mScanResult.capabilities).append("->")
                        .append(mScanResult.frequency).append("->")
                        .append(mScanResult.level).append("->")
                        .append(mScanResult.describeContents())
                        .append("\n\n");
            }
        }
        Log.i(TAG, "getScanResult: "+mStringBuffer.toString());
        //返回结果
        return mStringBuffer.toString();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //断开当前连接网络
    public void disconnectWifi(){
        //获取网络ID
        int netId=getNetWorkId();
        //设置网络不可用
        mWifiManager.disableNetwork(netId);
        //断开网络
        mWifiManager.disconnect();
        mWifiInfo=null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //锁定
    public void acquireWifiLock(){
        mWifiLock.acquire();
    }

    //解锁
    public void releaseWifiLock(){
        //判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    //创建一个wifilock
    public void creatWifiLock(){
        mWifiLock=mWifiManager.createWifiLock("WifiSharing");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //得到配置好的网络
    public List<WifiConfiguration> getWifiConfigurationList() {
        return mWifiConfigurationList;
    }

    //指定配置好的网络进行连接
    public boolean connectConfiguration(int index){
        if (index>=mWifiConfigurationList.size()) {
            return false;
        }else {
            return mWifiManager.enableNetwork(mWifiConfigurationList.get(index).networkId,
                    true);
        }
    }

    //指定配置好的网络进行连接
    public boolean connectConfiguration(WifiConfiguration wcf){

        return mWifiManager.enableNetwork(wcf.networkId,
                true);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //得到连接的ID
    public int getNetWorkId(){
        return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
    }

    //得到ip地址
    public int getIpAddress(){
        return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();
    }

    //信号强度
    public int getRssi(){
        return (mWifiInfo==null)?0:mWifiInfo.getRssi();
    }


    //得到MAC地址
    public String getMacAddress(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();
    }

    //得到BSSID地址
    public String getBSSID(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
    }

    //得到SSID（名字）
    public String getSSID(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getSSID().substring(1,mWifiInfo.getSSID().length()-1);
    }

    //得到wifiinfo所有信息
    public String getWifiInfo(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.toString();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //添加一个网络并连接
    public int addNetwork(WifiConfiguration wcg){
        //添加网络
//        return wcgID;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mConnectivityManager.requestNetwork(new NetworkRequest.Builder()
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .setNetworkSpecifier(wcg.SSID)
//            .build(),
//                    new ConnectivityManager.NetworkCallback(){
//                        @Override
//                        public void onAvailable(Network network) {
//                            super.onAvailable(network);
//                        }
//                    });
//        }else {
//            disconnectWifi();
            int wcgID=mWifiManager.addNetwork(wcg);
            mWifiManager.enableNetwork(wcgID,true);
//            mWifiManager.saveConfiguration();
//            mWifiManager.reconnect();
//        }

//        for (WifiConfiguration configuration : mWifiConfigurationList) {
//            Log.d(TAG, "addNetwork: "+configuration.SSID);
//        }
//        Log.d(TAG, "addNetwork: ------------------------------------------");
//        for (WifiConfiguration configuration : mWifiConfigedSpecifiedList) {
//            Log.d(TAG, "addNetwork: "+configuration.SSID);
//        }
//        if (isExsits(wcg.SSID) == null) {
//            Log.d(TAG, "addNetwork: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//        }
//        boolean state=false;
//        if (mWifiManager.setWifiEnabled(true)) {
//            mWifiConfigurationList=mWifiManager.getConfiguredNetworks();
//            for(Iterator<WifiConfiguration> iterator=mWifiConfigurationList.iterator();iterator.hasNext();){
//                WifiConfiguration c=iterator.next();
//                if (c.SSID.equals(wcg.SSID)) {
//                    state=mWifiManager.enableNetwork(c.networkId,true);
//                }else {
//                    mWifiManager.disableNetwork(c.networkId);
//                }
//                mWifiManager.reconnect();
//            }
//        }
        return wcgID;

    }


    //创建wifi热点的
    public int connectWifi(String SSID, String Password, int Type)
    {
        Log.d(TAG, "createWifiInfo: 不存在"+SSID);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.status = WifiConfiguration.Status.DISABLED;
        wifiConfig.priority = 40;
        wifiConfig.SSID = "\"" + SSID + "\"";
        //无密码
        if(Type == 1) //WIFICIPHER_NOPASS
        {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.clear();
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }
        //有密码
        if(Type == 2) //WIFICIPHER_WEP
        {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            if (getHexKey(Password)) wifiConfig.wepKeys[0] = Password;
            else wifiConfig.wepKeys[0] = "\"".concat(Password).concat("\"");
            wifiConfig.wepTxKeyIndex = 0;

        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.preSharedKey = "\"".concat(Password).concat("\"");
        }


        //连接
        int wcgID=mWifiManager.addNetwork(wifiConfig);
        mWifiManager.enableNetwork(wcgID,true);
        //更新配置
        againGetWifiConfigurations();
        againGetWifiInfo();
        return wcgID;
    }


    public WifiConfiguration isExsits(String SSID)
    {
        againGetWifiConfigurations();
        for (WifiConfiguration existingConfig : mWifiConfigurationList)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

    private static boolean getHexKey(String s) {
        if (s == null) {
            return false;
        }

        int len = s.length();
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                continue;
            }
            return false;
        }
        return true;
    }

}
