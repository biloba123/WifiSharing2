package com.lvqingyang.wifisharing.User;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.User;
import com.lvqingyang.wifisharing.tools.MyDialog;
import com.lvqingyang.wifisharing.view.CardItem;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import frame.tool.MyToast;

import static com.lvqingyang.wifisharing.base.AppContact.getDateFromDatePicker;

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
        this.cibirthday = findViewById(R.id.ci_birthday);
        this.cinick = findViewById(R.id.ci_nick);
        this.ciaccount = findViewById(R.id.ci_account);
        this.cipwd = findViewById(R.id.ci_pwd);
        this.cisex = findViewById(R.id.ci_sex);
        this.civhead = findViewById(R.id.civ_head);
        mRlAvater = findViewById(R.id.ll_avater);
        mCivHead = findViewById(R.id.civ_head);
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

        cinick.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNickDialog();
            }
        });

        cisex.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetSexDialog();
            }
        });

        ciaccount.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToast.info(EditPersonalInfoActivity.this, R.string.tel_can_not_reset);
            }
        });

        cipwd.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPwdDialog();
            }
        });

        cibirthday.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetBirthdayDialog();
            }
        });
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
        if (mUser.getAvater()!=null) {
            Glide.with(this)
                    .load(mUser.getAvaterUrl())
                    .into(civhead);
        }

        ciaccount.setHint(mUser.getMobilePhoneNumber());
        cinick.setHint(mUser.getNick());
        Boolean isMale=mUser.getSex();
        if (isMale != null) {
            cisex.setHint(isMale?"男":"女");
        }else {
            cisex.setHint(getString(R.string.not_edit));
        }

        BmobDate date=mUser.getBirthday();
        if (date != null) {
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
                    deleteRecursive(getFilesDir());

                    Uri source=data.getData();
                    String sourceStr=source.toString();

                    UCrop.of(source,
                            Uri.fromFile(new File(getFilesDir(), sourceStr.substring(sourceStr.lastIndexOf('/')))))
                            .withAspectRatio(1, 1)
                            .start(this);
                }
            }

            case UCrop.REQUEST_CROP:{
                if (resultCode == RESULT_OK ) {
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "onActivityResult: "+resultUri.getPath());
                        //上传头像
                        postAvater(resultUri.getPath());

                    }

                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                    if (BuildConfig.DEBUG) Log.d(TAG, "onActivityResult: "+cropError);
                    MyToast.error(this, R.string.error);
                }
            }
        }
    }

    /**
     * 删除目录下所有文件
     * @param dir
     */
    void deleteRecursive(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    /**
     * 上传头像
     * @param path
     */
    private void postAvater(final String path){
        MyToast.loading(this, R.string.posting);
        final BmobFile bmobFile=new BmobFile(new File(path));
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User u=new User();
                    u.setAvater(bmobFile);
                    User.updateUser(u, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {//上传成功
                                MyToast.cancel();
                                MyToast.success(EditPersonalInfoActivity.this, R.string.post_succ);
                                //删除旧头像
                                if (mUser.getAvater()!=null) {
                                    deleteFile(mUser.getAvater());
                                }
                                //显示新头像
                                Glide.with(EditPersonalInfoActivity.this)
                                        .load(path)
                                        .into(civhead);
                                //更新本地数据
//                                User.syscUserInfo();
                                changedInfo();
                            }else {
                                MyToast.cancel();
                                MyToast.error(EditPersonalInfoActivity.this, R.string.post_fail);
                                deleteFile(bmobFile);
                                if (BuildConfig.DEBUG) Log.d(TAG, "update user done: "+e.toString());
                            }
                        }
                    });
                }else {
                    MyToast.cancel();
                    MyToast.error(EditPersonalInfoActivity.this, R.string.post_fail);
                    if (BuildConfig.DEBUG) Log.d(TAG, "done: "+e.toString());
                }
            }
        });
    }

    /**
     * 更改信息后调用
     */
    private void changedInfo(){
        mUser=BmobUser.getCurrentUser(User.class);//更新本地变量
        setResult(RESULT_OK);
    }

    /**
     * 删除文件
     * @param file
     */
    private void deleteFile(BmobFile file){
        file.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "done: delete succ");
                }else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "done: delete fail");
                }
            }
        });
    }

    /**
     * 修改昵称
     */
    private void showEditNickDialog(){
        View v=getLayoutInflater()
                .inflate(R.layout.dialog_reset_nick, null);
        final EditText et = v.findViewById(R.id.et);
        et.setText(mUser.getNick());

        final MyDialog dialog=new MyDialog(this)
                .setTitle(R.string.reset_nick)
                .setView(v)
                .setNegBtn(android.R.string.cancel, null)
                .setPosBtn(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newNick=et.getText().toString();
                        if (!TextUtils.equals(newNick, mUser.getNick())) {
                            User u=new User();
                            u.setNick(newNick);
                            User.updateUser(u, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        MyToast.success(EditPersonalInfoActivity.this, R.string.reset_succ);
                                        cinick.setHint(newNick);
                                        changedInfo();

                                    }else {
                                        MyToast.error(EditPersonalInfoActivity.this, R.string.reset_fail);
                                    }
                                }
                            });
                        }
                    }
                });
        dialog.show(getSupportFragmentManager());

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dialog.setPosBtnEnable(s.length()>0);
            }
        });

    }

    /**
     * 修改性别
     */
    private void showResetSexDialog(){
        View v=getLayoutInflater()
                .inflate(R.layout.dialog_reset_sex, null);
        final RadioGroup rg = v.findViewById(R.id.rg);

        int oldCheckedId=-1;
        if (mUser.getSex()!=null) {
            rg.check(oldCheckedId=(mUser.getSex()?R.id.rb_male : R.id.rb_female));
        }

        final int finalOldCheckedId = oldCheckedId;
        new MyDialog(this)
                .setTitle(R.string.reset_sex)
                .setView(v)
                .setNegBtn(android.R.string.cancel, null)
                .setPosBtn(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int checkedId=rg.getCheckedRadioButtonId();
                        if (checkedId!=-1) {
                            if (checkedId != finalOldCheckedId) {
                                User user=new User();
                                user.setSex(checkedId == R.id.rb_male);
                                User.updateUser(user, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            MyToast.success(EditPersonalInfoActivity.this, R.string.reset_succ);
                                            cisex.setHint(checkedId == R.id.rb_male?"男" : "女");
                                            changedInfo();
                                        }else {
                                            MyToast.error(EditPersonalInfoActivity.this, R.string.reset_fail);
                                        }
                                    }
                                });
                            }
                        }
                    }
                })
                .show(getSupportFragmentManager());
    }

    /**
     * 修改密码
     */
    private void showResetPwdDialog(){
        View v=getLayoutInflater()
                .inflate(R.layout.dialog_reset_password, null);

        final EditText etnewpwd = v.findViewById(R.id.et_new_pwd);
        final EditText etcurrentpwd = v.findViewById(R.id.et_current_pwd);

        final MyDialog dialog=new MyDialog(this)
                .setTitle(R.string.reset_pwd)
                .setView(v)
                .setNegBtn(android.R.string.cancel, null)
                .setPosBtn(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPwd=etcurrentpwd.getText().toString(),
                                newPwd=etnewpwd.getText().toString();
                        BmobUser.updateCurrentUserPassword(oldPwd, newPwd, new UpdateListener() {

                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    if (BuildConfig.DEBUG) Log.d(TAG, "done: 密码修改成功，可以用新密码进行登录啦");
                                    MyToast.success(EditPersonalInfoActivity.this, R.string.reset_succ);
                                    changedInfo();
                                }else{
                                    MyToast.error(EditPersonalInfoActivity.this, R.string.reset_fail);
                                    if (BuildConfig.DEBUG) Log.d(TAG, "done: 失败:" + e.getMessage());
                                }
                            }

                        });
                    }
                });
        dialog.show(getSupportFragmentManager());

        dialog.setPosBtnEnable(false);

        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dialog.setPosBtnEnable(s.length()>=6);
            }
        };

        etcurrentpwd.addTextChangedListener(textWatcher);
        etnewpwd.addTextChangedListener(textWatcher);


    }

    /**
     * 修改生日
     */
    private void showResetBirthdayDialog(){
        View view=getLayoutInflater()
                .inflate(R.layout.dialog_reset_birthday, null);
        final DatePicker dp = view.findViewById(R.id.dp);

        dp.setMaxDate(new Date().getTime());


        AlertDialog dialog=new AlertDialog.Builder(this)
                .setTitle(R.string.set_birthday)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        User user=new User();
                        final BmobDate bd=new BmobDate(getDateFromDatePicker(dp));
                        user.setBirthday(bd);
                        User.updateUser(user, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    if (BuildConfig.DEBUG) Log.d(TAG, "done: 密码修改成功，可以用新密码进行登录啦");
                                    MyToast.success(EditPersonalInfoActivity.this, R.string.reset_succ);
                                    cibirthday.setHint(bd.getDate().substring(0,10));
                                    changedInfo();
                                }else{
                                    MyToast.error(EditPersonalInfoActivity.this, R.string.reset_fail);
                                    if (BuildConfig.DEBUG) Log.d(TAG, "done: 失败:" + e.getMessage());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.cancel,null)
                .create();

        dialog.show();

    }

}
