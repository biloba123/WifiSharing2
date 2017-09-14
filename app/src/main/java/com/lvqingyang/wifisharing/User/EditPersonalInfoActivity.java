package com.lvqingyang.wifisharing.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.bean.User;
import com.lvqingyang.wifisharing.view.CardItem;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import de.hdodenhof.circleimageview.CircleImageView;
import frame.base.BaseActivity;

public class EditPersonalInfoActivity extends BaseActivity {

    /**
     * view
     */
    private de.hdodenhof.circleimageview.CircleImageView civhead;
    private com.lvqingyang.wifisharing.view.CardItem cisex;
    private com.lvqingyang.wifisharing.view.CardItem cipwd;
    private CardItem cinick;
    private CardItem cibirthday;
    private CardItem ciaccount;

     /**
     * fragment
     */


     /**
     * data
     */
     private User mUser;

     /**
     * tag
     */
     private static final String TAG = "EditPersonalInfoActivit";

    public static void start(Context context) {
        Intent starter = new Intent(context, EditPersonalInfoActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    public static Intent newIntent(Context context) {
        Intent starter = new Intent(context, EditPersonalInfoActivity.class);
//        starter.putExtra();
        return starter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_personal_info;
    }

    @Override
    protected void initView() {
        initeActionbar(R.string.edit_personal_info,true);
        this.cibirthday = (CardItem) findViewById(R.id.ci_birthday);
        this.cinick = (CardItem) findViewById(R.id.ci_nick);
        this.ciaccount = (CardItem) findViewById(R.id.ci_account);
        this.cipwd = (CardItem) findViewById(R.id.ci_pwd);
        this.cisex = (CardItem) findViewById(R.id.ci_sex);
        this.civhead = (CircleImageView) findViewById(R.id.civ_head);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        mUser= BmobUser.getCurrentUser(User.class);

    }

    @Override
    protected void setData() {
        showUserInfo();
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

    /**
     * 显示用户信息
     */
    private void showUserInfo(){
        ciaccount.setHint(mUser.getMobilePhoneNumber());
        cinick.setHint(mUser.getNick());
        Boolean isMale=mUser.getSex();
        if (isMale != null) {
            cisex.setHint(isMale?"男":"女");
        }else {
            cisex.setHint(getString(R.string.not_edit));
        }

        BmobDate date=mUser.getBirthday();
        if (isMale != null) {
            cibirthday.setHint(date.getDate().substring(0,10));
        }else {
            cibirthday.setHint(getString(R.string.not_edit));
        }
    }
}
