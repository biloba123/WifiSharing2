package com.lvqingyang.wifisharing.User.Setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;

public class WebViewActivity extends BaseActivity {


    private WebView mWebView;
    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_URL = "URL";

    public static void start(Context context, String title, String url) {
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(KEY_TITLE, title);
        starter.putExtra(KEY_URL, url);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        initeActionbar(getIntent().getStringExtra(KEY_TITLE), true);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings =   mWebView .getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {
        mWebView.loadUrl(getIntent().getStringExtra(KEY_URL));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
