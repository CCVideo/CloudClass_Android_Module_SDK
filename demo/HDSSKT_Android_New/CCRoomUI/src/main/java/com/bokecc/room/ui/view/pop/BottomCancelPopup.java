package com.bokecc.room.ui.view.pop;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;


import com.bokecc.ccdocview.DensityUtil;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.listener.BaseOnItemTouch;
import com.bokecc.room.ui.listener.OnClickListener;
import com.bokecc.room.ui.utils.PopupAnimUtil;
import com.bokecc.room.ui.view.adapter.StringAdapter;
import com.bokecc.room.ui.view.widget.RecycleViewDivider;

import java.util.ArrayList;

/**
 * 作者 ${CC视频}.<br/>
 */

public class BottomCancelPopup extends BasePopupWindow {

    private TextView mTip;
    private TextView mCancel;
    private RecyclerView mChooseType;

    private OnCancelClickListener mOnCancelClickListener;
    private OnChooseClickListener mOnChooseClickListener;
    private StringAdapter mAdapter;
    private int mPosition = -1;

    public BottomCancelPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewCreated() {
        mTip = findViewById(R.id.id_choose_tip);
        mCancel = findViewById(R.id.id_choose_cancel);
        mChooseType = findViewById(R.id.id_choose_type);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnCancelClickListener != null) {
                    mOnCancelClickListener.onCancel();
                }
            }
        });
        mChooseType.setLayoutManager(new LinearLayoutManager(mContext));
        mChooseType.addItemDecoration(new RecycleViewDivider(mContext,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(mContext, 1), Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BETWEEN));
        mAdapter = new StringAdapter(mContext);
        mChooseType.addOnItemTouchListener(new BaseOnItemTouch(mChooseType,
                new OnClickListener() {
                    @Override
                    public void onClick(RecyclerView.ViewHolder viewHolder) {
                        dismiss();
                        mPosition = mChooseType.getChildAdapterPosition(viewHolder.itemView);
                    }
                }));
        setOnPopupDismissListener(new OnPopupDismissListener() {
            @Override
            public void onDismiss() {
                if (mOnChooseClickListener != null && mPosition != -1) {
                    mOnChooseClickListener.onClick(mPosition);
                    mPosition = -1;
                }
            }
        });
        mChooseType.setAdapter(mAdapter);
    }

    @Override
    protected int getContentView() {
        return R.layout.bottom_cancel_layout_ui;
    }

    @Override
    protected Animation getEnterAnimation() {
        return PopupAnimUtil.getDefTranslateEnterAnim();
    }

    @Override
    protected Animation getExitAnimation() {
        return PopupAnimUtil.getDefTranslateExitAnim();
    }

    public void setChooseDatas(ArrayList<String> chooseDatas) {
        mAdapter.bindDatas(chooseDatas);
    }

    public void removeChoose(int index) {
        mAdapter.remove(index);
    }

    public void add(int index, String value) {
        mAdapter.add(index, value);
    }

    public void clear() {
        mAdapter.clear();
    }

    public void setIndexColor(int index, int color) {
        mAdapter.setIndexColor(index, color);
    }

    public void update(int index, String value) {
        mAdapter.update(index, value);
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        mOnCancelClickListener = onCancelClickListener;
    }

    public void setOnChooseClickListener(OnChooseClickListener onChooseClickListener) {
        mOnChooseClickListener = onChooseClickListener;
    }

    public void setTip(String tip) {
        if (TextUtils.isEmpty(tip)) {
            return;
        }
        mTip.setVisibility(View.VISIBLE);
        mTip.setText(tip);
    }

    public interface OnChooseClickListener {
        void onClick(int index);
    }

    public interface OnCancelClickListener {
        void onCancel();
    }

}
