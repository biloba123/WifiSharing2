package com.lvqingyang.wifisharing.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.bean.MarkerBean;

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
    private Gson mGson=new Gson();
    private ImageView mIvType;
    private TextView mTvName, mTvLocation,
            mTvUpdateTime, mTvUseCount, mTvDirection, mTvGo;
    private static final String TAG = "MyInfoWindowAdapter";
    private OnGoListener mOnGoListener;


    public interface OnGoListener{
        void onWalkRoute(Marker marker);
    }

    public MyInfoWindowAdapter(Context context, OnGoListener lis){
        mContext=context;
        mOnGoListener=lis;
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
    public void render(final Marker marker, View view) {
        //如果想修改自定义Infow中内容，请通过view找到它并修改
        mIvType=view.findViewById(R.id.iv);
        mTvName=view.findViewById(R.id.tv_name);
        mTvLocation=view.findViewById(R.id.tv_location);
        mTvUpdateTime=view.findViewById(R.id.tv_update_time);
        mTvUseCount=view.findViewById(R.id.tv_user_count);
        mTvDirection=view.findViewById(R.id.tv_direction);
        mTvGo=view.findViewById(R.id.tv_go);


        mTvName.setText(marker.getTitle());
        MarkerBean bean=mGson.fromJson(marker.getSnippet(),MarkerBean.class);
        if (bean.isFixed()) {//hotspot
            Glide.with(mContext)
                    .load(R.drawable.ic_route_map)
                    .into(mIvType);
            mTvUpdateTime.setVisibility(View.GONE);
            mTvUseCount.setVisibility(View.VISIBLE);
            mTvUseCount.setText("使用人次："+bean.getUseCount());
        }else{
            Glide.with(mContext)
                    .load(R.drawable.ic_wifi_map)
                    .into(mIvType);
            mTvUseCount.setVisibility(View.GONE);
            mTvUpdateTime.setVisibility(View.VISIBLE);
            mTvUpdateTime.setText("更新时间："+bean.getUpdateTime().substring(5,16));
        }
        mTvLocation.setText(bean.getLocation());
        mTvDirection.setText(bean.getDirection()+"m");

        if (mOnGoListener != null) {
            mTvGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnGoListener.onWalkRoute(marker);
                }
            });

        }
    }
}
