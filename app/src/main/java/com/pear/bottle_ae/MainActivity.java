package com.pear.bottle_ae;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pear.bottle_ae.Model.RegisterUser;
import com.pear.bottle_ae.Model.ResponseUser;
import com.pear.bottle_ae.Service.Factory;
import com.pear.bottle_ae.Service.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private View view_load,view_register;
    private LayoutInflater layoutInflater;
    private ArrayList<View> arrayList = new ArrayList<View>();
    private ArrayList<String> list_titles = new ArrayList<String>();
    private ViewPager pager;
    private TabLayout tabLayout;
    private Button load_button , register_button;
    private EditText load_name,load_pwd,register_name,register_nickname,register_pwd,register_con_pwd;
    private RadioGroup radioGroup;
    private String url =" https://bottle.resetbypear.com/api/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Retrofit retrofit = Factory.createRetrofit(url , MainActivity.this);
        final Services services = retrofit.create(Services.class);
        layoutInflater = LayoutInflater.from(this);
        pager = (ViewPager) findViewById(R.id.page);
        init();
        load_button.setOnClickListener(loadOnclickListener(services));
        register_button.setOnClickListener(registerOnclickListener(services));

        checkAndApplyNecessaryPermission(MainActivity.this);
    }
    /*
    初始化所有变量以及页面
    Created by jiayin on 2017/12/26.
     */
    public void init() {
        view_load = layoutInflater.inflate(R.layout.load,null);
        view_register = layoutInflater.inflate(R.layout.register , null);
        tabLayout = findViewById(R.id.tab);
        load_name = view_load.findViewById(R.id.load_name_edit);
        load_pwd = view_load.findViewById(R.id.load_psd_edit);
        register_name = view_register.findViewById(R.id.register_name_edit);
        register_pwd = view_register.findViewById(R.id.register_pwd_edit);
        register_con_pwd = view_register.findViewById(R.id.register_con_pwd_edit);
        register_nickname = view_register.findViewById(R.id.register_nickname_edit);
        radioGroup = view_register.findViewById(R.id.buttons);
        arrayList.add(view_load);
        arrayList.add(view_register);
        list_titles.add("登录");
        list_titles.add("注册");
        tabLayout.addTab(tabLayout.newTab().setText(list_titles.get(0)) , true);
        tabLayout.addTab(tabLayout.newTab().setText(list_titles.get(1)));
        MySimpleAdapter myAdapter = new MySimpleAdapter(arrayList);
        pager.setAdapter(myAdapter);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabsFromPagerAdapter(myAdapter);
        load_button = view_load.findViewById(R.id.load);
        register_button = view_register.findViewById(R.id.register);
    }
    /*
        用于适配viewPager 实现滑动
        Created by jiayin on 2017/12/26.
         */
    class MySimpleAdapter extends PagerAdapter {
        private ArrayList<View> arrayList_view = new ArrayList<View>();
        MySimpleAdapter(ArrayList<View> list) {
            arrayList_view = list;
        }
        @Override
        public int getCount() {
            return arrayList_view.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(arrayList_view.get(position));//添加页卡
            return arrayList_view.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(arrayList_view.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list_titles.get(position);//页卡标题
        }

    }
    private View.OnClickListener registerOnclickListener(final Services services) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int buttonId = radioGroup.getCheckedRadioButtonId();
                final String username = register_name.getText().toString()+"";
                String nickname = register_nickname.getText().toString();
                final String pwd = register_pwd.getText().toString();
                String con_pwd = register_con_pwd.getText().toString();
                if (username.isEmpty()) {
                    register_name.setError("用户名不能为空");
                } else if (nickname.isEmpty()) {
                    register_nickname.setError("昵称不能为空");
                } else if (pwd.isEmpty()) {
                    register_pwd.setError("密码不能为空");
                } else if (con_pwd.isEmpty()) {
                    register_con_pwd.setError("确认密码不能为空");
                } else if (buttonId == -1){
                    TextView radioButton = view_register.findViewById(R.id.sex);
                    radioButton.setError("请选择性别");
                } else {
                    if (con_pwd.equals(pwd)) {
                        RadioButton radioButton = view_register.findViewById(radioGroup.getCheckedRadioButtonId());
                        String gender = radioButton.getText().toString();
                        Map<String , String > map = new HashMap<>();
                        map.put("username" , username);
                        map.put("nickname" , nickname);
                        map.put("password" , pwd);
                        RegisterUser registerUser = new RegisterUser();
                        registerUser.setNickname(nickname);
                        registerUser.setPwd(pwd);
                        registerUser.setUsername(username);
                        if (gender.equals("男")) {
                            registerUser.setGender("male");
                            map.put("gender" , "male");
                        } else {
                            registerUser.setGender("female");
                            map.put("gender" , "female");
                        }
                        Log.i("before" , "aaa");
                        Gson gson=new Gson();
                        String strEntity = gson.toJson(map);
                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
                        services.postUser(body)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<ResponseUser>() {
                                    @Override
                                    public void onCompleted() {
                                        final ProgressBar progressBar = findViewById(R.id.progressbar);
                                        tabLayout.setVisibility(View.GONE);
                                        pager.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.VISIBLE);
                                        Map<String , String > map = new HashMap<>();
                                        map.put("username" , username);
                                        map.put("password" , pwd);
                                        Gson gson=new Gson();
                                        String strEntity = gson.toJson(map);
                                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
                                        services.loadUser(body)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Subscriber<ResponseUser>() {
                                                    @Override
                                                    public void onCompleted() {
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        HttpException exception = (HttpException) e;
                                                        if (exception.response().code() == 403) {
                                                            load_name.setError("用户名或密码错误");
                                                            Log.i("errorcode" , "403");
                                                        }
                                                    }

                                                    @Override
                                                    public void onNext(ResponseUser user) {
                                                        startActivity(new Intent(MainActivity.this,MainActivity1.class));
                                                    }
                                                });
                                        //Toast.makeText(MainActivity.this , "注册成功，请切换至登陆页面登录" , Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        HttpException exception = (HttpException) e;
                                        if (exception.response().code() == 409) {
                                            register_name.setError("用户名已存在");
                                        }
                                    }

                                    @Override
                                    public void onNext(ResponseUser user) {
                                        Log.i("error",user.getStatus());
                                    }
                                });
                    } else {
                        register_con_pwd.setError("请输入和密码一致的确认密码");
                    }
                }
            }
        };
    }
    private View.OnClickListener loadOnclickListener(final Services services) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = load_name.getText().toString();
                final String pwd = load_pwd.getText().toString();
                if (name.isEmpty()) {

                    load_name.setError("请输入用户名");
                } else if (pwd.isEmpty()) {
                    load_pwd.setError("请输入密码");
                } else {
                    final ProgressBar progressBar = findViewById(R.id.progressbar);
                    tabLayout.setVisibility(View.GONE);
                    pager.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String , String > map = new HashMap<>();
                    map.put("username" , name);
                    map.put("password" , pwd);
                    Gson gson=new Gson();
                    String strEntity = gson.toJson(map);
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
                    services.loadUser(body)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ResponseUser>() {
                                @Override
                                public void onCompleted() {
                                    finish();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    HttpException exception = (HttpException) e;
                                    if (exception.response().code() == 403) {
                                        load_name.setError("用户名或密码错误");
                                        Log.i("errorcode" , "403");
                                    }
                                }

                                @Override
                                public void onNext(ResponseUser user) {
                                    startActivity(new Intent(MainActivity.this,MainActivity1.class));
                                }
                            });

                }
            }
        };
    }

    /**
     * Added by Bowen Wu in 2018/01/07
     * Aim to check permission needed by our App
     */
    public static void checkAndApplyNecessaryPermission(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                                            Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
        List<String> permissionsNeedToApply = new ArrayList<>();
        for (int i = 0; i < permissions.length; ++i) {
            if (ActivityCompat.checkSelfPermission(activity, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                permissionsNeedToApply.add(permissions[i]);
            }
        }
        // 申请权限
        try {
            String[] arrayPermissions = new String[permissionsNeedToApply.size()];
            permissionsNeedToApply.toArray(arrayPermissions);
            ActivityCompat.requestPermissions(activity, arrayPermissions, 1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 权限回调出现了诡异的问题，填坑
     * @return 是否包含所有的必要权限
     */
    public static boolean whetherHaveAllNecessaryPermission(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
        for (int i = 0; i < permissions.length; ++i) {
            if (ActivityCompat.checkSelfPermission(activity, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            String[] permissions,
                                            int[] grantResults) {
        if (whetherHaveAllNecessaryPermission(MainActivity.this) == false) {
                // 一些权限没有获得，弹出对话框，退出或者重新申请权限
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.fail_to_get_permission);
                builder.setMessage(R.string.no_permission_handle);
                builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 重新请求权限
                        MainActivity.checkAndApplyNecessaryPermission(MainActivity.this);
                    }
                });
                builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 退出应用
                        System.exit(0);
                    }
                });
                builder.show();

            }
        }
}

