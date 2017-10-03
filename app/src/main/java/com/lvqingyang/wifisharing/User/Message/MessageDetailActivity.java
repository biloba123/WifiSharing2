package com.lvqingyang.wifisharing.User.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.Message;

public class MessageDetailActivity extends BaseActivity {

    private android.widget.TextView tvtitle;
//    private android.widget.TextView tvtime;
    private android.widget.TextView tvcontent;
    private Message mMsg;
    private static final String KEY_MESSAGE = "MESSAGE";

    public static void start(Context context, Message msg) {
        Intent starter = new Intent(context, MessageDetailActivity.class);
        starter.putExtra(KEY_MESSAGE, msg);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_detail;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.msg_detail, true);
        this.tvcontent = (TextView) findViewById(R.id.tv_content);
//        this.tvtime = (TextView) findViewById(R.id.tv_time);
        this.tvtitle = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        mMsg= (Message) getIntent().getSerializableExtra(KEY_MESSAGE);
    }

    @Override
    protected void setData() {
        if (mMsg!=null) {
            tvtitle.setText(mMsg.getTitle());
            tvcontent.setText(mMsg.getContent());
//            tvtime.setText(mMsg.getCreatedAt().substring(5, 16));
        }
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }
}
