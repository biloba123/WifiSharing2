package com.lvqingyang.wifisharing.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/10/2
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class Message extends BmobObject {
    private Integer type;//消息类型
    private String title;
    private Boolean isWebPage;
    private String url;
    private String content;
    private User receiver;

    public static final int GLOBAL = 0;

    public Message(int type, String title, String content, User receiver) {
        this.type = type;
        this.isWebPage=false;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getWebPage() {
        return isWebPage;
    }

    public void setWebPage(Boolean webPage) {
        isWebPage = webPage;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    /**
     * 发布消息
     * @param type 类型
     * @param title 标题
     * @param content 内容或url
     * @param receiver 接受者
     * @param lis
     */
    public static void postMessage(int type, String title, String content, User receiver,
                                   SaveListener<String> lis){
        Message m=new Message(type, title, content, receiver);
        m.save(lis);
    }

    public static void getMessages(FindListener<Message> lis){
        User user=BmobUser.getCurrentUser(User.class);

        BmobQuery<Message> bq1=new BmobQuery<>();
        bq1.addWhereEqualTo("receiver", new BmobPointer(user));
        BmobQuery<Message> bq2=new BmobQuery<>();
        bq2.addWhereEqualTo("type", GLOBAL);

        List<BmobQuery<Message>> list=new ArrayList<>();
        list.add(bq1);
        list.add(bq2);

        BmobQuery<Message> bq=new BmobQuery<>();
        bq.or(list);
        bq.order("-createdAt")
                .findObjects(lis);
    }

    public static void getMessageCount(CountListener lis){
        User user=BmobUser.getCurrentUser(User.class);

        BmobQuery<Message> bq1=new BmobQuery<>();
        bq1.addWhereEqualTo("receiver", new BmobPointer(user));
        BmobQuery<Message> bq2=new BmobQuery<>();
        bq2.addWhereEqualTo("type", GLOBAL);

        List<BmobQuery<Message>> list=new ArrayList<>();
        list.add(bq1);
        list.add(bq2);

        BmobQuery<Message> bq=new BmobQuery<>();
        bq.or(list);
        bq.count(Message.class, lis);
    }

}
