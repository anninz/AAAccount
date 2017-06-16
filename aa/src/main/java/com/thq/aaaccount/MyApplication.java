package com.thq.aaaccount;


import android.app.Application;
import android.content.Context;

import com.thq.aaaccount.Utils.Utils;

/**
 * Created by tianhongqi on 17-6-2.
 */

public class MyApplication extends Application {

    static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.instance(this);
        mContext = getApplicationContext();
    }

    public static Context getMyApplicationContext() {
        return mContext;
    }
}
