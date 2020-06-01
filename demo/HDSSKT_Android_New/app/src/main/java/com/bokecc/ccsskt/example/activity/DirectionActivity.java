package com.bokecc.ccsskt.example.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bokecc.ccsskt.example.CCApplication;
import com.bokecc.ccsskt.example.R;
import com.bokecc.ccsskt.example.base.TitleActivity;
import com.bokecc.ccsskt.example.base.TitleOptions;
import com.bokecc.ccsskt.example.entity.RoomDes;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCCityListSet;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;

import butterknife.OnClick;

/**
 * 横竖屏选择界面
 * @modify wy
 */
public class DirectionActivity extends TitleActivity<DirectionActivity.DirectionViewHolder> {

    private static final String KEY_ROOM_ROLE = "direction_key_room_role";
    private static final String KEY_ROOM_USERID = "direction_key_room_userid";
    private static final String KEY_ROOM_ROOMID = "direction_key_room_roomid";
    private static final String KEY_ROOM_DESC = "direction_key_room_des";

    private static Intent newIntent(Context context, int role, String userid, String roomid, RoomDes roomDes) {
        Intent intent = new Intent(context, DirectionActivity.class);
        intent.putExtra(KEY_ROOM_ROLE, role);
        intent.putExtra(KEY_ROOM_USERID, userid);
        intent.putExtra(KEY_ROOM_ROOMID, roomid);
        intent.putExtra(KEY_ROOM_DESC, roomDes);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void startSelf(Context context, int role, String userid, String roomid, RoomDes roomDes) {
        context.startActivity(newIntent(context, role, userid, roomid, roomDes));
    }

    private int mRole;
    private String mUserId, mRoomId;
    private RoomDes mRoomDes;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_direction;
    }

    @Override
    protected DirectionViewHolder getViewHolder(View contentView) {
        return new DirectionViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(DirectionViewHolder holder) {
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("课堂模式").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mRole = getIntent().getExtras().getInt(KEY_ROOM_ROLE);
        mUserId = getIntent().getExtras().getString(KEY_ROOM_USERID);
        mRoomId = getIntent().getExtras().getString(KEY_ROOM_ROOMID);
        mRoomDes = (RoomDes) getIntent().getExtras().getSerializable(KEY_ROOM_DESC);
        initDispatch();
    }

    /**
     * 初始化城市列表
     */
    private void initDispatch(){
        CCAtlasClient.getInstance().dispatch(mUserId, new CCAtlasCallBack<CCCityListSet>() {
            @Override
            public void onSuccess(final CCCityListSet ccCityBean) {
                CCApplication.mFisrtCityName = ccCityBean.getloc();
                CCApplication.mAreaCode = ccCityBean.getareacode();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {

            }
        });
    }

    final class DirectionViewHolder extends TitleActivity.ViewHolder {

        DirectionViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.id_direction_class_h)
        void classHorizontal() {
            CCApplication.sClassDirection = 1;
            goValidateActivity();
        }

        @OnClick(R.id.id_direction_class_v)
        void classVertical() {
            CCApplication.sClassDirection = 0;
            goValidateActivity();
        }

        private void goValidateActivity() {
            if(mRole == CCAtlasClient.PRESENTER){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole, false);
            } else if(mRole == CCAtlasClient.ASSISTANT){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole, false);
            } else if(mRole == CCAtlasClient.TALKER){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole, mRoomDes.getTalkerAuthtype() == 2);
            } else if(mRole == CCAtlasClient.INSPECTOR){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole,  mRoomDes.getInspectorAuthtype() == 2);
            } else if(mRole == CCAtlasClient.AUDITOR){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole, mRoomDes.getAudienceAuthtype() == 2);
            }

        }
    }

}
