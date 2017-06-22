package com.thq.aaaccount;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.thq.aaaccount.Utils.ChartUtils;
import com.thq.aaaccount.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Fragment_PBB extends Fragment {

    private static final String TAG = "THQ MainActivity";

    private List<Member> myDataset;
    private Map<String, Member> mMembers;
    private Map<String, ConsumeTypeMoneyPo> mConsumeTypes;
    private Map<String, ConsumeTypeMoneyPo> mIncomeTypes;
    private List<Item> mItems;
    private Set<String> mSet;

    float mTotalPay = 0;
    float mTotalCost = 0;
    float mTotalPrepaid = 0;

    TextView mIncomeSwitchBtn;
    TextView mExpendSwitchBtn;

    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;
    String mActivityFileName = null;
    String mActivityId = null;
    /**
     * 用来与外部activity交互的
     */
    private FragmentInteraction mActivityListener;

    PieChart mChart;

    boolean mIsIncomeMode = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.fragment_piechart_pbb, container, false);
        initView(mView);
        return mView;
    }

    private void initView(View view) {

        mIncomeSwitchBtn = (TextView) view.findViewById(R.id.pbb_switch_income);
        mIncomeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(v);
            }
        });
        mExpendSwitchBtn = (TextView) view.findViewById(R.id.pbb_switch_expend);
        mExpendSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(v);
            }
        });

        mActivityListener = ((FragmentInteraction)(getActivity()));

        mActivityId = mActivityListener.getActivityId();
        mActivityFileName = "personalbook";

        mSP = getActivity().getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        mEditor = mSP.edit();

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mItems = new ArrayList<>();

        mMembers = new HashMap<>();

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

        mChart = (PieChart) view.findViewById(R.id.consume_pie_chart);

        countItems();
        showChart2(mChart);

    }

    private void countItems() {
        mConsumeTypes = new HashMap<>();
        mIncomeTypes = new HashMap<>();
        for (Item item:mItems) {

            if ("".equals(item.mItemType) || item.mItemType == null) continue;


            if (item.mItemType.equals("expand")) {
                float to = 0;
                to += Float.valueOf(item.mTotal);
                if (!mConsumeTypes.containsKey(item.mItemName)) {
                    ConsumeTypeMoneyPo item1 = new ConsumeTypeMoneyPo(item.mItemName, Float.valueOf(item.mTotal));
                    item.mTotal = String.valueOf(to);
                    mConsumeTypes.put(item.mItemName, item1);
                } else {
                    mConsumeTypes.get(item.mItemName).add(Float.valueOf(to));
                }
            } else {

                float to = 0;
                to += Float.valueOf(item.mTotal);
                if (!mIncomeTypes.containsKey(item.mItemName)) {
                    ConsumeTypeMoneyPo item1 = new ConsumeTypeMoneyPo(item.mItemName, Float.valueOf(item.mTotal));
                    item.mTotal = String.valueOf(to);
                    mIncomeTypes.put(item.mItemName, item1);
                } else {
                    mIncomeTypes.get(item.mItemName).add(Float.valueOf(to));
                }
            }
        }
    }

    private void loadItems() {
        mSet =  Utils.getSPSet("Items", null, mActivityFileName);
        mSet = Utils.sortByValue(mSet, 3, true);
//        mSet = Utils.sortByValue(mSet, 3, true);
        String date = null;
        float total = 0;
        Item lastDateItem = null;
        if (mSet != null) {
            for (String item : mSet) {
                String[] strs = item.split("\\#");
//                Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
                Item item1 = new Item(strs[0], strs[1], strs[2], strs[3], strs[4], (strs.length > 5?strs[5]:null));
                if (!strs[3].toString().equals(date)) {
                    date = strs[3];
                    if (lastDateItem != null) lastDateItem.mTotal = String.valueOf(total);
                    total = 0;
                    lastDateItem = new Item(date, "");
                    mItems.add(lastDateItem);
                }
                total += Float.valueOf(strs[1]);
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

        public Item(String mItemName, String mTotal) {
            this.mItemName = mItemName;
            this.mTotal = mTotal;
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


    private void showChart2(PieChart pieChart/*, PieData pieData*/) {
        pieChart.setUsePercentValues(true);//设置value是否用显示百分数,默认为false
        pieChart.setDescription("消费情况");//设置描述
        pieChart.setDescriptionTextSize(20);//设置描述字体大小
//pieChart.setDescriptionColor(); //设置描述颜色
//pieChart.setDescriptionTypeface();//设置描述字体

        pieChart.setExtraOffsets(5, 5, 5, 5);//设置饼状图距离上下左右的偏移量

        pieChart.setDragDecelerationFrictionCoef(0.55f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难

        pieChart.setDrawCenterText(false);//是否绘制中间的文字
        pieChart.setCenterTextColor(Color.BLACK);//中间的文字颜色
        pieChart.setCenterTextSize(18);//中间的文字字体大小

        pieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
        pieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        pieChart.setHoleRadius(78f);//饼状图中间的圆的半径大小

        pieChart.setTransparentCircleColor(Color.WHITE);//设置圆环的颜色
        pieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        pieChart.setTransparentCircleRadius(60f);//设置圆环的半径值

// enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);//设置饼状图是否可以旋转(默认为true)
        pieChart.setRotationAngle(10);//设置饼状图旋转的角度

        pieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮(默认为true)

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);//设置每个tab的显示位置
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);

// entry label styling
        pieChart.setDrawEntryLabels(true);//设置是否绘制Label
        pieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
//pieChart.setEntryLabelTypeface(mTfRegular);
        pieChart.setEntryLabelTextSize(10f);//设置绘制Label的字体大小

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });//设值点击时候的回调
        pieChart.animateY(800, Easing.EasingOption.EaseInQuad);//设置Y轴上的绘制动画

        ArrayList<ConsumeTypeMoneyPo> consumeTypeMoneyVoList;
        if (mIsIncomeMode) {
            consumeTypeMoneyVoList = new ArrayList<>(mIncomeTypes.values());
        } else {
            consumeTypeMoneyVoList = new ArrayList<>(mConsumeTypes.values());
        }
//        consumeTypeMoneyVoList.add(new ConsumeTypeMoneyPo("吃饭", 12.3f));
//        consumeTypeMoneyVoList.add(new ConsumeTypeMoneyPo("电影", 23.3f));
//        consumeTypeMoneyVoList.add(new ConsumeTypeMoneyPo("唱歌", 33.3f));
//        consumeTypeMoneyVoList.add(new ConsumeTypeMoneyPo("坐车", 12.3f));
//        consumeTypeMoneyVoList.add(new ConsumeTypeMoneyPo("聚会", 112.3f));
//        consumeTypeMoneyVoList.add(new ConsumeTypeMoneyPo("住宿", 122.3f));

        float totalMoney = 0;
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for(ConsumeTypeMoneyPo typeMoneyVo : consumeTypeMoneyVoList){
            PieEntry pieEntry = new PieEntry((float)typeMoneyVo.getTotalMoney(), typeMoneyVo.getConsumeTypeName());
            pieEntries.add(pieEntry);
            totalMoney += typeMoneyVo.getTotalMoney();
        }
        String centerText = Utils.getActivityNameFromId(mActivityId)+"消费\n¥"+totalMoney;
        pieChart.setCenterText(centerText);//设置中间的文字
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(getPieChartColors(consumeTypeMoneyVoList.size()));
        pieDataSet.setSliceSpace(1f);//设置选中的Tab离两边的距离
        pieDataSet.setSelectionShift(5f);//设置选中的tab的多出来的
        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLUE);

        pieChart.setData(pieData);
// undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private ArrayList<Integer> getPieChartColors(int i) {
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色
//        colors.add(Color.rgb(205, 205, 205));
//        colors.add(Color.rgb(114, 188, 223));
//        colors.add(Color.rgb(255, 123, 124));
//        colors.add(Color.rgb(57, 135, 200));
//        colors.add(Color.rgb(87, 235, 100));
//        colors.add(Color.rgb(227, 135, 220));
        for (int j = 0; j < i; j++) {
            colors.add(ChartUtils.nextColor());
        }
        return colors;
    }

    class ConsumeTypeMoneyPo {
        float mTotalMoney;
        String mConsumeTypeName;

        public ConsumeTypeMoneyPo(String mConsumeTypeName, float mTotalMoney) {
            this.mConsumeTypeName = mConsumeTypeName;
            this.mTotalMoney = mTotalMoney;
        }

        public void add (float value) {
            mTotalMoney += value;
        }

        public float getTotalMoney() {
            return mTotalMoney;
        }

        public String getConsumeTypeName() {
            return mConsumeTypeName;
        }
    }

    public void switchMode(View view) {
        TextView textView = (TextView)view;
        if (textView.getText().toString().contains("支出")) {
            if (mIsIncomeMode) {
                mExpendSwitchBtn.setBackground(getActivity().getDrawable(R.drawable.button_text_backgrand_left_selected));
                mIncomeSwitchBtn.setBackground(getActivity().getDrawable(R.drawable.button_text_backgrand_right));
            } else {
                return;
            }
        } else {
            if (mIsIncomeMode) {
                return;
            } else {
                mExpendSwitchBtn.setBackground(getActivity().getDrawable(R.drawable.button_text_backgrand_left));
                mIncomeSwitchBtn.setBackground(getActivity().getDrawable(R.drawable.button_text_backgrand_right_selected));
            }
        }
        mIsIncomeMode = !mIsIncomeMode;
        showChart2(mChart);
    }

} 