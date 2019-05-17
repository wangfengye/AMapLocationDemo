package com.amap.location.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class Purpose_SignIn_Activity extends CheckPermissionsActivity implements View.OnClickListener, AMapLocationListener{

    TextView tvResult = null;
    Button btnSign = null;

    AMapLocationClient locationClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purpose_signin);
        setTitle(getResources().getString(R.string.signInPurpose));
        tvResult = (TextView) findViewById(R.id.tv_result);
        btnSign = (Button) findViewById(R.id.btn_signin);
        btnSign.setOnClickListener(this);

        locationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        /**
         * 设置签到场景，相当于设置为：
         * option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
         * option.setOnceLocation(true);
         * option.setOnceLocationLatest(true);
         * option.setMockEnable(false);
         * option.setWifiScan(true);
         * option.setGpsFirst(false);
         * 其他属性均为模式属性。
         * 如果要改变其中的属性，请在在设置定位场景之后进行
         */

        locationClient.setLocationOption(option);
        //设置定位监听
        locationClient.setLocationListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_signin){
            tvResult.setText("正在获取位置...");
            if(null != locationClient) {
                //签到只需调用startLocation即可
                locationClient.startLocation();
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            tvResult.setText("签到成功，签到经纬度：(" + aMapLocation.getLatitude() + "," + aMapLocation.getLongitude()+ ")");
        } else {
            //可以记录错误信息，或者根据错误错提示用户进行操作，Demo中只是打印日志
            Log.e("AMap", "签到定位失败，错误码：" + aMapLocation.getErrorCode() + ", " + aMapLocation.getLocationDetail());
            //提示错误信息
            tvResult.setText("签到失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时，需要销毁定位client
        if(null != locationClient){
            locationClient.onDestroy();
        }
    }
}
