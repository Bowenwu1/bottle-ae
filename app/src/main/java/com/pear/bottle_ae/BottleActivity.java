package com.pear.bottle_ae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.pear.bottle_ae.Adapter.CommonAdapter;
import com.pear.bottle_ae.Adapter.ViewHolder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Young on 2018/1/4.
 */

public class BottleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommonAdapter commonAdapter;
    private List<Map<String, Object>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getBooleanExtra("isPick", true)) {
            getWindow().setTitle("捡到的瓶子");
        } else {
            getWindow().setTitle("扔出的瓶子");
        }
        setContentView(R.layout.bottle_list);
        data = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter = new CommonAdapter(this, R.layout.list_item, data) {
            @Override
            public void convert(ViewHolder holder, Object o) {
                TextView time = holder.getView(R.id.item_time);
                TextView content = holder.getView(R.id.item_content);
                TextView read = holder.getView(R.id.item_read);
                ImageView pic = holder.getView(R.id.item_pic);
                time.setText("123");
                content.setText("456");
                read.setText("789");
                pic.setImageResource(R.drawable.bottle_in_pickuplist_yellow);
            }
        };
        recyclerView.setAdapter(commonAdapter);
    }
}