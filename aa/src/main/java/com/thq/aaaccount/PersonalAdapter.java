package com.thq.aaaccount;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PersonalAdapter extends FragmentPagerAdapter {

    //定义三个Fragment的索引
    public static final int Fragment_Index_0=0;
    public static final int Fragment_Index_1=1;
    public static final int Fragment_Index_2=2;

    private String[] mTitles = new String[]{"账本", "报表", "钱包"};

    public PersonalAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int Index) {
        Fragment mFragemnt=null;
        switch(Index) {
          case Fragment_Index_0:
              mFragemnt=new Fragment_PZB();
              break;
          case Fragment_Index_1:
              mFragemnt=new Fragment_PBB();
              break;
          case Fragment_Index_2:
              mFragemnt=new Fragment_PQB();
              break;
        }
        return mFragemnt;
    }

    @Override
    public int getCount()
    {
        return 3;
    }


    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}