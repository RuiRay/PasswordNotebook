package com.ionesmile.cipherbox.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ionesmile.cipherbox.R;
import com.ionesmile.cipherbox.manager.CommonManager;
import com.ionesmile.cipherbox.manager.PreferenceManager;
import com.ionesmile.cipherbox.model.table.CipherTable;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iOnesmile on 2017/8/18 0006.
 */
public class CipherSwipeAdapter extends SwipeMenuAdapter<CipherSwipeAdapter.ViewHolder> {

    private Context context;
    protected List<CipherTable> mDatas = new ArrayList<>();
    private LayoutInflater mInflater;

    public CipherSwipeAdapter(Context context, List<CipherTable> data) {
        this.context = context;
        this.mDatas = data;
        mInflater = LayoutInflater.from(context);
    }

    public void notifyDataChanged(List<CipherTable> data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }

    public List<CipherTable> getData() {
        return mDatas;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        View contentView = mInflater.inflate(R.layout.item_cipher, parent, false);
        return contentView;
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        ViewHolder viewHolder = new ViewHolder(realContentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CipherTable itemData = mDatas.get(position);
        holder.renderItem(position, itemData);
        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position, itemData);
                }
            });
        }
        if (mLongListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongListener.onItemLongClick(position, itemData);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (mDatas == null ? 0 : mDatas.size());
    }

    private OnItemClickListener mListener;

    public CipherSwipeAdapter setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
        return this;
    }

    public interface OnItemClickListener<T> {
        /**
         * RecyclerView 的 Item 的点击事件
         *
         * @param position
         * @param data
         */
        void onItemClick(int position, T data);
    }

    private OnItemLongClickListener mLongListener;

    public CipherSwipeAdapter setOnItemLongClickListener(OnItemLongClickListener li) {
        mLongListener = li;
        return this;
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(int position, CipherTable data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ImageView iconIv;
        TextView nameTv;
        TextView timeTv;
        TextView descTv;

        public ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        public void init(View rootView) {
            iconIv = (ImageView) rootView.findViewById(R.id.iv_icon);
            nameTv = (TextView) rootView.findViewById(R.id.tv_name);
            timeTv = (TextView) rootView.findViewById(R.id.tv_time);
            descTv = (TextView) rootView.findViewById(R.id.tv_describe);
        }

        public void renderItem(int position, CipherTable data) {
            CommonManager.setLogoImage(iconIv, data.getIcon());
            nameTv.setText(data.getName());
            descTv.setText(getDescText(data));
            timeTv.setText(getShowText(data));
        }

        private String getShowText(CipherTable data) {
            int showType = PreferenceManager.getListShowOptions(timeTv.getContext());
            switch (showType){
                case 1:
                    return simpleDateFormat.format(data.getCreateTime());
                case 2:
                    return simpleDateFormat.format(data.getUpdateTime());
                case 3:
                    return data.getType().getTitle();
                default:
                    return null;
            }
        }

        private String getDescText(CipherTable data) {
            if (!TextUtils.isEmpty(data.getUrl())) {
                return data.getUrl();
            }
            return data.getRemark();
        }
    }
}
