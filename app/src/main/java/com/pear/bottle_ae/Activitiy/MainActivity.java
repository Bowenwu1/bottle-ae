package com.pear.bottle_ae.Activitiy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pear.bottle_ae.Factory;
import com.pear.bottle_ae.Model.User;
import com.pear.bottle_ae.R;
import com.pear.bottle_ae.RegisterUser;
import com.pear.bottle_ae.Services;

import java.util.ArrayList;
import java.util.HashMap;
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


    }
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
                String username = register_name.getText().toString()+"";
                String nickname = register_nickname.getText().toString();
                String pwd = register_pwd.getText().toString();
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
                                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                                }
                            });

                }
            }
        };
    }
}

