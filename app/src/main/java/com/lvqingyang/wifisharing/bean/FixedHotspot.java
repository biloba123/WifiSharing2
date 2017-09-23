package com.lvqingyang.wifisharing.bean;

import com.amap.api.location.AMapLocation;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.lvqingyang.wifisharing.bean.Hotspot.MAX_DISTANCE;

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
public class FixedHotspot extends BmobObject{
    private String ssid;
    private String password;
    private User user;
    private String location;
    private BmobGeoPoint point;
    private Integer useCount;

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

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public static void postHotspot(String ssid, String  pwd,
                                   AMapLocation amapLocation, SaveListener<String> listener){
        FixedHotspot hotspot=new FixedHotspot();
        hotspot.setUser();
        hotspot.setSsid(ssid);
        hotspot.setPassword(pwd);
        hotspot.setLocation(getLocation(amapLocation));
        hotspot.setPoint(new BmobGeoPoint(amapLocation.getLongitude(),amapLocation.getLatitude()));
        hotspot.setUseCount(0);
        hotspot.save(listener);

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

    public static void getNearFixedHotspot(BmobGeoPoint userLoc,FindListener<FixedHotspot> lis){
        BmobQuery<FixedHotspot> query = new BmobQuery<>();
        query.setLimit(500)
                .addWhereWithinKilometers("point",userLoc,MAX_DISTANCE)
                .findObjects(lis);
    }
}

