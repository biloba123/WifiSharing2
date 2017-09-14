package frame.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myframe.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Random;

import frame.permission.CheckPermissionsActivity;
import frame.tool.MyToast;
import frame.tool.NetWorkUtils;

public abstract class BaseActivity extends CheckPermissionsActivity
{
    private static final String TAG = "BaseActivity";
    private FrameLayout mContentFl;
    private LinearLayout mEmptyLl;
    private LinearLayout mLoadFailLl;
    private AVLoadingIndicatorView mLoadingIndicatorView;
    private LinearLayout mLoadingLl;
    private TextView mRetryTv;
    private View mRootView;


    protected void onCreate(@Nullable Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        Bundle localBundle = getIntent().getExtras();
        if (localBundle != null)
            getBundleExtras(localBundle);
        setContentView(R.layout.layout_base);
        this.mContentFl = ((FrameLayout)findViewById(R.id.fl));
        this.mEmptyLl = ((LinearLayout)findViewById(R.id.layout_empty));
        this.mLoadFailLl = ((LinearLayout)findViewById(R.id.layout_load_fail));
        this.mRetryTv = ((TextView)findViewById(R.id.tv_retry));
        this.mRetryTv.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                BaseActivity.this.onRetryClick();
            }
        });
        this.mLoadingLl = ((LinearLayout)findViewById(R.id.layout_loading));
        this.mLoadingIndicatorView = ((AVLoadingIndicatorView)findViewById(R.id.avl));
        setIndicator();
        View localView = getLayoutInflater().inflate(getLayoutId(), null);
        if (localView.findViewById(R.id.toolbar) == null){
            this.mRootView = localView;
        }else {
            this.mRootView = localView.findViewById(R.id.view_root);
        }
        localView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.mContentFl.addView(localView);
        initView();
        setListener();
        initData();
        setData();
    }


    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void setListener();

    protected abstract void initData();

    protected abstract void setData();

    protected abstract void getBundleExtras(Bundle bundle);



    public void initToolbar(String title, boolean isDisplayHome)
    {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
        {
            localActionBar.setTitle(title);
            localActionBar.setDisplayHomeAsUpEnabled(isDisplayHome);
        }
    }


    protected void initeActionbar(int titleId, boolean isDisplayHome)
    {
        initeActionbar(getString(titleId), isDisplayHome);
    }

    protected void initeActionbar(String title, boolean isDisplayHome)
    {
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
        {
            localActionBar.setTitle(title);
            localActionBar.setDisplayHomeAsUpEnabled(isDisplayHome);
        }
    }

    protected void loadFail()
    {
        this.mLoadingIndicatorView.smoothToHide();
        this.mLoadingLl.setVisibility(View.GONE);
        this.mRootView.setVisibility(View.GONE);
        this.mEmptyLl.setVisibility(View.GONE);
        this.mLoadFailLl.setVisibility(View.VISIBLE);
    }

    protected void loading()
    {
        this.mRootView.setVisibility(View.GONE);
        this.mLoadFailLl.setVisibility(View.GONE);
        this.mEmptyLl.setVisibility(View.GONE);
        this.mLoadingLl.setVisibility(View.VISIBLE);
        this.mLoadingIndicatorView.smoothToShow();
    }

    protected void onRetryClick()
    {
        loading();
    }

    protected void loadComplete()
    {
        this.mLoadingIndicatorView.smoothToHide();
        this.mLoadingLl.setVisibility(View.GONE);
        this.mLoadFailLl.setVisibility(View.GONE);
        this.mEmptyLl.setVisibility(View.GONE);
        this.mRootView.setVisibility(View.VISIBLE);
        Animation localAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        this.mRootView.setAnimation(localAnimation);
    }

    protected void showEmpty()
    {
        this.mLoadingIndicatorView.smoothToHide();
        this.mLoadingLl.setVisibility(View.GONE);
        this.mLoadFailLl.setVisibility(View.GONE);
        this.mRootView.setVisibility(View.GONE);
        this.mEmptyLl.setVisibility(View.VISIBLE);
    }

    protected boolean checkInternet()
    {
        boolean isConnected=NetWorkUtils.isNetworkConnected(this);
        if (!isConnected)
            MyToast.info(this, R.string.no_internet, Toast.LENGTH_SHORT);
        return isConnected;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        if (paramMenuItem.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    private void setIndicator()
    {
        String[] arrayOfString = getResources().getStringArray(R.array.arr_indicator);
        int i = new Random().nextInt(arrayOfString.length);
        this.mLoadingIndicatorView.setIndicator(arrayOfString[i]);
    }
}