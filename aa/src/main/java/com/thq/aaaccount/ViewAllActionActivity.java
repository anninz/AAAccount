package com.thq.aaaccount;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class ViewAllActionActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Activity> myDataset;
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
        setContentView(R.layout.activity_view_all_action);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            mActivityId = extras.getString("activityId");
//        }
        mActivityFileName = "allactivity";// + mActivityId;//Utils.getIdFromActivityName(this, activityName);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name_view_item);


        mSP = getSharedPreferences(mActivityFileName , Context.MODE_PRIVATE);
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

    }

    @Override
    public void onResume() {
        super.onResume();
        myDataset.clear();
        loadActivitys();
        if (mAdapter == null) {
            mAdapter = new ViewAllActionActivity.MyAdapter(this, myDataset);
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

    private void loadActivitys() {
        mSet =  mSP.getStringSet("allactivitys", null);
        if (mSet != null) {
            for (String activity : mSet) {
                String[] strs = activity.split("\\#");
                String members = Utils.getSPSet("Members", null, "activity"+strs[1]).toString();
//                Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
                Activity activity1 = new Activity(strs[0], members, "null", "null", "null");
                myDataset.add(activity1);
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
//        String activityId = Utils.getIdFromActivityName(key);
        Utils.setSPSet("allactivitys", null, "allactivity");
        if (mSet == null) mSet = new HashSet<>();
//        Set<String> mtemsSet = new HashSet<>();

        Log.i(TAG, "commit: THQ1 " + key);

        for (String s:mSet) {
            if (s.contains(key)) {
                mSet.remove(s);
                break;
            }
        }
        Utils.setSPSet("allactivitys", mSet, "allactivity");
    }

    class Activity {
        public String mActivityName;
        public String mCreater;
        public String mLocal;
        public String mCreateDate;
        public String mMembers;
        Activity (String actionName, String members, String local, String total, String creater) {
            mActivityName = actionName;
            mCreater = creater;
            mLocal = local;
            mCreateDate = total;
            mMembers = members;
        }
    }


    public void deleteActivity(String name) {
        mAdapter.deleteActivity(name);
        commit(name);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mActivityNameView;
        public TextView mCreaterView;
        public TextView mMembersView;
        public TextView mCreateDateView;
        public TextView mCreateLocalView;
        public Button mDelete;
        public Button mEdit;

        public ViewHolder(View v) {
            super(v);

            mMembersView = (TextView) v.findViewById(R.id.members);
            mActivityNameView = (TextView) v.findViewById(R.id.action_name);
            mCreaterView = (TextView) v.findViewById(R.id.creater);
            mCreateDateView = (TextView) v.findViewById(R.id.create_date);
            mCreateLocalView = (TextView) v.findViewById(R.id.create_local);
            mDelete = (Button) v.findViewById(R.id.button7);
            mEdit = (Button) v.findViewById(R.id.button8);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("".equals(mActivityNameView.getText().toString()))) {
                        showAlertDialog(mActivityNameView.getText().toString());
//                            deleteActivity(mActivityNameView.getText().toString());
                    }
                }
            });

            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("".equals(mActivityNameView.getText().toString()))) {
                        editActivity(mActivityNameView.getText().toString()/*.split("\\:")[1]*/);
                    }
                }
            });

        }
    }

    public void showAlertDialog(final String key){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("警告");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_dialer);
        alertDialogBuilder.setMessage("删除后，所有该活动相关的信息都将消失，请确定要删除吗？");
        alertDialogBuilder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteActivity(key);
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
//        alertDialogBuilder.setNeutralButton("没感觉", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(ViewAllActionActivity.this, arg1 + "不讨厌也不喜欢", Toast.LENGTH_SHORT).show();
//            }
//        });
        // 方式一
        /*AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();*/
        // 方式二
        alertDialogBuilder.show();
    }

    private void editActivity(String activityName) {
        Intent intent = new Intent(this, ViewActionItemActivity.class);
//        intent.putExtra("itemName", itemName);
        intent.putExtra("activityId", ""+Utils.getIdFromActivityName(activityName));
        startActivity(intent);
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Activity> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Activity> myDataset) {
            mDataset = myDataset;
            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Activity> getPeoples() {
            return mDataset;
        }

        public void update() {
//            mDataset.clear();
//            mDataset = myDataset;
            notifyDataSetChanged();
        }


        private void deleteActivity(String name) {
            for (Activity p:mDataset) {
                if (p.mActivityName.equals(name)) {
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
                    .inflate(R.layout.item_view_all_action, parent, false);
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
            Activity pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
            holder.mActivityNameView.setText(pat.mActivityName);
            holder.mCreateLocalView.setText(pat.mMembers);
            holder.mCreaterView.setText(pat.mCreater);
            holder.mCreateDateView.setText(pat.mCreateDate);
            holder.mMembersView.setText(pat.mLocal);
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


    public void createAction(View view) {
        Intent intent = new Intent(this, CreateActionActivity.class);
        startActivity(intent);
    }

    public void getBill(View view) {
        if ( mSP.getStringSet("Members", null) != null) {
            Intent intent = new Intent(ViewAllActionActivity.this, ViewBillActivity.class);
            intent.putExtra("activityId", mActivityId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先创建活动", Toast.LENGTH_SHORT).show();
        }
    }

}
