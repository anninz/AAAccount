package com.thq.aaaccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.thq.aaaccount.widget.SlidingTabLayout;


public class PersonalFragmentActivity extends AppCompatActivity implements FragmentInteraction {

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


        setContentView(R.layout.fragment_viewpager_personal);
        mTabLayout = (SlidingTabLayout) findViewById(R.id.p_tab_layout);
        mPage = (ViewPager) findViewById(R.id.p_viewPager);
        mToolbar = (Toolbar) findViewById(R.id.p_toolbar);
        //替换ActionBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(false);

        PagerAdapter adapter = new PersonalAdapter(getSupportFragmentManager());
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



    public void addItem(View view) {
        Intent intent = new Intent(this, AddPersonalItemActivity.class);
        startActivity(intent);
    }
}