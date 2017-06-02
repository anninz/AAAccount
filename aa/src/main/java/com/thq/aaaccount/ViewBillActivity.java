package com.thq.aaaccount;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thq.aaaccount.widget.DividerItemDecoration;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewBillActivity extends AppCompatActivity {

    private static final String TAG = "THQ MainActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Member> myDataset;
    private Map<String, Member> mMembers;
    private List<Item> mItems;
    private MyAdapter mAdapter;
    private Set<String> mSet;
    List<ViewHolder> mHolder;


    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;
    String mActivityFileName = null;
    String mActivityId = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_member_bill);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mActivityId = extras.getString("activityId");
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name_view_bill);

        mActivityFileName = "activity" + mActivityId;

        mSP = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        mEditor = mSP.edit();

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mItems = new ArrayList<>();

        mMembers = new HashMap<>();

        loadMembers();
        loadItems();

        for (Map.Entry<String, Member> entry : mMembers.entrySet()) {

            myDataset.add(entry.getValue());

        }


        mAdapter = new ViewBillActivity.MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        for (ViewHolder viewHolder:mHolder) {
//            if (viewHolder.patNum > 0) {
//                StringBuffer stringBuffer = new StringBuffer();
//                stringBuffer.append(viewHolder.patNum + "#");
//                stringBuffer.append(viewHolder.apkPath + "#");
////                stringBuffer.append(viewHolder.mTextView.getText().toString());
//                mSet.add(stringBuffer.toString());
//            }
/*
            if (viewHolder.mCheckBox.isChecked()) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(viewHolder.mEditText.getText().toString() + "#");
                stringBuffer.append(viewHolder.apkPath + "#");
                stringBuffer.append(viewHolder.mTextView.getText().toString());
                mSet.add(stringBuffer.toString());
            }
*/
        }
//        setSPSet("PatSet", mSet);
    }

    private void loadItems() {
        mSet =  mSP.getStringSet("Items", null);
        if (mSet != null) {
            for (String item : mSet) {
                String[] strs = item.split("\\#");
                Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
                Item item1 = new Item(strSplit(strs[0]), strSplit(strs[2]), strSplit(strs[3]), strSplit(strs[4]), Float.parseFloat(strSplit(strs[5])));
                mItems.add(item1);
                String[] strs2 = item1.mMembers.split("\\,");
                String[] strs3 = item1.mPayer.split("\\,");
                String[] strs4 = item1.mTotal.split("\\+");
                for (int i = 0; i < strs3.length; i++) {
                    Member member = mMembers.get(strs3[i]);
                    if (i > strs4.length - 1) {
                        member.mPay += 0;
                    } else {
                        member.mPay += Float.parseFloat(strs4[i]);
                    }
                }
                for (String m:strs2) {
                    Member member = mMembers.get(m);
                    member.mCost += item1.mAverage;
//                    for (String pm:strs3) {
//                        if (m.equals(pm)) {
//                            Log.i(TAG, "loadItems1: THQ1" + m + " " + item1.mPayer);
//                            member.mPay += item1.mTotal;
//                        }
//                    }
                    member.mJoinedItems.add(item1.mItemName);
                }
            }
        }
    }

    private void loadMembers() {
        mSet =  mSP.getStringSet("Members", null);
        if (mSet != null) {
            for (String member : mSet) {
                mMembers.put(member, new Member(member));
            }

        }
    }

    private String strSplit(String item) {
        String[] strs = item.split("\\:");
        return strs[1];
    }

    private void setSPString(String key, String value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSPInt(String key, int value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void setSPSet(String key, Set<String> value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    class Item {
        public String mItemName;
        public String mMembers;
        public String mPayer;
        public String mTotal;
        public float mAverage;
        Item (String actionName, String members, String payer, String total, float average) {
            mItemName = actionName;
            mMembers = members;
            mPayer = payer;
            mTotal = total;
            mAverage = average;
        }
    }

    class Member {
        public String mName;
        public float mPay;
        public float mCost;
        public float mTotal;
        List<String> mJoinedItems = new ArrayList<>();
        Member (String name) {
            mName = name;
        }
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mJoindItem;
        public TextView mName;
        public TextView mCost;
        public TextView mTotal;
        public TextView mPay;

        public ViewHolder(View v) {
            super(v);

            mName = (TextView) v.findViewById(R.id.member_name);
            mJoindItem = (TextView) v.findViewById(R.id.joined_item);
            mCost = (TextView) v.findViewById(R.id.cost);
            mTotal = (TextView) v.findViewById(R.id.create_date);
            mPay = (TextView) v.findViewById(R.id.pay);

        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Member> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Member> myDataset) {
            mDataset = myDataset;
            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Member> getPeoples() {
            return mDataset;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
            // create a new view
//            isHost = true;
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_member_bill, parent, false);
//            isHost = false;
            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            mHolder.add(vh);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Member pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
            holder.mName.setText(pat.mName);
            holder.mJoindItem.setText("参与:"+pat.mJoinedItems.toString());
            holder.mPay.setText("支出:"+pat.mPay);
            holder.mCost.setText("花费:"+pat.mCost);
            holder.mTotal.setText("总共:"+(pat.mCost - pat.mPay));
            Log.i(TAG, "onBindViewHolder: THQ");
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };
}
