package com.lvqingyang.wifisharing.Wifi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lvqingyang.wifisharing.base.BaseFragment;

/**
 * Author：LvQingYang
 * Date：2017/8/26
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class ShareHotspotFragment extends BaseFragment {

    public static ShareHotspotFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ShareHotspotFragment fragment = new ShareHotspotFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }
}
