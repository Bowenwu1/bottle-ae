package com.pear.bottle_ae.Activitiy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.pear.bottle_ae.R;

import org.w3c.dom.Text;

import java.util.LinkedHashMap;

/**
 * Created by Young on 2018/1/6.
 */

public class DetailActivity  extends AppCompatActivity {
    private TextView detail_content, detail_time, detail_name;
    private ImageView detail_img;

    private LinkedHashMap<String, Object> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detail_content = (TextView)findViewById(R.id.detail_content);
        detail_name = (TextView)findViewById(R.id.detail_name);
        detail_time = (TextView)findViewById(R.id.detail_time);
        detail_img = (ImageView)findViewById(R.id.detail_img);
        data = (LinkedHashMap<String, Object>)getIntent().getExtras().get("data");
        if (data != null) {
            detail_content.setText(data.get("content").toString());
            switch ((int)data.get("style")) {
                    case 0:
                        detail_img.setImageResource(R.drawable.bottle_small_blue);
                    case 1:
                        detail_img.setImageResource(R.drawable.bottle_small_green);
                    case 2:
                        detail_img.setImageResource(R.drawable.bottle_small_yellow);
                    default:
                        detail_img.setImageResource(R.drawable.bottle_small_gray);
            }
            detail_time.setText(data.get("time").toString());
            detail_name.setText(data.get("location").toString());
        }
    }
}
