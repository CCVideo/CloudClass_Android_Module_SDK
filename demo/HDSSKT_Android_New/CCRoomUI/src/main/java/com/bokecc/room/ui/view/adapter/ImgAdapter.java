package com.bokecc.room.ui.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.view.adapter.ImgAdapter.DocImgViewHolder;


/**
 * 作者 ${CC视频}.<br/>
 */

public class ImgAdapter extends BaseRecycleAdapter<DocImgViewHolder, String> {

    public ImgAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final DocImgViewHolder holder, int position) {
        Glide.with(mContext).load(mDatas.get(position)).fitCenter().into(holder.mDocImg);
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.doc_img_layout;
    }

    @Override
    public DocImgViewHolder getViewHolder(View itemView, int viewType) {
        return new DocImgViewHolder(itemView);
    }

    final class DocImgViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        ImageView mDocImg;

        DocImgViewHolder(View itemView) {
            super(itemView);
            mDocImg = itemView.findViewById(R.id.id_doc_img);
        }
    }

}
