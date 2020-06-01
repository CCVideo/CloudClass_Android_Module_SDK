package com.bokecc.room.ui.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bokecc.room.ui.R;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;
import com.bokecc.room.ui.view.widget.ClearEditLayout;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCInteractBean;


public class LoopTimeActivity extends TitleActivity<LoopTimeActivity.LoopTimeViewHolder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_max_lianmai_ui;
    }

    @Override
    protected LoopTimeViewHolder getViewHolder(View contentView) {
        return new LoopTimeViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(final LoopTimeViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.mipmap.title_back).
                rightStatus(TitleOptions.VISIBLE).rightValue("保存").
                titleStatus(TitleOptions.VISIBLE).title("轮播频率").
                onTitleClickListener(new TitleOptions.OnTitleClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }

                    @Override
                    public void onRight() {
                    }
                }).
                build();
        setTitleOptions(options);
        setRightEnabled(false);
        holder.mInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        holder.mInput.showSoftboard();
        holder.mInput.setOnEditTextChangedListener(new ClearEditLayout.OnEditTextChangedListener() {
            @Override
            public void onChanged(Editable s) {
                if (s.length() > 0) {
                    setRightEnabled(true);
                } else {
                    setRightEnabled(false);
                }
            }
        });
        CCInteractBean interactBean = CCAtlasClient.getInstance().getInteractBean();
        holder.mInput.setText(String.valueOf(interactBean.getVunionCount() > 16 ? 16 :
                interactBean.getRoomMaxMaiCount()));

    }

    final class LoopTimeViewHolder extends TitleActivity.ViewHolder {

        private ClearEditLayout mInput;

        LoopTimeViewHolder(View view) {
            super(view);
            mInput = view.findViewById(R.id.id_class_name_value);
        }

    }
}
