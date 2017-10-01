package com.lvqingyang.wifisharing.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/9/27
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class AppContact {
    public static final float HOTSPOT_PRICE = 0.05f;
    public static final float HOTSPOT_FIXED_PRICE = 0.005f;

    public static boolean hasAnyMarketInstalled(Context context) {
        Intent intent =new Intent();
        intent.setData(Uri.parse("market://details?id=android.browser"));

        List list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return 0!= list.size();
    }

}
