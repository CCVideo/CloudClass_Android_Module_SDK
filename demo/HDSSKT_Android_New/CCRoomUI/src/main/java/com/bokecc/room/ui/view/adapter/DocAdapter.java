package com.bokecc.room.ui.view.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.room.ui.R;
import com.bokecc.ccdocview.model.DocInfo;
import com.bumptech.glide.Glide;


/**
 * 作者 ${CC视频}.<br/>
 */

public class DocAdapter extends BaseRecycleAdapter<DocAdapter.DocViewHolder, DocInfo> {

    private OnDelOnClickListener mOnDelOnClickListener;

    public DocAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(DocViewHolder holder, int position) {
        final DocInfo docInfo = mDatas.get(position);
        holder.mName.setText(docInfo.getName());
        holder.mSize.setText(Formatter.formatFileSize(mContext, docInfo.getSize()));
        Glide.with(mContext).load(docInfo.getThumbnailsUrl()).fitCenter().into(holder.mIcon);
        holder.mDel.setOnClickListener(new DelOnClicklistener(position, docInfo));
    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.doc_item_layout;
    }

    @Override
    public DocViewHolder getViewHolder(View itemView, int viewType) {
        return new DocViewHolder(itemView);
    }

    public void setOnDelOnClickListener(OnDelOnClickListener onDelOnClickListener) {
        mOnDelOnClickListener = onDelOnClickListener;
    }

    public interface OnDelOnClickListener {
        void onDel(int position, DocInfo docInfo);
    }

    private class DelOnClicklistener implements View.OnClickListener {

        private int mPosition;
        private DocInfo mDocInfo;

        DelOnClicklistener(int position, DocInfo docInfo) {
            mPosition = position;
            mDocInfo = docInfo;
        }

        @Override
        public void onClick(View v) {
            if (mOnDelOnClickListener != null) {
                mOnDelOnClickListener.onDel(mPosition, mDocInfo);
            }
        }
    }

    final class DocViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        private ImageView mIcon;
        private TextView mName;
        private TextView mSize;
        private Button mDel;

        DocViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.id_doc_item_icon);
            mName = itemView.findViewById(R.id.id_doc_item_name);
            mSize = itemView.findViewById(R.id.id_doc_item_size);
            mDel = itemView.findViewById(R.id.id_doc_item_del);
        }
    }

}
