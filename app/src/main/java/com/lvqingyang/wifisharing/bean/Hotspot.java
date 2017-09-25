package com.lvqingyang.wifisharing.bean;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.lvqingyang.wifisharing.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import frame.tool.MyPrefence;

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
public class Hotspot extends BmobObject{
    private String ssid;
    private String password;
    private User user;
    private String location;
    private BmobGeoPoint point;
    private String lastUpdate;
    private static final String TAG = "Hotspot";
    private static final String HOTSPOT_ID = "HOTSPOT_ID";
    public static final double MAX_DISTANCE = 2;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUser() {
        this.user =BmobUser.getCurrentUser(User.class);;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BmobGeoPoint getPoint() {
        return point;
    }

    public void setPoint(BmobGeoPoint point) {
        this.point = point;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static void postHotspot(String ssid, String  pwd,
                                   AMapLocation amapLocation, SaveListener<String> listener){
        Hotspot hotspot=new Hotspot();
        hotspot.setUser();
        hotspot.setSsid(ssid);
        hotspot.setPassword(pwd);
        hotspot.setLocation(getLocation(amapLocation));
        hotspot.setLastUpdate(getUpdateTime(amapLocation));
        hotspot.setPoint(new BmobGeoPoint(amapLocation.getLongitude(),amapLocation.getLatitude()));

        hotspot.save(listener);

    }

    public static void updateHotspot(AMapLocation amapLocation,String hotspotId){
        Hotspot hotspot=new Hotspot();
        hotspot.setLocation(getLocation(amapLocation));
        hotspot.setLastUpdate(getUpdateTime(amapLocation));
        hotspot.setPoint(new BmobGeoPoint(amapLocation.getLongitude(),amapLocation.getLatitude()));
        hotspot.update(hotspotId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "done: update succ");
                }else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "done: "+e.toString());
                }
            }
        });
    }

    public static void saveIdToPrefence(Context context,String hotspotId){
        MyPrefence.getInstance(context)
                .saveString(HOTSPOT_ID,hotspotId);
    }

    public static void deleteHotspot(Context context){
        final MyPrefence mp=MyPrefence.getInstance(context);
        String hotspotId=mp.getString(HOTSPOT_ID);
        if (hotspotId != null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "deleteHotspot: "+hotspotId);
            Hotspot hotspot = new Hotspot();
            hotspot.setObjectId(hotspotId);
            hotspot.delete(new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Log.i("bmob","成功");
                        mp.clearData(HOTSPOT_ID);
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    public static String getUpdateTime(AMapLocation amapLocation){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(amapLocation.getTime());
        return df.format(date);
    }

    public static String getLocation(AMapLocation amapLocation){
        return amapLocation.getProvince() + amapLocation.getCity()
                + amapLocation.getDistrict() +  amapLocation.getStreet()
                +  amapLocation.getStreetNum();
    }

    public static void getNearHotspot(BmobGeoPoint userLoc,FindListener<Hotspot> lis){
        BmobQuery<Hotspot> query = new BmobQuery<>();
                query.setLimit(500)
                        .addWhereWithinKilometers("point",userLoc,MAX_DISTANCE)
                        .findObjects(lis);
    }
}

