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

import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.widget.DividerItemDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ViewAllActionActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;

    private TextView mHint;

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
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mHint = (TextView) findViewById(R.id.action_hint);

        mSet = new HashSet<>();
        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();
        myDataset.clear();
        loadActivitys();
        if (myDataset.size() == 0) {
            mHint.setVisibility(View.VISIBLE);
        } else {
            mHint.setVisibility(View.GONE);
        }
        if (mAdapter == null) {
            mAdapter = new MyAdapter(this, myDataset);
            mAdapter.setOnItemClickListener(onItemClickListener);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.update();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadActivitys() {
        mSet =  mSP.getStringSet("allactivitys", null);
        mSet = sortByValue(mSet);
        if (mSet != null) {
            for (String activity : mSet) {
                String[] strs = activity.split("\\#");
                Set<String> members = Utils.getSPSet("Members", null, "activity"+strs[1]);
//                Log.i(TAG, "loadItems: " + strs[0] + strs[1] + strs[2] + strs[3] + strs[4]);
                Activity activity1 = new Activity(strs[0], loadMembers(members), "china", strs[2], strs[3]);
                myDataset.add(activity1);
            }
        }
    }

    private String loadMembers(Set<String> members) {
        Set<String> set = new HashSet<>();
        for (String s:members) {
            set.add(s.split("\\#")[0]);
        }
        return set.toString();
    }

    public static Set<String> sortByValue(Set<String> set){
        if (set == null) return null;
        List<String> setList= new ArrayList<String>(set);
        Collections.sort(setList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // TODO Auto-generated method stub
                return o2.toString().split("\\#")[2].compareTo(o1.toString().split("\\#")[2]);
            }

        });
        set = new LinkedHashSet<String>(setList);//这里注意使用LinkedHashSet
        return set;
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
        Activity (String actionName, String members, String local, String date, String creater) {
            mActivityName = actionName;
            mCreater = creater;
            mLocal = local;
            mCreateDate = date;
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mActivityNameView;
        public TextView mCreaterView;
        public TextView mMembersView;
        public TextView mCreateDateView;
        public TextView mCreateLocalView;
        public Button mDelete;
        public Button mEdit;

        private MyItemClickListener myItemClickListener;

        public ViewHolder(View v, MyItemClickListener iemClickListener) {
            super(v);

            myItemClickListener = iemClickListener;
            v.setOnClickListener(this);

            mMembersView = (TextView) v.findViewById(R.id.view_action_members);
            mActivityNameView = (TextView) v.findViewById(R.id.view_action_action_name);
            mCreaterView = (TextView) v.findViewById(R.id.view_action_creater);
            mCreateDateView = (TextView) v.findViewById(R.id.view_action_date);
            mCreateLocalView = (TextView) v.findViewById(R.id.view_action_create_local);
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

        @Override
        public void onClick(View v) {
            myItemClickListener.onItemClick(v, getPosition());
        }
    }

    public void showAlertDialog(final String key){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("警告");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
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

    private void viewActivity(String activityName) {
        Intent intent = new Intent(this, ViewActionItemActivity.class);
//        intent.putExtra("itemName", itemName);
        intent.putExtra("activityId", ""+Utils.getIdFromActivityName(activityName));
        startActivity(intent);
    }

    private void editActivity(String activityName) {
        Intent intent = new Intent(this, CreateActionActivity.class);
        intent.putExtra("activityName", activityName);
        intent.putExtra("activityId", ""+Utils.getIdFromActivityName(activityName));
        startActivity(intent);
    }

    public interface MyItemClickListener {
        public void onItemClick(View view,int position);
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Activity> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        MyItemClickListener mItemClickListener;

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

        /**
         * 设置Item点击监听
         * @param listener
         */
        public void setOnItemClickListener(MyItemClickListener listener){
            this.mItemClickListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
//            isHost = true;
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view_all_action, parent, false);
//            isHost = false;
            v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v, mItemClickListener);
//            mHolder.add(vh);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Activity pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
            holder.mActivityNameView.setText(pat.mActivityName);
            holder.mCreateLocalView.setText(pat.mLocal);
            holder.mCreaterView.setText(pat.mCreater);
            holder.mCreateDateView.setText(pat.mCreateDate);
            holder.mMembersView.setText(pat.mMembers);
//            Log.i(TAG, "onBindViewHolder: THQ");
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
//
//    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
//        @Override
//        public void onItemClick(View view, int position) {
//            viewActivity(myDataset.get(position).mActivityName);
//        }
//    };

    private MyItemClickListener onItemClickListener = new MyItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            viewActivity(myDataset.get(position).mActivityName);
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




    String mDataDir = "/data/data/com.thq.aaaccount/shared_prefs/";
    String mBackupDir = "/sdcard/aaaccount/shared_prefs/";
    public void backupActivityEvent(View view) {
        showBackupDialog();
    }

    public void restoreActivityEvent(View view) {
        showRestoreDialog();
    }

    public void backupActivity() {
        SimpleDateFormat sDateFormat =   new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        copyFolder(mDataDir, mBackupDir+date+"/");
        Toast.makeText(ViewAllActionActivity.this, "备份完成！！", Toast.LENGTH_SHORT).show();
    }

    public void restoreActivity() {
        File a=new File(mBackupDir);
        final String[] file=a.list();
        if (file.length == 0) {
            Toast.makeText(ViewAllActionActivity.this, "没有可还原数据！！", Toast.LENGTH_SHORT).show();
            return;
        }
        new MultiAlertDialog(this).showSingleAlertDialog(file, new ArrayList<String>(), new MultiAlertDialog.CallbackResultListener() {
            @Override
            public void done(List<String> result) {
                File file1 = new File(mDataDir);
                if (file1.exists()) file1.delete();
                copyFolder(mBackupDir + result.get(0), mDataDir);
                Toast.makeText(ViewAllActionActivity.this, "还原完成！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showRestoreDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("警告");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setMessage("还原后，现有的数据将消失，确定要还原吗？");
        alertDialogBuilder.setPositiveButton("还原", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                restoreActivity();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertDialogBuilder.show();
    }

    public void showBackupDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("警告");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setMessage("确定好备份数据吗？");
        alertDialogBuilder.setPositiveButton("备份", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                backupActivity();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertDialogBuilder.show();
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }
}
