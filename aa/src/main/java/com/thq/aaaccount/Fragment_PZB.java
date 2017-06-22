package com.thq.aaaccount;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;
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

import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.refresh_view.AddBtnCircleView;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fragment_PZB extends Fragment {

    private static final String TAG = "THQ MainActivity";

    private RecyclerView mRecyclerView;

    private PullToRefreshView mPullToRefreshView;

    private LinearLayoutManager mLayoutManager;
    private List<Item> myDataset;
    private List<Item> mItems;
//    private TraceListAdapter mAdapter;
    private MyAdapter mAdapter;
    private Set<String> mSet;

    float mTotalPay = 0;
    float mTotalCost = 0;
    float mTotalPrepaid = 0;


    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;
    String mActivityFileName = null;
    String mActivityId = null;

    FloatingActionButton mFloatBtn;

    AddBtnCircleView mAddBtn;

    TextView mTotalHeadTv;
    TextView mIncomeHeadTv;
    TextView mIncomeHeadContentTv;
    TextView mExpendHeadTv;
    TextView mExpendHeadContentTv;

    int mYear;
    int mMonth;
    int mDay;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivityListener = ((FragmentInteraction)(getActivity()));

        mActivityId = mActivityListener.getActivityId();
        mActivityFileName = "personalbook";

        mSP = getActivity().getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        mEditor = mSP.edit();

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mItems = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.activity_view_personal_zb, container, false);
        initView(mView);
        return mView;  
    }

    private void initView(View view) {

        //RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFloatBtn = (FloatingActionButton) view.findViewById(R.id.menu_labels_totop);
        mFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
            }
        });

        mAddBtn = (AddBtnCircleView) view.findViewById(R.id.add_p_bill);

        mTotalHeadTv = (TextView) view.findViewById(R.id.tvTopText);
        mIncomeHeadTv = (TextView) view.findViewById(R.id.tvIncomeHead);
        mExpendHeadTv = (TextView) view.findViewById(R.id.tvExpendHead);
        mIncomeHeadContentTv = (TextView) view.findViewById(R.id.tvIncomeContent);
        mExpendHeadContentTv = (TextView) view.findViewById(R.id.tvExpendContent);

        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAddBtn.stop(true);
                        mPullToRefreshView.setRefreshing(false);
                        mAddBtn.performClick();
                    }
                }, 500l);
                mAddBtn.start();
            }

            @Override
            public void onCurrentDragPercent(float percent) {
                mAddBtn.setPercent(percent, true);
            }

            @Override
            public void onCancel() {
                mAddBtn.stop();
            }
        });

//        initData();
    }


    @Override
    public void onResume() {
        super.onResume();
        mItems.clear();
        initData();
        if (mAdapter == null) {
            mAdapter = new MyAdapter(getActivity(), mItems);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter = null;
    }

    /*
            private void initData() {
                // 模拟一些假的数据
                traceList.add(new Trace("2016-05-25 17:48:00", "餐饮32.5"));
                traceList.add(new Trace("2016-05-25 14:13:00", "酒店32.5"));
                traceList.add(new Trace("2016-05-25 13:01:04", "购物32.5"));
                traceList.add(new Trace("2016-05-25 12:19:47", "打牌32.5"));
                traceList.add(new Trace("2016-05-25 11:12:44", "麻将32.5"));
                traceList.add(new Trace("工资32.5", "2016-05-25 11:12:44"));
                traceList.add(new Trace("2016-05-24 03:12:12", "工资32.5"));
                traceList.add(new Trace("2016-05-23 21:06:46", "餐饮32.5"));
                traceList.add(new Trace("奖金32.5", "2016-05-25 11:12:44"));
                traceList.add(new Trace("2016-05-23 18:59:41", "购物32.5"));
                traceList.add(new Trace("2016-05-23 18:35:32", "酒店32.5"));
                mAdapter = new TraceListAdapter(getActivity(), traceList);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mAdapter);
            }*/
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    String mHintTime = null;
    int mCurrentMonth = 0;
    int mLastDateIndex = 0;

    private void initData() {

        loadItems();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (mLayoutManager.findFirstVisibleItemPosition() == pastVisiblesItems) return;
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

//                visibleItemCount = mLayoutManager.getChildCount();
//                totalItemCount = mLayoutManager.getItemCount();
//                Log.v("...", "Last Item Wow !" + currentPosition);

                Item item = mItems.get(pastVisiblesItems);
                if (!"".equals(item.mTime) && !(item.mTime == null)) return;

                mHintTime = item.mItemName;

                Calendar ca = Utils.convertStringToDate(item.mDate);

//                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH) + 1;
//                int day = ca.get(Calendar.DAY_OF_MONTH);

                if (month != mCurrentMonth) {
                    mCurrentMonth = month;
                    updateHeadView(month, dy>0?pastVisiblesItems:mLastDateIndex, dy>0);
                }
                mLastDateIndex = pastVisiblesItems;

//                if (loading) {
//                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                        // 判断点
//                        loading = false;
//                    }
//                }
            }
        });

        updateHeadView(mMonth, 0, true);
    }

    private void updateHeadView(int month1, int begin, boolean isDown) {

        String month = ""+month1;
        float income = 0;
        float expend = 0;
        if (isDown) {
            for (int i = begin + 1; i < mItems.size(); i++) {

                Item item = mItems.get(i);
                if ("".equals(item.mTime) || item.mTime == null) continue;
                String[] strs = item.mDate.split("\\-");
                if (!month.equals(strs[1])) break;

                if (item.mItemType.equals("expand")) {
                    expend += Float.valueOf(item.mTotal);
                } else {
                    income += Float.valueOf(item.mTotal);
                }
            }
        } else {
            for (int i = begin - 1; i > 0; i--) {

                Item item = mItems.get(i);
                if ("".equals(item.mTime) || item.mTime == null) continue;
                String[] strs = item.mDate.split("\\-");
                if (!month.equals(strs[1])) break;

                if ("".equals(item.mTime) || item.mTime == null) break;

                if (item.mItemType.equals("expand")) {
                    expend += Float.valueOf(item.mTotal);
                } else {
                    income += Float.valueOf(item.mTotal);
                }
            }
        }
        mTotalHeadTv.setText(month + "月结余:" + (income - expend));
        mIncomeHeadTv.setText(month + "月收入");
        mExpendHeadTv.setText(month + "月支出");
        mExpendHeadContentTv.setText(""+expend);
        mIncomeHeadContentTv.setText(""+income);
    }


    /**
     * 用来与外部activity交互的
     */
    private FragmentInteraction mActivityListener;



    private void loadItems() {
        mSet =  Utils.getSPSet("Items", null, mActivityFileName);
        mSet = Utils.sortByValue(mSet, 3, true);
//        mSet = Utils.sortByValue(mSet, 3, true);
        String lastdate = null;
        String currentDate = null;
        float total = 0;
        Item lastDateItem = null;

        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH) + 1;
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        Date date = null;

        if (mSet != null) {
            for (String item : mSet) {
                String[] strs = item.split("\\#");
                ca = Utils.convertStringToDate(strs[3].toString());

                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH) + 1;
                int day = ca.get(Calendar.DAY_OF_MONTH);
                if (year == mYear) {
                    if (month == mMonth) {
                        if (day == mDay) {
                            currentDate = "今天";
                        } else if (day - mDay == -1) {
                            currentDate = "昨天";
                        } else if (day - mDay == -2) {
                            currentDate = "前天";
                        } else {
                            currentDate = "" + day + "日";
                        }
                    } else {
                        currentDate = "" + month + "月" + day + "日";
                    }
                } else {
                    currentDate = "" + year + "年"  + month + "月" + day + "日";
                }

                Item item1 = new Item(strs[0], strs[1], strs[2], strs[3], strs[4], (strs.length > 5?strs[5]:null));
                if (!strs[3].toString().equals(lastdate)) {
                    lastdate = strs[3];
                    if (lastDateItem != null) lastDateItem.mTotal = String.valueOf(total);
                    total = 0;
                    lastDateItem = new Item(currentDate, "", strs[3]);
                    mItems.add(lastDateItem);
                }
                if (strs[2].contains("income")) {
                    total += Float.valueOf(strs[1]);
                } else {
                    total -= Float.valueOf(strs[1]);
                }
                mItems.add(item1);
            }

            if (lastDateItem != null) lastDateItem.mTotal = String.valueOf(total);
        }
    }

    private String strSplit(String item) {
        String[] strs = item.split("\\:");
        if (strs.length == 1) return strs[0];
        return strs[1];
    }

    class Item {
        public String mItemName;
        public String mTotal;
        public String mItemType;
        public String mDate;
        public String mTime;
        public String mRemark;

        public Item(String mItemName, String mTotal, String mItemType, String mDate, String mTime, String mRemark) {
            this.mItemName = mItemName;
            this.mTotal = mTotal;
            this.mItemType = mItemType;
            this.mDate = mDate;
            this.mTime = mTime;
            this.mRemark = mRemark;
        }

        public Item(String mItemName, String mTotal, String date) {
            this.mItemName = mItemName;
            this.mTotal = mTotal;
            mDate = date;
        }
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIncomeRemark, tvExpendRemark;
        private TextView tvAcceptTime, tvAcceptStation;
        private TextView tvTopLine, tvDot;
        public ViewHolder(View itemView) {
            super(itemView);
            tvAcceptTime = (TextView) itemView.findViewById(R.id.tvAcceptHead);
            tvAcceptStation = (TextView) itemView.findViewById(R.id.tvAcceptType);
            tvIncomeRemark = (TextView) itemView.findViewById(R.id.tvAcceptTime);
            tvExpendRemark = (TextView) itemView.findViewById(R.id.tvAcceptPayer);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);
        }

        public void bindHolder(Item trace) {
            if ("".equals(trace.mItemType) || trace.mItemType == null) {
                tvAcceptTime.setText(trace.mItemName);
                tvAcceptStation.setText(trace.mTotal);
                tvAcceptTime.setTextSize(16);
                tvAcceptStation.setTextSize(16);
            } else {
                tvAcceptTime.setTextSize(14);
                tvAcceptStation.setTextSize(14);
                if (trace.mItemType.equals("income")) {
                    tvAcceptTime.setText(trace.mItemName + trace.mTotal);
                    tvIncomeRemark.setText(trace.mRemark);
                } else {
                    tvExpendRemark.setText(trace.mRemark);
                    tvAcceptStation.setText(trace.mItemName + trace.mTotal);
                }
                tvDot.setBackground(getActivity().getDrawable(ItemPickerDialog.getIntance().getItemIconNormalId(trace.mItemName)));
            }
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Item> mDataset;
        private final TypedValue mTypedValue = new TypedValue();
        private static final int TYPE_TOP = 0x0000;
        private static final int TYPE_NORMAL= 0x0001;

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
            View v;
            if (viewType == TYPE_TOP) {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_line_trace_first, parent, false);

            } else {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_line_trace, parent, false);
            }
            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ViewHolder itemHolder = (ViewHolder) holder;
            if (getItemViewType(position) == TYPE_TOP) {
                // 第一行头的竖线不显示
//                itemHolder.tvTopLine.setVisibility(View.INVISIBLE);
//                final ViewGroup.LayoutParams lp = itemHolder.tvTopLine.getLayoutParams();
//                lp.height= 150;//lp.height=LayoutParams.WRAP_CONTENT;
//                itemHolder.tvTopLine.setLayoutParams(lp);
                // 字体颜色加深
//                itemHolder.tvAcceptTime.setTextColor(0xff555555);
//                itemHolder.tvAcceptStation.setTextColor(0xff555555);
                itemHolder.tvDot.setBackgroundResource(R.drawable.timelline_dot_first);
            } else if (getItemViewType(position) == TYPE_NORMAL) {
//                itemHolder.tvTopLine.setVisibility(View.VISIBLE);
//                itemHolder.tvAcceptTime.setTextColor(0xff999999);
//                itemHolder.tvAcceptStation.setTextColor(0xff999999);
                itemHolder.tvDot.setBackgroundResource(R.drawable.timelline_dot_normal);
            }

            itemHolder.tvAcceptTime.setText("");
            itemHolder.tvAcceptStation.setText("");
            itemHolder.tvIncomeRemark.setText("");
            itemHolder.tvExpendRemark.setText("");

            itemHolder.bindHolder(mDataset.get(position));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }


        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_TOP;
            }
            return TYPE_NORMAL;
        }
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };

}