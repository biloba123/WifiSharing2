package com.lvqingyang.wifisharing.Login;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.MainActivity;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import com.lvqingyang.wifisharing.base.BaseActivity;
import frame.tool.MyPrefence;
import frame.tool.MyToast;

/**
 * Author：LvQingYang
 * Date：2017/8/22
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class LoginActivity extends BaseActivity {

    /**
     * view
     */
    private android.widget.EditText etusername;
    private android.widget.EditText etpassword;
    private android.widget.Button btgo;
    private android.widget.TextView forgetpwd;
    private android.support.design.widget.FloatingActionButton fab;

     /**
     * fragment
     */


     /**
     * data
     */
     private MyPrefence mMyPrefence;

     /**
     * tag
     */
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_REGISTER = 632;


    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    public static Intent newIntent(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
//        starter.putExtra();
        return starter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            // 允许用户使用应用
            MainActivity.start(this);
            finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        this.forgetpwd = (TextView) findViewById(R.id.forget_pwd);
        this.btgo = (Button) findViewById(R.id.bt_go);
        this.etpassword = (EditText) findViewById(R.id.et_password);
        this.etusername = (EditText) findViewById(R.id.et_username);
    }

    @Override
    protected void setListener() {
        //跳转注册页面
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setExitTransition(null);
                    getWindow().setEnterTransition(null);


                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
                    startActivityForResult(RegisterActivity.newIntent(LoginActivity.this), REQUEST_REGISTER,options.toBundle());
                }else {
                    startActivityForResult(RegisterActivity.newIntent(LoginActivity.this),REQUEST_REGISTER);
                }
            }
        });

        //登录
        btgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=etusername.getText().toString(),
                        pwd=etpassword.getText().toString();
                if (verify(userName,pwd)) {
                    if (checkInternet()) {
                        MyToast.loading(LoginActivity.this, R.string.logining);
                        User.login(userName, pwd, new LogInListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                MyToast.cancel();
                                if (user != null) {
                                    if (BuildConfig.DEBUG) Log.d(TAG, "done: login succ");
                                    MyToast.success(LoginActivity.this, getString(R.string.login_succ),Toast.LENGTH_SHORT);
                                    setResult(RESULT_OK);
                                    finish();
                                }else {
                                    if (BuildConfig.DEBUG) Log.d(TAG, "done: login fail");
                                    MyToast.error(LoginActivity.this, getString(R.string.login_fail)+"："+e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                }
            }
        });

        //忘记密码
        forgetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgetPwdDialog();
            }
        });
    }

    @Override
    protected void initData() {
        mMyPrefence=MyPrefence.getInstance(this);
    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{
        };
    }

    /**
     * 验证输入
     * @param userName
     * @param pwd
     * @return
     */
    private boolean verify(String userName, String pwd){
        if (TextUtils.isEmpty(userName)) {
            etusername.setError(getString(R.string.empty_user));
            return false;
        }

        if (TextUtils.isEmpty(pwd)) {
            etpassword.setError(getString(R.string.empty_pwd));
            return false;
        }

        return true;
    }

    /**
     * 忘记密码
     */
    private void showForgetPwdDialog(){
//        View v=getLayoutInflater().inflate(R.layout.dialog_edit_email,null);
//        //inite view
//        final EditText etEmail=v.findViewById(R.id.et_email);
//
//        AlertDialog dialog=new AlertDialog.Builder(this)
//                .setView(v)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (checkInternet()) {
//                            final String email = etEmail.getText().toString();
//                            BmobUser.resetPasswordByEmail(email, new UpdateListener() {
//
//                                @Override
//                                public void done(BmobException e) {
//                                    if(e==null){
//                                        MyToast.success(LoginActivity.this, "重置密码请求成功，请到" + email + "邮箱进行密码重置操作",Toast.LENGTH_SHORT);
//                                    }else{
//                                        Log.d(TAG,"失败:" + e.getMessage());
//                                        MyToast.error(LoginActivity.this, e.getMessage(),Toast.LENGTH_SHORT);
//                                    }
//                                }
//                            });
//                        }
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel,null)
//                .create();
//        dialog.show();
//
//        final Button btnPos=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        btnPos.setEnabled(false);
//
//        etEmail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                btnPos.setEnabled(MyUser.emailFormat(charSequence.toString()));
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_REGISTER) {
            if (resultCode==RESULT_OK) {//获取注册成功信息直接填入
                etusername.setText(data.getStringExtra(RegisterActivity.KEY_TEL));
                etpassword.setText(data.getStringExtra(RegisterActivity.KEY_PASSWORD));
            }
        }
    }
}
