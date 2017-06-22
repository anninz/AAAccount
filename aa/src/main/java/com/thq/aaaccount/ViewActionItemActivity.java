package com.thq.aaaccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.widget.DividerItemDecoration;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ViewActionItemActivity extends AppCompatActivity {

    private static final String TAG = "THQ MainActivity";

    private Toolbar mToolbar;

    private TextView mHint;

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
        if ("".equals(mActivityId) || mActivityId == null) mActivityId = String.valueOf(Utils.getLastestActivityId());
        mActivityFileName = "activity" + mActivityId;//Utils.getIdFromActivityName(this, activityName);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitle(R.string.app_name_view_item);
        mToolbar.setTitle(Utils.getActivityNameFromId(mActivityId));
        setSupportActionBar(mToolbar);


//        mSP = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
//        mEditor = mSP.edit();

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setmDivider(getDrawable(R.drawable.list_divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mHint = (TextView) findViewById(R.id.item_hint);

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();

        if ("".equals(mActivityId) || mActivityId == null) mActivityId = String.valueOf(Utils.getLastestActivityId());
        mActivityFileName = "activity" + mActivityId;//Utils.getIdFromActivityName(this, activityName);
        myDataset.clear();
        loadItems();
        if (myDataset.size() == 0) {
            mHint.setVisibility(View.VISIBLE);
        } else {
            mHint.setVisibility(View.GONE);
        }
        if (mAdapter == null) {
            mAdapter = new ViewActionItemActivity.MyAdapter(this, myDataset);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.update();
        }

    }

    private void loadItems() {
        mSet =  Utils.getSPSet("Items", null, mActivityFileName);
        mSet = sortByValue(mSet);
        if (mSet != null) {
            for (String item : mSet) {
                String[] strs = item.split("\\#");
//                Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
                Item item1 = new Item(strs[0], strs[2], strs[3], strs[4], strs[5], "备注：" + (strs.length > 7?strs[7]:null));
                myDataset.add(item1);
            }
        }
    }

    public static Set<String> sortByValue(Set<String> set){
        if (set == null) return null;
        List<String> setList= new ArrayList<String>(set);
        Collections.sort(setList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // TODO Auto-generated method stub
                return o2.toString().split("\\#")[6].compareTo(o1.toString().split("\\#")[6]);
            }

        });
        set = new LinkedHashSet<String>(setList);//这里注意使用LinkedHashSet
        return set;
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
        public String mSummary;
        public Drawable mIcon;

        public Item(String mItemName, String mMembers, String mPayMan, String mTotal, String mAverage, String mSummary) {
            this.mItemName = mItemName;
            this.mMembers = mMembers;
            this.mPayMan = mPayMan;
            this.mTotal = mTotal;
            this.mAverage = mAverage;
            this.mSummary = mSummary;

            mIcon=getResources().getDrawable(ItemPickerDialog.getIntance().getItem(mItemName).mIconNormalId);
            mIcon.setBounds(0, 0, mIcon.getMinimumWidth(), mIcon.getMinimumHeight());

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
        public TextView mSummaryView;
        public TextView mTotal;
        public TextView mAverage;
        public Button mDelete;
        public Button mEdit;

        public ViewHolder(View v) {
            super(v);

            mPayMan = (TextView) v.findViewById(R.id.view_payer);
            mItemName = (TextView) v.findViewById(R.id.view_item_name);
            mMembers = (TextView) v.findViewById(R.id.view_members);
            mSummaryView = (TextView) v.findViewById(R.id.view_summary);
            mTotal = (TextView) v.findViewById(R.id.view_total);
            mAverage = (TextView) v.findViewById(R.id.view_average);
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
            holder.mSummaryView.setText(pat.mSummary);
            holder.mItemName.setCompoundDrawables(pat.mIcon, null, null, null);
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
            Intent intent = new Intent(ViewActionItemActivity.this, BillFragmentActivity.class);
            intent.putExtra("activityId", mActivityId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }


    public void createAction(View view) {
        Intent intent = new Intent(this, CreateActionActivity.class);
        startActivity(intent);
    }

    public void gotoAllAction(View view) {
        Intent intent = new Intent(this, ViewAllActionActivity.class);
//        intent.putExtra("activityId", ""+Utils.getLastestActivityId(this));
        startActivity(intent);
    }
}
