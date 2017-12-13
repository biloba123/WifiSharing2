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
     * View
     */
    private com.github.zagum.switchicon.SwitchIconView wifiswitch;
    private com.github.zagum.switchicon.SwitchIconView hotspotswitch;
    private android.support.v7.widget.Toolbar toolbar;
    private android.support.design.widget.TabLayout tablayout;
    private android.support.v4.view.ViewPager viewpager;
    /**
     * Fragment
     */
    private ConnectHotspotFragment mConnectFragment;
    private ShareHotspotFragment mShareHotspotFragment;


    /**
     * Data
     */
    private WifiManager mWifiManager;//wifi开关
    private WifiConnectListener mWifiConnectListener = new WifiConnectListener() {
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
    private List<Fragment> mFragmentList = new ArrayList<>();
//    //定位需要的声明
//    private LocationListener mLocationListener;
//
//    private boolean mIsShowError;
//    private String mObjectId=null;
//    private boolean mIsPostHotspot;
    /**
     * Tag
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
        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    protected void initView(View view) {
        this.viewpager = view.findViewById(R.id.view_pager);
        this.tablayout = view.findViewById(R.id.tab_layout);
        this.toolbar = view.findViewById(R.id.toolbar);
        this.wifiswitch = view.findViewById(R.id.siv_wifi);
        this.hotspotswitch = view.findViewById(R.id.siv_hotspot);
    }

    @Override
    protected void setListener() {
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    wifiswitch.setVisibility(View.VISIBLE);
                    hotspotswitch.setVisibility(View.GONE);
                } else {
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
                    ((ConnectHotspotFragment) mFragmentList.get(0)).wifiDisable();
                } else {
                    mWifiManager.setWifiEnabled(true);
                    ((ConnectHotspotFragment) mFragmentList.get(0)).wifiOpening();
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
        mWifiManager = (WifiManager) getActivity().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        mWifiHotUtil = WifiHotUtil.getInstance(getActivity());
    }

    @Override
    protected void setData() {
        wifiswitch.setIconEnabled(mWifiManager.isWifiEnabled());
        hotspotswitch.setIconEnabled(mWifiHotUtil.getWifiAPState() == WIFI_AP_STATE_ENABLED);


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
            addTabs(mFragmentList, mFragmentTitleList);
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
    private void addTabs(List<Fragment> fragments, List<String> titles) {
        String stringArray[] = getResources().getStringArray(R.array.wifi_tabs);
        fragments.add(mConnectFragment = ConnectHotspotFragment.newInstance());
        titles.add(stringArray[0]);
        fragments.add(mShareHotspotFragment = ShareHotspotFragment.newInstance());
        titles.add(stringArray[1]);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WiFiConnectService.removeWiFiConnectListener(mWifiConnectListener);
    }
}