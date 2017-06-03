package com.thq.aaaccount;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateActionItemActivity extends AppCompatActivity {

    private static final String TAG = "THQ MainActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Item> myDataset;
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
    TextView mActionNameView;
    TextView mAverageView;
    TextView mSelectedMembersView;
    TextView mPayerView;

    boolean mIsEditMode = false;
    String mActivityFileName = null;
    String mActivityName = null;
    String mActivityId = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_action_item);

        Bundle extras = getIntent().getExtras();
        String itemName = null;
        String activityId = null;
        if (extras != null) {
            itemName = extras.getString("itemName");
            activityId = extras.getString("activityId");
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (itemName == null) {
            mToolbar.setTitle(R.string.app_name_create_item);
        } else {
            mIsEditMode = true;
            mToolbar.setTitle(R.string.app_name_edit_item);
        }

        mTotalView = (TextView) findViewById(R.id.create_date);
        mItemNameView = (TextView) findViewById(R.id.action_name);
        mActionNameView = (TextView) findViewById(R.id.belong_action);
        mAverageView = (TextView) findViewById(R.id.average);
        mPayerView = (TextView) findViewById(R.id.cteate_time);
        mSelectedMembersView = (TextView) findViewById(R.id.members);

        mPayerView.addTextChangedListener(textWatcher);
        mTotalView.addTextChangedListener(textWatcher);

        mActivityId = activityId == null? String.valueOf(Utils.getLastestActivityId()):activityId;

        mActivityFileName = "activity" + mActivityId;

        mSP = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
        mEditor = mSP.edit();

//        mActions[0] = mSP.getString("ActionName", null);
        mActivitySet = Utils.getAllActivity();
        mActions = new String[mActivitySet.size()];

        mActivityName = mSP.getString("ActionName", null);

        int i1 = 0;
        for (String s:mActivitySet) {
            mActions[i1] = s.split("\\#")[0];
            if (mActions[i1].equals(mActivityName)) mOldSelectedActionIndex = i1;
            i1++;
        }
//        mActions = mActivitySet.toArray(mActions);

        mItemsSet = mSP.getStringSet("Items", null);
        mMemberSet = mSP.getStringSet("Members", null);
        mMembers = new String[mMemberSet.size()];
        mMembers = mMemberSet.toArray(mMembers);
        mMembersToDialog = new String[mMembers.length + 1];
        mMembersToDialog[mMembers.length] = "全选";
        for (int i = 0;i < mMembers.length; i++) {
            mMembersToDialog[i] = mMembers[i];
        }

        mActionNameView.setText("所属活动:" + mActivityName);
        if (mIsEditMode) {
            loadItemsToView(itemName);
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
        mPayerView.setText("payer");
        mSelectedMembersView.setText("members");
    }

    String selectedItem = null;
    private void loadItemsToView(String key) {
        if (mItemsSet != null) {
            for (String item : mItemsSet) {
                if (item.contains(key)) {
                    selectedItem = item;
                    String[] strs = item.split("\\#");
                    mItemNameView.setText(strs[0].split("\\:")[1]);
                    mSelectedMembersView.setText(strs[2]);
                    mSelectedMembersNum = strs[2].split("\\,").length;
                    mPayerView.setText(strs[3]);
                    mTotalView.setText(strs[4].split("\\:")[1]);
                    mAverageView.setText(strs[5]);
                    Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
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
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_dialer);
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
                Toast.makeText(CreateActionItemActivity.this, items[index], Toast.LENGTH_SHORT).show();
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
    }



    // 单选提示框
    private AlertDialog alertDialog2;
    public void showSingleAlertDialog(final View view){
//        final String[] items = {"Struts2","Spring","Hibernate","Mybatis","Spring MVC"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择并确认");
        alertBuilder.setSingleChoiceItems(mMembers, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                Toast.makeText(CreateActionItemActivity.this, mMembers[index], Toast.LENGTH_SHORT).show();
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                ((TextView)view).setText("付款人:" + mMembers[mSelectedActionIndex]);
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
        });
        alertDialog2 = alertBuilder.create();
        alertDialog2.show();
    }

    int mSelectedActionIndex = -1;
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
    boolean selectedAll = false;
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
                        selectedAll = true;
                        selectAll(alertDialog3, true);
                    } else {
                        selectMember(which);
//                        Toast.makeText(CreateActionItemActivity.this, "选中"+ mMembers[which], Toast.LENGTH_SHORT).show();
                    }
                }else {
                    // 取消选中
                    if (which == mMembersToDialog.length -1) {
                        selectedAll = false;
                        selectAll(alertDialog3, false);
                    } else {
                        unselectMember(which);
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

                if (selectedAll) {
                    for (String s:mMembers) {
                        mSelectedMenbers.append(s + ",");
                    }
                    selectedAll = false;
                    mSelectedMembersNum = mMembers.length;
                } else {
                    for (Integer i : selectedMemberIndex) {
                        mSelectedMenbers.append(mMembers[i] + ",");
                    }
                    mSelectedMembersNum = selectedMemberIndex.size();
                }
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

    private void resetAverageView() {
        mAverageView.setText("Average");
    }

    public void getAverage(View view) {
        String totalCost = mTotalView.getText().toString();
        String payer = mPayerView.getText().toString();
        if (totalCost.equals("") || mSelectedMembersNum == 0 || !payer.contains(":")) {
            Toast.makeText(CreateActionItemActivity.this, "请确定已输入成员，付款人和总花费！", Toast.LENGTH_SHORT).show();
            return;
        }
        int total = 0;

        String[] strs2 = totalCost.split("\\+");
        String[] strs3 = payer.split("\\:")[1].split("\\,");
        if (strs3.length != strs2.length) {
            Toast.makeText(CreateActionItemActivity.this, "请确保付款人和总花费数量相一致！", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(CreateActionItemActivity.this, "请确保总花费格式填写正确！", Toast.LENGTH_SHORT).show();
            return;
        }
        ((TextView)view).setText("人均:" + ((float)total/ mSelectedMembersNum));
    }

    public void commit(View view) {
        if (mItemNameView.getText().toString().equals("")
                || !mActionNameView.getText().toString().contains(":")
                || !mSelectedMembersView.getText().toString().contains(":")
                || !mAverageView.getText().toString().contains(":")
                || !mPayerView.getText().toString().contains(":")
                || mTotalView.getText().toString().equals("")) {
            Toast.makeText(CreateActionItemActivity.this, "请填写完整信息！", Toast.LENGTH_SHORT).show();
            return;
        }

        setSPSet("Items", null);
        if (mItemsSet == null) mItemsSet = new HashSet<>();
//        Set<String> mtemsSet = new HashSet<>();

        mItemsSet.remove(selectedItem);
            Log.i(TAG, "commit: THQ1 " + mItemsSet.size());
        mItemsSet.add(/*String.valueOf(mItemsSet.size())+*/"消费项:" + mItemNameView.getText().toString() + "#" + mActionNameView.getText().toString()
                + "#" + mSelectedMembersView.getText().toString() + "#" + mPayerView.getText().toString()
                + "#" + "总共:" + mTotalView.getText().toString() + "#" + mAverageView.getText().toString());
//        int i = 1;
//        for (String s:mItemsSet ) {
//            Log.i(TAG, "commit: THQ1 " + s);
//            mtemsSet.add(s);
//            i++;
//        }
        setSPSet("Items", mItemsSet);
        finish();
    }
}
