package com.lvqingyang.wifisharing.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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
public class Record extends BmobObject implements Parcelable {
    private User user;
    private Hotspot hotspot;
    private Float traffic;
    private Float cost;
    private Boolean isPay;
    private static final String TAG = "Record";

    public Record(boolean isPreset){
        if (isPreset) {
            user = BmobUser.getCurrentUser(User.class);
            traffic = 0.0f;
            cost = 0.0f;
            isPay = false;
        }
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hotspot getHotspot() {
        return hotspot;
    }

    public void setHotspot(Hotspot hotspot) {
        this.hotspot = hotspot;
    }

    public Float getTraffic() {
        return traffic;
    }

    public void setTraffic(Float traffic) {
        this.traffic = traffic;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Boolean getPay() {
        return isPay;
    }

    public void setPay(Boolean pay) {
        isPay = pay;
    }

    /**
     * 保存记录
     * @param hotspot
     * @param lis
     */
    public static void saveRecord(Hotspot hotspot, SaveListener<String> lis){
        Record record=new Record(true);
        record.setHotspot(hotspot);
        record.save(lis);
    }

    /**
     * 更新记录
     * @param objectId
     * @param traffic
     * @param cost
     */
    public static void updateRecord(String objectId, float traffic, float cost){
        Record r=new Record(false);
        r.setTraffic(traffic);
        r.setCost(cost);
        r.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","更新成功");
                }else{
                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    /**
     * 获取当前用户记录
     * @param lis
     */
    public static void getUserRecord(FindListener<Record> lis){
        BmobQuery<Record> query = new BmobQuery<>();
        User userInfo = BmobUser.getCurrentUser(User.class);
        query.addWhereEqualTo("user", new BmobPointer(userInfo));
         query//.setLimit().setSkip()//.order("-createdAt")
                 .include("hotspot")
                 .findObjects(lis);
    }

    public static void deleteRecord(String orderId, UpdateListener listener){
        Record record = new Record(false);
        record.setObjectId(orderId);
        record.delete(listener);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.hotspot, flags);
        dest.writeValue(this.traffic);
        dest.writeValue(this.cost);
        dest.writeValue(this.isPay);
    }

    protected Record(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
        this.hotspot = in.readParcelable(Hotspot.class.getClassLoader());
        this.traffic = (Float) in.readValue(Float.class.getClassLoader());
        this.cost = (Float) in.readValue(Float.class.getClassLoader());
        this.isPay = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Record> CREATOR = new Parcelable.Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}
