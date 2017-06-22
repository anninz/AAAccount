package com.thq.aaaccount;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.thq.aaaccount.widget.SlidingTabLayout;


public class BillFragmentActivity extends AppCompatActivity implements FragmentInteraction {

    private Toolbar mToolbar;
    private ViewPager mPage;
    private SlidingTabLayout mTabLayout;

    String mActivityId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mActivityId = extras.getString("activityId");
        }


        setContentView(R.layout.fragment_viewpager_bill);
        mTabLayout = (SlidingTabLayout) findViewById(R.id.tab_layout);
        mPage = (ViewPager) findViewById(R.id.viewPager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //替换ActionBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(false);

        PagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPage.setAdapter(adapter);

        mToolbar.setContentInsetsAbsolute(0, 0);
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setTitleTextColor(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        mTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        mTabLayout.setTabTitleTextSize(18);
        mTabLayout.setViewPager(mPage);

    }


    @Override
    public void process(String str) {
    }

    @Override
    public String getActivityId() {
        return mActivityId;
    }
}