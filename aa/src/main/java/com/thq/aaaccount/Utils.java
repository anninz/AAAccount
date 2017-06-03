package com.thq.aaaccount;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by tianhongqi on 17-6-2.
 */

public class Utils {

    private Utils (Context context) {

    }

    static private Context mContext;
    static public void instance(Context context) {
        mContext = context;
    }

    static public int getLastestActivityId() {
        SharedPreferences sp = mContext.getSharedPreferences("allactivity", Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet("allactivitys", null);
        if (set == null) return -1;
        int lastId = 0;
        for (String s:set) {
            String[] strings = s.split("\\#");
            int i = Integer.parseInt(strings[1]);
            lastId = i > lastId ? i : lastId;
        }
        return lastId;
    }

    static public String getIdFromActivityName(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("allactivity", Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet("allactivitys", null);
        for (String s:set) {
            String[] strings = s.split("\\#");
            if (strings[0].equals(key)) {
                return strings[1];
            }
        }
        return null;
    }

    static public Set<String> getAllActivity() {
        SharedPreferences sp = mContext.getSharedPreferences("allactivity", Context.MODE_PRIVATE);
        return sp.getStringSet("allactivitys", null);
    }

    static public int getAllActivityNums() {
        SharedPreferences sp = mContext.getSharedPreferences("allactivity", Context.MODE_PRIVATE);
        return sp.getStringSet("allactivitys", null).size();
    }

    static public void setSPString(String key, String value, String fileName) {
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static public  void setSPInt(String key, int value, String fileName) {
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    static public  void setSPSet(String key, Set<String> value, String fileName) {
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    static public Set<String> getSPSet(String key, Set<String> value, String fileName) {
        SharedPreferences sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return  sp.getStringSet(key, value);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putStringSet(key, value);
//        editor.commit();
    }
}
