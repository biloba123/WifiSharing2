package com.lvqingyang.wifisharing.Wifi.connect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.lvqingyang.wifisharing.R;
import com.white.progressview.CircleProgressView;

import com.lvqingyang.wifisharing.base.BaseActivity;

public class ScanResultInfoActivity extends BaseActivity {

    private com.white.progressview.CircleProgressView circleprogressview;
    private android.widget.TextView tvsign;
    private android.net.wifi.ScanResult mScanResult;

    private static final String KEY_SCAN_RESULT = "SCAN_RESULT";
    private static final int SIGN_GREAT = 85;
    private static final int SIGN_GOOD = 50;
    private static final String TAG = "ScanResultInfoActivity";
    private TextView mTvMac;
    private TextView mTvEncrypt;

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
        this.tvsign = findViewById(R.id.tv_sign);
        this.circleprogressview = findViewById(R.id.circle_progress_view);
        mTvMac = findViewById(R.id.tv_mac);
        mTvEncrypt = findViewById(R.id.tv_encrypt);
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
            color= Color.parseColor("#46bf16");
            tvsign.setText("信号：极佳");
        }else if (sign>=SIGN_GOOD) {
            color=Color.parseColor("#2272eb");
            tvsign.setText("信号：优");
        }else {
            color=Color.parseColor("#fb5c50");
            tvsign.setText("信号：一般");
        }
        circleprogressview.setReachBarColor(color);
        circleprogressview.setTextColor(color);
        tvsign.setTextColor(color);
        circleprogressview.setProgressInTime(0,sign,2000*sign/100);
        mTvMac.setText(mScanResult.BSSID);

        String capabilities=mScanResult.capabilities
                .replace("[WPS]","").replace("[ESS]","").replace("[P2P]","");
        mTvEncrypt.setText(TextUtils.isEmpty(capabilities)?"NONE":capabilities);
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
