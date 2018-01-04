package com.pear.bottle_ae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Created by Young on 2017/12/27.
 */

public class PersonalActivity extends AppCompatActivity {
    private LinearLayout layout1, layout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        layout1 = (LinearLayout)findViewById(R.id.layout1);
        layout2 = (LinearLayout)findViewById(R.id.layout2);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "123", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PersonalActivity.this,BottleActivity.class);
                intent.putExtra("isPick", true);
                startActivity(intent);
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "123", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PersonalActivity.this,BottleActivity.class);
                intent.putExtra("isPick", false);
                startActivity(intent);
            }
        });
    }
}