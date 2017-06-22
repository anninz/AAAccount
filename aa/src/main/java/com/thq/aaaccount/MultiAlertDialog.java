package com.thq.aaaccount;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianhongqi on 17-6-3.
 */

public class MultiAlertDialog {

    static String TAG = "MultiAlertDialog";

    Context mContext;
    MultiAlertDialog(Context context) {
        mContext = context;
    }

    //    boolean selectedAll = false;
    int mSelectedMembersNum = 0;

    final List<Integer> selectedMemberIndex = new ArrayList();

    private void selectMember(int index) {
        selectedMemberIndex.add(index);
    }

    private void unselectMember(int value) {
        int index = selectedMemberIndex.indexOf(value);
        selectedMemberIndex.remove(index);
    }

    private void selectAll(AlertDialog alertDialog, boolean all) {
        ListView listView = alertDialog.getListView();
        selectedMemberIndex.clear();
        for (int i = 0; i < listView.getCount() -1; i++) {
            if (all)selectedMemberIndex.add(i);
            listView.setItemChecked(i, all);
        }
    }

    private void copyResult(String[] org, List result) {
        for (Integer i : selectedMemberIndex) {
            result.add(org[i]);
        }
    }

    // 多选提示框
    private AlertDialog alertDialog3;
    String[] mMembersToDialog;
    public void showMembersAlertDialog(final String[] ori, final List<String> result, final CallbackResultListener listener){
//        final String[] items = {"Struts2","Spring","Hibernate","Mybatis","Spring MVC"};

        mMembersToDialog = new String[ori.length + 1];
        mMembersToDialog[ori.length] = "全选";
        for (int i = 0;i < ori.length; i++) {
            mMembersToDialog[i] = ori[i];
        }

        // 创建一个AlertDialog建造者
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(mContext);
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
//                        selectedAll = true;
                        selectAll(alertDialog3, true);
                    } else {
                        selectMember(which);
//                        Toast.makeText(CreateActionItemActivity.this, "选中"+ mMembers[which], Toast.LENGTH_SHORT).show();
                    }
                }else {
                    // 取消选中
                    if (which == mMembersToDialog.length -1) {
//                        selectedAll = false;
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

                Log.i(TAG, "onClick: THQ1 " + selectedMemberIndex.size());

                copyResult(ori, result);

                listener.done(result);

                mSelectedMembersNum = selectedMemberIndex.size();
                // 关闭提示框
                alertDialog3.dismiss();
            }
        });
        alertDialogBuilder.setNeutralButton("编辑", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                Intent intent = new Intent(mContext, EditMembersActivity.class);
                mContext.startActivity(intent);
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
    }

    interface CallbackResultListener {
        void done(List<String> result);
    }



    // 单选提示框
    private AlertDialog alertDialog2;
    private int mSingleSelectedIndex = 0;
    public void showSingleAlertDialog(final String[] ori, final List<String> result, final CallbackResultListener listener){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setTitle("请选择并确认");
        alertBuilder.setSingleChoiceItems(ori, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                mSingleSelectedIndex = index;
            }
        });
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码
                result.add(ori[mSingleSelectedIndex]);
                listener.done(result);
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
}
