package com.lvqingyang.wifisharing.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;

public class CreditActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, CreditActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_credit;
    }

    @Override
    protected void initView() {
        initToolbar(getString(R.string.my_credit), true);
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
    protected void onResume() {
        super.onResume();
        loading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadComplete();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
