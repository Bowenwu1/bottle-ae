package com.pear.bottle_ae;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.pear.bottle_ae.R;

import okhttp3.RequestBody;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wubowen on 2018/1/2.
 */

public class MapActivity extends AppCompatActivity {
    private MapView mapView = null;
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle;
    private UiSettings uiSettings;
    private FloatingActionButton add_bottle_button;
    private View dialogView;
    public String formatted_address = "";
    public Location myLocation;
    public GeocodeSearch geocodeSearch;

    public ResponseBottlesList responseBottlesList;

//    public static final double LATITUDE_THRESHOLD = 0.0001;
//    public static final double LONGITUDE_THRESHOLD = 0.0001;
//
//    private CameraPosition oldCameraPosition = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init();
        //获取地图控件引用
        mapView = (MapView)findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        aMap = mapView.getMap();
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(1000 * 60); // 每分钟更新一次位置
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER); // 连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动 但我发现一开始还是会移动到蓝点处
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(onMyLocationChangeListener);
        aMap.setOnCameraChangeListener(onCameraChangeListener);
        uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);

        // 不许缩放 同时设置合适的缩放比为最大
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel()));

        // 初始化地理编码搜索对象，并设置监听
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(onGeocodeSearchListener);
        aMap.setOnMapLoadedListener(onMapLoadedListener);
        // 初始化地址描述数据
        /**
         * 扔瓶子按钮的事件监听
         */
        add_bottle_button.setOnClickListener(addButtonOnClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    private void init() {
        add_bottle_button = (FloatingActionButton)findViewById(R.id.add_bottle_button);

        // 获取dialog的view，以提升弹出速度
        //
        //
    }

    public void ToastInfo(int stringID) {
        Toast.makeText(MapActivity.this, stringID, Toast.LENGTH_SHORT).show();
    }

    private void queryGeoCode() {
        Location location = aMap.getMyLocation();
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(location.getLatitude(), location.getLongitude()), 200, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
    }

    private void getNearbyBottles(double latitude, double longitude) {
        System.out.println("getNearbyBottles");
        Factory.getServices(MapActivity.this).getNearbyBottle(generateQueryMapForBottlesNearby(latitude, longitude, Bottle.SPAN_LATITUDE, Bottle.SPAN_LONGITUDE))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBottlesList>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted get nearby bottle completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("ERROR " + e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBottlesList responseBottlesList) {
                        Gson gson = new Gson();
                        String data = gson.toJson(responseBottlesList);
                        System.out.println("onNext data : " + data);
                        MapActivity.this.responseBottlesList = responseBottlesList;
                        MapActivity.this.refreshMapMarker();
                    }
                });
    }

    private Map<String, String> generateQueryMapForBottlesNearby(double latitude, double longitude, double span_latitude, double span_longitude) {
        Map<String, String> result = new HashMap<>();
        result.put("latitude", Double.toString(latitude));
        result.put("longitude", Double.toString(longitude));
        result.put("latitude_span", Double.toString(span_latitude));
        result.put("longitude_span", Double.toString(span_longitude));
        return result;
    }
    private void refreshMapMarker() {

    }
    // 各种 listener
    private GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            System.out.println("onRegeocodeSearched : " + regeocodeResult.getRegeocodeAddress().getFormatAddress());
            // not used
            if (1000 == i) {
                // 1000是反编码成功的错误码
                formatted_address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        }
    };

    private AMap.OnCameraChangeListener onCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            double latitude = cameraPosition.target.latitude;
            double longitude = cameraPosition.target.longitude;
            System.out.println("onCameraChange latitude : " + Double.toString(latitude) + " longitude : " + Double.toString(longitude));
        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            double latitude = cameraPosition.target.latitude;
            double longitude = cameraPosition.target.longitude;
            System.out.println("onCameraChangeFinish latitude : " + Double.toString(latitude) + " longitude : " + Double.toString(longitude));
            getNearbyBottles(latitude, longitude);

        }
    };

    private AMap.OnMyLocationChangeListener onMyLocationChangeListener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            queryGeoCode();
            myLocation = location;
            String positon = "";
            positon += new Double(location.getLongitude()).toString();
            positon += " ";
            positon += new Double(location.getLatitude()).toString();
            Log.d("Location", positon);
            System.out.println("onMyLocationChange : " + positon);
        }
    };

    private AMap.OnMapLoadedListener onMapLoadedListener = new AMap.OnMapLoadedListener() {
        @Override
        public void onMapLoaded() {
//            queryGeoCode();
        }
    };

    private View.OnClickListener addButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 弹出对话框
            LayoutInflater layoutInflater = LayoutInflater.from(MapActivity.this);
            final View dialogView = layoutInflater.inflate(R.layout.add_bottle_dialog, null);
            final TextInputLayout bottleLayout = (TextInputLayout)dialogView.findViewById(R.id.bottle_content_layout);
            final TextInputEditText bottleContent = (TextInputEditText)dialogView.findViewById(R.id.bottle_content);
            // RadioGroup的交互可以提升，后续有时间的话做
            final RadioGroup bottleType = (RadioGroup)dialogView.findViewById(R.id.bottle_type);
            // 缺省是普通瓶子
            final RadioButton defalutChoice = (RadioButton)dialogView.findViewById(R.id.bottle_type1);
            defalutChoice.setChecked(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            builder.setView(dialogView);
            builder.setNegativeButton(R.string.cancle, null);
            builder.setPositiveButton(R.string.send_bottle, null);
            // 为了手动控制对话框的消失
            // 感谢 https://www.jianshu.com/p/fc373d74b0c5 提出的解决方案
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            if (alertDialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("CLICK", "CLICK ON dialog positive button");
                        int checkedRadioButtonID = bottleType.getCheckedRadioButtonId();
                        int type = -1;
                        String content = bottleContent.getText().toString();
                        // 有缺省值，无需判断考虑是否会出现没选择的情况
                        switch (checkedRadioButtonID) {
                            case R.id.bottle_type1:
                                type = Bottle.STYLE_NORMAL;
                                break;
                            case R.id.bottle_type2:
                                type = Bottle.STYLE_VIP;
                                break;
                            case R.id.bottle_type3:
                                type = Bottle.STYLE_POOR_VIP;
                                break;
                            default:
                                type = Bottle.STYLE_NORMAL;
                        }
                        // 判断输入内容是否为空，如果为空，令TextInputLayout提示错误信息
                        // 且对话框不会因为确定的点击而消失
                        if (content.isEmpty()) {
                            bottleLayout.setErrorEnabled(true);
                            bottleContent.setError("瓶子内容不能为空");
                            return;
                        } else {
                            // 输入内容非空，发出请求，且告诉用户正在发送中，对话框自动消失
                            ToastInfo(R.string.sending);
                            Bottle bottleToSend;
                            if (null == myLocation) {
                                bottleToSend = new Bottle(content, type, 113.39195, 23.06656, formatted_address);
                            } else {
                                bottleToSend = new Bottle(content, type, myLocation.getLatitude(), myLocation.getLongitude(), formatted_address);
                            }
                            RequestBody requestBody = PostBodyHelper.ObjToRequestBody(bottleToSend);
                            System.out.println(requestBody.toString());
                            Factory.getServices(MapActivity.this).postBottle(requestBody)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<ResponseBottle>() {
                                        @Override
                                        public void onCompleted() {
                                            System.out.println("send bottle completed");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            System.out.println(e.getMessage());
                                            System.out.println(e.getCause());
                                            ToastInfo(R.string.send_fail);
                                        }

                                        @Override
                                        public void onNext(ResponseBottle bottle) {
                                            System.out.println(new Gson().toJson(bottle) + "  onNext");
                                            ToastInfo(R.string.send_complete);
                                        }
                                    });
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        }
    };
}
