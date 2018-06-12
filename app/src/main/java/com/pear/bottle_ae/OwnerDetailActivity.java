package com.pear.bottle_ae;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.pear.bottle_ae.Adapter.CommonAdapter;
import com.pear.bottle_ae.Adapter.ViewHolder;
import com.pear.bottle_ae.Model.BottleLocation;
import com.pear.bottle_ae.Model.Opener;
import com.pear.bottle_ae.Model.ResponseReadersList;
import com.pear.bottle_ae.Service.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OwnerDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter;

    private TextView detail_content, detail_time, detail_name;
    private ImageView detail_img;
    private HashMap<String, Object> data;
    private List<LinkedHashMap<String, Object>> openers_data;

    public ResponseReadersList responseReadersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openers_list);
        init();
        setCommonAdapter();
    }

    void init() {
        detail_content = (TextView) findViewById(R.id.detail_content);
        detail_name = (TextView) findViewById(R.id.detail_name);
        detail_time = (TextView) findViewById(R.id.detail_time);
        detail_img = (ImageView) findViewById(R.id.detail_img);
        data = (HashMap<String, Object>) getIntent().getExtras().get("data");
        if (data != null) {
            detail_content.setText(data.get("content").toString());
            int type = (int) data.get("type");
            if (type == 0) {
                detail_img.setImageResource(R.drawable.bottle_small_blue);
            } else if (type == 1) {
                detail_img.setImageResource(R.drawable.bottle_small_green);
            } else if (type == 2) {
                detail_img.setImageResource(R.drawable.bottle_small_yellow);
            } else  {
                detail_img.setImageResource(R.drawable.bottle_small_gray);
            }
            detail_time.setText(data.get("time").toString());
            BottleLocation bottleLocation = (BottleLocation) data.get("location");
            detail_name.setText(bottleLocation.formatted_address);
        }
        getData((int)data.get("id"));
    }

    void getData(int bottle_id) {
        openers_data = new ArrayList<>();
        Factory.getServices(OwnerDetailActivity.this).getBottleOpeners(bottle_id).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<ResponseReadersList>() {
                @Override
                public void onCompleted() {
                    Log.i("Service","ok");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Service", e.getMessage());
                }

                @Override
                public void onNext(ResponseReadersList usersList) {
                    responseReadersList = usersList;
                    List<Opener> openers = responseReadersList.data.openers;
                    for (int i = 0; i < openers.size(); i++) {
                        Opener opener = openers.get(i);
                        LinkedHashMap<String, Object> temp = new LinkedHashMap<>();
                        temp.put("nickname", opener.getNickname());
                        temp.put("gender", opener.getGender());
                        temp.put("time", opener.getOpenTime());
                        openers_data.add(temp);
                    }
                    commonAdapter.notifyDataSetChanged();
                }
            });
    }
    void setCommonAdapter() {
        recyclerView = (RecyclerView)findViewById(R.id.openers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter = new CommonAdapter<Map<String, Object>>(this, R.layout.list_item_user, openers_data) {
            @Override
            public void convert(ViewHolder holder, Map<String, Object> s) {
                TextView time = holder.getView(R.id.item_time);
                TextView nickname = holder.getView(R.id.item_nickname);
                TextView gender = holder.getView(R.id.item_gender);
                ImageView pic = holder.getView(R.id.item_pic);
                time.setText(s.get("time").toString());
                nickname.setText(s.get("nickname").toString());
                String g = s.get("gender").toString();
                gender.setText(g.equals("female") ? "女" : "男");
                pic.setImageResource(g.equals("female")
                        ? R.drawable.woman_in_bottle_content
                        : R.drawable.man_in_bottle_content);
            }
        };
        recyclerView.setAdapter(commonAdapter);
    }
}
