package com.lvqingyang.wifisharing.Wifi.connect.funication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lvqingyang.wifisharing.R;
import com.skyfishjy.library.RippleBackground;

import frame.base.BaseActivity;

public class SignActivity extends BaseActivity {

    private com.skyfishjy.library.RippleBackground rb;

    public static void start(Context context) {
        Intent starter = new Intent(context, SignActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        rb.startRippleAnimation();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.enhance_sign, true);
        this.rb = (RippleBackground) findViewById(R.id.rb);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
