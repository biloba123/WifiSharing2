package com.lvqingyang.wifisharing.bean;

import android.util.Log;

import com.lvqingyang.wifisharing.BuildConfig;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Author：LvQingYang
 * Date：2017/8/26
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class User extends BmobUser {
    private String nick;
    private Boolean sex; //性别（未设置：null ; 男：true）
    private BmobFile avater;//头像
    private BmobDate birthday;
    private Integer credit;//信用分
    private Float balance;//余额
    private static final String TAG = "User";

    public User() {
        credit=100;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public BmobFile getAvater() {
        return avater;
    }

    public void setAvater(BmobFile avater) {
        this.avater = avater;
    }

    public BmobDate getBirthday() {
        return birthday;
    }

    public void setBirthday(BmobDate birthday) {
        this.birthday = birthday;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getAvaterUrl(){
        return avater.getUrl();
    }

    public static void register(final String tel, final String smsCode, final String password,
                                final SaveListener<User> lis){
        User user = new User();
        user.setMobilePhoneNumber(tel);//设置手机号码（必填）
        user.setPassword(password);                  //设置用户密码
        user.setNick(tel);
        user.setBalance(0.0f);
        user.signOrLogin(smsCode, lis);
    }

    /**
     * 登录
     * @param userName
     * @param password
     * @param lis
     */
    public static void login(String userName,String password,LogInListener<User> lis){
        BmobUser.loginByAccount(userName, password, lis);
    }

    /**
     * 退出登录
     */
    public static void logout(){
        BmobUser.logOut();   //清除缓存用户对象
    }


        /**
         * 更新本地用户信息
         * 适用场景:登录后若web端的用户信息有更新 可以通过该方法拉取最新的用户信息并写到本地缓存(SharedPreferences)中<p>
         * 注意：需要先登录，否则会报9024错误
         *
         * @see ErrorCode E9024S
         */
        public static void syscUserInfo() {
            BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "done: "+s);
                    }else {
                        if (BuildConfig.DEBUG) Log.d(TAG, "done: "+e.toString());
                    }
                }
            });
        }

    public static void updateUser(User newUser, UpdateListener lis){
        BmobUser bmobUser = BmobUser.getCurrentUser(BmobUser.class);
        newUser.update(bmobUser.getObjectId(), lis);
    }

    public static void deleteOldAvater(){
        User user = BmobUser.getCurrentUser(User.class);
        if (user.getAvater()!=null) {
            user.getAvater().delete(new UpdateListener() {
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
    }
}
