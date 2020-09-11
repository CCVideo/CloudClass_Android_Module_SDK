package com.bokecc.cloudclass.demo.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者 ${CC视频}.<br/>
 */

public abstract class BaseRecycleAdapter<VH extends BaseRecycleAdapter.BaseViewHolder, T> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected List<T> mDatas;

    public BaseRecycleAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<>();
    }

    public void bindDatas(List<T> datas) {
        mDatas = datas;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).
                inflate(getItemView(viewType), parent, false);
        return getViewHolder(itemView, viewType);
    }

    @Override
    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public abstract int getItemView(int viewType);

    public abstract VH getViewHolder(View itemView, int viewType);

    public void remove(int index) {
        if (index < 0 || index >= mDatas.size()) {
            return;
        }
        mDatas.remove(index);
        notifyItemRemoved(index);
        if (index != mDatas.size()) {
            notifyItemRangeChanged(index, mDatas.size() - index);
        }
    }

    public void add(List<T> values) {
        mDatas.addAll(values);
        notifyDataSetChanged();
    }

    public void add(int index, T value) {
        mDatas.add(index, value);
        notifyItemInserted(index);
        if (index != mDatas.size()) {
            notifyItemRangeChanged(index, mDatas.size() - index);
        }
    }

    public void update(int index, T value, Object tag) {
        mDatas.set(index, value);
        notifyItemChanged(index, tag);
    }

    public void update(int index, T value) {
        update(index, value, null);
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

}

