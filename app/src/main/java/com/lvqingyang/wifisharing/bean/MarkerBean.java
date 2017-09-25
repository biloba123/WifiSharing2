package com.lvqingyang.wifisharing.bean;

import android.location.Location;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/9/24
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class MarkerBean {
    private boolean isFixed;
    private String location, updateTime;
    private int direction, useCount;
    private double latitude,longitude;


    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static MarkerBean getMarker(Hotspot nearHotspot, Location loc){
        MarkerBean bean=new MarkerBean();
        bean.setFixed(false);
        bean.setLocation(nearHotspot.getLocation());
        bean.setUpdateTime(nearHotspot.getLastUpdate());
        BmobGeoPoint bp=nearHotspot.getPoint();
        bean.setLatitude(bp.getLatitude());
        bean.setLongitude(bp.getLongitude());
        bean.setDirection((int) AMapUtils.calculateLineDistance(new LatLng(bp.getLatitude(),bp.getLongitude()),
                new LatLng(loc.getLatitude(),loc.getLongitude())));
        return bean;
    }

    public static MarkerBean getMarker(FixedHotspot nearHotspot, Location loc){
        MarkerBean bean=new MarkerBean();
        bean.setFixed(true);
        bean.setLocation(nearHotspot.getLocation());
        bean.setUseCount(nearHotspot.getUseCount());
        BmobGeoPoint bp=nearHotspot.getPoint();
        bean.setLatitude(bp.getLatitude());
        bean.setLongitude(bp.getLongitude());
        bean.setDirection((int) AMapUtils.calculateLineDistance(new LatLng(bp.getLatitude(),bp.getLongitude()),
                new LatLng(loc.getLatitude(),loc.getLongitude())));
        return bean;
    }
}
