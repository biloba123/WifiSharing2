package com.lvqingyang.wifisharing.Wifi.connect.funcation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SecurityActivity extends BaseActivity {

    private android.widget.ImageView ivanim;
    private String[] mChecks=new String[]{
//            "正在检查WiFi模块配置",
            "可以正常访问互联网",
            "WiFi已加密",
            "未受到ARP攻击",
            "未受到DNS攻击",
            "非钓鱼WiFi",
    };
    private static final int MAX_COUNT = 4;

    private LinearLayout mLlCheck;
    private View mLastView;
    private boolean mIsGoResult=false;

    public static void start(Context context) {
        Intent starter = new Intent(context, SecurityActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        mIsGoResult=true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_security;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.check_security,true);
        this.ivanim = (ImageView) findViewById(R.id.iv_anim);
        mLlCheck = (LinearLayout) findViewById(R.id.ll_check);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {
        Glide.with(this)
                .load(R.drawable.security_anim)
                .into(ivanim);

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int len=mChecks.length;
                try {
                    for (int i = 0; i < len; i++) {
                        subscriber.onNext(i);
                        Thread.sleep(1000);
                    }
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        checkDone();
                        if (mIsGoResult) {
                            ResultActivity.start(SecurityActivity.this, ResultActivity.RESULT_SECURITY);
                        }
                        finish();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        if (i==MAX_COUNT) {
                            mLlCheck.removeViewAt(0);
                        }

                        if (mLastView!=null) {
                            checkDone();
                        }

                        View view=getLayoutInflater().inflate(R.layout.item_check,null);
                        ImageView iv=view.findViewById(R.id.iv_indicator);
                        TextView tv=view.findViewById(R.id.tv_item);
                        AnimationDrawable ad= (AnimationDrawable) iv.getDrawable();
                        ad.start();
                        tv.setText(mChecks[i]);
                        mLlCheck.addView(view);

                        mLastView=view;
                    }
                });
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

    private void checkDone(){
        ImageView iv=mLastView.findViewById(R.id.iv_indicator);
        TextView tv=mLastView.findViewById(R.id.tv_item);
        AnimationDrawable ad= (AnimationDrawable) iv.getDrawable();
        ad.stop();
        iv.setImageResource(R.drawable.indicator_done);
        tv.setTextColor(getResources().getColor(R.color.sub_text_color_light));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsGoResult=false;
    }
}
