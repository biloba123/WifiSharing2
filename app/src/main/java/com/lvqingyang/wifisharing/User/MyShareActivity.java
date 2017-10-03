package com.lvqingyang.wifisharing.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.tools.MyDialog;
import com.lvqingyang.wifisharing.bean.Hotspot;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import frame.tool.SolidRVBaseAdapter;

public class MyShareActivity extends BaseActivity {

    private RecyclerView mRvShare;
    private SolidRVBaseAdapter mAdapter;
    private List<Hotspot> mShareHotspots=new ArrayList<>();
    private static final String TAG = "MyShareActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, MyShareActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_share;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.my_share, true);
        mRvShare = (RecyclerView) findViewById(R.id.rv_share);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        mAdapter=new SolidRVBaseAdapter<Hotspot>(this, mShareHotspots) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, Hotspot bean) {
                holder.setText(R.id.tv_ssid, bean.getSsid());
                holder.setText(R.id.tv_bssid, bean.getBssid());
                holder.setText(R.id.tv_income, bean.getIncome()+"");
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_my_share;
            }

            @Override
            protected void onItemClick(int position, Hotspot bean) {
                super.onItemClick(position, bean);
                shareHotspotInfo(bean);
            }
        };
    }

    @Override
    protected void setData() {
        mRvShare.setLayoutManager(new LinearLayoutManager(this));
        mRvShare.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading();
        getHotspots();
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
        getHotspots();
    }

    private void getHotspots(){
        Hotspot.getUserShareHotspot(new FindListener<Hotspot>() {
            @Override
            public void done(List<Hotspot> list, BmobException e) {
                if (e == null) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "done: "+new Gson().toJson(list));
                    if (list != null && list.size()>0) {
                        loadComplete();
                        mAdapter.clearAllItems();
                        mAdapter.addItems(list);
                    }else {
                        showEmpty();
                    }
                }else {
                    loadFail();
                }
            }
        });
    }

    private void shareHotspotInfo(Hotspot h){
        View v=getLayoutInflater().inflate(R.layout.dialog_my_share_wifi, null);
        TextView tvincome = (TextView) v.findViewById(R.id.tv_income);
        TextView tvusecount = (TextView) v.findViewById(R.id.tv_use_count);
        TextView location = (TextView) v.findViewById(R.id.location);
        TextView tvmac = (TextView) v.findViewById(R.id.tv_mac);

        tvmac.setText(h.getBssid());
        location.setText(h.getLocation());
        tvusecount.setText(h.getUseCount()+"");
        tvincome.setText(h.getIncome()+"元");

        new MyDialog(this)
                .setTitle(h.getSsid())
                .setView(v)
                .setPosBtn(android.R.string.ok, null)
                .setNegBtn(R.string.cancel_share, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //取消分享先不做，本来就几条数据...  
                    }
                })
                .show(getSupportFragmentManager());
    }
}
