package com.lvqingyang.wifisharing.Map;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.google.gson.Gson;
import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseFragment;
import com.lvqingyang.wifisharing.bean.FixedHotspot;
import com.lvqingyang.wifisharing.bean.Hotspot;
import com.lvqingyang.wifisharing.bean.MarkerBean;
import com.lvqingyang.wifisharing.overlay.WalkRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import frame.tool.MyToast;

/**
 * Author：LvQingYang
 * Date：2017/8/26
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class MapFragment extends BaseFragment implements LocationSource, AMap.OnMyLocationChangeListener, AMap.OnInfoWindowClickListener, AMap.OnMarkerClickListener, RouteSearch.OnRouteSearchListener, MyInfoWindowAdapter.OnGoListener {

    private Toolbar mToolbar;
    private MapView mMapView;
    private AMap mAMap;
    //标识
    private boolean mIsMoveTo = true;
    private static final String TAG = "MapFragment";
    private FloatingActionButton mFabMyLoc;
    private Location mLastLocation;
    private Gson mGson=new Gson();

    private List<Hotspot> mNearHotspots;
    private List<FixedHotspot> mNearFixedHotspots;
    private List<Marker> mMarkers=new ArrayList<>();
    private FloatingActionButton mFabReload;

    private RouteSearch mRouteSearch;
    private WalkRouteOverlay mWalkRouteOverlay;


    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_map,container,false);
        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        return view;
    }

    @Override
    protected void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        initToolbar(mToolbar,getString(R.string.title_map),false);
        mFabMyLoc = (FloatingActionButton) view.findViewById(R.id.fab_my_location);
        mFabReload = (FloatingActionButton) view.findViewById(R.id.fab_reload);
        mFabReload.setTag(R.drawable.ic_autorenew);
    }

    @Override
    protected void setListener() {
        mFabMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastLocation != null) {
                    moveTo(mLastLocation);
                }
            }
        });

        mFabReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFabReload.getTag().equals(R.drawable.ic_autorenew)) {
                    mFabReload.setImageResource(R.drawable.ar_sysc);
                    mFabReload.setTag(R.drawable.ar_sysc);
                    showNearbyHotspot();
                }
            }
        });
    }

    @Override
    protected void initData() {
        //获取地图对象
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker));
        mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.setOnMyLocationChangeListener(this);
        

        mAMap.showIndoorMap(true);    //true：显示室内地图


        //设置显示定位按钮 并且可以点击
        UiSettings settings = mAMap.getUiSettings();
        settings.setRotateGesturesEnabled(false);
        settings.setTiltGesturesEnabled(false);
        //设置定位监听
//        mAMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(false); //显示默认的定位按钮
        //縮放按鈕
        settings.setZoomControlsEnabled(false);
        settings.setScaleControlsEnabled(false);//控制比例尺控件是否显示
        // 是否可触发定位并显示定位层
        mAMap.setMyLocationEnabled(true);

        //绑定信息窗点击事件
        mAMap.setOnInfoWindowClickListener(this);
        mAMap.setOnMarkerClickListener(this);
        mAMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity(),this));
    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    /**
     * 定位按钮被按下时调用
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (BuildConfig.DEBUG) Log.d(TAG, "activate: ");
        mIsMoveTo=true;
    }

    @Override
    public void deactivate() {
        if (BuildConfig.DEBUG) Log.d(TAG, "deactivate: ");
    }

    /**
     * 自己位置回调
     * @param location
     */
    @Override
    public void onMyLocationChange(Location location) {
//        if (BuildConfig.DEBUG) Log.d(TAG, "onMyLocationChange: ");
        if (mLastLocation == null) {//第一次时自动获取周围hotspot
            mLastLocation=location;
            showNearbyHotspot();
        }else {
            mLastLocation=location;
        }
        // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
        if (mIsMoveTo) {
            moveTo(location);
            mIsMoveTo = false;
        }
    }

    /**
     *将中心移到我的位置
     * @param location
     */
    private void moveTo(Location location){
        //设置缩放级别
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //将地图移动到定位点
        Log.d(TAG, "onLocationChanged: "+location.getLatitude()+"  "+location.getLongitude());
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

    }

    /**
     * marker 的窗口点击回调
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onInfoWindowClick: ");
        showOrHideInfoWindow(marker);
    }

    /**
     *显示周围分享热点
     */
    public void showNearbyHotspot(){

        //user location
        BmobGeoPoint point=new BmobGeoPoint(mLastLocation.getLongitude(),mLastLocation.getLatitude());
        //get nearby hotspots
        Hotspot.getNearHotspot(point, new FindListener<Hotspot>() {
            @Override
            public void done(List<Hotspot> list, BmobException e) {
                mNearHotspots=list;
            }
        });
        //get nearby fixed hotspots
        FixedHotspot.getNearFixedHotspot(point, new FindListener<FixedHotspot>() {
            @Override
            public void done(List<FixedHotspot> list, BmobException e) {
                mNearFixedHotspots=list;
            }
        });

        if (BuildConfig.DEBUG) Log.d(TAG, "showNearbyHotspot: hotspot"+
                (mNearHotspots!=null?mNearHotspots.size():0)
                +", fixed"+(mNearFixedHotspots!=null?mNearFixedHotspots.size():0));


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stop anim
                            mFabReload.setImageResource(R.drawable.ic_autorenew);
                            mFabReload.setTag(R.drawable.ic_autorenew);

                            //clear old marker
                            for (Marker marker : mMarkers) {
                                marker.remove();
                            }
                            mMarkers.clear();

                            if (mWalkRouteOverlay != null) {
                                mWalkRouteOverlay.removeFromMap();
                            }

                            LatLng latLng;
                            String title, content;

                            if (mNearHotspots != null) {
                                for (Hotspot nearHotspot : mNearHotspots) {
                                    latLng=new LatLng(nearHotspot.getPoint().getLatitude(),
                                            nearHotspot.getPoint().getLongitude());
                                    title=nearHotspot.getSsid();
                                    content=mGson.toJson(MarkerBean.getMarker(nearHotspot, mLastLocation));
                                    mMarkers.add(makeMarker(latLng, title, content, false));
                                }
                            }


                            if (mNearFixedHotspots != null) {
                                for (FixedHotspot nearHotspot : mNearFixedHotspots) {
                                    latLng=new LatLng(nearHotspot.getPoint().getLatitude(),
                                            nearHotspot.getPoint().getLongitude());
                                    title=nearHotspot.getSsid();
                                    content=mGson.toJson(MarkerBean.getMarker(nearHotspot, mLastLocation));
                                    mMarkers.add(makeMarker(latLng, title, content, true));
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 制作marker
     * @param latLng marker位置
     * @param title
     * @param content
     * @param isFixed 是否为固定热点
     * @return
     */
    private Marker makeMarker(LatLng latLng, String title, String content, boolean isFixed){
        MarkerOptions options = new MarkerOptions();
        if (!isFixed) {//hotspot
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_wifi_mobile));
        }else{
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_wifi_fixed));
        }
        //位置
        options.position(latLng);
        //标题
        options.title(title);
        //子标题
        options.snippet(content);
        return mAMap.addMarker(options);
    }

    /**
     * marker自身被点击
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onMarkerClick: "+marker.getTitle());
        showOrHideInfoWindow(marker);
        return true;
    }

    /**
     * 是否显示iw
     * @param marker
     */
    private void showOrHideInfoWindow(Marker marker){
        MarkerBean b=mGson.fromJson(marker.getSnippet(),MarkerBean.class);
        if (marker.getTitle() != null) {
            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                if (!b.isFixed()) {//hotspot
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_wifi_mobile));
                }else{
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_wifi_fixed));
                }
            }else {
                marker.showInfoWindow();
                if (!b.isFixed()) {//hotspot
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_wifi_mobile_touch));
                }else{
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_wifi_fixed_touch));
                }
            }
        }
    }

    /**
     * 步行路线规划
     * @param startPoint
     * @param endPoint
     */
    private void walkRoute(LatLonPoint startPoint,LatLonPoint endPoint){
        if (mRouteSearch == null) {
            mRouteSearch=new RouteSearch(getActivity());
            mRouteSearch.setRouteSearchListener(this);
        }
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        //初始化query对象，fromAndTo是包含起终点信息，walkMode是步行路径规划的模式
        RouteSearch.WalkRouteQuery query =
                new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WALK_DEFAULT);
        mRouteSearch.calculateWalkRouteAsyn(query);//开始算路
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    /**
     * 步行规划路线查询结果
     * @param walkRouteResult
     * @param rCode
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int rCode) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onWalkRouteSearched: "+rCode);
        MyToast.cancel();
        if (rCode == 1000) {
            if (walkRouteResult != null && walkRouteResult.getPaths() != null
                    && walkRouteResult.getPaths().size() > 0) {
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
//                mAMap.clear();// 清理地图上的所有覆盖物
                mWalkRouteOverlay = new WalkRouteOverlay(getActivity(),
                        mAMap, walkPath, walkRouteResult.getStartPos(),
                        walkRouteResult.getTargetPos());
                mWalkRouteOverlay.removeFromMap();
                mWalkRouteOverlay.addToMap();
                mWalkRouteOverlay.zoomToSpan();
            } else {
                MyToast.warning(getActivity(), R.string.no_result);
            }
        } else if (rCode == 27) {
            MyToast.error(getActivity(), R.string.error_network);
        } else if (rCode == 32) {
            MyToast.error(getActivity(), R.string.error_key);
        } else {
            MyToast.error(getActivity(), R.string.error_other);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * go 被点击
     * @param marker
     */
    @Override
    public void onWalkRoute(Marker marker) {
        MyToast.loading(getActivity(), R.string.searching);
        marker.hideInfoWindow();
        MarkerBean bean=mGson.fromJson(marker.getSnippet(),MarkerBean.class);
        LatLonPoint startPoint=new LatLonPoint(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                endPoint=new LatLonPoint(bean.getLatitude(),bean.getLongitude());
        walkRoute(startPoint, endPoint);
    }
}
