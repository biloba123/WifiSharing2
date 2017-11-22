package com.lvqingyang.wifisharing.User.Wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.Record;

public class OrderActivity extends BaseActivity {

    private static final String KEY_RECORD = "RECORD";

    public static void start(Context context, Record record) {
        Intent starter = new Intent(context, OrderActivity.class);
        starter.putExtra(KEY_RECORD, (Parcelable) record);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    protected void initView() {

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
