package com.lvqingyang.wifisharing.base;

import android.app.Application;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.lvqingyang.wifisharing.R;

import cn.bmob.v3.Bmob;
import top.wefor.circularanim.CircularAnim;

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
        //初始化Instabug
        new Instabug.Builder(this, "4178243dc541d4bedac5548f8ce58355")
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .build();
        //初始化bmob
        Bmob.initialize(this, "eb5dc119e33ffd6f68d4ee6e6bfae698");
        //ripple动画配置
        CircularAnim.init(700, 500, R.color.colorPrimary);
    }
}
