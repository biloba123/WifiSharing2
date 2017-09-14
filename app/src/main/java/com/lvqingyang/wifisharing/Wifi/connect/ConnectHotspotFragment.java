package com.lvqingyang.wifisharing.Wifi.connect;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseFragment;
import com.lvqingyang.wifisharing.tool.WifiAdmin;

import frame.tool.SolidRVBaseAdapter;


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
 * Date：2017/3/18
 * Email：biloba12345@gamil.com
 * Info：
 */


public class ConnectHotspotFragment extends BaseFragment {

    /**
     * view
     */
    private android.widget.Switch swsharewifi;
    private android.widget.LinearLayout llnowifi;
    private android.support.v7.widget.RecyclerView rvwifi;
    private android.support.v4.widget.SwipeRefreshLayout srl;
    private View mLayNoWifi;

     /**
     * fragment
     */


     /**
     * data
     */
     private WifiAdmin mWifiAdmin;
    private SolidRVBaseAdapter mAdapter;
    private WifiConnectListener mWifiConnectListener=new WifiConnectListener() {
        @Override
        public void onWifiEnable() {
            srl.setEnabled(true);
            srl.setRefreshing(true);
            mWifiAdmin.scan();
        }

        @Override
        public void onWifiDisable() {
            srl.setEnabled(false);
        }

        @Override
        public void onScanResultAvailable() {
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

        }

        @Override
        public void onWifiDisconnected() {

        }
    };

     /**
     * tag
     */
     private static final String TAG = "ConnectHotspotFragment";
    private ImageView mIvState;
    private TextView mTvState;
    private Button mBtnOpenWifi;

    public static ConnectHotspotFragment newInstance() {
         
         Bundle args = new Bundle();
         
         ConnectHotspotFragment fragment = new ConnectHotspotFragment();
         fragment.setArguments(args);
         return fragment;
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
        mLayNoWifi = view.findViewById(R.id.layout_wifi_disable);
        mIvState = (ImageView) view.findViewById(R.id.iv_state);
        mTvState = (TextView) view.findViewById(R.id.tv_state);
        mBtnOpenWifi = (Button) view.findViewById(R.id.btn_open_wifi);
    }

    @Override
    protected void setListener() {
        WiFiConnectService.startService(getActivity());
        WiFiConnectService.addWiFiConnectListener(mWifiConnectListener);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWifiAdmin.scan();
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
            protected void onItemClick(int position, ScanResult bean) {
                super.onItemClick(position, bean);
                if (BuildConfig.DEBUG) Log.d(TAG, "onItemClick: "+position);
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

    private void wifiDisable(){
        srl.setVisibility(View.GONE);
        mLayNoWifi.setVisibility(View.VISIBLE);
        mIvState.setImageResource(R.drawable.home_default_icon);
        mTvState.setText(R.string.wifi_close_);
        mBtnOpenWifi.setVisibility(View.VISIBLE);
    }

    private void wifiOpening(){
        
    }
}
