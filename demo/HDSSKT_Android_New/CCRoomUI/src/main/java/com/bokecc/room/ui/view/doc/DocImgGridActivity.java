package com.bokecc.room.ui.view.doc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.listener.BaseOnItemTouch;
import com.bokecc.room.ui.listener.OnClickListener;
import com.bokecc.room.ui.view.adapter.ImgAdapter;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;

import java.util.ArrayList;


public class DocImgGridActivity extends TitleActivity<DocImgGridActivity.DocImgGridViewHolder> {


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_doc_img_grid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected DocImgGridViewHolder getViewHolder(View contentView) {
        return new DocImgGridViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final DocImgGridViewHolder holder) {
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.mipmap.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("跳转页面").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        ArrayList<String> allImgUrls = getIntent().getExtras().getStringArrayList("doc_img_list");
        ImgAdapter imgAdapter = new ImgAdapter(this);
        holder.mImgGrid.setLayoutManager(new GridLayoutManager(this, 4));
        holder.mImgGrid.setAdapter(imgAdapter);
        imgAdapter.bindDatas(allImgUrls);
        holder.mImgGrid.addOnItemTouchListener(new BaseOnItemTouch(holder.mImgGrid, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                int position = holder.mImgGrid.getChildAdapterPosition(viewHolder.itemView);
                Intent data = new Intent();
                data.putExtra("doc_img_grid_position", position);
                setResult(Config.DOC_GRID_RESULT_CODE, data);
                finish();
            }
        }));

    }



    final class DocImgGridViewHolder extends TitleActivity.ViewHolder {

        RecyclerView mImgGrid;

        DocImgGridViewHolder(View view) {
            super(view);
            mImgGrid = view.findViewById(R.id.id_doc_imgs);
        }
    }

}
