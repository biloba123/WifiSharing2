package com.lvqingyang.wifisharing.Wifi.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseFragment;
import com.lvqingyang.wifisharing.tools.MyDialog;
import com.lvqingyang.wifisharing.bean.ConnectDevice;
import com.lvqingyang.wifisharing.bean.Hotspot;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import frame.tool.MyToast;
import frame.tool.NetWorkUtils;
import frame.tool.SolidRVBaseAdapter;

/**
 * Author：LvQingYang
 * Date：2017/8/26
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class ShareHotspotFragment extends BaseFragment {

    /**
     * view
     */
    private Button mBtnOpenHotspot;
    private LinearLayout mLlClosed;
    private RelativeLayout mLlOpening;
    private RippleBackground mRbLeft;
    private RippleBackground mRbRight;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvName;
    private LinearLayout mLlNoDevice;
    private RecyclerView mRvDevice;
    private RelativeLayout mLlShare;
    private TextView mTvConnectCount;

     /**
     * fragment
     */


     /**
     * data
     */
     private WifiHotUtil mWifiHotUtil;
    private List<ConnectDevice> mConnectDeviceList=new ArrayList<ConnectDevice>();
    private SolidRVBaseAdapter mAdapter;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //是否上传热点
    private boolean mIsPost=false;
    private boolean mIsReadyPost=false;
    private String mHotspotId;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onLocationChanged: ");
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    if (mIsPost) {//已上传则更新热点主人位置
                        if (BuildConfig.DEBUG) Log.d(TAG, "onLocationChanged: update");
                        if (mHotspotId != null) {
                            Hotspot.updateHotspot(amapLocation,mHotspotId);
                        }
                    }else {//未上传
                        if (BuildConfig.DEBUG) Log.d(TAG, "onLocationChanged: post");
                        Hotspot.postHotspot(false, mWifiHotUtil.getValidApBssid(),
                                mWifiHotUtil.getValidApSsid(), mWifiHotUtil.getValidPassword(),
                                amapLocation, new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            if (BuildConfig.DEBUG) Log.d("ShareHotspotFragment", "done: save succ "+s);
                                            mHotspotId=s;
                                            mIsPost=true;
                                            Hotspot.saveIdToPrefence(getActivity(), s);
                                            mLlShare.setVisibility(View.VISIBLE);
                                            MyToast.success(getActivity(), R.string.share_succ);
                                        }else{
                                            if (BuildConfig.DEBUG) Log.d("ShareHotspotFragment", "done: "+e.toString());
                                        }
                                    }
                                });
                    }
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
     * tag
     */
    private static final String TAG = "ShareHotspotFragment";



    public static ShareHotspotFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ShareHotspotFragment fragment = new ShareHotspotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 显示配置热点对话框
     */
    public void showConfigHotspotDialog(){
        BmobUser bmobUser = BmobUser.getCurrentUser();
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_config_hotspot, null);
        final LinearLayout llpwd = (LinearLayout) view.findViewById(R.id.ll_pwd);
        final Switch swshare = (Switch) view.findViewById(R.id.sw_share);
        final EditText etpwd = (EditText) view.findViewById(R.id.et_pwd);
        final Switch swpwd = (Switch) view.findViewById(R.id.sw_pwd);
        final EditText etname = (EditText) view.findViewById(R.id.et_name);
        llpwd.setVisibility(View.GONE);

        if(bmobUser == null){
            MyToast.info(getActivity(), R.string.unlogin_can_not_share);
            swshare.setChecked(false);
            swshare.setEnabled(false);
        }else {
            swshare.setChecked(true);
        }

        etname.setText(mWifiHotUtil.getValidApSsid());
        etpwd.setText(mWifiHotUtil.getValidPassword());

        MyDialog dialog = new MyDialog(getActivity())
                .setTitle(R.string.personal_hotspot)
                .setView(view)
                .setPosBtn(R.string.create, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pwd = etpwd.getText().toString();
                        String name = etname.getText().toString();
                        if (swpwd.isChecked()) {
                            //有密码
                            if (swshare.isChecked()) {
                                if (NetWorkUtils.isNetworkConnected(getActivity())) {
                                    //发布
                                    mWifiHotUtil.turnOnWifiAp(name, pwd, WifiHotUtil.WifiSecurityType.WIFICIPHER_WPA2);
                                    mIsReadyPost=true;
                                }else {
                                    MyToast.error(getActivity(), R.string.not_open_net);
                                }
                            }else {
                                mWifiHotUtil.turnOnWifiAp(name, pwd, WifiHotUtil.WifiSecurityType.WIFICIPHER_WPA2);
                            }
                        } else {
                            mWifiHotUtil.turnOnWifiAp(name, null, WifiHotUtil.WifiSecurityType.WIFICIPHER_NOPASS);
                        }
                    }
                })
                .setNegBtn(android.R.string.cancel, null);
        dialog.show(getFragmentManager());

        final TextView tvPos = dialog.getTvpos();
        swpwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llpwd.setVisibility(View.VISIBLE);
                    enablePosTv(etpwd, tvPos, 8);
                } else {
                    llpwd.setVisibility(View.GONE);
                    tvPos.setEnabled(true);
                    tvPos.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

        etpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enablePosTv(etpwd, tvPos, 8);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enablePosTv(etname, tvPos, 1);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 检查是否有写设置权限
     * @return
     */
    public boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(getActivity());
            Log.d(TAG, "Can Write Settings: " + retVal);
            if(!retVal){
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_share_hotspot,container,false);
        return view;
    }

    @Override
    protected void initView(View view) {
        mLlClosed = (LinearLayout) view.findViewById(R.id.ll_closed);
        mBtnOpenHotspot = (Button) view.findViewById(R.id.btn_open_hotspot);
        mLlOpening = (RelativeLayout) view.findViewById(R.id.ll_opening);
        mRbLeft = (RippleBackground) view.findViewById(R.id.rb_left);
        mRbRight = (RippleBackground) view.findViewById(R.id.rb_right);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        mTvName=view.findViewById(R.id.tv_name);
        mLlNoDevice = (LinearLayout) view.findViewById(R.id.ll_no_device);
        mRvDevice = (RecyclerView) view.findViewById(R.id.rv_device);
        mLlShare=view.findViewById(R.id.ll_share);
        mTvConnectCount=view.findViewById(R.id.tv_connect_count);
    }

    @Override
    protected void setListener() {
        WiFiAPService.startService(getActivity());
        WiFiAPService.addWiFiAPListener(new WiFiAPListener() {
            @Override
            public void stateChanged(int state) {
                switchState(state);
            }
        });

        mBtnOpenHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSystemWritePermission()) {
                    showConfigHotspotDialog();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showConnectDevice();
            }
        });

    }

    @Override
    protected void initData() {
        mWifiHotUtil=WifiHotUtil.getInstance(getActivity());

        mAdapter=new SolidRVBaseAdapter<ConnectDevice>(getActivity(), mConnectDeviceList) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, ConnectDevice bean) {
                holder.setText(R.id.tv_ip,bean.getIp());
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_device;
            }

            @Override
            protected void onItemClick(int position, ConnectDevice bean) {
                super.onItemClick(position, bean);
                new MyDialog(getActivity())
                        .setTitle(getString(R.string.device_info))
                        .setMessage("IP: "+bean.getIp()+"\n\n"
                            +"Mac: "+bean.getMac())
                        .setPosBtn(android.R.string.ok,null)
                        .show(getFragmentManager());
            }
        };

        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(10000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }

    @Override
    protected void setData() {
        switchState(mWifiHotUtil.getWifiAPState());

        mRvDevice.setAdapter(mAdapter);
        mRvDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    private void enablePosTv(EditText et,TextView tvPos,int limit){
        if (et.getText().toString().length()<limit) {
            tvPos.setEnabled(false);
            tvPos.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }else {
            tvPos.setEnabled(true);
            tvPos.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivity(intent);
    }

    /**
     * 热点关闭
     */
    private void hotspotClosed(){
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mLlOpening.setVisibility(View.GONE);
        mRbLeft.stopRippleAnimation();
        mRbRight.stopRippleAnimation();

        mLlClosed.setVisibility(View.VISIBLE);
    }

    /**
     * 热点正在打开
     */
    private void hotspotOpening(){
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mLlClosed.setVisibility(View.GONE);

        mLlOpening.setVisibility(View.VISIBLE);
        mRbLeft.startRippleAnimation();
        mRbRight.startRippleAnimation();
    }

    /**
     * 已打开
     */
    private void hotspotOpened(){
        mLlClosed.setVisibility(View.GONE);
        mLlOpening.setVisibility(View.GONE);
        mRbLeft.stopRippleAnimation();
        mRbRight.stopRippleAnimation();

        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mTvName.setText(mWifiHotUtil.getValidApSsid());

        showConnectDevice();
    }

    /**
     * 在热点不同状态下做相应操作
     */
    private void switchState(int state){
        switch (state) {
            case WiFiAPListener.WIFI_AP_CLOSE_SUCCESS:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: close-------------------------");
                if (mIsPost&&mHotspotId!=null) {
                    Hotspot.deleteHotspot(getActivity());
                    mIsPost=false;
                    mHotspotId=null;
                }
                mLocationClient.stopLocation();
                hotspotClosed();
                break;
            }
            case WiFiAPListener.WIFI_AP_CLOSEING:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: closing-----------------------");
                break;
            }
            case WiFiAPListener.WIFI_AP_OPEN_SUCCESS:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: open-------------------------");
                if (mIsReadyPost) {
                    mLocationClient.startLocation();
                    mIsReadyPost=false;
                }
                hotspotOpened();
                break;
            }
            case WiFiAPListener.WIFI_AP_OPENING:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: opening-------------------------");
                hotspotOpening();
                break;
            }
        }
    }

    /**
     * 显示连接设备
     */
    private void showConnectDevice(){
        mAdapter.clearAllItems();
        mAdapter.addItems(mWifiHotUtil.getConnectedDevices());
        if (mConnectDeviceList.size()>0) {
            mLlNoDevice.setVisibility(View.GONE);
            mRvDevice.setVisibility(View.VISIBLE);
            mTvConnectCount.setText(mConnectDeviceList.size()+"台");
        }else {
            mRvDevice.setVisibility(View.GONE);
            mLlNoDevice.setVisibility(View.VISIBLE);
            mTvConnectCount.setText("0台");
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }
}
