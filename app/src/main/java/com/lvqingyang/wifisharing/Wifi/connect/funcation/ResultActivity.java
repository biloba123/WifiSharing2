package com.lvqingyang.wifisharing.Wifi.connect.funcation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;

public class ResultActivity extends BaseActivity {

    private android.widget.ImageView ivdone;
    private android.widget.TextView tvtitle;
    private android.widget.TextView tvinfo;
    private android.widget.LinearLayout llsecurity;
    private android.widget.LinearLayout llsign;
    private android.widget.LinearLayout llspeed;
    public static final int RESULT_SECURITY = 341;
    public static final int RESULT_SIGNAL = 886;
    public static final int RESULT_SPEED = 916;
    private static final String KEY_WHICH = "WHICH";
    private static final String KEY_INFO = "INFO";



    public static void start(Context context, int which, String info) {
        Intent starter = new Intent(context, ResultActivity.class);
        starter.putExtra(KEY_WHICH, which);
        starter.putExtra(KEY_INFO, info);

        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_result;
    }

    @Override
    protected void initView() {
        this.llspeed = findViewById(R.id.ll_speed);
        this.llsign = findViewById(R.id.ll_sign);
        this.llsecurity = findViewById(R.id.ll_security);
        this.tvinfo = findViewById(R.id.tv_info);
        this.tvtitle = findViewById(R.id.tv_title);
        this.ivdone = findViewById(R.id.iv_done);
    }

    @Override
    protected void setListener() {
        llsecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecurityActivity.start(ResultActivity.this);
                finish();
            }
        });

        llsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignActivity.start(ResultActivity.this);
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {
        Intent data=getIntent();
        int actionTitle, title, info, img;
        actionTitle=title=info=R.string.app_name;
        img=R.mipmap.ic_launcher;


        switch (data.getIntExtra(KEY_WHICH, -1)) {
            case RESULT_SECURITY:{
                llsecurity.setVisibility(View.GONE);
                actionTitle=R.string.check_security;
                title=R.string.security_ok;
                info=R.string.just_use;
                img=R.drawable.security_result_ok;
                break;
            }
            case RESULT_SIGNAL:{
                llsign.setVisibility(View.GONE);
                actionTitle=R.string.enhance_sign;
                title=R.string.sign_99;
                info=R.string.sign_enhanced;
                img=R.drawable.activity_signal_enhance_logo_done;
                break;
            }
            case RESULT_SPEED:{
                llspeed.setVisibility(View.GONE);
                actionTitle=R.string.enhance_sign;
                title=R.string.test_speed_finish;
                img=R.drawable.speed_test_result_finish;
                break;
            }
            default:{
                break;
            }
        }

        initeActionbar(actionTitle, true);
        tvtitle.setText(title);
        if (R.string.app_name==info) {
            tvinfo.setText(getIntent().getStringExtra(KEY_INFO));
        }else {
            tvinfo.setText(info);
        }
        Glide.with(this)
                .load(img)
                .into(ivdone);
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
