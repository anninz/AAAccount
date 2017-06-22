package com.thq.aaaccount;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.base.BaseActivity;


public class MainActivity extends BaseActivity {


    private Toolbar mToolbar;


    private CardView basicCard;
    private CardView highCard;
    private CardView useCard;

    private ImageView basicImage;
    private ImageView highImage;
    private ImageView useImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        if (Utils.getLastestActivityId() < 0) {
            Utils.setSPInt("activitynums", 0, "allactivity");
        }

//        image1 = (ImageView) findViewById(R.id.image);
//        //初始化时显示第一张图片
//        image1.setImageResource(R.drawable.pic5);
////        runImage();
//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] strings =  {Manifest.permission.WRITE_EXTERNAL_STORAGE};
             ActivityCompat.requestPermissions(this, strings, 100);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        basicCard = (CardView) findViewById(R.id.cv_main_basic);
        highCard = (CardView) findViewById(R.id.cv_main_high);
        useCard = (CardView) findViewById(R.id.cv_main_use);

        basicImage = (ImageView) findViewById(R.id.iv_main_basic);
        highImage = (ImageView) findViewById(R.id.iv_main_high);
        useImage = (ImageView) findViewById(R.id.iv_main_use);
    }

    @Override
    public void initData() {
        Glide.with(this).load(R.mipmap.heng_3).into(basicImage);
        Glide.with(this).load(R.mipmap.heng_1).into(highImage);
        Glide.with(this).load(R.mipmap.heng_4).into(useImage);
    }

    @Override
    public void initListener() {
        basicCard.setOnClickListener(this);
        highCard.setOnClickListener(this);
        useCard.setOnClickListener(this);
    }


    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.cv_main_basic:
                Intent i1 = new Intent(this, PersonalFragmentActivity.class);
                startActivity(i1, ActivityOptions.makeSceneTransitionAnimation(this, basicCard, "personal").toBundle());
                break;
            case R.id.cv_main_high:
                Intent i2 = new Intent(this, ViewActionItemActivity.class);
                startActivity(i2, ActivityOptions.makeSceneTransitionAnimation(this, highCard, "all").toBundle());
                break;
            case R.id.cv_main_use:
                Intent i3 = new Intent(this, AboutActivity.class);
                startActivity(i3, ActivityOptions.makeSceneTransitionAnimation(this, useCard, "about").toBundle());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_main_about:
//                Intent intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

 /*   public void createAction(View view) {
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
    }*/


    private void runImage() {
        handler.post(myRunnable);

    }
    private ImageView image1;
    //定义一个int数组来存放图片，在R文件中图片是以int形式存在的
    int[] images = new int[] {
//            R.drawable.pic1,
//            R.drawable.pic2,
//            R.drawable.pic1,
//            R.drawable.pic2,
//            R.drawable.pic1,
             };
    //定义一图片计数器
    int currentImg = 0;
    //定义一个handler来进行隔时间操作
    private Handler handler = new Handler();
    private Runnable myRunnable = new Runnable() {
        public void run() {
            //每隔1秒切换一次
            handler.postDelayed(this, 8000);

            if (currentImg >= 4) {
                currentImg = -1;
            }
            // 改变ImageView里显示的图片
            image1.setImageResource(images[++currentImg]);

        }
    };

}
