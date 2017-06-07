package com.thq.aaaccount;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thq.aaaccount.widget.DividerItemDecoration;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tianhongqi on 17-6-1.
 */
public class CreateActionActivity extends AppCompatActivity {


    private static final String TAG = "CreateActionActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Member> myDataset;
    private MyAdapter mAdapter;
    private Set<String> mSet;
    private Set<String> mActivitySet;
    List<ViewHolder> mHolder;

    private Set<String> mMemberSet;
    List<String> mSelectedMembers;
    String[] mMembers;

    SharedPreferences mActivitySP;
    SharedPreferences.Editor mActivityEditor;

    private EditText mActionNameView;
    private EditText mPrepaid;

    private float mHistoryPrepaid = -1;

    private MultiAlertDialog mMultiAlertDialog;

    boolean mIsEditMode = false;
    String mActivityId = null;
    String mActivityName = null;

    int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_action);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mActivityName = extras.getString("activityName");
            mActivityId = extras.getString("activityId");
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mActivityName == null) {
            mToolbar.setTitle(R.string.app_name_create_action);
        } else {
            mIsEditMode = true;
            mToolbar.setTitle(R.string.app_name_edit_action);
        }
        setSupportActionBar(mToolbar);


        mActivitySP = getSharedPreferences("allactivity", Context.MODE_PRIVATE);
        mActivityEditor = mActivitySP.edit();

        mActionNameView = (EditText) findViewById(R.id.edit_text_action_name);
        mPrepaid = (EditText) findViewById(R.id.edit_text_host_name);
        TextView textView = (TextView) findViewById(R.id.import_members);
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSet = new HashSet<>();
        mActivitySet = new HashSet<>();
        mActivitySet = mActivitySP.getStringSet("allactivitys", null);

        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        if (mIsEditMode) {
            Set<String> set = Utils.getSPSet("Members", null, "activity" + mActivityId);
            for (String str:set) {
                String[] strs = str.split("\\#");
                myDataset.add(new Member(strs[0], strs[1]));
            }
            mActionNameView.setText(mActivityName);
        }
        myDataset.add(new Member("", ""));

        mAdapter = new MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);

        mMultiAlertDialog = new MultiAlertDialog(this);
//        mMemberSet = mSP.getStringSet("Members", null);

        mSelectedMembers = new ArrayList<>();

        initDatePicker();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mMemberSet = MemberDict.getIntance().getMembers(CreateActionActivity.this);
                if (mMemberSet != null) {
                    mMembers = new String[mMemberSet.size()];
                    mMembers = mMemberSet.toArray(mMembers);
                }
            }
        }).start();
    }

    private void initDatePicker() {
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setSPString(String key, String value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void setSPInt(String key, int value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void setSPSet(String key, Set<String> value) {
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    class Member {
        String mName;
        String mPrepaid;
        Bitmap mIcon;
        Member(String name, String prepaid) {
            mName = name;
            mPrepaid = prepaid;
        }
    }


    public void addPeople(String name) {
        if (mMemberSet == null || !mMemberSet.contains(name)) {
            MemberDict.getIntance().addMember(this, name);
        }
        mAdapter.addPeople(name);
    }

    public void deletePeople(String name) {
        mAdapter.deletePeople(name);
    }

    public void updatePeople() {
        mAdapter.notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;

        public EditText mMemberName;
        public EditText mPrepaid;

        public Button mAdd;

        Member mMember;


        private TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (mAdd.getText().toString().equals("-")){
                    mAdd.setText("√");
                    mAdd.setTextColor(CreateActionActivity.this.getResources().getColor(R.color.primary_dark));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        };


        public ViewHolder(View v) {
            super(v);

            mImageView = (ImageView) v.findViewById(R.id.menber_image);
            mMemberName = (EditText) v.findViewById(R.id.menber_name);
            mPrepaid = (EditText) v.findViewById(R.id.prepaid);
            mAdd = (Button) v.findViewById(R.id.add_menber);

            mMemberName.addTextChangedListener(textWatcher);
            mPrepaid.addTextChangedListener(textWatcher);

            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("".equals(mMemberName.getText().toString()))) {
                        if ("+".equals(mAdd.getText().toString())) {
                            Log.i(TAG, "onClick: " + mMemberName.getText());
                            if (!"".equals(mPrepaid.getText().toString())) {
                                mHistoryPrepaid = Float.parseFloat(mPrepaid.getText().toString());
                            }
                            addPeople(mMemberName.getText().toString());
                            mAdd.setText("-");
                        } else if ("-".equals(mAdd.getText().toString())) {
                            deletePeople(mMemberName.getText().toString());
                        } else {
                            mMember.mName = mMemberName.getText().toString();
                            mMember.mPrepaid = mPrepaid.getText().toString();
                            updatePeople();
                            mAdd.setText("-");
                        }
                    }
                }
            });
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<Member> mDataset;
        private final TypedValue mTypedValue = new TypedValue();
        private Member editingPeople;

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Member> myDataset) {
            mDataset = myDataset;
            if (!mIsEditMode) {
                editingPeople = myDataset.get(0);
            } else {
                editingPeople = myDataset.get(myDataset.size()-1);
            }
            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Member> getMembers() {
            return mDataset;
        }

        private void addPeople(String name) {
            if (mHistoryPrepaid == -1) {
                if (!"".equals(mPrepaid.getText().toString())) {
                    mHistoryPrepaid = Integer.parseInt(mPrepaid.getText().toString());
                } else {
                    mHistoryPrepaid = 0;
                }
            }
            editingPeople.mName = name;
            editingPeople.mPrepaid = ""+mHistoryPrepaid;
            editingPeople = new Member("","");
            editingPeople.mPrepaid = ""+mHistoryPrepaid;
            mDataset.add(editingPeople);
            this.notifyDataSetChanged();
        }

        private void deletePeople(String name) {
            for (Member p:mDataset) {
                if (p.mName.equals(name)) {
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
                    .inflate(R.layout.item_create_activity, parent, false);
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
            Member pat = mDataset.get(position);
            holder.mMember = pat;
//            holder.mTextView.setText(pat.patName);
            if (pat.mIcon != null) {
                BitmapDrawable bd = new BitmapDrawable(getResources(), pat.mIcon);
                holder.mImageView.setImageDrawable(bd);
            }
            holder.mMemberName.setText(pat.mName);
            holder.mPrepaid.setText(pat.mPrepaid);
            if (pat.mName.equals("")) {
                holder.mMemberName.requestFocus();
                holder.mAdd.setText("+");
                holder.mAdd.setTextColor(CreateActionActivity.this.getResources().getColor(R.color.primary_dark));
            } else {
                holder.mAdd.setTextColor(CreateActionActivity.this.getResources().getColor(android.R.color.holo_red_light));
                holder.mAdd.setText("-");
            }

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

    public void createActionDone(View v) {
        String activityName = mActionNameView.getText().toString();
        if (activityName.equals("") || myDataset.size() <= 1) {
            Toast.makeText(this, "请填写完整信息！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mActivitySet != null && !(mIsEditMode && activityName.equals(mActivityName))) {
            for (String s : mActivitySet) {
                if (s.split("\\#")[0].equals(activityName)) {
                    Toast.makeText(this, "活动名已存在，请重新输入！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        String selectedItem = null;
        for (String s : mActivitySet) {
            if (s.split("\\#")[0].equals(mActivityName)) {
                selectedItem = s;
            }
        }

        if (mIsEditMode) {
            int activitynums = Integer.parseInt(mActivityId) -1;

            if (mActivitySet == null) mActivitySet = new HashSet<>();


            mActivitySet.remove(selectedItem);

            mActivitySet.add(activityName + "#" + (activitynums + 1) + "#" +
                    selectedItem.split("\\#")[2]
                    + "#" + myDataset.get(0).mName
            );
            Utils.setSPSet("allactivitys", null, "allactivity");
            Utils.setSPSet("allactivitys", mActivitySet, "allactivity");

            for (Member p : myDataset) {
                if (!p.mName.equals("")) {
                    mSet.add(p.mName + "#" + p.mPrepaid);
                }
            }

            String activityFileName = "activity" + (activitynums + 1);

            Utils.setSPString("ActionName", activityName, activityFileName);
            Utils.setSPSet("Members", null, activityFileName);
            Utils.setSPSet("Members", mSet, activityFileName);
        } else {
            int activitynums = mActivitySP.getInt("activitynums", -1);

            if (mActivitySet == null) mActivitySet = new HashSet<>();
            mActivitySet.add(activityName + "#" + (activitynums + 1) + "#" +
                    new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay)
                    + "#" + myDataset.get(0).mName
            );
            Utils.setSPSet("allactivitys", null, "allactivity");
            Utils.setSPSet("allactivitys", mActivitySet, "allactivity");
            Utils.setSPInt("activitynums", activitynums + 1, "allactivity");


//        setSPString("ActionName", mActionNameView.getText().toString());
//        myDataset = mAdapter.getPeoples();
            for (Member p : myDataset) {
                if (!p.mName.equals("")) {
                    mSet.add(p.mName + "#" + p.mPrepaid);
                }
            }

            Utils.setSPString("ActionName", activityName, "activity" + (activitynums + 1));
            Utils.setSPString("HostName", myDataset.get(0).mName, "activity" + (activitynums + 1));
            Utils.setSPSet("Members", mSet, "activity" + (activitynums + 1));
//        setSPSet("Members", mSet);
        }
        finish();
    }

    public void importMembers(View view) {
        if (mMembers == null) {
            Toast.makeText(this, "暂没有常用联系人！", Toast.LENGTH_SHORT).show();
            return;
        }
        mMultiAlertDialog.showMembersAlertDialog(mMembers, mSelectedMembers, new MultiAlertDialog.CallbackResultListener() {

            @Override
            public void done(List<String> result) {
                for (String s:mSelectedMembers) {
                    addPeople(s);
                }
            }
        });
    }
}
