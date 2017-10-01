package com.lvqingyang.wifisharing.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lvqingyang.wifisharing.R;

/**
 * Author：LvQingYang
 * Date：2017/9/13
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class MyDialog extends DialogFragment {
    private Context mContext;
    private String mTitle;
    private View mRootView;
    private View mView;//dialog 内容
    private String mPosBtnName,mNegBtnName;
    private View.OnClickListener mPosLis,mNegLis;
    private int mDialogWidth=320;//默认dialog宽度dp
    private TextView tvpos,tvneg;//确认取消
    private TextView tvtitle;
    private FrameLayout container;
    private LinearLayout llbtn;


    public MyDialog(Context context) {
        mContext = context;
        mRootView= LayoutInflater.from(mContext).inflate(R.layout.dialog_base,null);
        this.llbtn = (LinearLayout) mRootView.findViewById(R.id.ll_btn);
        this.tvpos = (TextView) mRootView.findViewById(R.id.tv_pos);
        this.tvneg = (TextView) mRootView.findViewById(R.id.tv_neg);
        this.container = (FrameLayout) mRootView.findViewById(R.id.container);
        this.tvtitle = (TextView) mRootView.findViewById(R.id.tv_title);

    }

    public MyDialog setTitle(int titleId){
        mTitle=mContext.getString(titleId);
        return this;
    }

    public MyDialog setTitle(String title){
        mTitle=title;
        return this;
    }

    public MyDialog setMessage(int msgId){
        return setMessage(mContext.getString(msgId));
    }

    public MyDialog setMessage(String msg){
        TextView tv= (TextView) LayoutInflater.from(mContext).inflate(R.layout.dialog_base_msg,null);
        tv.setText(msg);
        mView=tv;
        return this;
    }

    public MyDialog setView(View view){
        mView=view;
        return this;
    }

    public MyDialog setPosBtn(int nameId, View.OnClickListener listener){
        return setPosBtn(mContext.getString(nameId),listener);
    }

    public MyDialog setPosBtn(String name, View.OnClickListener listener){
        mPosBtnName=name;
        mPosLis=listener;
        return this;
    }

    public MyDialog setNegBtn(int nameId, View.OnClickListener listener){
        return setNegBtn(mContext.getString(nameId),listener);
    }

    public MyDialog setNegBtn(String name, View.OnClickListener listener){
        mNegBtnName=name;
        mNegLis=listener;
        return this;
    }


    public MyDialog setDialogWidth(int dialogWidth) {
        mDialogWidth = dialogWidth;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog=getDialog();
        if (dialog != null) {
            int width= (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, mDialogWidth,
                    getResources().getDisplayMetrics()
            );
            dialog.getWindow()
                    .setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(null);
        }
    }

    public void show(FragmentManager fragmentManager){
        show(fragmentManager,null);
    }

    public TextView getTvpos() {
        return tvpos;
    }

    public TextView getTvneg() {
        return tvneg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        tvtitle.setText(mTitle);
        if (mView != null) {
            FrameLayout.LayoutParams layoutParams=
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mView.setLayoutParams(layoutParams);
            container.addView(mView);
        }

        if (mPosBtnName==null&&mNegBtnName==null) {
            llbtn.setVisibility(View.GONE);
        }else {
            if(mPosBtnName != null){
                tvpos.setText(mPosBtnName);
                tvpos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPosLis != null) {
                            mPosLis.onClick(view);
                        }
                        dismiss();
                    }
                });
            }else {
                tvpos.setVisibility(View.GONE);
            }

            if (mNegBtnName != null) {
                tvneg.setText(mNegBtnName);
                tvneg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mNegLis != null) {
                            mNegLis.onClick(view);
                        }
                        dismiss();
                    }
                });
            }else {
                tvneg.setVisibility(View.GONE);
            }

        }


        return new AlertDialog.Builder(mContext)
                .setView(mRootView).create();
    }
}
