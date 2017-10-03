package com.lvqingyang.wifisharing.Wifi.connect;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.Wifi.connect.funcation.SecurityActivity;
import com.lvqingyang.wifisharing.Wifi.connect.funcation.SignActivity;
import com.lvqingyang.wifisharing.base.AppContact;
import com.lvqingyang.wifisharing.base.BaseFragment;
import com.lvqingyang.wifisharing.tools.MyDialog;
import com.lvqingyang.wifisharing.bean.Hotspot;
import com.lvqingyang.wifisharing.bean.MyScanResult;
import com.lvqingyang.wifisharing.bean.Record;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import frame.tool.MyToast;
import frame.tool.NetWorkUtils;
import frame.tool.SolidRVBaseAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.wefor.circularanim.CircularAnim;


/**
 * 连接WIFI
 * 提供WiFi基本功能：扫描附近WiFi，连接
 * @author Lv Qingyang
 * @see com.lvqingyang.wifisharing.Wifi.WifiFragment
 * @since v1.0
 * @date 2017/9/15
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */

public class ConnectHotspotFragment extends BaseFragment {

    /**
     * View
     */
//    private android.widget.Switch swsharewifi;
    private android.widget.LinearLayout llnowifi;
    private android.support.v7.widget.RecyclerView rvwifi;
    private android.support.v4.widget.SwipeRefreshLayout srl;

    //Wifi closed
    private View mLayNoWifi;
    private ImageView mIvState;
    private TextView mTvState;
    private Button mBtnOpenWifi;

    private CardView mCvSate;
    //Wifi connecting
    private RippleBackground mRippleBackground;
    private TextView mTvName;

    //wifi connect
    private ImageView mIvCenter;
    private ImageView ivwifisign;
    private TextView tvwifiname;
    private TextView tvupload;
    private TextView tvdownload;
    private TextView tvconnectcount;
    private TextView tvsecuritycheck;
    private ImageView ivsafety;
    private TextView tvsign;
    private TextView tvspeed;
    private LinearLayout llconnected;
    private ImageView mIvShare;

    //是否开启测速
    private boolean mIsOnSpeeding;

    //onScanResultAvailable会被多次调用
    private boolean mIsFirstAvailable=false;

    private List<MyScanResult> mMyScanResults=new ArrayList<>();

    /**
     * 对wifi进行管理
     */
    private WifiAdmin mWifiAdmin;
    private SolidRVBaseAdapter mAdapter;
    private boolean mIsConnectingWifi=false;
    //忽略重连第一次接受到的已连接
    private boolean mIsFirstReceiveConnected=false;
    //保存用户位置
    private AMapLocation mLastLocation;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onLocationChanged: ");
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    mLastLocation=amapLocation;
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    /**
     * wifi状态回调
     */
    private WifiConnectListener mWifiConnectListener=new WifiConnectListener() {
        @Override
        public void onWifiEnable() {
            mWifiAdmin.againGetWifiInfo();
            if (BuildConfig.DEBUG) Log.d(TAG, "onWifiEnable: ");
            srl.setEnabled(true);
            srl.setRefreshing(true);
            mWifiAdmin.scan();
            wifiEnable();
        }

        @Override
        public void onWifiDisable() {
            mWifiAdmin.againGetWifiInfo();
            mIsConnectingWifi=false;
            if (BuildConfig.DEBUG) Log.d(TAG, "onWifiDisable: ");
            srl.setEnabled(false);
            wifiDisable();
        }

        @Override
        public void onScanResultAvailable() {
            if (BuildConfig.DEBUG) Log.d(TAG, "onScanResultAvailable: ");
            if (mIsFirstAvailable) {
                mIsFirstAvailable=false;
                srl.setRefreshing(false);
                mAdapter.clearAllItems();
                mAdapter.addItems(MyScanResult.transToMyScanResults(mWifiAdmin.getScanResultList()));
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount()==0) {
                    rvwifi.setVisibility(View.GONE);
                    llnowifi.setVisibility(View.VISIBLE);
                    //再进行一次扫描
                    mWifiAdmin.scan();
                }else {
                    findShareWifi();
                    llnowifi.setVisibility(View.GONE);
                    rvwifi.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public void onWifiConnected() {
            if (BuildConfig.DEBUG) Log.d(TAG, "onWifiConnected: "+mWifiAdmin.isConnected()+" "+mWifiAdmin.isConnecting());
            if (!mIsFirstReceiveConnected) {
                mWifiAdmin.againGetWifiInfo();
                wifiConnected(mIsConnectingWifi);
                mIsConnectingWifi=false;
            }else {
                mIsFirstReceiveConnected=false;
            }

        }

        @Override
        public void onWifiDisconnected() {
            if (BuildConfig.DEBUG) Log.d(TAG, "onWifiDisconnected: "+mWifiAdmin.isConnecting());
            mWifiAdmin.againGetWifiInfo();
            if (!mIsConnectingWifi) {
                wifiDisConnected();
            }
        }

        @Override
        public void onWifiSignChange() {
            updateConnectedWifi();
        }
    };

     /**
     * tag
     */
     private static final String TAG = "ConnectHotspotFragment";

    public static ConnectHotspotFragment newInstance() {
         
         Bundle args = new Bundle();
         
         ConnectHotspotFragment fragment = new ConnectHotspotFragment();
         fragment.setArguments(args);
         return fragment;
     }

    /**
     * wifi关闭
     */
    public void wifiDisable(){
        srl.setVisibility(View.GONE);
        mLayNoWifi.setVisibility(View.VISIBLE);
        mBtnOpenWifi.setVisibility(View.VISIBLE);
        mIvState.setImageResource(R.drawable.wifi_open);
        mTvState.setText(R.string.wifi_close_);
        mIsOnSpeeding=false;
    }

    /**
     * 正在开启
     */
    public void wifiOpening(){
        srl.setVisibility(View.GONE);
        mBtnOpenWifi.setVisibility(View.GONE);
        mLayNoWifi.setVisibility(View.VISIBLE);
        mIvState.setImageResource(R.drawable.wifi_signal);
        AnimationDrawable ad= (AnimationDrawable) mIvState.getDrawable();
        ad.start();
        mTvState.setText(R.string.wifi_opening);
    }

    /**
     * 打开状态
     */
    public void wifiEnable(){
        mLayNoWifi.setVisibility(View.GONE);
        Drawable d= mIvState.getDrawable();
        if (d instanceof AnimationDrawable) {
            ((AnimationDrawable)d).stop();
        }
        srl.setVisibility(View.VISIBLE);
        rvwifi.setVisibility(View.GONE);
        llnowifi.setVisibility(View.VISIBLE);
    }

    /**
     * 正在连接
     * @param ssid
     */
    public void wifiConnecting(String ssid){
        llconnected.setVisibility(View.GONE);
        mIvShare.setVisibility(View.GONE);

        mCvSate.setVisibility(View.VISIBLE);
        mRippleBackground.setVisibility(View.VISIBLE);
        mTvName.setText(ssid);
        mRippleBackground.startRippleAnimation();
        AnimationDrawable ad= (AnimationDrawable) mIvCenter.getDrawable();
        ad.start();
    }

    /**
     * 连接上
     * @param showAnim 是否显示circle anim
     */
    public void wifiConnected(boolean showAnim){
        WifiInfo wifiInfo=mWifiAdmin.getWifiInfo();

        mRippleBackground.setVisibility(View.GONE);
        mRippleBackground.stopRippleAnimation();
        AnimationDrawable ad= (AnimationDrawable) mIvCenter.getDrawable();
        ad.stop();

        if (showAnim) {
            CircularAnim.show(mCvSate).go();
        }else {
            mCvSate.setVisibility(View.VISIBLE);
        }
        llconnected.setVisibility(View.VISIBLE);
        mIvShare.setVisibility(View.VISIBLE);
        if (wifiInfo != null) {
            updateConnectedWifi();
        }else {
            tvwifiname.setText(R.string.app_name);
        }

        tvconnectcount.setText(getString(R.string.connect_count)+1);

        //开启测速
        runSpeedThred();
        mIsOnSpeeding=true;
    }

    /**
     * wifi断开连接
     */
    public void wifiDisConnected(){
        mCvSate.setVisibility(View.GONE);
        mRippleBackground.stopRippleAnimation();
        AnimationDrawable ad= (AnimationDrawable) mIvCenter.getDrawable();
        ad.stop();
        mIsOnSpeeding=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWifiAdmin.isNetCardFriendly()) {
//            srl.setRefreshing(true);
            mIsFirstAvailable=true;
            if (mWifiAdmin.isConnected()) {
                wifiConnected(false);
            }
        }

    }


    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_connect_hotspot,container,false);
        return view;
    }

    @Override
    protected void initView(View view) {
        this.srl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        this.rvwifi = (RecyclerView) view.findViewById(R.id.rv_wifi);
        this.llnowifi = (LinearLayout) view.findViewById(R.id.ll_no_wifi);
//        this.swsharewifi = (Switch) view.findViewById(R.id.sw_share_wifi);

        //Wifi close
        mLayNoWifi = view.findViewById(R.id.layout_wifi_disable);
        mIvState = (ImageView) view.findViewById(R.id.iv_state);
        mTvState = (TextView) view.findViewById(R.id.tv_state);
        mBtnOpenWifi = (Button) view.findViewById(R.id.btn_open_wifi);

        mCvSate = (CardView) view.findViewById(R.id.cv_state);
        //Wifi connecting
        mRippleBackground = (RippleBackground) view.findViewById(R.id.rb);
        mIvCenter = (ImageView) view.findViewById(R.id.iv_center);
        mTvName = (TextView) view.findViewById(R.id.tv_name);


        //Wifi connect
        this.llconnected = (LinearLayout) view.findViewById(R.id.ll_connected);
        this.tvspeed = (TextView) view.findViewById(R.id.tv_speed);
        this.tvsign = (TextView) view.findViewById(R.id.tv_sign);
        this.ivsafety = (ImageView) view.findViewById(R.id.iv_safety);
        this.tvsecuritycheck = (TextView) view.findViewById(R.id.tv_security_check);
        this.tvconnectcount = (TextView) view.findViewById(R.id.tv_connect_count);
        this.tvdownload = (TextView) view.findViewById(R.id.tv_download);
        this.tvupload = (TextView) view.findViewById(R.id.tv_upload);
        this.tvwifiname = (TextView) view.findViewById(R.id.tv_wifi_name);
        this.ivwifisign = (ImageView) view.findViewById(R.id.iv_wifi_sign);
        mIvShare=view.findViewById(R.id.iv_share);
    }

    @Override
    protected void setListener() {
        WiFiConnectService.startService(getActivity());
        WiFiConnectService.addWiFiConnectListener(mWifiConnectListener);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsFirstAvailable=true;
                updateConnectedWifi();
                mWifiAdmin.scan();
            }
        });

        ivsafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityWithCircularAnim(view, SecurityActivity.class);
            }
        });

        tvsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityWithCircularAnim(view, SignActivity.class);
            }
        });

        mBtnOpenWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWifiAdmin.openNetCard();
                wifiOpening();
            }
        });

        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareDialog();
            }
        });
    }

    @Override
    protected void initData() {
        mWifiAdmin=new WifiAdmin(getActivity());

        mAdapter=new SolidRVBaseAdapter<MyScanResult>(getActivity(), mMyScanResults) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, final MyScanResult myScanResult) {
                final ScanResult scanResult=myScanResult.getScanResult();
                holder.setText(R.id.tv_name,scanResult.SSID);
                //信号强度
                final boolean isLocked=scanResult.capabilities.contains("WEP")||scanResult.capabilities.contains("PSK")||
                        scanResult.capabilities.contains("WEP");
                ImageView ivLevel=holder.getView(R.id.iv_level);
                if(isLocked){
                    ivLevel.setImageResource(R.drawable.ic_wifi_close);
                }else {
                    ivLevel.setImageResource(R.drawable.ic_wifi_open);
                }
                ivLevel.setImageLevel(WifiManager.calculateSignalLevel(scanResult.level,5));

                holder.getView(R.id.iv_tag).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanResultInfoActivity.start(getActivity(), scanResult);

                    }
                });

                ImageView ivShare=holder.getView(R.id.iv_share);
                if (myScanResult.getHotspot()!=null) {
                    ivShare.setVisibility(View.VISIBLE);
                }else {
                    ivShare.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_wifi;
            }

            @Override
            protected void onItemClick(int position, final MyScanResult myScanResult) {
                super.onItemClick(position, myScanResult);
                if (myScanResult.getHotspot()!=null) {//分享热点
                    BmobUser bmobUser = BmobUser.getCurrentUser();
                    if(bmobUser != null){//已登录
                        View view=LayoutInflater.from(getActivity())
                                .inflate(R.layout.dialog_connect_shared_wifi, null);
                        TextView tvprice = (TextView) view.findViewById(R.id.tv_price);
                        TextView tvhotspottype = (TextView) view.findViewById(R.id.tv_hotspot_type);
                        if (myScanResult.getHotspot().isFixed()) {
                            tvprice.setText(AppContact.HOTSPOT_FIXED_PRICE+"/M");
                            tvhotspottype.setText(R.string.fixed_hotspot);
                        }else {
                            tvprice.setText(AppContact.HOTSPOT_PRICE+"/M");
                            tvhotspottype.setText(R.string.hotspot);
                        }

                        new MyDialog(getActivity())
                                .setTitle(myScanResult.getScanResult().SSID)
                                .setView(view)
                                .setPosBtn(R.string.connect, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (NetWorkUtils.isNetworkConnected(getActivity())) {
                                            //先判断用户是否有未支付记录
                                            Record.getUserRecord(new FindListener<Record>() {
                                                @Override
                                                public void done(List<Record> list, BmobException e) {
                                                    if (e == null) {
                                                        if (list.size()>0) {//有未支付记录
                                                            if (BuildConfig.DEBUG)
                                                                Log.d(TAG, "done: 有未支付记录");
                                                        }else {
                                                            ScanResult result = myScanResult.getScanResult();
                                                            String pwd=myScanResult.getHotspot().getPassword();
                                                            boolean isConnectSucc=false;
                                                            if (result.capabilities.contains("WEP")) {
                                                                isConnectSucc=connecting(result, pwd, 2);
                                                            }else {
                                                                isConnectSucc=connecting(result, pwd, 3);
                                                            }

                                                            if (isConnectSucc) {
                                                                //连接成功则创建记录
                                                                Record.saveRecord(myScanResult.getHotspot(), new SaveListener<String>() {
                                                                    @Override
                                                                    public void done(String s, BmobException e) {
                                                                        if (e == null) {
                                                                            if (BuildConfig.DEBUG)
                                                                                Log.d(TAG, "done: record created");
                                                                        }else {
                                                                            if (BuildConfig.DEBUG)
                                                                                Log.d(TAG, "done: create record: "+e.toString());
                                                                            MyToast.error(getActivity(), R.string.load_error);
                                                                            //忘记网络...
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                        }
                                                    }else {
                                                        if (BuildConfig.DEBUG)
                                                            Log.d(TAG, "done: record: "+e.toString());
                                                        MyToast.error(getActivity(), R.string.load_error);
                                                    }

                                                }
                                            });
                                        }else {
                                            MyToast.error(getActivity(), R.string.network_disable);
                                        }
                                    }
                                })
                                .setNegBtn(android.R.string.cancel, null)
                                .show(getFragmentManager());
                    }else {
                        MyToast.info(getActivity(), R.string.unlogin_can_not_use);
                    }
                }else {
                    final ScanResult scanResult = myScanResult.getScanResult();
                    final boolean isLocked = scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("PSK") ||
                            scanResult.capabilities.contains("WEP");
                    if (BuildConfig.DEBUG) Log.d(TAG, "onItemClick: ");

                    if (!(scanResult.SSID.equals(mWifiAdmin.getSSID())
                            && scanResult.BSSID.equals(mWifiAdmin.getBSSID()))) {
                        Log.d(TAG, "onItemClick: 不是当前连接wifi");
                        //未开启网卡则开启
//                    if (!mWifiAdmin.isNetCardFriendly()) {
//                        mWifiAdmin.openNetCard();
//                    }
                        WifiConfiguration configuration = mWifiAdmin.isExsits(scanResult.SSID);
                        if (configuration == null) {
                            Log.d(TAG, "onClick: 未配置");
                            //如果是用户分享的Wifi还未处理
                            if (isLocked) {
                                showEditPassDialog(scanResult);
                            } else {
                                if (BuildConfig.DEBUG) Log.d(TAG, "onItemClick: 无密码");
                                connecting(scanResult, null, 1);
                            }
                        } else {
                            Log.d(TAG, "onClick: 已配置");
                            mIsConnectingWifi = true;
                            mIsFirstReceiveConnected = true;
                            wifiConnecting(scanResult.SSID);
                            //有配置直接连接
                            if (!mWifiAdmin.connectConfiguration(configuration)) {
                                MyToast.error(getActivity(), R.string.connect_error);
                            }
                        }
                    }
                }
            }
        };

        initLocationClient();
    }

    @Override
    protected void setData() {
        rvwifi.setLayoutManager(new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvwifi.setAdapter(mAdapter);

        if (mWifiAdmin.isNetCardFriendly()) {
            wifiEnable();
        }else {
            wifiDisable();
        }
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WiFiConnectService.removeWiFiConnectListener(mWifiConnectListener);
        mIsOnSpeeding=false;
    }

    /**
     * 连接有密码wifi
     * @param result
     */
    private void showEditPassDialog(final ScanResult result){
        View v=LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_edit_eifi_pwd,null);
        final EditText editText = (EditText) v.findViewById(R.id.et_password);

        MyDialog dialog=new MyDialog(getActivity())
                .setTitle(result.SSID)
                .setView(v)
                .setNegBtn(android.R.string.cancel,null)
                .setPosBtn(R.string.connect, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String psd=editText.getText().toString();
                        if (!psd.isEmpty()) {
                            if (result.capabilities.contains("WEP")) {
                                connecting(result,psd,2);
                            }else {
                                connecting(result,psd,3);
                            }
                        }
                    }
                });

        dialog.show(getFragmentManager());
        final TextView positiveTv=dialog.getTvpos();
        positiveTv.setEnabled(false);
        positiveTv.setTextColor(getResources().getColor(R.color.accent_grey));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()<8) {
                    positiveTv.setTextColor(getResources().getColor(R.color.accent_grey));
                    positiveTv.setEnabled(false);
                }else {
                    positiveTv.setTextColor(getResources().getColor(R.color.colorAccent));
                    positiveTv.setEnabled(true);
                }
            }
        });

    }

    /**
     * 更新当前连接wifi状态
     */
    private void updateConnectedWifi(){
        if (NetWorkUtils.isNetworkConnected(getActivity())) {
            mLocationClient.startLocation();
        }

        mWifiAdmin.againGetWifiInfo();
        tvwifiname.setText(mWifiAdmin.getSSID());
        //信号强度
        ivwifisign.setImageLevel(WifiManager.calculateSignalLevel(mWifiAdmin.getRssi(),5));
        tvsign.setText(WifiManager.calculateSignalLevel(mWifiAdmin.getRssi(),100)+1+"%");
    }

    /**
     * 连接wifi
     * @param result 要连接的ScanResult
     * @param pass wifi密码
     * @param type wifi加密类型：0-无密码，1-WEP，2-WPA
     */
    private boolean connecting(ScanResult result,String pass,int type){
        if (BuildConfig.DEBUG) Log.d(TAG, "connecting: ***********************************************************************");
        mIsConnectingWifi=true;
        mIsFirstReceiveConnected=true;
        wifiConnecting(result.SSID);
        int wcgID= mWifiAdmin.connectWifi(result.SSID,
                pass,type);
        Log.d(TAG, "connecting: "+wcgID);
        if (wcgID==-1) {
            MyToast.error(getActivity(), R.string.connect_error);
            return false;
        }
        return true;
    }

    /**
     * 有动画的跳转
     * @param startView
     * @param c
     */
    private void startActivityWithCircularAnim(View startView, final Class c){
        CircularAnim.fullActivity(getActivity(), startView)
                .colorOrImageRes(R.color.bg_funcation)
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        startActivity(new Intent(getActivity(),c));
                    }
                });
    }

    //测速线程
    public void runSpeedThred(){
        final int count = 2;
        Observable.create(new Observable.OnSubscribe<int[]>() {
            @Override
            public void call(Subscriber<? super int[]> subscriber) {
                try {
                    int[] speeds=new int[2];
                    long total_get_data = TrafficStats.getTotalRxBytes();
                    long total_post_data=TrafficStats.getTotalTxBytes();
                    while (mIsOnSpeeding){
                        long traffic_data =TrafficStats.getTotalRxBytes() - total_get_data;
                        total_get_data = TrafficStats.getTotalRxBytes();
                        speeds[0]=(int)traffic_data /count;

                        traffic_data =TrafficStats.getTotalTxBytes() - total_post_data;
                        total_post_data = TrafficStats.getTotalTxBytes();
                        speeds[1]=(int)traffic_data /count;
                        subscriber.onNext(speeds);
                        Thread.sleep(count*1000);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<int[]>() {
                    @Override
                    public void onNext(int[] responce) {
//                        Log.d(TAG, "onNext: "+responce[0]+"  "+responce[1]);
                        tvupload.setText(responce[1]>1024?String.format("%.1fkb/s",responce[1]/1024f)
                                :responce[1]+"b/s");
                        tvdownload.setText(responce[0]>1024?String.format("%.1fkb/s",responce[0]/1024f)
                                :responce[0]+"b/s");
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: 网速停测");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }

    /**
     * 检查显示周围分享热点
     */
    private void findShareWifi(){
        if (mLastLocation != null) {
            BmobGeoPoint point=new BmobGeoPoint(mLastLocation.getLongitude(), mLastLocation.getLatitude());

            Hotspot.getNearHotspot(
                    point,
                    new FindListener<Hotspot>() {
                        @Override
                        public void done(List<Hotspot> list, BmobException e) {
                            if (list != null) {
                                for (MyScanResult myScanResult : mMyScanResults) {
                                    for (Hotspot hotspot : list) {
                                        if (TextUtils.equals(myScanResult.getScanResult().BSSID,
                                                hotspot.getBssid())) {
                                            myScanResult.setHotspot(hotspot);
                                            if (BuildConfig.DEBUG) Log.d(TAG, "done: "+myScanResult.getScanResult().SSID);
                                        }
                                    }
                                }

                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    }
            );
        }
    }

    private void initLocationClient(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
//        mLocationOption.setInterval(10000);
        mLocationOption.setOnceLocation(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 分享当前WiFi
     */
    private void showShareDialog(){
        View view=LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_share_wifi, null);
        EditText etpassword = (EditText) view.findViewById(R.id.et_password);
        TextView tvmac = (TextView) view.findViewById(R.id.tv_mac);

        tvmac.setText(mWifiAdmin.getBSSID());

        MyDialog dialog = new MyDialog(getActivity())
            .setTitle("是否分享“" + mWifiAdmin.getSSID() + "”？")
            .setView(view)
            .setPosBtn(R.string.share, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }).setNegBtn(android.R.string.cancel, null);

        dialog.show(getFragmentManager());

        final TextView tvPos=dialog.getTvpos();
        tvPos.setEnabled(false);
        tvPos.setTextColor(getResources().getColor(R.color.accent_grey));

        etpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()<8) {
                    tvPos.setEnabled(false);
                    tvPos.setTextColor(getResources().getColor(R.color.accent_grey));
                }else {
                    tvPos.setEnabled(false);
                    tvPos.setTextColor(getResources().getColor(R.color.text_color));
                }
            }
        });
    }

}
