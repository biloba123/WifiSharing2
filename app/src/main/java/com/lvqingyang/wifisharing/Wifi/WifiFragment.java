package com.lvqingyang.wifisharing.Wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.zagum.switchicon.SwitchIconView;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.Wifi.connect.ConnectHotspotFragment;
import com.lvqingyang.wifisharing.Wifi.connect.WiFiConnectService;
import com.lvqingyang.wifisharing.Wifi.connect.WifiConnectListener;
import com.lvqingyang.wifisharing.Wifi.share.ShareHotspotFragment;
import com.lvqingyang.wifisharing.Wifi.share.WiFiAPListener;
import com.lvqingyang.wifisharing.Wifi.share.WiFiAPService;
import com.lvqingyang.wifisharing.Wifi.share.WifiHotUtil;
import com.lvqingyang.wifisharing.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.lvqingyang.wifisharing.Wifi.share.WifiHotUtil.WIFI_AP_STATE_ENABLED;

/**
 * Author：LvQingYang
 * Date：2017/8/26
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class WifiFragment extends BaseFragment {
    /**
     *   View
     */
    private com.github.zagum.switchicon.SwitchIconView wifiswitch;
    private com.github.zagum.switchicon.SwitchIconView hotspotswitch;
    private android.support.v7.widget.Toolbar toolbar;
    private android.support.design.widget.TabLayout tablayout;
    private android.support.v4.view.ViewPager viewpager;
    /**
     *   Fragment
     */
    private ConnectHotspotFragment mConnectFragment;
    private ShareHotspotFragment mShareHotspotFragment;


    /**
     *   Data
     */
    private WifiManager mWifiManager;//wifi开关
    private WifiConnectListener mWifiConnectListener=new WifiConnectListener() {
        @Override
        public void onWifiEnable() {
//            MyToast.info(getActivity(), R.string.wifi_open);
            wifiswitch.setIconEnabled(true);
        }

        @Override
        public void onWifiDisable() {
//            MyToast.info(getActivity(), R.string.wifi_close);
            wifiswitch.setIconEnabled(false);
        }

        @Override
        public void onScanResultAvailable() {

        }

        @Override
        public void onWifiConnected() {

        }

        @Override
        public void onWifiDisconnected() {

        }

        @Override
        public void onWifiSignChange() {

        }
    };
    private WifiHotUtil mWifiHotUtil;
    private  List<Fragment> mFragmentList=new ArrayList<>();
//    //定位需要的声明
//    private LocationListener mLocationListener;
//
//    private boolean mIsShowError;
//    private String mObjectId=null;
//    private boolean mIsPostHotspot;
    /**
     *   Tag
     */
    private static final String TAG = "WifiFragment";

    public static WifiFragment newInstance() {

        Bundle args = new Bundle();
        WifiFragment fragment = new WifiFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mLocationListener= (LocationListener) activity;
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mLocationListener=null;
//    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi,container,false);
    }

    @Override
    protected void initView(View view) {
        this.viewpager = (ViewPager) view.findViewById(R.id.view_pager);
        this.tablayout = (TabLayout) view.findViewById(R.id.tab_layout);
        this.toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.wifiswitch = (SwitchIconView) view.findViewById(R.id.siv_wifi);
        this.hotspotswitch=(SwitchIconView) view.findViewById(R.id.siv_hotspot);
    }

    @Override
    protected void setListener() {
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0) {
                    wifiswitch.setVisibility(View.VISIBLE);
                    hotspotswitch.setVisibility(View.GONE);
                }else {
                    wifiswitch.setVisibility(View.GONE);
                    hotspotswitch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        WiFiConnectService.startService(getActivity());
        WiFiConnectService.addWiFiConnectListener(mWifiConnectListener);

        //WiFi开关
        wifiswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiswitch.isIconEnabled()) {
                    mWifiManager.setWifiEnabled(false);
                    ((ConnectHotspotFragment)mFragmentList.get(0)).wifiDisable();
                }else {
                    mWifiManager.setWifiEnabled(true);
                    ((ConnectHotspotFragment)mFragmentList.get(0)).wifiOpening();
                }
                wifiswitch.setIconEnabled(!wifiswitch.isIconEnabled());
            }
        });

        //Hotspot开关
        hotspotswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ShareHotspotFragment) mFragmentList.get(1)).checkSystemWritePermission()) {
                    if (hotspotswitch.isIconEnabled()) {
                        mWifiHotUtil.closeWifiAp();
                        hotspotswitch.setIconEnabled(false);
                    } else {
                        ((ShareHotspotFragment) mFragmentList.get(1)).showConfigHotspotDialog();
                    }
                }
            }
        });

//        //热点监听
        WiFiAPService.startService(getActivity());
        WiFiAPService.addWiFiAPListener(new WiFiAPListener() {
            @Override
            public void stateChanged(int state) {
                switch (state) {
                    case WiFiAPListener.WIFI_AP_CLOSE_SUCCESS:
                        hotspotswitch.setIconEnabled(false);
//                        if (mIsPostHotspot) {
//                            //删除信息
//                            deleteHotspot();
//                        }
                        break;
                    case WiFiAPListener.WIFI_AP_OPEN_SUCCESS:
                        hotspotswitch.setIconEnabled(true);
//                        if (mIsPostHotspot) {
//                            Log.d(TAG, "stateChanged: 开始定位");
//                            //定位发布
//                            mLocationListener.startLoc();
//                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        //注意使用ApplicationContext
        mWifiManager= (WifiManager) getActivity().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        mWifiHotUtil=WifiHotUtil.getInstance(getActivity());
    }

    @Override
    protected void setData() {
        wifiswitch.setIconEnabled(mWifiManager.isWifiEnabled());
        hotspotswitch.setIconEnabled(mWifiHotUtil.getWifiAPState()== WIFI_AP_STATE_ENABLED);


        viewpager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager()));
//        viewpager.setOffscreenPageLimit(viewpager.getAdapter().getCount());
        // 设置ViewPager的数据等
        tablayout.setupWithViewPager(viewpager);
        //tab均分,适合少的tab
        tablayout.setTabMode(TabLayout.MODE_FIXED);

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

//    @Override
//    protected void onEventComing(EventCenter center) {
//        Object object=center.getData();
//        if (object instanceof WifiChangeEvent) {
//            wifiswitch.setIconEnabled(((WifiChangeEvent)object).isWifiEnable());
//        }
//    }


    //ViewPager适配器
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            addTabs(mFragmentList,mFragmentTitleList);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //添加tab
    private void addTabs(List<Fragment> fragments, List<String> titles){
        String stringArray[]=getResources().getStringArray(R.array.wifi_tabs);
        fragments.add(mConnectFragment= ConnectHotspotFragment.newInstance());
        titles.add(stringArray[0]);
        fragments.add(mShareHotspotFragment=ShareHotspotFragment.newInstance());
        titles.add(stringArray[1]);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WiFiConnectService.removeWiFiConnectListener(mWifiConnectListener);
    }

    //    //创建热点
//    private void showCreateApDialog(){
//        mIsPostHotspot=false;
//
//        View v=LayoutInflater.from(getActivity())
//                .inflate(R.layout.dialog_edit_ap,null);
//        final EditText editText = (EditText) v.findViewById(R.id.pass);
//
//        final TextInputLayout tl= (TextInputLayout) v.findViewById(R.id.tl);
//        final Switch sw=(Switch)v.findViewById(R.id.post_switch);
//
//
//        final EditText nameEt=(EditText) v.findViewById(R.id.name);
//        String ssid=mWifiHotUtil.getValidApSsid(),
//                special=getString(R.string.wifi_special);
//        nameEt.setText(ssid.endsWith(special)?ssid.replace(special,""):ssid);
//
//        AlertDialog dialog=new AlertDialog.Builder(getActivity())
//                .setView(v)
//                .setNegativeButton(android.R.string.cancel,null)
//                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mIsShowError=true;
//                        String psd=editText.getText().toString();
//                        String name=nameEt.getText().toString();
//                        if (!name.isEmpty()) {
//                            if (tl.getVisibility()== View.VISIBLE) {
//                                //有密码
//                                if (mIsPostHotspot=sw.isChecked()) {
//                                    Log.d(TAG, "onClick: 发布热点");
//                                    name+=getString(R.string.wifi_special);
//                                }
//                                mWifiHotUtil.turnOnWifiAp(name,psd, WifiHotUtil.WifiSecurityType.WIFICIPHER_WPA2);
//                            }else{
//                                mWifiHotUtil.turnOnWifiAp(name,null, WifiHotUtil.WifiSecurityType.WIFICIPHER_NOPASS);
//                            }
//                        }
//                    }
//                }).create();
//        dialog.show();
//
//        final Button posiBtn=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        ((Switch) v.findViewById(R.id.pass_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                tl.setVisibility(isChecked?View.VISIBLE:View.GONE);
//                sw.setVisibility(isChecked?View.VISIBLE:View.GONE);
//                posiBtn.setEnabled(!isChecked);
//            }
//        });
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                if (s.length()<8) {
////                    editText.setError(getString(R.string.password_not_enough));
////                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length()<8) {
//                    posiBtn.setEnabled(false);
//                }else {
//                    posiBtn.setEnabled(true);
//                }
//            }
//        });
//    }
//
//
//
//    public void locationChanged(AMapLocation amapLocation) {
//        Log.d(TAG, "locationChanged: 定位");
//        if (mWifiHotUtil.getWifiAPState()==WIFI_AP_STATE_ENABLED&&mIsPostHotspot) {
//            if (amapLocation != null) {
//                if (amapLocation.getErrorCode() == 0) {
//                    //定位成功回调信息，设置相关消息
//                    Log.d(TAG, "onLocationChanged: "+amapLocation.getLatitude()+"  "+amapLocation.getLongitude());
//                    if (mObjectId == null) {
//                        postHotspot(amapLocation);
//                    }else {
//                        updateHotspot(amapLocation);
//                    }
//                } else {
//                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                    Log.e("AmapError", "location Error, ErrCode:"
//                            + amapLocation.getErrorCode() + ", errInfo:"
//                            + amapLocation.getErrorInfo());
//                }
//            }
//        }
//    }
//
//
//
//    //发布wifi信息到服务器
//    private void postHotspot(AMapLocation amapLocation){
//        Log.d(TAG, "postHotspot: ");
//        //热点是否开启
//        if (mWifiHotUtil.getWifiAPState()==WIFI_AP_STATE_ENABLED) {
//            Hotspot hotspot=new Hotspot();
//            hotspot.setSsid(mWifiHotUtil.getValidApSsid());
//            hotspot.setPassword(mWifiHotUtil.getValidPassword());
//
//            String loc=amapLocation.getCity()+amapLocation.getDistrict()+
//                    amapLocation.getStreet()+amapLocation.getStreetNum();
//            hotspot.setLocation(loc);
//
//            hotspot.setUserId((String) BmobUser.getObjectByKey("objectId"));
//            hotspot.setPoint(new BmobGeoPoint(amapLocation.getLongitude(),amapLocation.getLatitude()));
//
//            hotspot.save(new SaveListener<String>() {
//                @Override
//                public void done(String s, BmobException e) {
//                    if(e==null){
//                        mObjectId=s;
//                        showSuccToast(R.string.post_succ);
//                    }else{
//                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                        if (mIsShowError) {
//                            mIsShowError=false;
//                            showErrorToast(R.string.post_error);
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    //更新服务器热点信息
//    private void updateHotspot(AMapLocation amapLocation){
//        Log.d(TAG, "updateHotspot: ");
//        Hotspot hotspot=new Hotspot();
//        hotspot.setSsid(mWifiHotUtil.getValidApSsid());
//        hotspot.setPassword(mWifiHotUtil.getValidPassword());
//
//        String loc=amapLocation.getCity()+amapLocation.getDistrict()+
//                amapLocation.getStreet()+amapLocation.getStreetNum();
//        hotspot.setLocation(loc);
//
//        hotspot.setPoint(new BmobGeoPoint(amapLocation.getLongitude(),amapLocation.getLatitude()));
//
//        hotspot.update(mObjectId, new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                Log.d(TAG, "done: 更新");
//            }
//        });
//    }
//
//    //删除热点信息
//    private void deleteHotspot(){
//        Log.d(TAG, "deleteHotspot: ");
//        if (mObjectId != null) {
//            Hotspot hotspot=new Hotspot();
//            hotspot.setObjectId(mObjectId);
//            hotspot.delete(new UpdateListener() {
//                @Override
//                public void done(BmobException e) {
//                    if (e==null) {
//                        mObjectId=null;
//                        Log.d(TAG, "done: 删除");
//                    }
//                }
//            });
//        }
//    }
}
