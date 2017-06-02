package com.thq.aaaccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private Toolbar mToolbar;


    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        mSP = getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor = mSP.edit();
    }

    public void createAction(View view) {
        Intent intent = new Intent(MainActivity.this, CreateActionActivity.class);
        startActivity(intent);
    }

    public void gotoAction(View view) {
        Intent intent = new Intent(MainActivity.this, ViewActionItemActivity.class);
        startActivity(intent);
    }


    public void createActionItem(View view) {
        if ( mSP.getStringSet("Members", null) != null) {
            Intent intent = new Intent(MainActivity.this, CreateActionItemActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }
}
