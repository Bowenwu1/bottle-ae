package com.pear.bottle_ae;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.pear.bottle_ae.Model.Bottle;
import com.pear.bottle_ae.Model.ResponseBottle;
import com.pear.bottle_ae.Model.ResponseBottlesList;
import com.pear.bottle_ae.Model.ResponseUser;
import com.pear.bottle_ae.Model.User;
import com.pear.bottle_ae.Adapter.MyPagerAdapter;
import com.pear.bottle_ae.Service.Factory;
import com.pear.bottle_ae.Service.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Young on 2017/12/28.
 */

public class MainActivity1 extends AppCompatActivity {
    public static User owner;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private View homePage, personalPage,bottlePage;
    private List<View> viewList;
    private LinearLayout layout1, layout2;
    private Services services;
    private TextView nickname, loadname;
    private ImageView personal_img;

    /**
     * Variable for Bowen Wu
     */
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        SetViewPager();
        CreatePersonal();
        CreateMap(savedInstanceState);
    }
    private void SetViewPager() {
        LayoutInflater inflater = getLayoutInflater();
        personalPage = inflater.inflate(R.layout.activity_personal, null);
        homePage = inflater.inflate(R.layout.activity_map, null);
        viewList = new ArrayList<>();
        viewList.add(personalPage);
        viewList.add(homePage);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(viewList));
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.personal_page:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.home_page:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
    private void CreatePersonal() {
        layout1 = (LinearLayout)personalPage.findViewById(R.id.layout1);
        layout2 = (LinearLayout)personalPage.findViewById(R.id.layout2);
        nickname = (TextView)personalPage.findViewById(R.id.nickname);
        personal_img = (ImageView)personalPage.findViewById(R.id.personal_img);
        loadname = (TextView)personalPage.findViewById(R.id.loadname);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this,BottleActivity.class);
                intent.putExtra("isPick", true);
                startActivity(intent);
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this,BottleActivity.class);
                intent.putExtra("isPick", false);
                startActivity(intent);
            }
        });
        Factory.getServices(MainActivity1.this).get().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseUser>() {
                    @Override
                    public void onCompleted() {
                        Log.i("user", "completed");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("user", e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseUser responseUser) {
                        owner = responseUser.getUser();
                        if (owner.equals(null)) {
                            Log.e("user", "OWNERISNULL");
                        }
                       String _nickname =  owner.getNickname();
                       String _gender = owner.getGender();
                       String _loadname = owner.getUsername();
                       if (_gender.equals("male")) {
                           personal_img.setImageResource(R.drawable.man);
                       } else {
                             personal_img.setImageResource(R.drawable.woman);
                       }
                       nickname.setText(_nickname);
                       loadname.setText(_loadname);
                    }
                });
    }
    private void CreateMap(Bundle savedInstanceState) {
        add_bottle_button = (FloatingActionButton)homePage.findViewById(R.id.add_bottle_button);
        //获取地图控件引用
        mapView = (MapView)homePage.findViewById(R.id.map);
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
        aMap.setOnMarkerClickListener(onMarkerClickListener);
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

    /**
     * Created by Bowen Wu
     * Code for MapView
     */

    public void ToastInfo(int stringID) {
        Toast.makeText(MainActivity1.this, stringID, Toast.LENGTH_SHORT).show();
    }

    private void queryGeoCode() {
        Location location = aMap.getMyLocation();
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(location.getLatitude(), location.getLongitude()), 200, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
    }

    private void getNearbyBottles(double latitude, double longitude) {
        System.out.println("getNearbyBottles");
        Factory.getServices(MainActivity1.this).getNearbyBottle(generateQueryMapForBottlesNearby(latitude, longitude, Bottle.SPAN_LATITUDE, Bottle.SPAN_LONGITUDE))
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
                        ToastInfo(R.string.internal_error);
                    }

                    @Override
                    public void onNext(ResponseBottlesList responseBottlesList) {
                        Gson gson = new Gson();
                        String data = gson.toJson(responseBottlesList);
                        System.out.println("onNext data : " + data);
                        clearBottleMarker();
                        MainActivity1.this.responseBottlesList = responseBottlesList;
                        MainActivity1.this.refreshMapMarker();
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
        List<Bottle> list = responseBottlesList.data.bottles;
        for (int i = 0; i < list.size(); ++i) {
            list.get(i).marker = aMap.addMarker(getMarkerForBottle(list.get(i)));
        }
    }
    private void clearBottleMarker() {
        if (null == responseBottlesList) {
            return;
        }
        for (int i = 0; i < responseBottlesList.data.bottles.size(); ++i) {
            if (null != responseBottlesList.data.bottles.get(i).marker) {
                responseBottlesList.data.bottles.get(i).marker.destroy();
            }
        }
    }
    private MarkerOptions getMarkerForBottle(Bottle bottle) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), bottle.getIconID(myLocation))));
        markerOptions.position(bottle.getLocation());
        return markerOptions;
    }
    private void openBottle(Bottle bottle) {
        System.out.println(bottle.bottle_id);
        // 这是一个距离过远的瓶子，不能打开
        if (!bottle.whetherInArea(myLocation)) {
            ToastInfo(R.string.too_far_to_open_bottle);
            return;
        }
        ToastInfo(R.string.opening_bottle);
        Factory.getServices(MainActivity1.this).openBottle(bottle.bottle_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<ResponseBottle>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        HttpException httpException = (HttpException)e;
                        System.out.println("open Bottle onError : " + httpException.response().code());
                        ToastInfo(R.string.internal_error);
                    }

                    @Override
                    public void onNext(ResponseBottle bottle) {
                        // 显示对话框
//                        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity1.this);
//                        View dialogView = layoutInflater.inflate(R.layout.open_bottle_dialog, null);
//                        TextView content = (TextView)dialogView.findViewById(R.id.content);
//                        content.setText(bottle.data.content);
//                        TextView formatted_address = (TextView)dialogView.findViewById(R.id.formatted_address);
//                        formatted_address.setText(bottle.data.location.formatted_address);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity1.this);
//                        builder.setView(dialogView);
//                        builder.show();
                        // 跳转到detail
                        Intent intent = new Intent(MainActivity1.this, DetailActivity.class);
                        intent.putExtras(bottle.data.toDetailBundle());
                        startActivity(intent);

                    }
                });
    }
    // 各种 listener
    private GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//            System.out.println("onRegeocodeSearched : " + regeocodeResult.getRegeocodeAddress().getFormatAddress());
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

    private AMap.OnMarkerClickListener onMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            System.out.println("onMarkerClick : " + marker.toString());
            // 遍历寻找marker对应的瓶子
            // 当瓶子数量增多时，这样处理可能会造成ANR
            // 为了先实现功能，姑且这样
            // by Bowen Wu in 2018/01/05
            List<Bottle> bottlesList = responseBottlesList.data.bottles;
            for (int i = 0; i < bottlesList.size(); ++i) {
                // 判断是不是同一个marker，即引用相同
                // 相同，弹出对话框显示详情
                // 并且通知服务端，我开启了这个瓶子
                if (bottlesList.get(i).marker.equals(marker)) {
                    System.out.println("find corresponding marker : " + bottlesList.get(i).content);
                    openBottle(bottlesList.get(i));
                }
            }
            return true;
        }
    };
    private View.OnClickListener addButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 弹出对话框
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity1.this);
            final View dialogView = layoutInflater.inflate(R.layout.add_bottle_dialog, null);
            final TextInputLayout bottleLayout = (TextInputLayout)dialogView.findViewById(R.id.bottle_content_layout);
            final TextInputEditText bottleContent = (TextInputEditText)dialogView.findViewById(R.id.bottle_content);
            // RadioGroup的交互可以提升，后续有时间的话做
            final RadioGroup bottleType = (RadioGroup)dialogView.findViewById(R.id.bottle_type);
            // 缺省是普通瓶子
            final RadioButton defalutChoice = (RadioButton)dialogView.findViewById(R.id.bottle_type1);
            defalutChoice.setChecked(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity1.this);
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
                                bottleToSend = new Bottle(content, type, 113.39195, 23.06656, formatted_address, null);
                            } else {
                                bottleToSend = new Bottle(content, type, myLocation.getLatitude(), myLocation.getLongitude(), formatted_address, null);
                            }
                            RequestBody requestBody = PostBodyHelper.ObjToRequestBody(bottleToSend);
                            System.out.println(requestBody.toString());
                            Factory.getServices(MainActivity1.this).postBottle(requestBody)
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