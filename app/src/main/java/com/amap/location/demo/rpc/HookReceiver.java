package com.amap.location.demo.rpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;

import java.util.List;
import java.util.Queue;

/**
 * @author maple on 2019/5/10 13:42.
 * @version v1.0
 * @see 1040441325@qq.com
 * 广播实现进程间通信(单向)
 */
public class HookReceiver extends BroadcastReceiver implements AMapLocationListener {
    public static final String TAG = "HookReceiver";
    AMapLocationClient locationClient;
    ScanResult scanResult;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: start");
        locationClient = new AMapLocationClient(context);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        option.setOnceLocation(true);
        option.setOnceLocationLatest(true);
        option.setMockEnable(false);
        option.setWifiScan(true);
        option.setGpsFirst(false);
        option.setHttpTimeOut(2000);
        locationClient.setLocationOption(option);
        locationClient.setLocationListener(this);
        locationClient.startLocation();
        WifiManager ma= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> data = ma.getScanResults();

        if (data!=null&&data.size()>0){
            scanResult = data.get(0);
            for (int i = 1; i < data.size(); i++) {
                if (data.get(i).level>scanResult.level)scanResult=data.get(i);
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            Log.i(TAG, "onLocationChanged:wifi" + scanResult.SSID+"("+scanResult.BSSID+")"
                    +"\n经纬度" + location.getLatitude() + "," + location.getLongitude()
                    + "\n地址:" + location.getAddress()
                    + "\n定位类型: " + location.getLocationType()
                    + "\n精度 : " + location.getAccuracy() + "米");

        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e(TAG, "签到定位失败，错误码：" + location.getErrorCode() + ", " + location.getLocationDetail());
        }
        if (null != locationClient) locationClient.onDestroy();
    }
}
