package com.lvqingyang.wifisharing.User;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.User;
import com.lvqingyang.wifisharing.view.CardItem;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import de.hdodenhof.circleimageview.CircleImageView;
import frame.tool.MyToast;

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
    private File mAvaterFile;

     /**
     * tag
     */
     private static final String TAG = "EditPersonalInfoActivit";
    private static final String FILE_AVATER = "avater.png";
    private RelativeLayout mRlAvater;
    private static final int CHOOSE_PHOTO = 588;
    private CircleImageView mCivHead;

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
        mRlAvater = (RelativeLayout) findViewById(R.id.ll_avater);
        mCivHead = (CircleImageView) findViewById(R.id.civ_head);
    }

    @Override
    protected void setListener() {
        mRlAvater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent("android.intent.action.GET_CONTENT");
                intent2.setType("image/*");
                startActivityForResult(intent2,CHOOSE_PHOTO);
            }
        });
    }

    @Override
    protected void initData() {
        mUser= BmobUser.getCurrentUser(User.class);
        mAvaterFile=new File(getFilesDir(), FILE_AVATER);
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CHOOSE_PHOTO:{
                if (resultCode==RESULT_OK){
                    UCrop.of(data.getData(), Uri.fromFile(mAvaterFile))
                            .withAspectRatio(1, 1)
                            .start(this);
                }
            }

            case UCrop.REQUEST_CROP:{
                if (resultCode == RESULT_OK ) {
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "onActivityResult: "+resultUri);
                        Glide.with(this)
                                .load(resultUri.getPath())
                                .into(civhead);
                    }else {
                        MyToast.error(this, R.string.error);
                    }

                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    if (BuildConfig.DEBUG) Log.d(TAG, "onActivityResult: "+cropError);
                    MyToast.error(this, R.string.error);
                }
            }
        }
    }


}
