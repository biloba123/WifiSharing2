package com.lvqingyang.wifisharing.User.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.User.Setting.WebViewActivity;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.Message;
import com.lvqingyang.wifisharing.tools.BottomDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;
import frame.tool.SolidRVBaseAdapter;

public class MessageActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private SolidRVBaseAdapter mAdapter;
    private List<Message> mMessageList=new ArrayList<>();

    public static int[] mColorArr=new int[]{
            R.color.accent_red,
            R.color.accent_pink,
            R.color.accent_purple,
            R.color.accent_deep_purple,
            R.color.accent_indago,
            R.color.accent_blue,
            R.color.accent_cyan,
            R.color.accent_teal,
            R.color.accent_green,
            R.color.accent_amber,
            R.color.accent_orange,
            R.color.accent_brown,
            R.color.accent_grey,
            R.color.accent_black,
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, MessageActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.my_message, true);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_msg);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        onRetryClick();

        mAdapter=new SolidRVBaseAdapter<Message>(this, mMessageList) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, Message bean) {
                holder.setText(R.id.tv_title, bean.getTitle());
                holder.setText(R.id.tv_preview, bean.getContent().trim());
                holder.setText(R.id.tv_time, bean.getCreatedAt().substring(5,16));


                CircleImageView civ=holder.getView(R.id.civ_bg);
                civ.setImageResource(mColorArr[new Random().nextInt(mColorArr.length)]);
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_message;
            }

            @Override
            protected void onItemClick(int position, Message bean) {
                super.onItemClick(position, bean);
                if (bean.getWebPage()) {
                    WebViewActivity.start(MessageActivity.this,
                            bean.getTitle(), bean.getUrl());
                }else {
                    MessageDetailActivity.start(MessageActivity.this, bean);
                }
            }
        };
    }

    @Override
    protected void setData() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

    @Override
    protected void onRetryClick() {
        super.onRetryClick();
        Message.getMessages(new FindListener<Message>() {
            @Override
            public void done(List<Message> list, BmobException e) {
                if (e == null) {
                    if (list!=null && list.size()>0) {
                        loadComplete();
                        mAdapter.clearAllItems();
                        mAdapter.addItems(list);
                    } else {
                        showEmpty();
                    }
                }else {
                    loadFail();
                }
            }
        });
    }

    private void showMessageDetailDialog(Message message){
        View view=getLayoutInflater()
                .inflate(R.layout.dialog_message_detail, null);
        TextView tvcontent = (TextView) view.findViewById(R.id.tv_content);
        TextView tvtitle = (TextView) view.findViewById(R.id.tv_title);

        tvtitle.setText(message.getTitle());
        tvcontent.setText(message.getContent());

        new BottomDialog(this)
                .setView(view)
                .show(getSupportFragmentManager());

    }
}
