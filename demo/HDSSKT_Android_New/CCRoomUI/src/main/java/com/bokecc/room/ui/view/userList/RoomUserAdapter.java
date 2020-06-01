package com.bokecc.room.ui.view.userList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.view.menu.MenuTopView;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCUser;
import com.bumptech.glide.Glide;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.RoomUser;
import com.bokecc.common.utils.DensityUtil;
import com.bokecc.room.ui.view.adapter.BaseRecycleAdapter;

/**
 * 作者 ${王德惠}.<br/>
 */
public class RoomUserAdapter extends BaseRecycleAdapter<RoomUserAdapter.RoomUserViewHolder, RoomUser> {

    private  MenuTopView.MenuTopListener mListener;
    private int mLianmaiMode;
    private int mType;

    public RoomUserAdapter(Context context, int type, @CCAtlasClient.LianmaiMode int lianmaiMode, MenuTopView.MenuTopListener mListener) {
        super(context);
        mType = type;
        mLianmaiMode = lianmaiMode;
        this.mListener = mListener;
    }

    @Override
    public void onBindViewHolder(RoomUserViewHolder holder, final int position) {
        RoomUser roomUser = mDatas.get(position);
        final CCUser user = roomUser.getUser();

        if(user.getSendCup()){
            holder.mReward.setVisibility(View.VISIBLE);
            holder.mCup.setVisibility(View.VISIBLE);
            holder.mRewardSize.setVisibility(View.VISIBLE);
            holder.mRewardSize.setText("x" + user.getCupIndex());
        }

        if (user.getSendFlower()) {
            holder.mReward.setVisibility(View.VISIBLE);
            holder.mFlower.setVisibility(View.VISIBLE);
            holder.mRewardSize.setVisibility(View.VISIBLE);
            holder.mRewardSize.setText("x" + user.getFlowerIndex());
        }

        if(!user.getSendFlower()&& !user.getSendCup()){
            holder.mReward.setVisibility(View.GONE);
            holder.mFlower.setVisibility(View.GONE);
            holder.mRewardSize.setVisibility(View.GONE);
        }

        if (mType == CCAtlasClient.PRESENTER && user.getUserRole() != CCAtlasClient.PRESENTER) {
            holder.mArrow.setVisibility(View.VISIBLE);
        } else if(mType == CCAtlasClient.ASSISTANT && user.getUserRole() != CCAtlasClient.ASSISTANT) {
            if(user.getUserRole() == CCAtlasClient.PRESENTER){
                holder.mArrow.setVisibility(View.GONE);
            } else {
                holder.mArrow.setVisibility(View.VISIBLE);
            }
        } else {
            if (mType == CCAtlasClient.TALKER) {
                holder.mArrow.setVisibility(View.GONE);
            } else {
                holder.mArrow.setVisibility(View.INVISIBLE);
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mLianmai.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.rightMargin = DensityUtil.dp2px(mContext, 10);
            holder.mLianmai.setLayoutParams(params);
        }
        if (user.getPlatForm() == CCAtlasClient.MOBILE) {
            holder.mDevice.setImageResource(R.mipmap.user_phone);
        } else {
            holder.mDevice.setImageResource(R.mipmap.user_computer);
        }
        if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_MAI_ING) {
            holder.mLianmai.setVisibility(View.VISIBLE);
            holder.mStatusWait.setVisibility(View.GONE);
            holder.mStatusing.setVisibility(View.VISIBLE);
        } else if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IN_MAI) {
            holder.mLianmai.setVisibility(View.VISIBLE);
            holder.mStatusing.setVisibility(View.GONE);
            holder.mStatusWait.setVisibility(View.VISIBLE);
            int resId;
            String value;
            Drawable drawable;
            if (mLianmaiMode == CCAtlasClient.LIANMAI_MODE_FREE) {
                resId = R.mipmap.user_wait_icon;
                value = "第" + roomUser.getMaiIndex() + "位,排麦中...";
            } else {
                resId = R.mipmap.user_hand_icon;
                value = "第" + roomUser.getMaiIndex() + "位,举手中...";
            }
            drawable = mContext.getResources().getDrawable(resId);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mStatusWait.setCompoundDrawables(drawable, null, null, null);
            holder.mStatusWait.setText(value);
        } else if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IDLE) {
            holder.mLianmai.setVisibility(View.GONE);
            holder.mStatusWait.setVisibility(View.GONE);
            holder.mStatusing.setVisibility(View.GONE);
        } else if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_INVITE_MAI) {
            holder.mLianmai.setVisibility(View.VISIBLE);
            holder.mStatusing.setVisibility(View.GONE);
            holder.mStatusWait.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.user_invite_icon);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.mStatusWait.setCompoundDrawablePadding(DensityUtil.dp2px(mContext, 3));
            holder.mStatusWait.setCompoundDrawables(drawable, null, null, null);
            holder.mStatusWait.setText("邀请连麦中...");
        }
        holder.mUserName.setText(user.getUserName());
        if (user.getUserRole() == CCAtlasClient.PRESENTER) {
            holder.mIdentity.setVisibility(View.VISIBLE);
            holder.mGag.setVisibility(View.GONE);
            holder.mLianmai.setVisibility(View.VISIBLE);
        } else if(user.getUserRole() == CCAtlasClient.ASSISTANT) {
            holder.mIdentity.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.mipmap.assistant_ident).into(holder.mIdentity);
            holder.mLianmai.setVisibility(View.VISIBLE);
            if (!user.getUserSetting().isAllowChat()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mGag.getLayoutParams();
                if (holder.mHand.getVisibility() == View.GONE) {
                    if (holder.mLianmai.getVisibility() == View.GONE) {
                        if (holder.mArrow.getVisibility() == View.GONE) {
                            params.addRule(RelativeLayout.ALIGN_PARENT_END);
                        } else {
                            params.addRule(RelativeLayout.LEFT_OF, holder.mArrow.getId());
                        }
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, holder.mHand.getId());
                    }
                } else {
                    params.addRule(RelativeLayout.LEFT_OF, holder.mLianmai.getId());
                }
                params.rightMargin = DensityUtil.dp2px(mContext, 10);
                holder.mGag.setLayoutParams(params);
                holder.mGag.setVisibility(View.VISIBLE);
            } else {
                holder.mGag.setVisibility(View.GONE);
            }
        } else {
            holder.mIdentity.setVisibility(View.GONE);
            if (user.getUserSetting().isHandUp()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mHand.getLayoutParams();
                if (holder.mLianmai.getVisibility() == View.GONE) {
                    if (holder.mArrow.getVisibility() == View.GONE) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, holder.mArrow.getId());
                    }
                } else {
                    params.addRule(RelativeLayout.LEFT_OF, holder.mLianmai.getId());
                }
                holder.mHand.setLayoutParams(params);
                holder.mHand.setVisibility(View.VISIBLE);
            } else {
                holder.mHand.setVisibility(View.GONE);
            }
            if (!user.getUserSetting().isAllowChat()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mGag.getLayoutParams();
                if (holder.mHand.getVisibility() == View.GONE) {
                    if (holder.mLianmai.getVisibility() == View.GONE) {
                        if (holder.mArrow.getVisibility() == View.GONE) {
                            params.addRule(RelativeLayout.ALIGN_PARENT_END);
                        } else {
                            params.addRule(RelativeLayout.LEFT_OF, holder.mArrow.getId());
                        }
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, holder.mHand.getId());
                    }
                } else {
                    params.addRule(RelativeLayout.LEFT_OF, holder.mLianmai.getId());
                }
                params.rightMargin = DensityUtil.dp2px(mContext, 10);
                holder.mGag.setLayoutParams(params);
                holder.mGag.setVisibility(View.VISIBLE);
            } else {
                holder.mGag.setVisibility(View.GONE);
            }
            if (user.getUserSetting().isAllowDraw()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mDraw.getLayoutParams();
                params.removeRule(RelativeLayout.ALIGN_PARENT_END);//移除旧的规则，否则新规则不生效
                params.removeRule(RelativeLayout.LEFT_OF);
                if (holder.mGag.getVisibility() == View.GONE) {
                    if (holder.mHand.getVisibility() == View.GONE) {
                        if (holder.mLianmai.getVisibility() == View.GONE) {
                            if (holder.mArrow.getVisibility() == View.GONE) {
                                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                            } else {
                                params.addRule(RelativeLayout.LEFT_OF, holder.mArrow.getId());
                            }
                        } else {
                            params.addRule(RelativeLayout.LEFT_OF, holder.mLianmai.getId());
                        }
                    } else {
                        params.addRule(RelativeLayout.LEFT_OF, holder.mHand.getId());
                    }
                } else {
                    params.addRule(RelativeLayout.LEFT_OF, holder.mGag.getId());
                }
                params.rightMargin = DensityUtil.dp2px(mContext, 10);
                holder.mDraw.setLayoutParams(params);
                holder.mDraw.setVisibility(View.VISIBLE);
            } else {
                holder.mDraw.setVisibility(View.GONE);
            }
        }
        if(mType==CCAtlasClient.PRESENTER){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == mDatas.size()){
                        return;
                    }
                    if (mListener!=null) {
                        mListener.onClickUser(user,position);
                    }

                }
            });
        }

    }

    @Override
    public int getItemView(int viewType) {
        return R.layout.view_user_item_layout;
    }

    @Override
    public RoomUserViewHolder getViewHolder(View itemView, int viewType) {
        return new RoomUserViewHolder(itemView);
    }

    final class RoomUserViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        private ImageView mDevice;
        private TextView mUserName;
        private ImageView mIdentity;
        private RelativeLayout mLianmai;
        private TextView mStatusWait;
        private ImageView mStatusing;
        private ImageView mGag;
        private ImageView mDraw;
        private ImageView mArrow;
        private ImageView mHand;
        private RelativeLayout mReward;
        private ImageView mFlower;
        private ImageView mCup;
        private TextView mRewardSize;
        private View itemView;

        RoomUserViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mDevice = itemView.findViewById(R.id.id_user_device_icon);
            mUserName = itemView.findViewById(R.id.id_user_name);
            mIdentity = itemView.findViewById(R.id.id_user_identity);
            mLianmai = itemView.findViewById(R.id.id_user_status_lianmai);
            mStatusWait = itemView.findViewById(R.id.id_user_status_wait);
            mStatusing = itemView.findViewById(R.id.id_user_status_ing);
            mGag = itemView.findViewById(R.id.id_user_status_gag);
            mDraw = itemView.findViewById(R.id.id_user_status_draw);
            mArrow = itemView.findViewById(R.id.id_user_arrow);
            mHand = itemView.findViewById(R.id.id_user_status_hand);
            mReward = itemView.findViewById(R.id.id_user_status_reward);
            mFlower = itemView.findViewById(R.id.id_user_status_flower);
            mCup = itemView.findViewById(R.id.id_user_status_cup);
            mRewardSize = itemView.findViewById(R.id.id_reward_item_size);
        }
    }

}
