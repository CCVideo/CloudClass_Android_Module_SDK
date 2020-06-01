package com.bokecc.room.ui.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.listener.BaseOnItemTouch;
import com.bokecc.room.ui.listener.OnClickListener;
import com.bokecc.room.ui.view.adapter.DocAdapter;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;
import com.bokecc.room.ui.view.widget.RecycleViewDivider;
import com.bokecc.ccdocview.model.DocInfo;
import com.bokecc.ccdocview.model.RoomDocs;

import java.util.ArrayList;


public class DocListActivity extends TitleActivity<DocListActivity.DocListViewHolder> {

    private DocAdapter mDocAdapter;
    private static final String TAG = "DocListActivity";

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_doc_list_ui;
    }

    @Override
    protected void beforeSetContentView() {
        if (CCRoomActivity.sClassDirection == 1) {
            //取消标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected DocListViewHolder getViewHolder(View contentView) {
        return new DocListViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final DocListViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.mipmap.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("提取文档").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mDocAdapter = new DocAdapter(this);
        holder.mEmptyLayout.setVisibility(View.VISIBLE);
        holder.mDocs.setVisibility(View.GONE);
        holder.mDocs.setLayoutManager(new LinearLayoutManager(this));
        holder.mDocs.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, 1, Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BOTTOM));
        holder.mDocs.setAdapter(mDocAdapter);
        fetchRoomDocs();
        holder.mDocs.addOnItemTouchListener(new BaseOnItemTouch(holder.mDocs, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                int position = holder.mDocs.getChildAdapterPosition(viewHolder.itemView);
                selectDoc(position);
            }
        }));
        mDocAdapter.setOnDelOnClickListener(new DocAdapter.OnDelOnClickListener() {
            @Override
            public void onDel(int position, DocInfo docInfo) {
                deleteDoc(position, docInfo);
            }
        });

    }

    /**
     * 选中指定位置的文档
     */
    private void selectDoc(int position) {
        DocInfo docInfo = mDocAdapter.getDatas().get(position);
        Intent data = new Intent();
        data.putExtra("selected_doc", docInfo);
        setResult(Config.DOC_LIST_RESULT_CODE, data);
        finish();
    }

    /**
     * 获取直播间文档列表
     */
    private void fetchRoomDocs() {
        showProgress();
        CCDocViewManager.getInstance().getRoomDocs(null, new CCDocViewManager.AtlasCallBack<RoomDocs>() {
            @Override
            public void onSuccess(final RoomDocs roomDocs) {
                dismissProgress();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (roomDocs.getDoneDocs().size() > 0) {
                            if(mViewHolder != null){
                                mViewHolder.mDocs.setVisibility(View.VISIBLE);
                                mViewHolder.mEmptyLayout.setVisibility(View.GONE);
                                mDocAdapter.bindDatas(roomDocs.getDoneDocs());
                                mDocAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if(mViewHolder != null){
                                mViewHolder.mDocs.setVisibility(View.GONE);
                                mViewHolder.mEmptyLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(String err) {
                Tools.log(TAG, "fetch onFailure: " + err);
                dismissProgress();
                Tools.showToast(err);
            }
        });
    }

    /**
     * 删除指定文档
     */
    private void deleteDoc(final int positon, final DocInfo docInfo) {
        showLoading();
        CCDocViewManager.getInstance().delDoc(docInfo.getRoomId(), docInfo.getDocId(), new CCDocViewManager
                .AtlasCallBack<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismissLoading();
//                mEventBus.post(new MyEBEvent(Config.DOC_DEL, docInfo.getDocId()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDocAdapter.getDatas().remove(positon);
                        mDocAdapter.notifyItemRemoved(positon);
                        if (positon != mDocAdapter.getDatas().size()) {
                            mDocAdapter.notifyItemRangeChanged(positon, mDocAdapter.getDatas().size() - positon);
                        }
                        if (mDocAdapter.getDatas().size() <= 0) {
                            mViewHolder.mDocs.setVisibility(View.GONE);
                            mViewHolder.mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String err) {
                Log.e("tag", "onFailure: " + err);
                dismissLoading();
                Tools.showToast(err);
            }
        });
    }

    final class DocListViewHolder extends TitleActivity.ViewHolder {

        private RecyclerView mDocs;
        private RelativeLayout mEmptyLayout;

         DocListViewHolder(View view) {
            super(view);
             mDocs = view.findViewById(R.id.id_doc_list);
             mEmptyLayout = view.findViewById(R.id.id_doc_list_empty);
             view.findViewById(R.id.id_doc_board).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent data = new Intent();
                     DocInfo docInfo = new DocInfo();
                     docInfo.setDocMode(0);
                     docInfo.setUseSDK(false);
                     docInfo.setPosition(1);
                     docInfo.setPageTotalNum(1);
                     docInfo.setDocId("WhiteBorad");
                     docInfo.setName("WhiteBorad");
                     ArrayList<String> allImgUrls = new ArrayList<>();
                     docInfo.setAllImgUrls(allImgUrls);
                     data.putExtra("selected_doc_board", docInfo);
                     setResult(Config.DOC_BOARD_RESULT_CODE, data);
                     finish();
                 }
             });
        }

    }

}
