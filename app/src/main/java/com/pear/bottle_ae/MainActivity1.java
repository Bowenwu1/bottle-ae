package com.pear.bottle_ae;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.pear.bottle_ae.Model.ResponseUser;
import com.pear.bottle_ae.Model.User;
import com.pear.bottle_ae.Adapter.MyPagerAdapter;
import com.pear.bottle_ae.Service.Factory;
import com.pear.bottle_ae.Service.Services;

import java.util.ArrayList;
import java.util.List;

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
    private MapView mapView = null;
    private AMap aMap = null;
    private MyLocationStyle myLocationStyle;
    private UiSettings uiSettings;
    private Services services;
    private TextView nickname, loadname;
    private ImageView personal_img;
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
                Toast.makeText(getApplicationContext(), "123", Toast.LENGTH_LONG).show();
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
        //获取地图控件引用
        mapView = (MapView)homePage.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        aMap = mapView.getMap();
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW); // 连续定位，且将视角移到地图中心点
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                String positon = "";
                positon += new Double(location.getLongitude()).toString();
                positon += " ";
                positon += new Double(location.getLatitude()).toString();
                Log.d("Location", positon);
            }
        });
        uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);

        // 不许缩放
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(false);
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
}