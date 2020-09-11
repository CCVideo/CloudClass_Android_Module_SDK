package com.bokecc.cloudclass.demo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.cloudclass.demo.R;
import com.bokecc.cloudclass.demo.recycle.BaseRecycleAdapter;
import com.bokecc.cloudclass.demo.recycle.SelectAdapter;
import com.bokecc.sskt.base.bean.CCCityInteractBean;

/**
 * 作者 ${cc}.<br/>
 */

public class CityAdapter extends SelectAdapter<CityAdapter.DocViewHolder, CCCityInteractBean> {

    public CityAdapter(Context context) {
        super(context);
    }
    @Override
    public void onBindViewHolder(DocViewHolder holder, int position) {
        final CCCityInteractBean list = mDatas.get(position);
        holder.mName.setText(list.getdataloc());
//        holder.mDelay.setText(list.getPingTime());
        if(mSelPosition == position){
            holder.mIcon.setVisibility(View.VISIBLE);
            holder.mIcon.setImageResource(R.drawable.choose_icon);
        } else {
            holder.mName.setText(list.getdataloc());
            holder.mIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.city_item_layout;
    }

    @Override
    public DocViewHolder getViewHolder(View itemView, int viewType) {
        return new DocViewHolder(itemView);
    }

    final class DocViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        TextView mName;
        TextView mDelay;
        ImageView mIcon;

        DocViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.id_city_item_name);
            mDelay = itemView.findViewById(R.id.id_city_item_delay);
            mIcon = itemView.findViewById(R.id.id_city_choose_icon);
        }
    }

}
