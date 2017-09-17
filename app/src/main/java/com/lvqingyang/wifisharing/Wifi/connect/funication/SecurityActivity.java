package com.lvqingyang.wifisharing.Wifi.connect.funication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lvqingyang.wifisharing.R;

import frame.base.BaseActivity;

public class SecurityActivity extends BaseActivity {

    private android.widget.ImageView ivanim;

    public static void start(Context context) {
        Intent starter = new Intent(context, SecurityActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_security;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.check_security,true);
        this.ivanim = (ImageView) findViewById(R.id.iv_anim);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {
        Glide.with(this)
                .load(R.drawable.security_anim)
                .into(ivanim);
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
