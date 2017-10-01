package com.lvqingyang.wifisharing.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;

public class AboutUsActivity extends BaseActivity {


    private WebView mWebView;

    public static void start(Context context) {
        Intent starter = new Intent(context, AboutUsActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.about_us, true);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings =   mWebView .getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {
        mWebView.loadUrl("http://iwifi.zeblog.cn/");
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
