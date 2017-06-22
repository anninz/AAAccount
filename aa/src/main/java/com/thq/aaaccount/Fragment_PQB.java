package com.thq.aaaccount;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.FloatProperty;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.widget.DividerItemDecoration;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Fragment_PQB extends Fragment {

    private static final String TAG = "Fragment_PQB";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Item> mItems;
    private MyAdapter mAdapter;
    private Set<String> mSet;

    PullToRefreshView mPullToRefreshView;

    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;
    String mActivityFileName = null;
    String mActivityId = null;

    TextView mTotalTv;
    float mTotal;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivityListener = ((FragmentInteraction)(getActivity()));

        mActivityFileName = "wallet";

        mSP = getActivity().getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        mEditor = mSP.edit();

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        mItems = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.activity_view_personal_qb, container, false);
        initView(mView);
        return mView;  
    }

    private void initView(View view) {


        //RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setmDivider(getActivity().getDrawable(R.drawable.list_divider_white));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTotalTv = (TextView) view.findViewById(R.id.tvTotalContent);

        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 800l);
            }

            @Override
            public void onCurrentDragPercent(float percent) {
            }

            @Override
            public void onCancel() {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        mItems.clear();
        mTotal = 0;
        loadItems();
        mAdapter = new MyAdapter(getActivity(), mItems);
        mRecyclerView.setAdapter(mAdapter);
        mTotalTv.setText(""+mTotal);
    }

    /**
     * 用来与外部activity交互的
     */
    private FragmentInteraction mActivityListener;


    private void loadItems() {
        mSet =  mSP.getStringSet("Items", null);
        if(mSet == null || mSet.size() == 0) {

            mSet = new HashSet<>();
            String[] strings = {"现金", "支付宝", "微信", "信用卡", "储蓄卡"};
            for (String str:strings) {

                mSet.add(str + "#" + 0 + "#" + 0);
                Item item = new Item(str, "0", "0");
                mItems.add(item);
            }

            Utils.setSPSet("Items", null, mActivityFileName);
            Utils.setSPSet("Items", mSet, mActivityFileName);
        } else if (mSet != null) {
            for (String item : mSet) {
                String[] strs = item.split("\\#");
                Item item1 = new Item(strs[0], strs[1], strs[2]);
                mTotal += item1.mTotal;
                mItems.add(item1);
            }
        }
    }

    private String strSplit(String item) {
        String[] strs = item.split("\\:");
        if (strs.length == 1) return strs[0];
        return strs[1];
    }

    class Item {
        public String mItemName;
        public String mIncome;
        public String mExpend;
        public float mTotal;

        public Item(String mItemName, String mIncome, String mExpend) {
            this.mItemName = mItemName;
            this.mIncome = mIncome;
            this.mExpend = mExpend;
            mTotal = Float.valueOf(mIncome) - Float.valueOf(mExpend);
        }
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in getActivity() case
        public TextView mWalletTypeTv;
        public TextView mTotalTv;
        public TextView mIconTv;

        public ViewHolder(View v) {
            super(v);

            mWalletTypeTv = (TextView) v.findViewById(R.id.qb_item_type_name);
            mTotalTv = (TextView) v.findViewById(R.id.qb_item_total);
            mIconTv = (TextView) v.findViewById(R.id.qb_item_icon);

        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Item> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Item> myDataset) {
            mDataset = myDataset;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Item> getPeoples() {
            return mDataset;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_personal_qb, parent, false);
//            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at getActivity() position
            // - replace the contents of the view with that element
//            Log.i(TAG, "onBindViewHolder: position = " + position);
                Item pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
                holder.mWalletTypeTv.setText(pat.mItemName);
                holder.mIconTv.setText(pat.mItemName.substring(0, 1));
                holder.mTotalTv.setText(""+pat.mTotal);
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