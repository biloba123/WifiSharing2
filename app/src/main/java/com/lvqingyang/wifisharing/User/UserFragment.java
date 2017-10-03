package com.lvqingyang.wifisharing.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.lvqingyang.wifisharing.Login.LoginActivity;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.User.Credit.CreditActivity;
import com.lvqingyang.wifisharing.User.Message.MessageActivity;
import com.lvqingyang.wifisharing.User.Setting.SettingActivity;
import com.lvqingyang.wifisharing.base.AppContact;
import com.lvqingyang.wifisharing.base.BaseFragment;
import com.lvqingyang.wifisharing.bean.User;
import com.lvqingyang.wifisharing.view.CardItem;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;
import frame.tool.MyToast;
import frame.tool.NetWorkUtils;

/**
 * Author：LvQingYang
 * Date：2017/8/26
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class UserFragment extends BaseFragment {

    /**
     * view
     */
    private Toolbar mToolbar;
    private de.hdodenhof.circleimageview.CircleImageView civhead;
    private android.widget.TextView tvusername;
    private android.widget.TextView tvcredit;
    private android.widget.LinearLayout llemail;
    private android.widget.RelativeLayout rluserinfo;
    private com.lvqingyang.wifisharing.view.CardItem cifeedback;
    private com.lvqingyang.wifisharing.view.CardItem ciupdate;
    private com.lvqingyang.wifisharing.view.CardItem ciscore;
    private com.lvqingyang.wifisharing.view.CardItem sisharefriend;

    private Toolbar toolbar;
    private CardItem ciservicecenter;
    private ImageView mIvWifiSign;
    private TextView mTvDay;
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
     private static final int REQUEST_LOGIN = 0;
    private static final int REQUEST_SETTING = 578;
    private static final int REQUEST_EDIT_INFO = 62;
    private static final String TAG = "UserFragment";
    private CardItem mCiMessage;
    private CardItem mCiMyShare;
    private CardItem mCiWallet;
    private CardItem mCiCredit;

    public static UserFragment newInstance() {

        Bundle args = new Bundle();

        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_uesr,container,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AnimationDrawable)mIvWifiSign.getDrawable()).start();
    }

    @Override
    protected void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        initToolbar(mToolbar,getString(R.string.title_my),false);
        this.sisharefriend = (CardItem) view.findViewById(R.id.si_share_friend);
        this.ciscore = (CardItem) view.findViewById(R.id.ci_score);
        this.ciupdate = (CardItem) view.findViewById(R.id.ci_update);
        this.cifeedback = (CardItem) view.findViewById(R.id.ci_feedback);
        this.rluserinfo = (RelativeLayout) view.findViewById(R.id.rl_user_info);
        this.llemail = (LinearLayout) view.findViewById(R.id.ll_email);
        this.tvcredit = (TextView) view.findViewById(R.id.tv_credit);
        this.tvusername = (TextView) view.findViewById(R.id.tv_username);
        this.civhead = (CircleImageView) view.findViewById(R.id.civ_head);
        this.ciservicecenter = (CardItem) view.findViewById(R.id.ci_service_center);
        mIvWifiSign = (ImageView) view.findViewById(R.id.iv_wifi_sign);
        mTvDay = (TextView) view.findViewById(R.id.tv_day);
        mCiMessage = (CardItem) view.findViewById(R.id.ci_message);
        mCiMyShare = (CardItem) view.findViewById(R.id.ci_my_share);
        mCiWallet = (CardItem) view.findViewById(R.id.ci_my_wallet);
        mCiCredit = (CardItem) view.findViewById(R.id.ci_credit);
    }

    @Override
    protected void setListener() {
        rluserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser != null) {
                    //修改信息页面
                    startActivityForResult(EditPersonalInfoActivity.newIntent(getContext()),REQUEST_EDIT_INFO);
                }else {
                    startActivityForResult(LoginActivity.newIntent(getContext()),REQUEST_LOGIN);
                }
            }
        });

        mCiWallet.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogined()) {
                    WalletActivity.start(getActivity());
                }
            }
        });

        mCiMessage.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogined()) {
                    MessageActivity.start(getActivity());
                }
            }
        });

        mCiMyShare.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogined()) {
                    MyShareActivity.start(getActivity());
                }
            }
        });

        mCiCredit.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogined()) {
                    CreditActivity.start(getActivity());
                }
            }
        });

        ciservicecenter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Instabug.invoke(InstabugInvocationMode.CHATS_LIST);
            }
        });

        cifeedback.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Instabug.invoke(InstabugInvocationMode.NEW_CHAT);
            }
        });

        ciupdate.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetWorkUtils.isNetworkConnected(getActivity())) {
                    MyToast.loading(getActivity(), R.string.checking);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }finally {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyToast.cancel();
                                        MyToast.success(getActivity(), R.string.no_new);
                                    }
                                });
                            }
                        }
                    }).start();
                }else {
                    MyToast.info(getActivity(), R.string.no_internet);
                }
            }
        });

        ciscore.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppContact.hasAnyMarketInstalled(getActivity())) {
                    scoreForMyApp();
                }else {
                    MyToast.warning(getActivity(), R.string.no_market);
                }
            }
        });

        sisharefriend.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWithFirend();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting:
                startActivityForResult(SettingActivity.newIntent(getContext()),REQUEST_SETTING);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 显示用户信息
     */
    private void showUserInfo(){
        if (mUser != null) {
            tvusername.setText(mUser.getUsername());
            llemail.setVisibility(View.VISIBLE);
            tvcredit.setText(getString(R.string.credit_points)+" "+mUser.getCredit());
        }else {
            llemail.setVisibility(View.GONE);
            tvusername.setText(R.string.unlogin);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN://登录成功
                if (resultCode== Activity.RESULT_OK) {
                    reloadUser();
                }
                break;
            case REQUEST_SETTING://退出登录
                if (resultCode== Activity.RESULT_OK) {
                    reloadUser();
                }
                break;
        }

    }

    private void reloadUser(){
        mUser=BmobUser.getCurrentUser(User.class);
        showUserInfo();
    }

    private boolean isLogined(){
        if (mUser == null) {
            MyToast.info(getActivity(), R.string.login_first);
            return false;
        }
        return true;
    }

    private void scoreForMyApp(){
        Uri uri = Uri.parse("market://details?id="+ getActivity().getPackageName());

        Intent intent =new Intent(Intent.ACTION_VIEW,uri);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    private void shareWithFirend(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_with_firend));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
