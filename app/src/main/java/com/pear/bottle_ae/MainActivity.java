package com.pear.bottle_ae;

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

import java.util.ArrayList;

import retrofit2.Retrofit;
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
    private String url =" https://bottle.resetbypear.com/api/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retrofit retrofit = Factory.createRetrofit(url);
        final Services services = retrofit.create(Services.class);
        layoutInflater = LayoutInflater.from(this);
        pager = (ViewPager) findViewById(R.id.page);
        init();
        load_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = load_name.getText().toString();
                final String pwd = load_pwd.getText().toString();
                if (name.isEmpty()) {
                    load_name.setError("请输入用户名");
                } else if (pwd.isEmpty()) {
                    load_pwd.setError("请输入密码");
                } else {
                    services.getUser(name)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<User>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.i("yes" , e.toString());
                                }

                                @Override
                                public void onNext(User user) {
                                    Log.i("yes" , "yes");
                                    String status = user.getStatus();
                                    if (status.equals("OK")) {
                                        User.detail detail = user.getDetail();
                                        if (detail.getPwd().equals(pwd)) {

                                        } else {
                                            load_pwd.setError("请输入正确的用户名");
                                        }
                                    } else {
                                        load_name.setError("用户名不存在");
                                    }
                                }
                            });
                    //
                }
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
}
