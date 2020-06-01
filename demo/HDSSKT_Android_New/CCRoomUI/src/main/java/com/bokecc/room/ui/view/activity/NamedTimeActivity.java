package com.bokecc.room.ui.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.bokecc.ccdocview.DensityUtil;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.listener.BaseOnItemTouch;
import com.bokecc.room.ui.listener.OnClickListener;
import com.bokecc.room.ui.model.NamedTime;
import com.bokecc.room.ui.view.adapter.NamedTimeAdapter;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;
import com.bokecc.room.ui.view.widget.RecycleViewDivider;

import java.util.ArrayList;


public class NamedTimeActivity extends TitleActivity<NamedTimeActivity.NamedTimeViewHolder> {

    private NamedTimeAdapter mNamedTimeAdapter;
    private int mCurPosition;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_named_time_ui;
    }

    @Override
    protected NamedTimeViewHolder getViewHolder(View contentView) {
        return new NamedTimeViewHolder(contentView);
    }
    @Override
    protected void beforeSetContentView() {
        if (CCRoomActivity.sClassDirection == 1) {
            //取消标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    @Override
    protected void onBindViewHolder(final NamedTimeViewHolder holder) {
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.mipmap.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("点名").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        int curTime = getIntent().getExtras().getInt("current_continue_time");
        holder.mTimeList.setLayoutManager(new LinearLayoutManager(this));
        holder.mTimeList.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f), Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BETWEEN));
        mNamedTimeAdapter = new NamedTimeAdapter(this);
        ArrayList<NamedTime> times = new ArrayList<>();
        times.add(new NamedTime(10, 10 == curTime));
        times.add(new NamedTime(20, 20 == curTime));
        times.add(new NamedTime(30, 30 == curTime));
        times.add(new NamedTime(60, 60 == curTime));
        times.add(new NamedTime(120, 120 == curTime));
        times.add(new NamedTime(180, 180 == curTime));
        times.add(new NamedTime(300, 300 == curTime));
        mNamedTimeAdapter.bindDatas(times);
        holder.mTimeList.setAdapter(mNamedTimeAdapter);
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i).getSeconds() == curTime) {
                mCurPosition = i;
                break;
            }
        }
        holder.mTimeList.addOnItemTouchListener(new BaseOnItemTouch(holder.mTimeList, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                int position = holder.mTimeList.getChildAdapterPosition(viewHolder.itemView);
                if (mCurPosition == position) {
                    goBackWithData();
                    return;
                }
                mNamedTimeAdapter.getDatas().get(position).setSelected(true);
                mNamedTimeAdapter.getDatas().get(mCurPosition).setSelected(false);
                mNamedTimeAdapter.update(mCurPosition, mNamedTimeAdapter.getDatas().get(mCurPosition));
                mNamedTimeAdapter.update(position, mNamedTimeAdapter.getDatas().get(position));
                mCurPosition = position;
                goBackWithData();
            }
        }));
    }

    private void goBackWithData() {
        mTitleBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent data = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("time", mNamedTimeAdapter.getDatas().get(mCurPosition).getSeconds());
                data.putExtras(bundle);
                setResult(100, data);
                finish();
            }
        }, 100);
    }

    final class NamedTimeViewHolder extends TitleActivity.ViewHolder {

        private RecyclerView mTimeList;

        NamedTimeViewHolder(View view) {
            super(view);
            mTimeList = view.findViewById(R.id.id_named_time_list);
        }
    }


}
