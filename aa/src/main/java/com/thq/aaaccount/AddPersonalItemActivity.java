package com.thq.aaaccount;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddPersonalItemActivity extends AppCompatActivity {

    private static final String TAG = "CreateAction";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Type> myDataset;
    private List<Type> mIncomeset;
    private Set<String> mMemberSet;
    private Set<String> mActivitySet;
    private Set<String> mItemsSet;


    SharedPreferences mSP;
    SharedPreferences.Editor mEditor;

    String[] mMembers;
    StringBuffer mSelectedMenbers = new StringBuffer();

    String[] mActions ;

    TextView mTotalView;
    TextView mItemNameView;
    TextView mCreateDateView;
    TextView mCreateTimeView;
    TextView mActionNameView;
    TextView mAverageView;
    TextView mSelectedMembersView;
    TextView mWalletType;
    TextView mSummaryView;

    ImageButton mSwitchView;

    boolean mIsEditMode = false;
    String mActivityFileName = null;
    String mActivityName = null;
    String mActivityId = null;
    String mHostName = null;

    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

    ItemPickerDialog mItemPickerDialog;

    GridLayoutManager mGridManager;
    private MyAdapter mAdapter;

    TimePickerDialog timePickerDialog;

    boolean mIsIncome;

    int mCurrentSelectedType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_item);

        Bundle extras = getIntent().getExtras();
        String itemName = null;
        String activityId = null;
        if (extras != null) {
            itemName = extras.getString("itemName");
            activityId = extras.getString("activityId");
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (itemName == null) {
            mToolbar.setTitle(R.string.app_name_create_item);
        } else {
            mIsEditMode = true;
            mToolbar.setTitle(R.string.app_name_edit_item);
        }
        setSupportActionBar(mToolbar);
//
        mTotalView = (TextView) findViewById(R.id.total);
//        mItemNameView = (TextView) findViewById(R.id.item_name);
        mCreateDateView = (TextView) findViewById(R.id.create_date);
        Drawable drawable=getResources().getDrawable(R.drawable.date);
        drawable.setBounds(0,0,70,70);
        mCreateDateView.setCompoundDrawables(drawable,null, null ,null);

        mCreateTimeView = (TextView) findViewById(R.id.create_time);
        Drawable drawable1=getResources().getDrawable(R.drawable.time);
        drawable1.setBounds(0,0,70,70);
        mCreateTimeView.setCompoundDrawables(null,null,drawable1,null);

        mSwitchView = (ImageButton) findViewById(R.id.expand_switch);
//        mActionNameView = (TextView) findViewById(R.id.belong_action);
//        mAverageView = (TextView) findViewById(R.id.average);
        mWalletType = (TextView) findViewById(R.id.tv_wallet_type);
        mSummaryView = (TextView) findViewById(R.id.item_summary);
//        mSelectedMembersView = (TextView) findViewById(R.id.members);
//        mSummaryView = (TextView) findViewById(R.id.item_summary);

//       mPayerView.addTextChangedListener(textWatcher);
//        mTotalView.addTextChangedListener(textWatcher);
//        mSelectedMembersView.addTextChangedListener(textWatcher);
//
//        mActivityId = activityId == null? String.valueOf(Utils.getLastestActivityId()):activityId;

        mActivityFileName = "personalbook";

        mSP = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        mEditor = mSP.edit();


        mItemsSet = mSP.getStringSet("Items", null);
/*
//        mActions[0] = mSP.getString("ActionName", null);
        mActivitySet = Utils.getAllActivity();
        mActions = new String[mActivitySet.size()];

        mActivityName = mSP.getString("ActionName", null);
        mHostName = mSP.getString("HostName", null);
        mPayerView.setText("付款人:"+mHostName);

        int i1 = 0;
        for (String s:mActivitySet) {
            mActions[i1] = s.split("\\#")[0];
            if (mActions[i1].equals(mActivityName)) mOldSelectedActionIndex = i1;
            i1++;
        }
//        mActions = mActivitySet.toArray(mActions);


        loadMembers();

        mMembersToDialog = new String[mMembers.length + 1];
        mMembersToDialog[mMembers.length] = "全选";
        for (int i = 0;i < mMembers.length; i++) {
            mMembersToDialog[i] = mMembers[i];
        }

        mActionNameView.setText("所属活动:" + mActivityName);
        if (mIsEditMode) {
            loadItemsToView(itemName);
        } else {

            intiMembers();
        }*/
        initDatePicker();
        initTimePicker();

//        mItemNameView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getIntance().showItemAlertDialog(AddPersonalItemActivity.this, new CallbackResultListener() {
//                    @Override
//                    public void done(int position) {
//                        Item item = getIntance().getItem(position);
//                        mItemNameView.setText(item.mItemName);
//
//                        Drawable nav_up=getResources().getDrawable(item.mIconNormalId);
//                        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
//                        mItemNameView.setCompoundDrawables(nav_up, null, null, null);
//                    }
//                });
//            }
//        });

        initItemPicker();
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            mCurrentSelectedType = position;
            mAdapter.update(position);
        }
    };

    private void initItemPicker() {
//        mView = mContext.getLayoutInflater().inflate(R.layout.dialog_recycler_view, null);
        //RecyclerView

        myDataset = new ArrayList<>();
        for (int i = 0; i < ItemPickerDialog.ITEM_EXPEND_NAME.length; i++) {
            myDataset.add(new Type(ItemPickerDialog.ITEM_EXPEND_NAME[i], ItemPickerDialog.ITEM_EXPEND_ICON_NORMAL_ID[i], ItemPickerDialog.ITEM_EXPEND_ICON_SELECTED_ID[i]));
        }

        mIncomeset = new ArrayList<>();
        for (int i = 0; i < ItemPickerDialog.ITEM_INCOME_NAME.length; i++) {
            mIncomeset.add(new Type(ItemPickerDialog.ITEM_INCOME_NAME[i], ItemPickerDialog.ITEM_INCOME_ICON_NORMAL_ID[i], ItemPickerDialog.ITEM_INCOME_ICON_SELECTED_ID[i]));
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mGridManager = new GridLayoutManager(this, 5);
        mRecyclerView.setLayoutManager(mGridManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initDatePicker() {
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        if (!mIsEditMode) {
            mCreateDateView.setText(new StringBuffer().append(" ").append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
            mCreateTimeView.setText(ca.get(Calendar.HOUR_OF_DAY) + " : " + ca.get(Calendar.MINUTE) + " ");
        }
    }

    private void initTimePicker() {

        timePickerDialog = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCreateTimeView.setText("" + hourOfDay + " : " + minute + " ");
            }
        },0,0,false);

    }

    public void switchItemType(View view) {
        mAdapter.mLastSelectedIndes = -1;
        if (mIsIncome) {
            mSwitchView.setImageResource(R.drawable.expand_switch);
            mAdapter.setmDataset(myDataset);
            mAdapter.update(0);
        } else {
            mSwitchView.setImageResource(R.drawable.income_switch);
            mAdapter.setmDataset(mIncomeset);
            mAdapter.update(0);
        }
        mIsIncome = !mIsIncome;
    }

    public void showDatePicker(View view) {
        showDialog(DATE_DIALOG);
    }

    public void showTimePicker(View view) {
        timePickerDialog.setTitle("pick");
        timePickerDialog.show();
    }

    private void  intiMembers() {
        StringBuffer mSelectedMenbers = new StringBuffer();

        Log.i(TAG, "onClick: THQ1 " + selectedMemberIndex.size());

        for (String i : mMembers) {
            mSelectedMenbers.append(i + ",");
        }
        mSelectedMembersNum = selectedMemberIndex.size();
        mSelectedMembersView.setText("参与人:" + mSelectedMenbers);


        if (selectedMemberIndex.size()==0) {
            for (int i = 0; i < mMembersToDialog.length; i++) {
                selectedMemberIndex.add(i);
            }
        }
        mSelectedMembersNum = mMembers.length;


        for (int i = 0; i < mMembers.length; i++) {
            if (mMembers[i].equals(mHostName)) {
                selectedPayManIndex.add(i);
                break;
            }
        }
    }

    private void loadMembers() {
        mMemberSet = mSP.getStringSet("Members", null);
        mMembers = new String[mMemberSet.size()];
        int i = 0;
        for (String s:mMemberSet) {
            mMembers[i++] = s.split("\\#")[0];
        }
    }

    private void reloadData() {
        mItemsSet = null;
        mMemberSet = null;
        SharedPreferences mSP = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
//        mEditor = mSP.edit();

        mMemberSet = mSP.getStringSet("Members", null);
        mItemsSet = mSP.getStringSet("Items", null);
        mMembers = new String[mMemberSet.size()];
        mMembers = mMemberSet.toArray(mMembers);
        mMembersToDialog = new String[mMembers.length + 1];
        mMembersToDialog[mMembers.length] = "全选";
        for (int i = 0;i < mMembers.length; i++) {
            mMembersToDialog[i] = mMembers[i];
        }
        mSelectedMembersView.setText("members");
    }

    String selectedItem = null;
    private void loadItemsToView(String key) {
        if (mItemsSet != null) {
            for (String item : mItemsSet) {
                if (item.contains(key)) {
                    selectedItem = item;
                    String[] strs = item.split("\\#");
                    mItemNameView.setText(strs[0]/*.split("\\:")[1]*/);
                    mSelectedMembersView.setText(strs[2]);
                    mSelectedMembersNum = strs[2].split("\\,").length;
                    mWalletType.setText(strs[3]);
                    mTotalView.setText(strs[4].split("\\:")[1]);
                    mAverageView.setText(strs[5]);
                    mCreateDateView.setText(strs[6]);
                    mSummaryView.setText(strs.length > 7?strs[7]:null);
//                    Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
                    break;
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        for (ViewHolder viewHolder:mHolder) {
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
//        }
//        setSPSet("PatSet", mSet);
    }

    private void loadItems () {
        mMemberSet =  mSP.getStringSet("items", null);
//        for (St)
//        myDataset.addAll(mSet);
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

    class Item {
        public String mActionName;
        public String mMembers;
        public String mPayMan;
        public String mTotalView;
        public String mAverageView;
        Item (String actionName, String members, String payMan, String total, String average) {
            mActionName = actionName;
            mMembers = members;
            mPayMan = payMan;
            mTotalView = total;
            mAverageView = average;
        }
    }


    public void showAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("警告");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setMessage("切换后将重置本页面，确定要切换吗？");
        alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                reloadData();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertDialogBuilder.show();
    }

    // 信息列表提示框
    private AlertDialog alertDialog1;
    public void showListAlertDialog(View view){
        final String[] items = {"Struts2","Spring","Hibernate","Mybatis","Spring MVC"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择并确认");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int index) {
                Toast.makeText(AddPersonalItemActivity.this, items[index], Toast.LENGTH_SHORT).show();
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
    }



    int mSelectedActionIndex = -1;
    // 单选提示框
    private AlertDialog alertDialog2;
    public void showWalletAlertDialog(final View view){
        final String[] items = {"现金","支付宝","微信","信用卡","储蓄卡"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//        alertBuilder.setTitle("请选择并确认");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                mSelectedActionIndex = index;

                ((TextView)view).setText("" + items[mSelectedActionIndex]);
                // 关闭提示框
                alertDialog2.dismiss();
            }
        });
/*        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                ((TextView)view).setText("" + items[mSelectedActionIndex]);
                // 关闭提示框
                alertDialog2.dismiss();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 业务逻辑代码

                // 关闭提示框
                alertDialog2.dismiss();
            }
        });*/
        alertDialog2 = alertBuilder.create();
        alertDialog2.show();
    }

    int mOldSelectedActionIndex = -1;
    // 单选提示框
    private AlertDialog alertDialog4;
    public void showActionsAlertDialog(final View view){
//        final String[] items = {"Struts2","Spring","Hibernate","Mybatis","Spring MVC"};

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择该消费项所属活动");
        alertBuilder.setSingleChoiceItems(mActions, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                mSelectedActionIndex = index;
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                ((TextView)view).setText("所属活动:" + mActions[mSelectedActionIndex]);
                if (mSelectedActionIndex != -1 && mSelectedActionIndex != mOldSelectedActionIndex) {
                    mOldSelectedActionIndex = mSelectedActionIndex;
                    mActivityFileName = "activity" + Utils.getIdFromActivityName(mActions[mSelectedActionIndex]);
//                    Toast.makeText(CreateActionItemActivity.this, "已切换到" +mActivityFileName, Toast.LENGTH_SHORT).show();
                    showAlertDialog();
                }
                // 关闭提示框
                alertDialog4.dismiss();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 业务逻辑代码

                // 关闭提示框
                alertDialog4.dismiss();
            }
        });
        alertDialog4 = alertBuilder.create();
        alertDialog4.show();
        alertDialog4.getListView().setItemChecked(mOldSelectedActionIndex,true);
    }




    final List<Integer> selectedMemberIndex = new ArrayList();
    private void selectMember(int index) {
        selectedMemberIndex.add(index);
    }
    private void unselectMember(int value) {
        int index = selectedMemberIndex.indexOf(value);
        selectedMemberIndex.remove(index);
    }


    String[] mMembersToDialog;
    int mSelectedMembersNum = 0;
    // 多选提示框
    private AlertDialog alertDialog3;
    public void showMembersAlertDialog(final View view){
//        final String[] items = {"Struts2","Spring","Hibernate","Mybatis","Spring MVC"};


        // 创建一个AlertDialog建造者
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(this);
        // 设置标题
        alertDialogBuilder.setTitle("请选择参与人");
        // 参数介绍
        // 第一个参数：弹出框的信息集合，一般为字符串集合
        // 第二个参数：被默认选中的，一个布尔类型的数组
        // 第三个参数：勾选事件监听
        alertDialogBuilder.setMultiChoiceItems(mMembersToDialog, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // dialog：不常使用，弹出框接口
                // which：勾选或取消的是第几个
                // isChecked：是否勾选
                if (isChecked) {
                    // 选中
                    if (which == mMembersToDialog.length -1) {
                        selectAll(alertDialog3, true);
                    } else {
                        selectMember(which);
//                        Toast.makeText(CreateActionItemActivity.this, "选中"+ mMembers[which], Toast.LENGTH_SHORT).show();
                    }
                }else {
                    // 取消选中
                    if (which == mMembersToDialog.length -1) {
                        selectAll(alertDialog3, false);
                    } else {
                        unselectMember(which);

                        if (selectedMemberIndex.contains(mMembersToDialog.length-1)){
                            int index = selectedMemberIndex.indexOf(mMembersToDialog.length-1);
                            selectedMemberIndex.remove(index);
                            alertDialog3.getListView().setItemChecked(mMembersToDialog.length-1, false);
                        }
//                        Toast.makeText(CreateActionItemActivity.this, "取消选中"+ mMembers[which], Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                StringBuffer mSelectedMenbers = new StringBuffer();

                Log.i(TAG, "onClick: THQ1 " + selectedMemberIndex.size());

                for (Integer i : selectedMemberIndex) {
                    if (i >= mMembers.length) break;
                    mSelectedMenbers.append(mMembers[i] + ",");
                }
                mSelectedMembersNum = selectedMemberIndex.size();
                ((TextView)view).setText("参与人:" + mSelectedMenbers);
                // 关闭提示框
                alertDialog3.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 业务逻辑代码

                // 关闭提示框
                alertDialog3.dismiss();
            }
        });
        alertDialog3 = alertDialogBuilder.create();
        alertDialog3.show();

        ListView listView = alertDialog3.getListView();
        for (Integer i : selectedMemberIndex) {
            listView.setItemChecked(i, true);
        }
//        selectedMemberIndex.clear();
    }



    List<Integer> selectedPayManIndex = new ArrayList();
    // 多选提示框
    private AlertDialog alertDialog6;
    public void showPayManAlertDialog(final View view){
//        final String[] items = {"Struts2","Spring","Hibernate","Mybatis","Spring MVC"};

        // 创建一个AlertDialog建造者
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(this);
        // 设置标题
        alertDialogBuilder.setTitle("请选择付款人");
        // 参数介绍
        // 第一个参数：弹出框的信息集合，一般为字符串集合
        // 第二个参数：被默认选中的，一个布尔类型的数组
        // 第三个参数：勾选事件监听
        alertDialogBuilder.setMultiChoiceItems(mMembers, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // dialog：不常使用，弹出框接口
                // which：勾选或取消的是第几个
                // isChecked：是否勾选
                if (isChecked) {
                    // 选中
                    selectedPayManIndex.add(which);
//                    Toast.makeText(CreateActionItemActivity.this, "选中"+ mMembers[which], Toast.LENGTH_SHORT).show();
                }else {
                    // 取消选中
                    int index = selectedPayManIndex.indexOf(which);
                    selectedPayManIndex.remove(index);
//                    Toast.makeText(CreateActionItemActivity.this, "取消选中"+ mMembers[which], Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                StringBuffer mSelectedMenbers = new StringBuffer();

                Log.i(TAG, "onClick: THQ1 " + selectedPayManIndex.size());

                for (Integer i:selectedPayManIndex) {
                    mSelectedMenbers.append(mMembers[i] + ",");
                }
                ((TextView)view).setText("付款人:" + mSelectedMenbers);
                // 关闭提示框
//                resetAverageView();
                alertDialog6.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 业务逻辑代码

                // 关闭提示框
                alertDialog6.dismiss();
            }
        });
        alertDialog6 = alertDialogBuilder.create();
        alertDialog6.show();

        ListView listView = alertDialog6.getListView();
        for (Integer i : selectedPayManIndex) {
            listView.setItemChecked(i, true);
        }
//        selectedPayManIndex.clear();
    }


    private void selectAll(AlertDialog alertDialog, boolean all) {
        ListView listView = alertDialog.getListView();
        selectedMemberIndex.clear();
        for (int i = 0; i < listView.getCount() -1; i++) {
            if (all)selectedMemberIndex.add(i);
            listView.setItemChecked(i, all);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            Log.d("TAG","afterTextChanged--------------->");

            String totalCost = mTotalView.getText().toString();
            String payer = mWalletType.getText().toString();
            if (totalCost.equals("") || mSelectedMembersNum == 0 || !payer.contains(":")) {
                mAverageView.setText("Average");
                return;
            }

            String[] strs2 = totalCost.split("\\+");
            String[] strs3 = payer.split("\\:")[1].split("\\,");
            if (strs3.length != strs2.length) {
                mAverageView.setText("Average");
                return;
            }
            resetAverageView();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            Log.d("TAG","beforeTextChanged--------------->");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            Log.d("TAG","onTextChanged--------------->");
        }
    };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            mCreateDateView.setText(new StringBuffer().append(" ").append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
        }
    };

    private void resetAverageView() {
        getAverage(mAverageView);
    }


    public void getAverage(View view) {
        String totalCost = mTotalView.getText().toString();
        String payer = mWalletType.getText().toString();
        if (totalCost.equals("") || mSelectedMembersNum == 0 || !payer.contains(":")) {
            Toast.makeText(AddPersonalItemActivity.this, "请确定已输入成员，付款人和总花费！", Toast.LENGTH_SHORT).show();
            return;
        }
        int total = 0;

        String[] strs2 = totalCost.split("\\+");
        String[] strs3 = payer.split("\\:")[1].split("\\,");
        if (strs3.length != strs2.length) {
            Toast.makeText(AddPersonalItemActivity.this, "请确保付款人和总花费数量相一致！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (totalCost.contains("+")) {
                for (String s : strs2) {
                    total += Integer.parseInt(s);
                }
            } else {
                total = Integer.parseInt(totalCost);
            }
        } catch(Exception e) {
            Toast.makeText(AddPersonalItemActivity.this, "请确保总花费格式填写正确！", Toast.LENGTH_SHORT).show();
            return;
        }
        ((TextView)view).setText("人均:" + ((float)total/ mSelectedMembersNum));
    }


    class Type {
        public String mItemName;
        public int mIconNormalId;
        public int mIconSelectedId;
        boolean isChecked = false;

        public Type(String mItemName, int mIconNormalId, int mIconSelectedId) {
            this.mItemName = mItemName;
            this.mIconNormalId = mIconNormalId;
            this.mIconSelectedId = mIconSelectedId;
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mItemNameView;
        public ImageView mIconView;
        public LinearLayout mLayout;

        public ViewHolder(View v) {
            super(v);

            mItemNameView = (TextView) v.findViewById(R.id.view_item_picker_name);
            mIconView = (ImageView) v.findViewById(R.id.view_item_picker_icon);
            mLayout = (LinearLayout) v.findViewById(R.id.view_item_picker_layout);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Type> mDataset;
        private final TypedValue mTypedValue = new TypedValue();
        private int mLastSelectedIndes = 0;

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Type> myDataset) {
            mDataset = myDataset;
            myDataset.get(0).isChecked = true;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public void setmDataset(List<Type> myDataset) {
            mDataset = myDataset;
        }

        public List<Type> getItems() {
            return mDataset;
        }

        public void update(int position) {
            if (position == mLastSelectedIndes) return;
            mDataset.get(position).isChecked = true;
            if (mLastSelectedIndes != -1) mDataset.get(mLastSelectedIndes).isChecked = false;
            mLastSelectedIndes = position;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
//            isHost = true;
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_item_picker, parent, false);
//            isHost = false;
            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Type pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
            holder.mItemNameView.setText(pat.mItemName);
            if (pat.isChecked) {
                holder.mIconView.setImageResource(pat.mIconSelectedId);
                holder.mItemNameView.setTextColor(Color.WHITE);
                holder.mLayout.setBackgroundColor(getResources().getColor(R.color.primary));
            } else {
                holder.mIconView.setImageResource(pat.mIconNormalId);
                holder.mItemNameView.setTextColor(getResources().getColor(R.color.primary));
                holder.mLayout.setBackgroundColor(Color.WHITE);
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    public void commit(View view) {
        if ("".equals(mTotalView.getText().toString())) {
            Toast.makeText(AddPersonalItemActivity.this, "请填写完整信息！", Toast.LENGTH_SHORT).show();
            return;
        }

        setSPSet("Items", null);
        if (mItemsSet == null) mItemsSet = new HashSet<>();

//        mItemsSet.remove(selectedItem);
//            Log.i(TAG, "commit: THQ1 " + mItemsSet.size());
        mItemsSet.add(ItemPickerDialog.getIntance().getItem(mCurrentSelectedType,!mIsIncome).mItemName + "#" + mTotalView.getText().toString()
                + "#" + (mIsIncome?"income":"expand")+ "#" + mCreateDateView.getText().toString().trim() + "#" + mCreateTimeView.getText().toString().trim() + "#" +
                mSummaryView.getText().toString());
        setSPSet("Items", mItemsSet);



        SharedPreferences mSP = getSharedPreferences("wallet", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSP.edit();

        Set<String> mSet = mSP.getStringSet("Items", null);
        String type = mWalletType.getText().toString();

        String selectedItem = null;
        float income = 0;
        float expend = 0;
        float total = 0;
        for (String s : mSet) {
            if (s.split("\\#")[0].equals(type)) {
                selectedItem = s;
                break;
            }
        }

        String[] strs = selectedItem.split("\\#");
        income = Float.valueOf(strs[1]);
        expend = Float.valueOf(strs[2]);
        total = Float.valueOf(mTotalView.getText().toString());
        total = mIsIncome ? (income += total) : (expend += total);
        mSet.remove(selectedItem);
        mSet.add(type+"#"+income+"#"+expend);

        mEditor.putStringSet("Items", null);
        mEditor.commit();

        mEditor.putStringSet("Items", mSet);
        mEditor.commit();
        finish();
    }

}
