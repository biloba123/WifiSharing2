package com.lvqingyang.wifisharing.User.Credit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

public class CreditActivity extends BaseActivity {

    private CreditSesameView mCreditSesameView;

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
        mCreditSesameView = findViewById(R.id.sesame_view);
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
        onRetryClick();
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
        User.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    loadComplete();
                    showUserInfo();
                }else {
                    loadFail();
                }
            }
        });
    }

    private void showUserInfo(){
        User user= BmobUser.getCurrentUser(User.class);
        mCreditSesameView.setSesameValues(user.getCredit());
    }
}
