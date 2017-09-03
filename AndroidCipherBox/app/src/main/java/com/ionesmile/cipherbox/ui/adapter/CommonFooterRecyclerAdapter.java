package com.ionesmile.cipherbox.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ionesmile.cipherbox.ui.adapter.base.render.ItemClick;
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemExpand;
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemExpandControl;
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemRender;
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemSelect;
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemSetExpandControl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by iOnesmile on 2016/12/6 0006.
 */
public class CommonFooterRecyclerAdapter<T> extends BaseRecyclerAdapter<T> implements ItemExpandControl {

    private SimpleRecyclerAdapter.IViewHolderCallback viewHolderCallback;
    protected Set<Integer> mSelectedSet;
    protected Set<Integer> mExpandSet;
    private ItemClick.ItemClickListener mItemClickListener;
    private View emptyContentView;

    /**
     * Constructor
     * @param callback
     * @param data
     */
    public CommonFooterRecyclerAdapter(SimpleRecyclerAdapter.IViewHolderCallback callback, List<T> data) {
        this.viewHolderCallback = callback;
        mDatas = data;
        mSelectedSet = new HashSet<>();
        mExpandSet = new HashSet<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View contentView = viewHolderCallback.getRecyclerItemView();
        return new RecyclerView.ViewHolder(contentView) {};
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, T data) {
        viewHolderCallback.onRecyclerBindViewHolder(viewHolder, realPosition);
        T itemData = mDatas.get(realPosition);
        // 是否可以控制选中，逻辑限制在第一个判断
        if (viewHolder.itemView instanceof ItemSetExpandControl) {
            ((ItemSetExpandControl) viewHolder.itemView).setSelectControl(this);
        }
        // 渲染内容
        if (viewHolder.itemView instanceof ItemRender){
            ItemRender itemRender = ((ItemRender<T>)viewHolder.itemView);
            itemRender.renderItem(realPosition, itemData);
        }
        // 渲染选中
        if (viewHolder.itemView instanceof ItemSelect){
            ItemSelect itemSelect = (ItemSelect) viewHolder.itemView;
            if (isSelected(realPosition, itemData)){
                itemSelect.onSelected(realPosition);
            } else {
                itemSelect.onUnselected(realPosition);
            }
        }
        // 渲染扩展
        if (viewHolder.itemView instanceof ItemExpand){
            ItemExpand itemExpand = (ItemExpand) viewHolder.itemView;
            if (isExpanded(realPosition, itemData)){
                itemExpand.onExpand(realPosition);
            } else {
                itemExpand.onUnexpand(realPosition);
            }
        }
        // 设置监听
        if (mItemClickListener != null && viewHolder.itemView instanceof ItemClick) {
            ((ItemClick) viewHolder.itemView).setItemClick(mItemClickListener, realPosition);
        }
    }

    @Override
    public CommonFooterRecyclerAdapter setOnItemClickListener(OnItemClickListener li) {
        super.setOnItemClickListener(li);
        return this;
    }

    /**
     * 设置 Item 中的点击事件
     * @param itemClickListener
     * @return
     */
    public CommonFooterRecyclerAdapter setItemClickListener(ItemClick.ItemClickListener itemClickListener){
        mItemClickListener = itemClickListener;
        return this;
    }

    protected boolean isSelected(int position, T data) {
        return mSelectedSet.contains(position);
    }

    protected boolean isExpanded(int position, T data) {
        return mExpandSet.contains(position);
    }

    public void notifyCancelSelected(){
        mSelectedSet.clear();
        notifyDataSetChanged();
    }

    public void setSingleSelected(int position){
        mSelectedSet.clear();
        mSelectedSet.add(position);
        notifyDataSetChanged();
    }

    public void setMultiSelected(int position){
        mSelectedSet.add(position);
        notifyDataSetChanged();
    }

    public void notifyDataChanged(List<T> data){
        this.mDatas = data;
        notifyDataSetChanged();
        refreshEmptyContentView();
    }

    public List<T> getData(){
        return mDatas;
    }

    @Override
    public void cancelItemExpand(int position) {
        mExpandSet.clear();
        notifyDataSetChanged();
    }

    @Override
    public void setItemExpanded(int position) {
        mExpandSet.clear();
        mExpandSet.add(position);
        notifyDataSetChanged();
    }

    @Override
    public boolean isItemExpanded(int position) {
        return mExpandSet.contains(position);
    }

    public void setEmptyContentView(View view) {
        this.emptyContentView = view;
        refreshEmptyContentView();
    }

    private void refreshEmptyContentView() {
        if (emptyContentView == null) {
            return;
        }
        if (mDatas == null || mDatas.size() == 0){
            if (emptyContentView.getVisibility() != View.VISIBLE){
                emptyContentView.setVisibility(View.VISIBLE);
            }
        } else {
            if (emptyContentView.getVisibility() == View.VISIBLE){
                emptyContentView.setVisibility(View.GONE);
            }
        }
    }
}
