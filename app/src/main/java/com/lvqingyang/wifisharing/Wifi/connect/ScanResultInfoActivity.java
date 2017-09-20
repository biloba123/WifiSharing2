package com.lvqingyang.wifisharing.Wifi.connect;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

import com.lvqingyang.wifisharing.R;
import com.white.progressview.CircleProgressView;

import frame.base.BaseActivity;

public class ScanResultInfoActivity extends BaseActivity {

    private com.white.progressview.CircleProgressView circleprogressview;
    private android.widget.TextView tvsign;
    private android.net.wifi.ScanResult mScanResult;

    private static final String KEY_SCAN_RESULT = "SCAN_RESULT";
    private static final int SIGN_GREAT = 85;
    private static final int SIGN_GOOD = 50;
    private static final String TAG = "ScanResultInfoActivity";
    private TextView mTvMac;

    public static void start(Context context, android.net.wifi.ScanResult s) {
        Intent starter = new Intent(context, ScanResultInfoActivity.class);
        starter.putExtra(KEY_SCAN_RESULT,s);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_result_info;
    }

    @Override
    protected void initView() {
        mScanResult=getIntent().getParcelableExtra(KEY_SCAN_RESULT);
        initeActionbar(mScanResult.SSID,true);
        this.tvsign = (TextView) findViewById(R.id.tv_sign);
        this.circleprogressview = (CircleProgressView) findViewById(R.id.circle_progress_view);
        mTvMac = (TextView) findViewById(R.id.tv_mac);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {
        int sign= WifiManager.calculateSignalLevel(mScanResult.level,100);
        int color;
        if (sign>=SIGN_GREAT) {
            color=getResources().getColor(android.R.color.holo_green_light);
            tvsign.setText("信号：极佳");
        }else if (sign>=SIGN_GOOD) {
            color=getResources().getColor(android.R.color.holo_blue_light);
            tvsign.setText("信号：优");
        }else {
            color=getResources().getColor(android.R.color.holo_red_light);
            tvsign.setText("信号：一般");
        }
        circleprogressview.setReachBarColor(color);
        circleprogressview.setTextColor(color);
        tvsign.setTextColor(color);
        circleprogressview.setProgressInTime(0,sign,2000*sign/100);
        mTvMac.setText(mScanResult.BSSID);
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
