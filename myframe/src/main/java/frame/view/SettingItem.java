package frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myframe.R;


/**
 * 　　┏┓　　  ┏┓+ +
 * 　┏┛┻━ ━ ━┛┻┓ + +
 * 　┃　　　             ┃
 * 　┃　　　━　　   ┃ ++ + + +
 * ████━████     ┃+
 * 　┃　　　　　　  ┃ +
 * 　┃　　　┻　　  ┃
 * 　┃　　　　　　  ┃ + +
 * 　┗━┓　　　┏━┛
 * 　　　┃　　　┃
 * 　　　┃　　　┃ + + + +
 * 　　　┃　　　┃
 * 　　　┃　　　┃ +  神兽保佑
 * 　　　┃　　　┃    代码无bug！
 * 　　　┃　　　┃　　+
 * 　　　┃　 　　┗━━━┓ + +
 * 　　　┃ 　　　　　　　┣┓
 * 　　　┃ 　　　　　　　┏┛
 * 　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　┃┫┫　┃┫┫
 * 　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/6/16
 * Email：biloba12345@gamil.com
 * Info：
 */

public class SettingItem extends FrameLayout {

    private final TextView mTvType;
    private final TextView mTvTitle;
    private final TextView mTvInfo;
    private final Switch mSwitch;
    private final View mRl;

    public SettingItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_setting,this);
        mTvType = (TextView) findViewById(R.id.tv_type);
        mTvTitle = (TextView)findViewById(R.id.tv_title);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mSwitch = (Switch) findViewById(R.id.sw);
        mRl = findViewById(R.id.rl);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SettingItem,
                0, 0);

        try {
            String type=a.getString(R.styleable.SettingItem_type);
            setText(mTvType,type);
            String title=a.getString(R.styleable.SettingItem_title);
            setText(mTvTitle,title);
            String info=a.getString(R.styleable.SettingItem_info);
            setText(mTvInfo,info);

            if (!a.getBoolean(R.styleable.SettingItem_showSw,false)) {
                mSwitch.setVisibility(GONE);
            }
        } finally {
            a.recycle();
        }
    }

    private void setText(TextView tv,String text){
        if (text == null) {
            tv.setVisibility(GONE);
        }else {
            tv.setText(text);
        }
    }

    public SettingItem setTitle(String title){
        mTvTitle.setText(title);
        return this;
    }

    public SettingItem setType(String typ){
        mTvType.setText(typ);
        return this;
    }

    public SettingItem setInfo(String info){
        mTvInfo.setText(info);
        return this;
    }

    public SettingItem setClickListener(OnClickListener listener){
        mRl.setOnClickListener(listener);
        return this;
    }

    public SettingItem setSwitch(boolean checked){
        mSwitch.setChecked(checked);
        return this;
    }

    public SettingItem setCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        mSwitch.setOnCheckedChangeListener(listener);
        return this;
    }

}
