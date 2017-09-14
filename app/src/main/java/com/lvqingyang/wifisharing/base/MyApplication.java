package com.lvqingyang.wifisharing.base;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * Author：LvQingYang
 * Date：2017/8/22
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "eb5dc119e33ffd6f68d4ee6e6bfae698");
    }
}
