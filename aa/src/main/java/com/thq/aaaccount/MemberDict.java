package com.thq.aaaccount;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tianhongqi on 17-6-3.
 */

public class MemberDict {

    private MemberDict() {

    }

    static MemberDict mMemberDict;
    public static MemberDict getIntance() {
        if (mMemberDict == null)
            mMemberDict = new MemberDict();
        return mMemberDict;
    }

    public Set<String> getMembers(Context context) {
        SharedPreferences sp = context.getSharedPreferences("membersdict", Context.MODE_PRIVATE);
        return sp.getStringSet("members", null);
    }

    public void addMember(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences("membersdict", Context.MODE_PRIVATE);
         sp.getStringSet("members", null);
        //将保存的set取出
        Set<String> set = sp.getStringSet("members", null);

        if (set == null) set = new HashSet<>();
        //将取出的set作为参数进行重构（解决问题的关键）
        set = new HashSet<>(set);

        //添加新数据
        set.add(name);

        //重新保存
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("members", set);
        editor.commit();
    };
}
