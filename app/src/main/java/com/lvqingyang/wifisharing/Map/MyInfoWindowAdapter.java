package com.lvqingyang.wifisharing.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.lvqingyang.wifisharing.R;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/9/23
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class MyInfoWindowAdapter implements AMap.InfoWindowAdapter {
    private Context mContext;
    private View infoWindow = null;

    public MyInfoWindowAdapter(Context context){
        mContext=context;
    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     */
    public View getInfoContents(Marker marker) {
        return null;
    }



    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    public View getInfoWindow(Marker marker) {
        if(infoWindow == null) {
            infoWindow = LayoutInflater.from(mContext).inflate(
                    R.layout.map_maker, null);
        }
        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
        //如果想修改自定义Infow中内容，请通过view找到它并修改
    }
}
