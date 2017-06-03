package com.thq.aaaccount;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thq.aaaccount.widget.DividerItemDecoration;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewActionItemActivity extends AppCompatActivity {

    private static final String TAG = "THQ MainActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Item> myDataset;
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
        setContentView(R.layout.activity_view_action_item);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mActivityId = extras.getString("activityId");
        }
        mActivityFileName = "activity" + mActivityId;//Utils.getIdFromActivityName(this, activityName);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name_view_item);


//        mSP = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
//        mEditor = mSP.edit();

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

    }

    @Override
    public void onResume() {
        super.onResume();
        myDataset.clear();
        loadItems();
        if (mAdapter == null) {
            mAdapter = new ViewActionItemActivity.MyAdapter(this, myDataset);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.update();
        }

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
//        mSP = null;
//        mSP = getSharedPreferences("activity" + mActivityId, Context.MODE_PRIVATE);
//        mSet = null;
//        mSet = new HashSet<>();
//        mSet =  mSP.getStringSet("Items", null);
        mSet =  Utils.getSPSet("Items", null, mActivityFileName);
        if (mSet != null) {
            for (String item : mSet) {
                String[] strs = item.split("\\#");
//                Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
                Item item1 = new Item(strs[0], strs[2], strs[3], strs[4], strs[5]);
                myDataset.add(item1);
            }
        }
    }

    private void setSPString(String key, String value) {
        SharedPreferences sp = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSPInt(String key, int value) {
        SharedPreferences sp = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void setSPSet(String key, Set<String> value) {
        SharedPreferences sp = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    private void commit(String key) {
        setSPSet("Items", null);
        if (mSet == null) mSet = new HashSet<>();
//        Set<String> mtemsSet = new HashSet<>();

        Log.i(TAG, "commit: THQ1 " + key);

        for (String s:mSet) {
            if (s.contains(key)) {
                mSet.remove(s);
                break;
            }
        }
        setSPSet("Items", mSet);
    }

    class Item {
        public String mItemName;
        public String mMembers;
        public String mPayMan;
        public String mTotal;
        public String mAverage;
        Item (String actionName, String members, String payMan, String total, String average) {
            mItemName = actionName;
            mMembers = members;
            mPayMan = payMan;
            mTotal = total;
            mAverage = average;
        }
    }


    public void deleteItem(String name) {
        mAdapter.deleteItem(name);
        commit(name);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mItemName;
        public TextView mMembers;
        public TextView mPayMan;
        public TextView mTotal;
        public TextView mAverage;
        public Button mDelete;
        public Button mEdit;

        public ViewHolder(View v) {
            super(v);

            mPayMan = (TextView) v.findViewById(R.id.cteate_time);
            mItemName = (TextView) v.findViewById(R.id.action_name);
            mMembers = (TextView) v.findViewById(R.id.members);
            mTotal = (TextView) v.findViewById(R.id.create_date);
            mAverage = (TextView) v.findViewById(R.id.average);
            mDelete = (Button) v.findViewById(R.id.button7);
            mEdit = (Button) v.findViewById(R.id.button8);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("".equals(mItemName.getText().toString()))) {
                            deleteItem(mItemName.getText().toString());
                    }
                }
            });

            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("".equals(mItemName.getText().toString()))) {
                        editItem(mItemName.getText().toString());
                    }
                }
            });

        }
    }

    private void editItem(String itemName) {
        Intent intent = new Intent(this, CreateActionItemActivity.class);
        intent.putExtra("itemName", itemName);
        intent.putExtra("activityId", mActivityId);
        startActivity(intent);
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Item> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Item> myDataset) {
            mDataset = myDataset;
            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Item> getPeoples() {
            return mDataset;
        }

        public void update() {
//            mDataset.clear();
//            mDataset = myDataset;
            notifyDataSetChanged();
        }


        private void deleteItem(String name) {
            for (Item p:mDataset) {
                if (p.mItemName.equals(name)) {
                    mDataset.remove(p);
                    break;
                }
            }
            this.notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
            // create a new view
//            isHost = true;
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_action_item, parent, false);
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
            Item pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
            holder.mItemName.setText(pat.mItemName);
            holder.mAverage.setText(pat.mAverage);
            holder.mMembers.setText(pat.mMembers);
            holder.mTotal.setText(pat.mTotal);
            holder.mPayMan.setText(pat.mPayMan);
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


    public void createActionItem(View view) {
        if (Utils.getSPSet("Members", null, mActivityFileName) != null) {
            Intent intent = new Intent(ViewActionItemActivity.this, CreateActionItemActivity.class);
            intent.putExtra("activityId", mActivityId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }

    public void getBill(View view) {
        if (Utils.getSPSet("Members", null, mActivityFileName) != null) {
            Intent intent = new Intent(ViewActionItemActivity.this, ViewBillActivity.class);
            intent.putExtra("activityId", mActivityId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }

}
