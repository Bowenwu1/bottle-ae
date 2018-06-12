package com.pear.bottle_ae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pear.bottle_ae.Adapter.CommonAdapter;
import com.pear.bottle_ae.Adapter.ViewHolder;
import com.pear.bottle_ae.Model.Bottle;
import com.pear.bottle_ae.Model.ResponseBottlesList;
import com.pear.bottle_ae.Service.Factory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Young on 2018/1/4.
 * Edited by zhuojun on 2018/6/12.
 */

public class BottleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter;
    private List<LinkedHashMap<String, Object>> data;
   // private Services services;
    private boolean isPick;
    public ResponseBottlesList responseBottlesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.bottle_list);
        setCommonAdapter();

    }
    void init() {
        String type = "";
        if(getIntent().getBooleanExtra("isPick", true)) {
            getWindow().setTitle("捡到的瓶子");
            type = "opened";
            isPick = true;
        } else {
            getWindow().setTitle("扔出的瓶子");
            type = "created";
            isPick = false;
        }
        getData(type);
    }
    void getData(String type) {
        data = new ArrayList<>();
        Factory.getServices(BottleActivity.this).getBottle(type).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBottlesList>() {
                    @Override
                    public void onCompleted() {
                        Log.i("Service","ok");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Service", e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBottlesList bottlesList) {
                        responseBottlesList = bottlesList;
                        List<Bottle> bottles= responseBottlesList.data.bottles;
                        for (int i = 0; i < bottles.size(); i++) {
                            Bottle b = bottles.get(i);
                            LinkedHashMap<String, Object> temp = new LinkedHashMap<>();
                            temp.put("id", b.bottle_id);
                            temp.put("location", b.location);
                            temp.put("type", b.style);
                            temp.put("time", b.created_at);
                            temp.put("content",b.content);
                            if (!isPick) {
                                temp.put("readcount",b.openers_count);
                            } else {
                                // temp.put("onwer", b.onwer);
                            }
                            data.add(temp);
                        }
                        commonAdapter.notifyDataSetChanged();
                    }
                });
    }
    void setCommonAdapter() {
        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter = new CommonAdapter<Map<String, Object>>(this, R.layout.list_item, data) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> s) {
                TextView time = holder.getView(R.id.item_time);
                TextView content = holder.getView(R.id.item_content);
                ImageView pic = holder.getView(R.id.item_pic);
                time.setText(s.get("time").toString());
                content.setText(s.get("content").toString());
                if (!isPick) {
                    TextView read = holder.getView(R.id.item_read);
                    read.setText(s.get("readcount")+"人看过");
                }
                switch ((int)s.get("type")) {
                    case 0:
                        pic.setImageResource(R.drawable.bottle_in_pickuplist_blue);
                        break;
                    case 1:
                        pic.setImageResource(R.drawable.bottle_in_pickuplist_green);
                        break;
                    case 2:
                        pic.setImageResource(R.drawable.bottle_in_pickuplist_yellow);
                        break;
                }
            }
        };
        recyclerView.setAdapter(commonAdapter);
        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent;
                if (isPick) {
                    intent = new Intent(BottleActivity.this, DetailActivity.class);
                } else {
                    intent = new Intent(BottleActivity.this, OwnerDetailActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", data.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {
                //  do nothing
            }
        });
    }
}