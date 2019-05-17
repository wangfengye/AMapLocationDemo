package com.amap.location.demo.rpc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author maple on 2019/5/11 16:21.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class HookAidlService extends Service implements AMapLocationListener {

    public static final String TAG = "LocService";
    public static volatile int locing = 2;
    final private List<Ap> mAps = new ArrayList<>();
    private LocationClient mBaiduClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocManager;
    }

    private AMapLocationClient locationClient;
    LocManager.Stub mLocManager = new LocManager.Stub() {
        @Override
        public List<Ap> loc() throws RemoteException {
            mAps.clear();
            mAps.add(new Ap());
            mAps.add(new Ap());     locing = 2;
            Log.i(TAG, "loc: " + Thread.currentThread().getName());
            baiduLoc();

                locationClient = new AMapLocationClient(HookAidlService.this);
                AMapLocationClientOption option = new AMapLocationClientOption();
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                option.setOnceLocation(true);
                option.setOnceLocationLatest(true);
                option.setMockEnable(false);
                option.setWifiScan(true);
                option.setGpsFirst(false);
                option.setHttpTimeOut(2000);
                locationClient.setLocationOption(option);
                locationClient.setLocationListener(HookAidlService.this);

            locationClient.startLocation();

            while (locing>0) {
                //waitting
            }
            Log.i(TAG, "loc: allfinished");
            return mAps;
        }
    };

    private void baiduLoc() {

            mBaiduClient = new LocationClient(this);
            LocationClientOption option = new LocationClientOption();
            option.setIsNeedAddress(true);
            option.setIsNeedLocationDescribe(true);
            option.setCoorType("bd0911");
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            mBaiduClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    Ap ap = mAps.get(1);
                    ap.setLatitude(location.getLatitude());
                    ap.setLongitude(location.getLongitude());
                    ap.setAccuracy((int) location.getRadius());
                    ap.setLocationType("百度_" + location.getCoorType());
                    ap.setAddress(location.getAddrStr());
                    locing--;
                    Log.i(TAG, "百度 finish: ");
                    if (mBaiduClient != null) mBaiduClient.stop();mBaiduClient=null;
                }
            });
            mBaiduClient.setLocOption(option);
        mBaiduClient.start();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        Ap ap = mAps.get(0);
        if (location.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
/*            Log.i(TAG, "onLocationChanged:wifi" + scanResult.SSID+"("+scanResult.BSSID+")"
                    +"\n经纬度" + location.getLatitude() + "," + location.getLongitude()
                    + "\n地址:" + location.getAddress()
                    + "\n定位类型: " + location.getLocationType()
                    + "\n精度 : " + location.getAccuracy() + "米");*/
            ap.setLatitude(location.getLatitude());
            ap.setLongitude(location.getLongitude());
            ap.setAccuracy((int) location.getAccuracy());
            ap.setLocationType("高德_" + location.getLocationType());
            ap.setAddress(location.getAddress());


        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            ap.setLatitude(0);
            ap.setLongitude(0);
            ap.setAccuracy(0);
            ap.setLocationType("高德_" + location.getErrorCode());
            ap.setAddress(location.getLocationDetail());
            Log.e("TAG", "签到定位失败，错误码：" + location.getErrorCode() + ", " + location.getLocationDetail());
        }
        locing --;
        Log.i(TAG, "高德 finish: ");
        if (null != locationClient) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
    }

}
