package com.ionesmile.cipherbox.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ionesmile.cipherbox.ui.adapter.base.render.ItemClick;
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemRender;
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemSelect;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by iOnesmile on 2016/11/10 0010.
 */
public class SimpleRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<T> mData;
    private IViewHolderCallback viewHolderCallback;
    protected Set<Integer> mSelectedSet;
    private ItemClick.ItemClickListener mItemClickListener;

    /**
     * Constructor
     * @param callback
     * @param data
     */
    public SimpleRecyclerAdapter(IViewHolderCallback callback, List<T> data) {
        this.viewHolderCallback = callback;
        this.mData = data;
        mSelectedSet = new HashSet<>();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public T getItem(int position){
        return mData.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        viewHolderCallback.onRecyclerBindViewHolder(holder, position);
        T itemData = mData.get(position);
        SparseArray<Object> sparseArray = new SparseArray<>(2);
        sparseArray.put(1, position);
        // 渲染内容
        if (holder.itemView instanceof ItemRender){
            ItemRender itemRender = ((ItemRender<T>)holder.itemView);
            itemRender.renderItem(position, itemData);
            sparseArray.put(2, itemData);
        }
        // 渲染选中
        if (holder.itemView instanceof ItemSelect){
            ItemSelect itemSelect = (ItemSelect) holder.itemView;
            if (isSelected(position, itemData)){
                itemSelect.onSelected(position);
            } else {
                itemSelect.onUnselected(position);
            }
        }
        // 设置监听
        if (mItemClickListener != null && holder.itemView instanceof ItemClick) {
            ((ItemClick) holder.itemView).setItemClick(mItemClickListener, position);
        }
        holder.itemView.setTag(sparseArray);
    }

    protected boolean isSelected(int position, T data) {
        return mSelectedSet.contains(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = viewHolderCallback.getRecyclerItemView();
        contentView.setOnClickListener(this);
        return new RecyclerView.ViewHolder(contentView) {};
    }

    public void notifyCancelSelected(){
        Iterator<Integer> iterator = mSelectedSet.iterator();
        while (iterator.hasNext()){
            int lastPosition = iterator.next();
            notifyItemChanged(lastPosition);
        }
        mSelectedSet.clear();
    }

    public void setSingleSelected(int position){
        Iterator<Integer> iterator = mSelectedSet.iterator();
        if (iterator.hasNext()){
            int lastPosition = iterator.next();
            mSelectedSet.clear();
            notifyItemChanged(lastPosition);
        }
        mSelectedSet.add(position);
        notifyItemChanged(position);
    }

    public void setMultiSelected(int position){
        mSelectedSet.add(position);
        notifyItemChanged(position);
    }

    public void notifyDataChanged(List<T> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    public List<T> getData(){
        return mData;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            SparseArray<Object> sparseArray = (SparseArray<Object>) view.getTag();
            int position = (int) sparseArray.get(1);
            Object data = sparseArray.get(2);
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onRecyclerViewItemClick(position, view, data);
        }
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onRecyclerViewItemClick(int position, View view, Object data);
    }

    public SimpleRecyclerAdapter setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
        return this;
    }

    /**
     * 设置 Item 中的点击事件
     * @param itemClickListener
     * @return
     */
    public SimpleRecyclerAdapter<T> setItemClickListener(ItemClick.ItemClickListener itemClickListener){
        mItemClickListener = itemClickListener;
        return this;
    }

    public interface IViewHolderCallback {
        /**
         *  返回一个View
         * @return  view extend ItemRender
         */
        View getRecyclerItemView();

        void onRecyclerBindViewHolder(RecyclerView.ViewHolder holder, final int position);
    }

    public static abstract class ViewHolderCallback implements IViewHolderCallback {

        @Override
        public void onRecyclerBindViewHolder(RecyclerView.ViewHolder holder, final int position){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.itemView.setLayoutParams(params);
        }
    }
}
