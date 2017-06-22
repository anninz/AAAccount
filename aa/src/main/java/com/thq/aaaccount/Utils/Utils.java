package com.thq.aaaccount.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
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

    static public String getActivityNameFromId(String id) {
        SharedPreferences sp = mContext.getSharedPreferences("activity"+id, Context.MODE_PRIVATE);
        return sp.getString("ActionName", null);
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


    public static Set<String> sortByValue(Set<String> set, final int index, final boolean isDimi){
        if (set == null) return null;
        List<String> setList= new ArrayList<String>(set);
        Collections.sort(setList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // TODO Auto-generated method stub
                if (isDimi) {
                    return o2.toString().split("\\#")[index].compareTo(o1.toString().split("\\#")[index]);
                } else {
                    return o1.toString().split("\\#")[index].compareTo(o2.toString().split("\\#")[index]);
                }
            }

        });
        set = new LinkedHashSet<String>(setList);//这里注意使用LinkedHashSet
        return set;
    }

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static int convertDpToPixel(int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static Calendar convertStringToDate(String currentDate) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        Calendar ca = Calendar.getInstance();
        try {
            date = sdf.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ca.setTime(date);
        return ca;
    }
}
