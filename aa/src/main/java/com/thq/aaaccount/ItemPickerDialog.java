package com.thq.aaaccount;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thq.aaaccount.widget.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianhongqi on 17-6-3.
 */

public class ItemPickerDialog {

    static String TAG = "MultiAlertDialog";


    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridManager;
    private List<Item> myDataset;
    private MyAdapter mAdapter;
    View mView;

    static ItemPickerDialog mItemPickerDialog;


//    private List<String> mItemNames;
//    private Map<String, Integer> mItems;

    public static ItemPickerDialog getIntance() {
        if (mItemPickerDialog == null) {
            mItemPickerDialog = new ItemPickerDialog(MyApplication.getMyApplicationContext());
        }
        return mItemPickerDialog;
    }

    Context mContext;
    private ItemPickerDialog(Context context) {
        mContext = context;

//        initName();

        myDataset = new ArrayList<>();
        for (int i = 0; i < ITEM_NAME.length; i++) {
            myDataset.add(new Item(ITEM_NAME[i], ITEM_ICON_NORMAL_ID[i], ITEM_ICON_SELECTED_ID[i]));
        }
        initItemPicker();
    }

   /* private void initName() {

        mItemNames = new ArrayList<>();
        mItemNames.add("zidy");
        mItemNames.add("cany");
        mItemNames.add("chongw");
        mItemNames.add("chux");
        mItemNames.add("cunk");
        mItemNames.add("dap");
        mItemNames.add("diany");
        mItemNames.add("fangc");
        mItemNames.add("gongz");
        mItemNames.add("gouw");
        mItemNames.add("gup");
        mItemNames.add("jij");
        mItemNames.add("other2");
        mItemNames.add("other3");
        mItemNames.add("other4");
        mItemNames.add("qit");
        mItemNames.add("xuex");
        mItemNames.add("yanj");
        mItemNames.add("yao");
        mItemNames.add("yil");
        mItemNames.add("yingeyp");
        mItemNames.add("youx");
        mItemNames.add("yul");
        mItemNames.add("yund");
        mItemNames.add("zuf");
        mItemNames.add("biyt");
        mItemNames.add("maj");
    }*/

/*    private static String[] ITEM_NAME = {
            "biyt",
            "cany",
            "chongw",
            "chux",
            "cunk",
            "dap",
            "diany",
            "fangc",
            "gongz",
            "gouw",
            "gup",
            "jij",
            "maj",
            "other2",
            "other3",
            "other4",
            "qit",
            "xuex",
            "yanj",
            "yao",
            "yil",
            "yingeyp",
            "youx",
            "yul",
            "yund",
            "zidy",
            "zuf"
    };*/

    private static String[] ITEM_NAME = {
            "餐饮",
            "交通",
            "电影",
            "酒店",
            "工资",
            "购物",
            "宠物",
            "存款",
            "打牌",
            "股票",
            "基金",
            "麻将",
            "KTV",
            "话费",
            "修理",
            "学习",
            "烟酒",
            "药",
            "医疗",
            "婴儿",
            "游戏",
            "娱乐",
            "运动",
            "租房",
            "避孕套",
            "其他",
            "自定义",
    };

    private static int[] ITEM_ICON_SELECTED_ID = {
            R.drawable.cany_select,
            R.drawable.chux_select,
            R.drawable.diany_select,
            R.drawable.fangc_select,
            R.drawable.gongz_select,
            R.drawable.gouw_select,
            R.drawable.chongw_select,
            R.drawable.cunk_select,
            R.drawable.dap_select,
            R.drawable.gup_select,
            R.drawable.jij_select,
            R.drawable.maj_select,
            R.drawable.other2_select,
            R.drawable.other3_select,
            R.drawable.other4_select,
            R.drawable.xuex_select,
            R.drawable.yanj_select,
            R.drawable.yao_select,
            R.drawable.yil_select,
            R.drawable.yingeyp_select,
            R.drawable.youx_select,
            R.drawable.yul_select,
            R.drawable.yund_select,
            R.drawable.zuf_select,
            R.drawable.biyt_select,
            R.drawable.qit_select,
            R.drawable.zidy_select,
    };

    private static int[] ITEM_ICON_NORMAL_ID = {
            R.drawable.cany,
            R.drawable.chux,
            R.drawable.diany,
            R.drawable.fangc,
            R.drawable.gongz,
            R.drawable.gouw,
            R.drawable.chongw,
            R.drawable.cunk,
            R.drawable.dap,
            R.drawable.gup,
            R.drawable.jij,
            R.drawable.maj,
            R.drawable.other2,
            R.drawable.other3,
            R.drawable.other4,
            R.drawable.xuex,
            R.drawable.yanj,
            R.drawable.yao,
            R.drawable.yil,
            R.drawable.yingeyp,
            R.drawable.youx,
            R.drawable.yul,
            R.drawable.yund,
            R.drawable.zuf,
            R.drawable.biyt,
            R.drawable.qit,
            R.drawable.zidy,
    };

    public Item getItem(int position) {
        return myDataset.get(position);
    }

    public Item getItem(String key) {
        for (Item item:myDataset) {
            if (key.contains(item.mItemName)) return item;
        }
        return null;
    }

    private void initItemPicker() {
//        mView = mContext.getLayoutInflater().inflate(R.layout.dialog_recycler_view, null);
        mView = ((LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_recycler_view, null);
        //RecyclerView
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mGridManager = new GridLayoutManager(mContext, 4);
        mRecyclerView.setLayoutManager(mGridManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MyAdapter(mContext, myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            mAdapter.update(position);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mListener.done(mAdapter.mLastSelectedIndes);
                    ((FrameLayout)mView.getParent()).removeView(mView);
                    // 关闭提示框
                    alertDialog4.dismiss();
                }
            }, 300l);
        }
    };

    Handler mHandler = new Handler();


    interface CallbackResultListener {
        void done(int position);
    }

    CallbackResultListener mListener;

    // 单选提示框
    private AlertDialog alertDialog4;
    public void showItemAlertDialog(Activity context, final CallbackResultListener listener){

        mListener = listener;

        if (mView.getParent() != null) ((FrameLayout)mView.getParent()).removeView(mView);

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setView(mView);
 /*       alertBuilder.setTitle("请选择消费类型");
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 业务逻辑代码

                listener.done(mAdapter.mLastSelectedIndes);
                ((FrameLayout)mView.getParent()).removeView(mView);
                // 关闭提示框
                alertDialog4.dismiss();
            }
        });
        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 业务逻辑代码

                ((FrameLayout)mView.getParent()).removeView(mView);
                // 关闭提示框
                alertDialog4.dismiss();
            }
        });*/
        alertDialog4 = alertBuilder.create();
        alertDialog4.show();
    }


    class Item {
        public String mItemName;
        public int mIconNormalId;
        public int mIconSelectedId;
        boolean isChecked = false;

        public Item(String mItemName, int mIconNormalId, int mIconSelectedId) {
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
        private List<Item> mDataset;
        private final TypedValue mTypedValue = new TypedValue();
        private int mLastSelectedIndes = 0;

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<Item> myDataset) {
            mDataset = myDataset;
            myDataset.get(0).isChecked = true;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public List<Item> getItems() {
            return mDataset;
        }

        public void update(int position) {
            if (position == mLastSelectedIndes) return;
            myDataset.get(position).isChecked = true;
            myDataset.get(mLastSelectedIndes).isChecked = false;
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
            Item pat = mDataset.get(position);
//            holder.mTextView.setText(pat.patName);
            holder.mItemNameView.setText(pat.mItemName);
            if (pat.isChecked) {
                holder.mIconView.setImageResource(pat.mIconSelectedId);
                holder.mItemNameView.setTextColor(Color.WHITE);
                holder.mLayout.setBackgroundColor(mContext.getResources().getColor(R.color.primary));
            } else {
                holder.mIconView.setImageResource(pat.mIconNormalId);
                holder.mItemNameView.setTextColor(mContext.getResources().getColor(R.color.primary));
                holder.mLayout.setBackgroundColor(Color.WHITE);
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
