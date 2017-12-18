package com.lvqingyang.wifisharing.User.Wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.AppContact;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.PayWay;
import com.lvqingyang.wifisharing.bean.Record;
import com.lvqingyang.wifisharing.tools.MyDialog;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import frame.tool.MyToast;
import frame.tool.SolidRVBaseAdapter;

public class OrderActivity extends BaseActivity {

    private static final String TAG = "OrderActivity";
    private static final String KEY_RECORD = "RECORD";
    private static final String KEY_ORDER_ID = "ORDER_ID";
    private String mOrderId;
    private android.widget.TextView tvhotspotname;
    private android.widget.TextView tvtraffic;
    private android.widget.TextView tvprice;
    private android.widget.TextView tvpay;
    private android.support.v7.widget.RecyclerView rvpay;
    private android.widget.Button btnsurepay;

    private Record mRecord;

    private List<PayWay> mPayWayList=PayWay.getPayWays();
    private SolidRVBaseAdapter mAdapter;
    private int mCheckedPos=0;

    public static void start(Context context, Record record, String orderId) {
        Intent starter = new Intent(context, OrderActivity.class);
        starter.putExtra(KEY_RECORD, (Parcelable) record);
        starter.putExtra(KEY_ORDER_ID, orderId);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.order_detail, true);
        this.btnsurepay = findViewById(R.id.btn_sure_pay);
        this.rvpay = findViewById(R.id.rv_pay);
        this.tvpay = findViewById(R.id.tv_pay);
        this.tvprice = findViewById(R.id.tv_price);
        this.tvtraffic = findViewById(R.id.tv_traffic);
        this.tvhotspotname = findViewById(R.id.tv_hotspot_name);

    }

    @Override
    protected void setListener() {
        btnsurepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToast.loading(OrderActivity.this, R.string.paying);
                if (BuildConfig.DEBUG) Log.d(TAG, "onClick: "+mOrderId+" "+mRecord.getObjectId());
                Record.deleteRecord(mOrderId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            MyToast.cancel();
                            showRateDialog();
                        }else {
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            MyToast.cancel();
                            MyToast.error(OrderActivity.this, R.string.pay_fail);
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void initData() {
        mAdapter=new SolidRVBaseAdapter<PayWay>(this, mPayWayList) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, PayWay bean) {
                holder.setImage(R.id.iv_icon, bean.getIconId());
                holder.setText(R.id.tv_name, bean.getName());
                holder.setText(R.id.tv_hint, bean.getHint());
                if (bean.getName().equals("个人钱包")) {
                    holder.getView(R.id.iv_check).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_pay_way;
            }

            @Override
            protected void onItemClick(int position, PayWay bean) {
                super.onItemClick(position, bean);
                checkPayWay(position);
            }
        };

        mRecord=getIntent().getParcelableExtra(KEY_RECORD);
        mOrderId=getIntent().getStringExtra(KEY_ORDER_ID);
    }

    private void checkPayWay(int position) {
        if (position!=mCheckedPos) {
            rvpay.getChildAt(mCheckedPos).findViewById(R.id.iv_check).setVisibility(View.INVISIBLE);
            rvpay.getChildAt(position).findViewById(R.id.iv_check).setVisibility(View.VISIBLE);
            mCheckedPos=position;
        }
    }

    @Override
    protected void setData() {
        rvpay.setAdapter(mAdapter);
        rvpay.setLayoutManager(new LinearLayoutManager(this));

        tvhotspotname.setText(mRecord.getHotspot().getSsid());
        tvtraffic.setText(mRecord.getTraffic()+"M");
        float price=mRecord.getHotspot().isFixed()?AppContact.HOTSPOT_FIXED_PRICE:
                AppContact.HOTSPOT_PRICE;
        tvprice.setText(price+"/M");
        tvpay.setText(price*mRecord.getTraffic()+"元");

    }

    private void showRateDialog() {
        View view=getLayoutInflater().inflate(R.layout.dialog_pay_succ, null);
        new MyDialog(this)
                .setTitle(R.string.pay_succ)
                .setView(view)
                .setPosBtn(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .show(getSupportFragmentManager());
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

}
