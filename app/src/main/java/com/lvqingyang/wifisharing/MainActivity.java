package com.lvqingyang.wifisharing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lvqingyang.wifisharing.Map.MapFragment;
import com.lvqingyang.wifisharing.User.UserFragment;
import com.lvqingyang.wifisharing.Wifi.WifiFragment;
import com.lvqingyang.wifisharing.Wifi.connect.WiFiConnectService;
import com.lvqingyang.wifisharing.Wifi.share.WiFiAPService;
import com.lvqingyang.wifisharing.base.BaseActivity;
import com.lvqingyang.wifisharing.bean.Hotspot;
import com.lvqingyang.wifisharing.tools.MyDialog;

import frame.tool.NetWorkUtils;

public class MainActivity extends BaseActivity {

    /**
     * view
     */
    private BottomNavigationView navigation;

    /**
    * fragment
    */
    private WifiFragment mWifiFragment;
    private MapFragment mMapFragment;
    private UserFragment mUserFragment;

     /**
     * data
     */
     private FragmentManager mFragmentManager;
     private Toast mToast;


     /**
     * tag
     */


    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedPermission()) {
            showRequestPermissionDialog();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NetWorkUtils.isNetworkConnected(this)) {
            Hotspot.deleteHotspot(this);
        }


        mFragmentManager=getSupportFragmentManager();
        if (savedInstanceState!=null) {
            mWifiFragment= (WifiFragment) mFragmentManager
                    .findFragmentByTag(WifiFragment.class.getName());
            mMapFragment= (MapFragment) mFragmentManager
                    .findFragmentByTag(MapFragment.class.getName());
            mUserFragment= (UserFragment) mFragmentManager
                    .findFragmentByTag(UserFragment.class.getName());
            mFragmentManager.beginTransaction()
                    .show(mWifiFragment)
                    .hide(mMapFragment)
                    .hide(mUserFragment)
                    .commit();
        }else {
            mWifiFragment=WifiFragment.newInstance();
            mMapFragment=MapFragment.newInstance();
            mUserFragment=UserFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.container, mWifiFragment, WifiFragment.class.getName())
                    .add(R.id.container, mMapFragment, MapFragment.class.getName())
                    .add(R.id.container, mUserFragment, UserFragment.class.getName())
                    .hide(mMapFragment)
                    .hide(mUserFragment)
                    .commit();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        navigation = findViewById(R.id.navigation);
    }

    @Override
    protected void setListener() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_wifi:
                        mFragmentManager.beginTransaction()
                                .hide(mMapFragment)
                                .hide(mUserFragment)
                                .show(mWifiFragment)
                                .commit();
                        return true;
                    case R.id.navigation_map:
                        mFragmentManager.beginTransaction()
                                .hide(mWifiFragment)
                                .hide(mUserFragment)
                                .show(mMapFragment)
                                .commit();
                        return true;
                    case R.id.navigation_my:
                        mFragmentManager.beginTransaction()
                                .hide(mMapFragment)
                                .hide(mWifiFragment)
                                .show(mUserFragment)
                                .commit();
                        return true;
                }
                return false;
            }

        });
    }

    @Override
    protected void initData() {
        mToast=Toast.makeText(this, getString(R.string.sure_quit), Toast.LENGTH_SHORT);
    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }


    @Override
    protected String[] getNeedPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 权限申请
     */
    private void showRequestPermissionDialog(){
        View view=getLayoutInflater().inflate(R.layout.dialog_request_permission,null);
        MyDialog dialog=new MyDialog(this)
                .setTitle(R.string.request_permission_)
                .setView(view)
                .setPosBtn(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkPermissions();
                    }
                });
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
         switch (paramMenuItem.getItemId()) {
            case R.id.item_setting:
                return false;
            default:
                return super.onOptionsItemSelected(paramMenuItem);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WiFiConnectService.stopService(this);
        WiFiAPService.stopService(this);
    }


//    @Override
//    public void onBackPressed() {
//        if (!mToast.getView().isShown()) {
//            mToast.show();
//        }else {
//            finish();
//        }
//    }
}
