package com.thq.aaaccount;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.thq.aaaccount.widget.DividerItemDecoration;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tianhongqi on 17-6-1.
 */
public class CreateActionActivity extends AppCompatActivity {


    private static final String TAG = "THQ MainActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Member> myDataset;
    private MyAdapter mAdapter;
    private Set<String> mSet;
    private Set<String> mActivitySet;
    List<ViewHolder> mHolder;


    SharedPreferences mActivitySP;
    SharedPreferences.Editor mActivityEditor;

    private EditText mActionNameView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_action);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name_create_action);


        mActivitySP = getSharedPreferences("allactivity", Context.MODE_PRIVATE);
        mActivityEditor = mActivitySP.edit();

        mActionNameView = (EditText) findViewById(R.id.edit_text_action_name);

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
        Member people = new Member("");
        myDataset.add(people);
        mAdapter = new MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);
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
        Bitmap mIcon;
        Member(String name) {
            mName = name;
        }
    }


    public void addPeople(String name) {
        mAdapter.addPeople(name);
    }

    public void deletePeople(String name) {
        mAdapter.deletePeople(name);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;

        public EditText mMenberName;

        public Button mAdd;

        int patNum;

//        public EditText mEditText;
//        Spinner mSpinner;

//        public CheckBox mCheckBox;

        public String apkPath;
//        public int position;

        public ViewHolder(View v) {
            super(v);

            mImageView = (ImageView) v.findViewById(R.id.menber_image);
            mMenberName = (EditText) v.findViewById(R.id.menber_name);
            mAdd = (Button) v.findViewById(R.id.add_menber);


//            mEditText = (EditText) v.findViewById(R.id.pat_num);
//            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
//            mSpinner = (Spinner) v.findViewById(R.id.spinner1);
//            mSpinner.setAdapter(adapter);

            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("".equals(mMenberName.getText().toString()))) {
                        if ("+".equals(mAdd.getText().toString())) {
                            Log.i(TAG, "onClick: " + mMenberName.getText());
                            addPeople(mMenberName.getText().toString());
                            mAdd.setText("-");
                        } else {
                            deletePeople(mMenberName.getText().toString());
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
            editingPeople = myDataset.get(0);
            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Member> getPeoples() {
            return mDataset;
        }

        private void addPeople(String name) {
            editingPeople.mName = name;
            editingPeople = new Member("");
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
                    .inflate(R.layout.item_view, parent, false);
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
//            holder.mTextView.setText(pat.patName);
            if (pat.mIcon != null) {
                BitmapDrawable bd = new BitmapDrawable(getResources(), pat.mIcon);
                holder.mImageView.setImageDrawable(bd);
            }
            holder.mMenberName.setText(pat.mName);
            if (pat.mName.equals("")) {
                holder.mMenberName.requestFocus();
                holder.mAdd.setText("+");
            } else {
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
        if (mActivitySet != null) {
            for (String s : mActivitySet) {
                if (s.split("\\#")[0].equals(activityName)) {
                    Toast.makeText(this, "活动名已存在，请重新输入！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        int activitynums = mActivitySP.getInt("activitynums", -1);

        if (mActivitySet == null) mActivitySet = new HashSet<>();
        mActivitySet.add(activityName+"#"+(activitynums+1));
        Utils.setSPSet("allactivitys", null, "allactivity", this);
        Utils.setSPSet("allactivitys", mActivitySet, "allactivity", this);
        Utils.setSPInt("activitynums", activitynums+1, "allactivity", this);


//        setSPString("ActionName", mActionNameView.getText().toString());
//        myDataset = mAdapter.getPeoples();
        for (Member p:myDataset) {
            if (!p.mName.equals("")) {
                mSet.add(p.mName);
            }
        }

        Utils.setSPString("ActionName", activityName, "activity"+(activitynums+1), this);
        Utils.setSPSet("Members", mSet, "activity"+(activitynums+1), this);
//        setSPSet("Members", mSet);
        finish();
    }

}
