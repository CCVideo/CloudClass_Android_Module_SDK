package com.bokecc.cloudclass.demo.activity;

import android.view.View;
import android.widget.TextView;

import com.bokecc.cloudclass.demo.R;
import com.bokecc.cloudclass.demo.base.TitleActivity;
import com.bokecc.cloudclass.demo.base.TitleOptions;

/**
 * 使用指南界面
 * Created by wdh on 2018/1/15.
 */
public class OperationActivity extends TitleActivity<OperationActivity.OperationViewHolder> {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_operation;
    }

    @Override
    protected OperationViewHolder getViewHolder(View contentView) {
        return new OperationViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(OperationViewHolder holder) {
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("使用指南").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);
    }
    final class OperationViewHolder extends TitleActivity.ViewHolder {

        TextView mfirstStep;
        OperationViewHolder(View view) {
            super(view);
            mfirstStep = view.findViewById(R.id.first_step);
        }
    }
}
