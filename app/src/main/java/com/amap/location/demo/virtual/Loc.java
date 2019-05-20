package com.amap.location.demo.virtual;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.location.demo.R;
import com.baidu.location.LocationClient;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.Screen;

import java.util.Date;
import java.util.HashMap;

/**
 * @author maple on 2019/5/15 16:23.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class Loc extends Activity implements AMapLocationListener {
    public static final String TAG = "loc";
    private final static String mMockProviderName = LocationManager.GPS_PROVIDER;
    private LocationManager mLocManger;
    private MapView mMapView;
    private AMap aMap;

    private Marker mMarker;
    private AMapLocation mLocation;
    private AMapLocationClient client;
    private Handler mHandler;
    private Runnable mRunable;
    private static double mStep = 0.0001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_loc);
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        showLocal();
        initVitrualocation();
        mHandler = new Handler();
        mRunable = new Runnable() {
            @Override
            public void run() {
                updateLoc();
                mHandler.postDelayed(this, 1000);
            }
        };
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.widget_mov, null);
        view.findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation.setLatitude(mLocation.getLatitude() + mStep);
                updateLoc();
            }
        });
        view.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation.setLongitude(mLocation.getLongitude() - mStep);
                updateLoc();
            }
        });
        view.findViewById(R.id.after).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation.setLatitude(mLocation.getLatitude() - mStep);
                updateLoc();
            }
        });
        view.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation.setLongitude(mLocation.getLongitude() + mStep);
                updateLoc();
            }
        });
        FloatWindow.with(getApplicationContext())
                .setWidth(dp2px(144))                               //设置控件宽高
                .setHeight(dp2px(144))
                .setX(100)                                   //设置控件初始位置
                .setY(Screen.height, 0.3f)
                .setView(view)
                .setDesktopShow(true)
                .build();

    }
    private int dp2px(int dp){
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);

    }
    private void showLocal() {
        client = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        client.setLocationListener(this);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        client.startLocation();
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mLocation.setLongitude(latLng.longitude);
                mLocation.setLatitude(latLng.latitude);
                if (mMarker != null) mMarker.destroy();
                mMarker = aMap.addMarker(new MarkerOptions().position(latLng).title("穿越点").snippet("DefaultMarker"));
                Log.i(TAG, "onMapClick: " + latLng.longitude + "," + latLng.latitude);
            }
        });

    }


    private void initVitrualocation() {
        mLocManger = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationProvider provider = mLocManger.getProvider(mMockProviderName);
        mLocManger.addTestProvider(mMockProviderName, provider.requiresNetwork(), provider.requiresSatellite(),
                provider.requiresCell(), provider.hasMonetaryCost(), provider.supportsAltitude(),
                provider.supportsSpeed(), provider.supportsBearing(), provider.getPowerRequirement(), provider.getAccuracy());
        mLocManger.setTestProviderEnabled(mMockProviderName, true);
        mLocManger.setTestProviderStatus(mMockProviderName, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
    }

    private void updateLoc() {
        try {
            Location mockLocation = new Location(mMockProviderName);
            HashMap<String, Double> map = GPSUtil.delta(mLocation.getLatitude(), mLocation.getLongitude());
            mockLocation.setLatitude(map.get("lat"));
            mockLocation.setLongitude(map.get("lon"));
            mockLocation.setAltitude(mLocation.getAltitude());
            mockLocation.setBearing(mLocation.getBearing());
            mockLocation.setSpeed(mLocation.getSpeed());
            mockLocation.setAccuracy(mLocation.getAccuracy());
            mockLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            mLocManger.setTestProviderLocation(mMockProviderName, mockLocation);
            //mLocManger.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, mockLocation);
            //mLocManger.setTestProviderLocation(LocationManager.PASSIVE_PROVIDER, mockLocation);

        } catch (Exception e) {
            Log.e(TAG, "updateLoc: " + e.getMessage());
            stopMockLoc();
        }
    }

    private void stopMockLoc() {
        try {
            mLocManger.removeTestProvider(mMockProviderName);
            // mLocManger.removeTestProvider(LocationManager.NETWORK_PROVIDER);
            // mLocManger.removeTestProvider(LocationManager.PASSIVE_PROVIDER);
        } catch (Exception e) {
            Log.e(TAG, "stopMockLoc: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        stopMockLoc();
        mHandler.removeCallbacks(mRunable);
        FloatWindow.destroy();
    }

    @Override
    public void onLocationChanged(AMapLocation loc) {
        mLocation = loc;
        Log.i(TAG, "onLocationChanged: " + loc.getLongitude() + "," + loc.getLatitude() + loc.getAddress());
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        mMarker = aMap.addMarker(new MarkerOptions().position(latLng).title("穿越点").snippet("DefaultMarker"));
        if (client != null) client.stopLocation();
        mHandler.post(mRunable);
    }

    private void showWidget() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Button button = new Button(getApplicationContext());
        button.setText("a");
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }

        });
        button.setBackgroundColor(Color.BLUE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = 500;
        layoutParams.height = 500;
        layoutParams.x = 100;
        layoutParams.y = 100;
        windowManager.addView(button, layoutParams);
    }
}
