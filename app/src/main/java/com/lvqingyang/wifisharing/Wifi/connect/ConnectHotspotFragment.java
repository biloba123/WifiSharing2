package com.lvqingyang.wifisharing.Wifi.connect;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.Wifi.connect.funication.SecurityActivity;
import com.lvqingyang.wifisharing.Wifi.connect.funication.SignActivity;
import com.lvqingyang.wifisharing.base.BaseFragment;
import com.lvqingyang.wifisharing.base.MyDialog;
import com.lvqingyang.wifisharing.tool.WifiAdmin;
import com.skyfishjy.library.RippleBackground;

import frame.tool.MyToast;
import frame.tool.SolidRVBaseAdapter;
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
    private android.widget.Switch swsharewifi;
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

    /**
     * 对wifi进行管理
     */
    private WifiAdmin mWifiAdmin;
    private SolidRVBaseAdapter mAdapter;
    private boolean mIsConnectingWifi=false;
    //忽略重连第一次接受到的已连接
    private boolean mIsFirstReceiveConnected=false;

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
            srl.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount()==0) {
                rvwifi.setVisibility(View.GONE);
                llnowifi.setVisibility(View.VISIBLE);
            }else {
                llnowifi.setVisibility(View.GONE);
                rvwifi.setVisibility(View.VISIBLE);
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

    public void wifiDisable(){
        srl.setVisibility(View.GONE);
        mLayNoWifi.setVisibility(View.VISIBLE);
        mBtnOpenWifi.setVisibility(View.VISIBLE);
        mIvState.setImageResource(R.drawable.home_default_icon);
        mTvState.setText(R.string.wifi_close_);
    }

    public void wifiOpening(){
        srl.setVisibility(View.GONE);
        mBtnOpenWifi.setVisibility(View.GONE);
        mLayNoWifi.setVisibility(View.VISIBLE);
        mIvState.setImageResource(R.drawable.wifi_signal);
        AnimationDrawable ad= (AnimationDrawable) mIvState.getDrawable();
        ad.start();
        mTvState.setText(R.string.wifi_opening);
    }

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

    public void wifiConnecting(String ssid){
        llconnected.setVisibility(View.GONE);

        mCvSate.setVisibility(View.VISIBLE);
        mRippleBackground.setVisibility(View.VISIBLE);
        mTvName.setText(ssid);
        mRippleBackground.startRippleAnimation();
        AnimationDrawable ad= (AnimationDrawable) mIvCenter.getDrawable();
        ad.start();
    }

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
        if (wifiInfo != null) {
            updateConnectedWifi();
        }else {
            tvwifiname.setText(R.string.app_name);
        }

        tvconnectcount.setText(getString(R.string.connect_count)+1);
    }

    public void wifiDisConnected(){
        mCvSate.setVisibility(View.GONE);
        mRippleBackground.stopRippleAnimation();
        AnimationDrawable ad= (AnimationDrawable) mIvCenter.getDrawable();
        ad.stop();
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
        this.swsharewifi = (Switch) view.findViewById(R.id.sw_share_wifi);

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
    }

    @Override
    protected void setListener() {
        WiFiConnectService.startService(getActivity());
        WiFiConnectService.addWiFiConnectListener(mWifiConnectListener);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
    }

    @Override
    protected void initData() {
        mWifiAdmin=new WifiAdmin(getActivity());

        mAdapter=new SolidRVBaseAdapter<android.net.wifi.ScanResult>(getActivity(), mWifiAdmin.getScanResultList()) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, android.net.wifi.ScanResult scanResult) {
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


            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_wifi;
            }

            @Override
            protected void onItemClick(int position, ScanResult scanResult) {
                super.onItemClick(position, scanResult);
                final boolean isLocked=scanResult.capabilities.contains("WEP")||scanResult.capabilities.contains("PSK")||
                        scanResult.capabilities.contains("WEP");
                if (BuildConfig.DEBUG) Log.d(TAG, "onItemClick: ");
                
                if (!(scanResult.SSID.equals(mWifiAdmin.getSSID())
                        &&scanResult.BSSID.equals(mWifiAdmin.getBSSID()))) {
                    Log.d(TAG, "onItemClick: 不是当前连接wifi");
                    //未开启网卡则开启
//                    if (!mWifiAdmin.isNetCardFriendly()) {
//                        mWifiAdmin.openNetCard();
//                    }
                    WifiConfiguration configuration=mWifiAdmin.isExsits(scanResult.SSID);
                    if (configuration == null) {
                        Log.d(TAG, "onClick: 未配置");
                        //如果是用户分享的Wifi还未处理
                        if (isLocked) {
                            showEditPassDialog(scanResult);
                        }else {
                            if (BuildConfig.DEBUG) Log.d(TAG, "onItemClick: 无密码");
                            connecting(scanResult,null,1);
                        }
                    }else {
                        Log.d(TAG, "onClick: 已配置");
                        mIsConnectingWifi=true;
                        mIsFirstReceiveConnected=true;
                        wifiConnecting(scanResult.SSID);
                        //有配置直接连接
                        if (!mWifiAdmin.connectConfiguration(configuration)) {
                            MyToast.error(getActivity(), R.string.connect_error);
                        }
                    }
                }
            }
        };
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
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WiFiConnectService.removeWiFiConnectListener(mWifiConnectListener);
    }

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

    private void updateConnectedWifi(){
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
    private void connecting(ScanResult result,String pass,int type){
        if (BuildConfig.DEBUG) Log.d(TAG, "connecting: ***********************************************************************");
        mIsConnectingWifi=true;
        mIsFirstReceiveConnected=true;
        wifiConnecting(result.SSID);
        int wcgID= mWifiAdmin.connectWifi(result.SSID,
                pass,type);
        Log.d(TAG, "connecting: "+wcgID);
        if (wcgID==-1) {
            MyToast.error(getActivity(), R.string.connect_error);
        }
    }

    private void startActivityWithCircularAnim(View startView, final Class c){
        CircularAnim.fullActivity(getActivity(), startView)
                .colorOrImageRes(R.color.colorPrimary)
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        startActivity(new Intent(getActivity(),c));
                    }
                });
    }
}
