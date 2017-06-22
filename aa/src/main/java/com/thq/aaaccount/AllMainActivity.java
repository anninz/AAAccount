package com.thq.aaaccount;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.base.BaseActivity;


public class AllMainActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_main);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        if (Utils.getLastestActivityId() < 0) {
            Utils.setSPInt("activitynums", 0, "allactivity");
        }

        image1 = (ImageView) findViewById(R.id.image);
        //初始化时显示第一张图片
//        image1.setImageResource(R.drawable.pic5);
//        runImage();

    }

    public void createAction(View view) {
        Intent intent = new Intent(AllMainActivity.this, CreateActionActivity.class);
        startActivity(intent);
    }

    public void gotoAllAction(View view) {
        Intent intent = new Intent(AllMainActivity.this, ViewAllActionActivity.class);
//        intent.putExtra("activityId", ""+Utils.getLastestActivityId(this));
        startActivity(intent);
    }

    public void gotoAction(View view) {
        if (Utils.getLastestActivityId() > 0) {
            Intent intent = new Intent(AllMainActivity.this, BillFragmentActivity.class);
            intent.putExtra("activityId", "" + Utils.getLastestActivityId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }
/*

    public void gotoAction(View view) {
        if (Utils.getLastestActivityId() > 0) {
            Intent intent = new Intent(AllMainActivity.this, ViewActionItemActivity.class);
            intent.putExtra("activityId", "" + Utils.getLastestActivityId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }
*/


    public void createActionItem(View view) {
        if (Utils.getLastestActivityId() > 0) {
            Intent intent = new Intent(AllMainActivity.this, CreateActionItemActivity.class);
//            intent.putExtra("activityName", itemName);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }


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
