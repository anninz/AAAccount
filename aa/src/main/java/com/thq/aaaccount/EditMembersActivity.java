package com.thq.aaaccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.thq.aaaccount.Utils.Utils;
import com.thq.aaaccount.widget.DividerItemDecoration;
import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EditMembersActivity extends AppCompatActivity {

    private static final String TAG = "THQ MainActivity";

    private Toolbar mToolbar;

    private TextView mHint;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<String> myDataset;
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
        setContentView(R.layout.activity_view_action_item);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mActivityId = extras.getString("activityId");
        }
        if ("".equals(mActivityId) || mActivityId == null) mActivityId = String.valueOf(Utils.getLastestActivityId());
        mActivityFileName = "activity" + mActivityId;//Utils.getIdFromActivityName(this, activityName);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitle(R.string.app_name_view_item);
        mToolbar.setTitle(Utils.getActivityNameFromId(mActivityId));
        setSupportActionBar(mToolbar);


//        mSP = getSharedPreferences(mActivityFileName, Context.MODE_PRIVATE);
//        mEditor = mSP.edit();

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setmDivider(getDrawable(R.drawable.list_divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mHint = (TextView) findViewById(R.id.item_hint);

        mSet = new HashSet<>();
        // specify an adapter (see also next example)

    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
        if (myDataset.size() == 0) {
            mHint.setVisibility(View.VISIBLE);
        } else {
            mHint.setVisibility(View.GONE);
        }
        if (mAdapter == null) {
            mAdapter = new EditMembersActivity.MyAdapter(this, myDataset);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.update();
        }

    }

    private void loadItems() {
        myDataset = new ArrayList<>(MemberDict.getIntance().getMembers(this));
    }

    public static Set<String> sortByValue(Set<String> set){
        if (set == null) return null;
        List<String> setList= new ArrayList<String>(set);
        Collections.sort(setList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                // TODO Auto-generated method stub
                return o2.toString().split("\\#")[6].compareTo(o1.toString().split("\\#")[6]);
            }

        });
        set = new LinkedHashSet<String>(setList);//这里注意使用LinkedHashSet
        return set;
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
        setSPSet("Items", null);
        if (mSet == null) mSet = new HashSet<>();
//        Set<String> mtemsSet = new HashSet<>();

        Log.i(TAG, "commit: THQ1 " + key);

        for (String s:mSet) {
            if (s.contains(key)) {
                mSet.remove(s);
                break;
            }
        }
        setSPSet("Items", mSet);
    }

    public void deleteItem(String name) {
        mAdapter.deleteItem(name);
        commit(name);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public EditText mItemName;
        public Button mDelete;

        public ViewHolder(View v) {
            super(v);

            mItemName = (EditText) v.findViewById(R.id.view_edit_name);
            mDelete = (Button) v.findViewById(R.id.button7);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!("".equals(mItemName.getText().toString()))) {
                            deleteItem(mItemName.getText().toString());
                    }
                }
            });

        }
    }

    private void editItem(String itemName) {
        Intent intent = new Intent(this, CreateActionItemActivity.class);
        intent.putExtra("itemName", itemName);
        intent.putExtra("activityId", mActivityId);
        startActivity(intent);
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mBackground;
        private List<String> mDataset;
        private final TypedValue mTypedValue = new TypedValue();

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<String> myDataset) {
            mDataset = myDataset;
            mHolder = new ArrayList<>();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<String> getPeoples() {
            return mDataset;
        }

        public void update() {
//            mDataset.clear();
//            mDataset = myDataset;
            notifyDataSetChanged();
        }


        private void deleteItem(String name) {
            for (String p:mDataset) {
                if (p.equals(name)) {
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
                    .inflate(R.layout.item_view_edit_member, parent, false);
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
//            holder.mTextView.setText(pat.patName);
            holder.mItemName.setText(mDataset.get(position));
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
