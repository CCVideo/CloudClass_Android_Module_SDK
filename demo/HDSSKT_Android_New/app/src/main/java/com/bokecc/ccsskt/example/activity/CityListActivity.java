package com.bokecc.ccsskt.example.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.adapter.CityAdapter;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.global.Config;
import com.bokecc.ccsskt.example.recycle.BaseOnItemTouch;
import com.bokecc.ccsskt.example.recycle.OnClickListener;
import com.bokecc.ccsskt.example.recycle.RecycleViewDivider;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCCityInteractBean;
import com.bokecc.sskt.base.bean.CCCityListSet;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;

import butterknife.BindView;

/**
 * 节点列表选择界面
 * @author cc
 */
public class CityListActivity extends TitleActivity<CityListActivity.ListViewHolder> {

    private CityAdapter mListAdapter;
    private String mRoomId;
    private String mUserId;

    @Override
    protected int getContentLayoutId() {
        return R.layout.list_layout;
    }

    @Override
    protected ListViewHolder getViewHolder(View contentView) {
        return new ListViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final ListViewHolder holder) {
        mRoomId = getIntent().getStringExtra("mRoomid");
        mUserId = getIntent().getStringExtra("mUserid");

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("线路切换").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mListAdapter = new CityAdapter(this);
        holder.mList.setLayoutManager(new LinearLayoutManager(this));
        holder.mList.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, 1, Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BOTTOM));
        holder.mList.setAdapter(mListAdapter);
        holder.mList.addOnItemTouchListener(new BaseOnItemTouch(holder.mList, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                int position = holder.mList.getChildAdapterPosition(viewHolder.itemView);
                selectCity(position);
            }
        }));

        getCityListData();
    }

    private void selectCity(int position) {
        CCCityInteractBean mCityInteractBean = mListAdapter.getDatas().get(position);
        mListAdapter.setSelPosition(position);
        Intent data = new Intent();
        data.putExtra("selected_city", mCityInteractBean);
        setResult(Config.CITYNAME, data);
        finish();
    }

    /**
     * 获取城市列表
     */
    private void getCityListData() {
        showProgress();
        CCAtlasClient.getInstance().dispatch(mUserId, new CCAtlasCallBack<CCCityListSet>() {
            @Override
            public void onSuccess(final CCCityListSet ccCityBean) {
                dismissProgress();

                if(ccCityBean != null){
                    mListAdapter.bindDatas(ccCityBean.getLiveListSet());
                    for (int i = 0; i < ccCityBean.getLiveListSet().size(); i++) {
                        if (ccCityBean.getLiveListSet().get(i).getdataareacode().equals(CCApplication.mAreaCode)) {
                            mListAdapter.setSelPosition(i);
                        }
                    }
                    mListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                dismissProgress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    final class ListViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_live_list) RecyclerView mList;

        public ListViewHolder(View view) {
            super(view);
        }

    }

}
