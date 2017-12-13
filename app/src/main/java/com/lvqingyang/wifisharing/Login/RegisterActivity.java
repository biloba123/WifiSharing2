package com.lvqingyang.wifisharing.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import frame.tool.MyToast;

/**
 * Author：LvQingYang
 * Date：2017/8/25
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class RegisterActivity extends BaseActivity {

    /**
     * view
     */
    private android.widget.EditText ettel;
    private android.widget.EditText etpassword;
    private android.widget.Button btgo;
    private android.support.v7.widget.CardView cvadd;
    private android.support.design.widget.FloatingActionButton fab;
    private EditText etsmscode;
    private Button btnrequestcode;

     /**
     * fragment
     */


     /**
     * data
     */
     private CountDownTimer mCountDownTimer;

     /**
     * tag
     */
     private static final String TAG = "RegisterActivity";
    public static final String KEY_TEL = "TEL";
    public static final String KEY_PASSWORD = "PASSWORD";


    public static Intent newIntent(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
//        starter.putExtra();
        return starter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        this.btnrequestcode = findViewById(R.id.btn_request_code);
        this.etsmscode = findViewById(R.id.et_sms_code);
        this.ettel = findViewById(R.id.et_tel);
        this.fab = findViewById(R.id.fab);
        this.cvadd = findViewById(R.id.cv_add);
        this.btgo = findViewById(R.id.bt_go);
        this.etpassword = findViewById(R.id.et_password);
    }

    @Override
    protected void setListener() {
//        ettel.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animateRevealClose();
                }
            }
        });

        btnrequestcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCountDownTimer==null) {
                    if (checkInternet()) {
                        final String tel = ettel.getText().toString();
                        if (tel.length() < 11) {
                            ettel.setError(getString(R.string.wrong_tel));
                        } else {
                            sendSmsCode(tel);
                            mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
                                @Override
                                public void onTick(long l) {
                                    btnrequestcode.setText(l / 1000 + "秒");
                                }

                                @Override
                                public void onFinish() {
                                    mCountDownTimer = null;
                                    btnrequestcode.setText(R.string.request_sms_code);
                                }
                            };
                            mCountDownTimer.start();
                        }
                    }

                }
            }
        });

        btgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tel=ettel.getText().toString(),
                        pwd=etpassword.getText().toString(),
                        smsCode=etsmscode.getText().toString();
                if (vertify(tel, smsCode, pwd)) {
                    if (checkInternet()) {
                        MyToast.loading(RegisterActivity.this, R.string.registering);
                        User.register( tel, smsCode, pwd, new SaveListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                MyToast.cancel();
                                if(e==null){
                                    Log.d(TAG,"注册成功:" +user.toString());
                                    MyToast.success(RegisterActivity.this, R.string.register_succ, Toast.LENGTH_SHORT);
                                    setResult(tel,pwd);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        animateRevealClose();
                                    }else {
                                        finish();
                                    }
                                }else{
                                    Log.e(TAG,e.getMessage());
                                    MyToast.error(RegisterActivity.this, getString(R.string.register_error)+"："+e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    private boolean vertify(String tel,String smsCode,String pwd){
        if (tel.length()<11) {
            ettel.setError(getString(R.string.wrong_tel));
            return false;
        }

        if (smsCode.length()<4) {
            etsmscode.setError(getString(R.string.wrong_sms_code));
            return false;
        }

        if (TextUtils.isEmpty(pwd)||pwd.length()<6) {
            etpassword.setError(getString(R.string.pwd_length));
            return false;
        }

        return true;

    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[0];
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvadd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvadd, cvadd.getWidth()/2,0, fab.getWidth() / 2, cvadd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvadd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvadd,cvadd.getWidth()/2,0, cvadd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvadd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_add);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animateRevealClose();
        }
    }



    private void setResult(String tel,String pwd){
        Intent data=new Intent();
        data.putExtra(KEY_TEL,tel)
                .putExtra(KEY_PASSWORD,pwd);
        setResult(RESULT_OK,data);
    }

    private void sendSmsCode(final String tel){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("mobilePhoneNumber",tel)
                .findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> object, BmobException e) {
                        if (e == null) {
                            Log.d(TAG,"查询成功：共"+object.size()+"条数据。");
                            if (object==null||object.size()==0) {
                                BmobSMS.requestSMSCode(tel, "您的验证码是`%smscode%`，有效期为`%ttl%`分钟。您正在使用`%appname%`的验证码。", new QueryListener<Integer>() {
                                    @Override
                                    public void done(Integer smsId, BmobException ex) {
                                        if (ex == null) {//验证码发送成功
                                            Log.i("smile", "短信id：" + smsId);//用于查询本次短信发送详情
                                        } else {
                                            if (BuildConfig.DEBUG) Log.e(TAG, "done: "+ex.getMessage() );
                                            MyToast.error(RegisterActivity.this, R.string.send_sms_fail);
                                        }
                                    }
                                });
                            }else {
                                MyToast.info(RegisterActivity.this, R.string.tel_registered);
                                resetSendSmsBtn();
                            }
                        } else {
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            MyToast.error(RegisterActivity.this, R.string.send_sms_fail);
                            resetSendSmsBtn();
                        }
                    }
                });
    }

    private void resetSendSmsBtn(){
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer=null;
            btnrequestcode.setText(R.string.request_sms_code);
        }
    }
}
