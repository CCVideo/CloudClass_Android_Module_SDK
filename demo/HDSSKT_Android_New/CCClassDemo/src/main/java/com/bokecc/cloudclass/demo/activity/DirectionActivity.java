package com.bokecc.cloudclass.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bokecc.cloudclass.demo.CCClassApplication;
import com.bokecc.cloudclass.demo.R;
import com.bokecc.cloudclass.demo.base.TitleActivity;
import com.bokecc.cloudclass.demo.base.TitleOptions;
import com.bokecc.cloudclass.demo.entity.RoomDes;
import com.bokecc.common.utils.Tools;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCCityListSet;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;

/**
 * 横竖屏选择界面
 * @modify wy
 */
public class DirectionActivity extends TitleActivity<DirectionActivity.DirectionViewHolder> {

    private static final String KEY_ROOM_ROLE = "direction_key_room_role";
    private static final String KEY_ROOM_USERID = "direction_key_room_userid";
    private static final String KEY_ROOM_ROOMID = "direction_key_room_roomid";
    private static final String KEY_ROOM_DESC = "direction_key_room_des";
    private static final String KEY_ROOM_TEMPLATE = "direction_key_room_template";


    private static Intent newIntent(Context context, int role, String userid, String roomid, RoomDes roomDes, String template) {
        Intent intent = new Intent(context, DirectionActivity.class);
        intent.putExtra(KEY_ROOM_ROLE, role);
        intent.putExtra(KEY_ROOM_USERID, userid);
        intent.putExtra(KEY_ROOM_ROOMID, roomid);
        intent.putExtra(KEY_ROOM_DESC, roomDes);
        if(!TextUtils.isEmpty(template))
        intent.putExtra(KEY_ROOM_TEMPLATE, template);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void startSelf(Context context, int role, String userid, String roomid, RoomDes roomDes) {
        context.startActivity(newIntent(context, role, userid, roomid, roomDes,null));
    }
    public static void startSelf(Context context, int role, String userid, String roomid, RoomDes roomDes,String template) {
        context.startActivity(newIntent(context, role, userid, roomid, roomDes,template));
    }

    private int mRole;
    private String mUserId, mRoomId;
    private RoomDes mRoomDes;
    private String mTemplate;

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

        Bundle bundle = getIntent().getExtras();
        mRole = bundle.getInt(KEY_ROOM_ROLE);
        mUserId = bundle.getString(KEY_ROOM_USERID);
        mRoomId = bundle.getString(KEY_ROOM_ROOMID);
        mRoomDes = (RoomDes) bundle.getSerializable(KEY_ROOM_DESC);
        mTemplate = bundle.getString(KEY_ROOM_TEMPLATE);

        if(!TextUtils.isEmpty(mTemplate)){
            findViewById(R.id.id_direction_class_v).setVisibility(View.GONE);
        }
        initDispatch();
    }

    /**
     * 初始化城市列表
     */
    private void initDispatch(){
        CCAtlasClient.getInstance().dispatch(mUserId, new CCAtlasCallBack<CCCityListSet>() {
            @Override
            public void onSuccess(final CCCityListSet ccCityBean) {
                CCClassApplication.mFisrtCityName = ccCityBean.getloc();
                CCClassApplication.mAreaCode = ccCityBean.getareacode();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {}
        });
    }

    final class DirectionViewHolder extends TitleActivity.ViewHolder {

        DirectionViewHolder(View view) {
            super(view);

            findViewById(R.id.id_direction_class_h).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    classHorizontal();
                }
            });

            findViewById(R.id.id_direction_class_v).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    classVertical();
                }
            });
        }

        void classHorizontal() {
            CCClassApplication.sClassDirection = 1;
            goValidateActivity();
        }

        void classVertical() {
            CCClassApplication.sClassDirection = 0;
            goValidateActivity();
        }

        private void goValidateActivity() {
            if(mRole == CCAtlasClient.PRESENTER){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole, false,mTemplate);
            } else if(mRole == CCAtlasClient.ASSISTANT){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole, false,mTemplate);
            } else if(mRole == CCAtlasClient.TALKER){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes, mRoomId, mUserId, mRole, mRoomDes.getTalkerAuthtype() == 2,mTemplate);
            } else if(mRole == CCAtlasClient.INSPECTOR){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole,  mRoomDes.getInspectorAuthtype() == 2,mTemplate);
            } else if(mRole == CCAtlasClient.AUDITOR){
                ValidateActivity.startSelf(DirectionActivity.this, mRoomDes.getName(), mRoomDes
                        .getDesc(), mRoomId, mUserId, mRole, mRoomDes.getAudienceAuthtype() == 2,mTemplate);
            }

        }
    }

}
