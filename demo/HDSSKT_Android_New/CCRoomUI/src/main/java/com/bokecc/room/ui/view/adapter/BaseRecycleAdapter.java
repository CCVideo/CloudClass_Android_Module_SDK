package com.bokecc.room.ui.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bokecc.common.utils.Tools;
import com.bokecc.sskt.base.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 继承自Recycler.Adapter
 * 包括两个泛型：VH:BaseViewHolder和T:数据对象
 * 作者 ${CC视频}.<br/>
 *
 */

public abstract class BaseRecycleAdapter<VH extends BaseRecycleAdapter.BaseViewHolder, T> extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected List<T> mDatas = new ArrayList<>();;

    public BaseRecycleAdapter(Context context) {
        mContext = context;
    }

    /**
     * 绑定当前数据
     * @param datas
     */
    public void bindDatas(List<T> datas) {
        mDatas = datas;
    }

    /**
     * 获取当前所有数据
     * @return
     */
    public List<T> getDatas() {
        return mDatas;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(getItemView(viewType), parent, false);
        return getViewHolder(itemView, viewType);
    }

    /**
     * 获取视图id
     * @param viewType
     * @return
     */
    public abstract int getItemView(int viewType);

    /**
     * 获取holder
     * @param itemView 跟视图
     * @param viewType 视图类型
     * @return
     */
    public abstract VH getViewHolder(View itemView, int viewType);


    @Override
    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public int getItemCount() {
//        LogUtil.e("Adapter","getItemCount:"+(mDatas == null ? 0 : mDatas.size()));
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * 移除一条数据
     * @param index
     */
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

    /**
     * 添加多条数据
     * @param values
     */
    public void add(List<T> values) {
        mDatas.addAll(values);
        notifyDataSetChanged();
    }

    /**
     * 添加一条数据
     * @param index
     * @param value
     */
    public void add(int index, T value) {
        mDatas.add(index, value);
        notifyItemInserted(index);
        if (index != mDatas.size()) {
            notifyItemRangeChanged(index, mDatas.size() - index);
        }
    }

    /**
     * 刷新一条数据
     * @param index
     * @param value
     */
    public void update(int index, T value) {
        update(index, value, null);
    }

    /**
     *
     * @param index
     * @param value
     * @param tag
     */
    public void update(int index, T value, Object tag) {
        mDatas.set(index, value);
        notifyItemChanged(index, tag);
    }

    /**
     * 清除数据
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }


    /**
     * 自定义Holder
     */
    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

}

