package com.lvqingyang.wifisharing.User.Setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.tools.MyDialog;

import cn.bmob.v3.BmobUser;
import frame.tool.MyToast;
import frame.view.SettingItem;

public class SettingActivity extends BaseActivity {

    private frame.view.SettingItem sidefaultwifitool;
    private frame.view.SettingItem sinotificationbar;
    private frame.view.SettingItem siautoturnonmobile;
    private frame.view.SettingItem siwifiblacklist;
    private frame.view.SettingItem siupdateremind;
    private frame.view.SettingItem simessageremind;
    private frame.view.SettingItem simessageremindnight;
    private frame.view.SettingItem simessageremindhotspot;
    private frame.view.SettingItem sihelp;
    private frame.view.SettingItem siaboutus;
    private android.widget.TextView tvlogout;

    public static Intent newIntent(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
//        starter.putExtra();
        return starter;
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.setting,true);
        this.tvlogout = findViewById(R.id.tv_logout);
        this.siaboutus = findViewById(R.id.si_about_us);
        this.sihelp = findViewById(R.id.si_help);
        this.simessageremindhotspot = findViewById(R.id.si_message_remind_hotspot);
        this.simessageremindnight = findViewById(R.id.si_message_remind_night);
        this.simessageremind = findViewById(R.id.si_message_remind);
        this.siupdateremind = findViewById(R.id.si_update_remind);
        this.siwifiblacklist = findViewById(R.id.si_wifi_blacklist);
        this.siautoturnonmobile = findViewById(R.id.si_auto_turn_on_mobile);
        this.sinotificationbar = findViewById(R.id.si_notification_bar);
        this.sidefaultwifitool = findViewById(R.id.si_default_wifi_tool);

        if(BmobUser.getCurrentUser() == null){
            tvlogout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        tvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        siaboutus.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.start(SettingActivity.this,
                        getString(R.string.about_us), getString(R.string.app_site));
            }
        });
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

    private void showLogoutDialog(){
        new MyDialog(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_sure)
                .setPosBtn(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BmobUser.logOut();   //清除缓存用户对象
                        MyToast.info(SettingActivity.this , R.string.logouted);
                        setResult(RESULT_OK);
                        tvlogout.setVisibility(View.GONE);
                    }
                })
                .setNegBtn(android.R.string.cancel,null)
                .show(getSupportFragmentManager());

    }
}
