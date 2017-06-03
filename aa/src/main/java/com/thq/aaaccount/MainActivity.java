package com.thq.aaaccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        if (Utils.getLastestActivityId() < 0) {
            Utils.setSPInt("activitynums", 0, "allactivity");
        }
    }

    public void createAction(View view) {
        Intent intent = new Intent(MainActivity.this, CreateActionActivity.class);
        startActivity(intent);
    }

    public void gotoAllAction(View view) {
        Intent intent = new Intent(MainActivity.this, ViewAllActionActivity.class);
//        intent.putExtra("activityId", ""+Utils.getLastestActivityId(this));
        startActivity(intent);
    }

    public void gotoAction(View view) {
        if (Utils.getLastestActivityId() > 0) {
            Intent intent = new Intent(MainActivity.this, ViewActionItemActivity.class);
            intent.putExtra("activityId", "" + Utils.getLastestActivityId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }


    public void createActionItem(View view) {
        if (Utils.getLastestActivityId() > 0) {
            Intent intent = new Intent(MainActivity.this, CreateActionItemActivity.class);
//            intent.putExtra("activityName", itemName);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }
}
