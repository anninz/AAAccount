package com.thq.aaaccount;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class Fragment_0 extends Fragment {

    private static final String TAG = "THQ MainActivity";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Member> myDataset;
    private Map<String, Member> mMembers;
    private List<Item> mItems;
    private MyAdapter mAdapter;
    private Set<String> mSet;

    float mTotalPay = 0;
    float mTotalCost = 0;
    float mTotalPrepaid = 0;


    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;
    String mActivityFileName = null;
    String mActivityId = null;
    
    
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.activity_view_member_bill, container, false);
        initView(mView);
        return mView;  
    }

    private void initView(View view) {
        mActivityListener = ((FragmentInteraction)(getActivity()));

        mActivityId = mActivityListener.getActivityId();
        mActivityFileName = "activity" + mActivityId;

        mSP = getActivity().getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        mEditor = mSP.edit();

        //RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mItems = new ArrayList<>();

        mMembers = new HashMap<>();

        loadMembers();
        loadItems();

        for (Map.Entry<String, Member> entry : mMembers.entrySet()) {

            Member member = entry.getValue();
            myDataset.add(member);
            mTotalPay += member.mPay;
            mTotalCost += member.mCost;
            if (member.mPrepaid > 0) {
                mTotalPrepaid += member.mPrepaid;
            }


        }


        mAdapter = new MyAdapter(getActivity(), myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * 用来与外部activity交互的
     */
    private FragmentInteraction mActivityListener;


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
                    if (member == null) continue;
                    if (i > strs4.length - 1) {
                        member.mPay += 0;
                    } else {
                        member.mPay += Float.parseFloat(strs4[i]);
                    }
                }
                for (String m:strs2) {
                    Member member = mMembers.get(m);
                    if (member == null) continue;
                    member.mCost += item1.mAverage;
                    member.mJoinedItems.add(item1.mItemName);
                }
            }
        }
    }

    private void loadMembers() {
        mSet =  mSP.getStringSet("Members", null);
        if (mSet != null) {
            for (String member : mSet) {
                String[] strs = member.split("\\#");
                mMembers.put(strs[0], new Member(strs[0], Float.parseFloat(strs[1])));
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
        public float mPrepaid;
        List<String> mJoinedItems = new ArrayList<>();
        Member (String name, float prepaid) {
            mName = name;
            mPrepaid = prepaid;
        }
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in getActivity() case
        public TextView mJoindItem;
        public TextView mName;
        public TextView mCost;
        public TextView mTotal;
        public TextView mPay;

        public ViewHolder(View v) {
            super(v);

            mName = (TextView) v.findViewById(R.id.bill_member_name);
            mJoindItem = (TextView) v.findViewById(R.id.bill_joined_item);
            mCost = (TextView) v.findViewById(R.id.bill_cost);
            mTotal = (TextView) v.findViewById(R.id.bill_total);
            mPay = (TextView) v.findViewById(R.id.bill_pay);

        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Member> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Member> myDataset) {
            mDataset = myDataset;
            myDataset.add(new Member("", 0));
//            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Member> getPeoples() {
            return mDataset;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
//            isHost = true;
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_member_bill, parent, false);
//            isHost = false;
            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
//            mHolder.add(vh);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at getActivity() position
            // - replace the contents of the view with that element
//            Log.i(TAG, "onBindViewHolder: position = " + position);
            if (mDataset.size() - 1 == position) {
                holder.mName.setVisibility(View.INVISIBLE);
                holder.mJoindItem.setVisibility(View.GONE);
                holder.mPay.setText("总预付:" + mTotalPrepaid);
                holder.mCost.setText("总花费:" + mTotalCost);
//                holder.mTotal.setVisibility(View.GONE);
                holder.mTotal.setText("总支付:" + mTotalPay);
            } else {

                holder.mName.setVisibility(View.VISIBLE);
                holder.mJoindItem.setVisibility(View.VISIBLE);
                Member pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
                holder.mName.setText(pat.mName);
                holder.mJoindItem.setText("参与:" + pat.mJoinedItems.toString());
                holder.mPay.setText("支出:" + pat.mPay);
                holder.mCost.setText("花费:" + pat.mCost);
                holder.mTotal.setText("总共:" + (pat.mCost - pat.mPay - pat.mPrepaid));

//                Log.i(TAG, "onBindViewHolder: THQ");
            }

//            Log.i(TAG, "onBindViewHolder: name = " + mDataset.get(position).mName);
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