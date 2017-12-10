package com.lvqingyang.wifisharing.bean;

import com.lvqingyang.wifisharing.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/11/23
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class PayWay {
    private int iconId;
    private String name;
    private String hint;

    public PayWay(int iconId, String name, String hint) {
        this.iconId = iconId;
        this.name = name;
        this.hint = hint;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public static List<PayWay> getPayWays(){
        List<PayWay> list=new ArrayList<>();
        list.add(new PayWay(R.drawable.ic_wallet_pay, "个人钱包", "- 推荐使用"));
        list.add(new PayWay(R.mipmap.alipay, "支付宝", "- 推荐支付宝用户使用"));
        list.add(new PayWay(R.mipmap.wechat_pay, "微信支付", "- 推荐微信用户使用"));

        return list;
    }
}
