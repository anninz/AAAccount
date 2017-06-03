package com.thq.aaaccount;


import android.app.Application;

/**
 * Created by tianhongqi on 17-6-2.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.instance(this);
    }
}
