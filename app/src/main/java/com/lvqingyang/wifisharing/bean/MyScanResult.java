package com.lvqingyang.wifisharing.bean;


import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/9/26
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class MyScanResult {
    private ScanResult scanResult;
    private Hotspot hotspot;

    public MyScanResult(ScanResult scanResult, Hotspot hotspot) {
        this.scanResult = scanResult;
        this.hotspot=hotspot;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public Hotspot getHotspot() {
        return hotspot;
    }

    public void setHotspot(Hotspot hotspot) {
        this.hotspot = hotspot;
    }

    public static List<MyScanResult> transToMyScanResults(List<ScanResult> scanResults){
        List<MyScanResult> myScanResults=new ArrayList<>();
        for (ScanResult scanResult : scanResults) {
            myScanResults.add(new MyScanResult(scanResult, null));
        }
        return myScanResults;
    }
}
