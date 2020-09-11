package com.bokecc.cloudclass.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bokecc.cloudclass.demo.CCClassApplication;
import com.bokecc.cloudclass.demo.R;
import com.bokecc.cloudclass.demo.base.TitleActivity;
import com.bokecc.cloudclass.demo.base.TitleOptions;
import com.bokecc.cloudclass.demo.entity.RoomDes;
import com.bokecc.cloudclass.demo.global.Config;
import com.bokecc.cloudclass.demo.popup.TxtLoadingPopup;
import com.bokecc.cloudclass.demo.util.SPUtil;
import com.bokecc.cloudclass.demo.view.EllipsizeEndTextView;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.drag.view.activity.StudentRoomSaas1v1Activity;
import com.bokecc.room.drag.view.activity.StudentRoomSaasActivity;
import com.bokecc.room.drag.view.base.CCRoomActivity;
import com.bokecc.room.ui.manager.InteractSessionManager;
import com.bokecc.room.ui.view.activity.InspectorRoomActivity;
import com.bokecc.room.ui.view.activity.StudentRoomActivity;
import com.bokecc.room.ui.view.activity.TeacherRoomActivity;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCCityInteractBean;
import com.bokecc.sskt.base.bean.CCInteractBean;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.bokecc.sskt.base.CCAtlasClient.INSPECTOR;
import static com.bokecc.sskt.base.CCAtlasClient.TALKER;

/**
 * 登录界面
 */
public class ValidateActivity extends TitleActivity<ValidateActivity.ValidateViewHolder> {

    private static final String KEY_ROOM_NAME = "room_name";
    private static final String KEY_ROOM_DESC = "room_desc";
    private static final String KEY_ROOM_ID = "room_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_LAYOUT_MODE = "layout_mode";
    private static final String KEY_ROOM_TEMPLATE = "template";
    private static final String KEY_STUDENT_LOGIN_NO_PWD = "student_login_no_pwd";
    private static final String ROOM_TYPE_SAAS = "32";
    private String mNickName;
    private String mPwd = "";
    private EventBus mEventBus;
    private String mTemplate;
    private int mLayoutMode;

    private static Intent newIntent(Context context, String roomName, String roomDesc, String roomId, String userId, int role, boolean isNoPwd,String template) {
        Intent intent = new Intent(context, ValidateActivity.class);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        intent.putExtra(KEY_ROOM_DESC, roomDesc);
        intent.putExtra(KEY_ROOM_ID, roomId);
        intent.putExtra(KEY_USER_ID, userId);
        if(!TextUtils.isEmpty(template))
        intent.putExtra(KEY_ROOM_TEMPLATE, template);
        intent.putExtra(KEY_ROLE, role);
        intent.putExtra(KEY_STUDENT_LOGIN_NO_PWD, isNoPwd);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private static Intent newIntent(Context context, RoomDes roomDes, String roomId, String userId, int role, boolean isNoPwd, String template) {
        Intent intent = new Intent(context, ValidateActivity.class);
        intent.putExtra(KEY_ROOM_NAME, roomDes.getName());
        intent.putExtra(KEY_ROOM_DESC, roomDes.getDesc());
        intent.putExtra(KEY_ROOM_ID, roomId);
        intent.putExtra(KEY_USER_ID, userId);
        intent.putExtra(KEY_LAYOUT_MODE, roomDes.getLayoutMode());
        if(!TextUtils.isEmpty(template))
            intent.putExtra(KEY_ROOM_TEMPLATE, template);
        intent.putExtra(KEY_ROLE, role);
        intent.putExtra(KEY_STUDENT_LOGIN_NO_PWD, isNoPwd);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void startSelf(Context context, String roomName, String roomDesc, String roomId, String userId, int role, boolean isNoPwd) {
        context.startActivity(newIntent(context, roomName, roomDesc, roomId, userId, role, isNoPwd,null));
    }
    public static void startSelf(Context context, String roomName, String roomDesc, String roomId, String userId, int role, boolean isNoPwd,String template) {
        context.startActivity(newIntent(context, roomName, roomDesc, roomId, userId, role, isNoPwd,template));
    }
    public static void startSelf(Context context, RoomDes roomDes, String roomId, String userId, int role, boolean isNoPwd,String template) {
        context.startActivity(newIntent(context, roomDes, roomId, userId, role, isNoPwd,template));
    }
    public static final String TAG = ValidateActivity.class.getSimpleName();

    private String mRoomId;
    private String mUserId;
    private int mRole;
    private boolean isNoPwd;

    private TxtLoadingPopup mLoading;
    private InputMethodManager imm;

    public volatile boolean isJump = false;
    protected SPUtil mSPUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSPUtil = SPUtil.getIntsance();
        InteractSessionManager mInteractSessionManager = InteractSessionManager.getInstance();
        mInteractSessionManager.addInteractListeners();
        mEventBus = EventBus.getDefault();
        mInteractSessionManager.setEventBus(mEventBus);
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(Boolean event) {}

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_validate;
    }

    @Override
    protected ValidateViewHolder getViewHolder(View contentView) {
        return new ValidateViewHolder(contentView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Config.CITYNAME) {
            CCCityInteractBean mCityName = (CCCityInteractBean) data.getSerializableExtra("selected_city");
            CCClassApplication.mAreaCode = mCityName.getdataareacode();
            CCClassApplication.mFisrtCityName = mCityName.getdataloc();
            final TitleOptions.Builder builder = new TitleOptions.Builder();
            final Intent intent = new Intent(this, CityListActivity.class);
            TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                    leftResId(R.drawable.title_back).
                    rightResId(R.drawable.layer_list).
                    rightStatus(TitleOptions.VISIBLE).
                    title("验证").
                    cityStatus(TitleOptions.VISIBLE).city(CCClassApplication.mFisrtCityName).
                    onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                        @Override
                        public void onLeft() {
                            finish();
                        }
                        @Override
                        public void onRight() {
                            intent.putExtra("mRoomid", getIntent().getStringExtra(KEY_ROOM_ID));
                            intent.putExtra("mUserid", getIntent().getStringExtra(KEY_USER_ID));
                            startActivityForResult(intent,Config.CITYSTATUS_RESULT_CODE);
                        }
                    }).build();
            setTitleOptions(options);
        }
    }

    @Override
    protected void onBindViewHolder(ValidateViewHolder holder) {
        final TitleOptions.Builder builder = new TitleOptions.Builder();
        final Intent intent = new Intent(this, CityListActivity.class);
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightResId(R.drawable.layer_list).
                rightStatus(TitleOptions.VISIBLE).
                title("验证").
                cityStatus(TitleOptions.VISIBLE).city(CCClassApplication.mFisrtCityName).
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                    @Override
                    public void onRight() {
                        intent.putExtra("mRoomid", getIntent().getStringExtra(KEY_ROOM_ID));
                        intent.putExtra("mUserid", getIntent().getStringExtra(KEY_USER_ID));
                        startActivityForResult(intent,Config.CITYSTATUS_RESULT_CODE);
                    }
                }).build();
        setTitleOptions(options);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        String roomName = getIntent().getStringExtra(KEY_ROOM_NAME);
        String roomDesc = getIntent().getStringExtra(KEY_ROOM_DESC);
        isNoPwd = getIntent().getBooleanExtra(KEY_STUDENT_LOGIN_NO_PWD, false);
        mRoomId = getIntent().getStringExtra(KEY_ROOM_ID);
        mUserId = getIntent().getStringExtra(KEY_USER_ID);
        mRole = getIntent().getIntExtra(KEY_ROLE, mRole);
        mLayoutMode = getIntent().getIntExtra(KEY_LAYOUT_MODE,0);
        mTemplate = getIntent().getStringExtra(KEY_ROOM_TEMPLATE);
        mNickName = mSPUtil.getString(mRoomId + "&" + mUserId + "&" + mRole + "-name");
        holder.mRoomName.setText(Html.fromHtml(roomName));
        if (!TextUtils.isEmpty(roomDesc)) {
            holder.mRoomDesc.setMaxLines(2);
            holder.mRoomDesc.setText(Html.fromHtml(roomDesc));
        } else {
            holder.mRoomDesc.setVisibility(View.GONE);
        }
        holder.mNickname.setText(mNickName);
        holder.mNickname.setSelection(mNickName.length());
        if (isNoPwd) {
            holder.mPwdLayout.setVisibility(View.GONE);
            holder.mPwdBottomLine.setVisibility(View.GONE);
        } else {
            mPwd = mSPUtil.getString(mRoomId + "&" + mUserId + "&" + mRole + "-password");
            holder.mPwdLayout.setVisibility(View.VISIBLE);
            holder.mPwdBottomLine.setVisibility(View.VISIBLE);
            holder.mPassword.setText(mPwd);
            holder.mPassword.setSelection(mPwd.length());
        }
        initLoadingPopup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    private void saveUserMsg() {
        mSPUtil.put(mRoomId + "&" + mUserId + "&" + mRole + "-name", mNickName);
        if (!isNoPwd)
            mSPUtil.put(mRoomId + "&" + mUserId + "&" + mRole + "-password", mPwd);
    }

    private void initLoadingPopup() {
        mLoading = new TxtLoadingPopup(this);
        mLoading.setTipValue("正在登录");
        mLoading.setOutsideCancel(false);
        mLoading.setKeyBackCancel(false);
    }

    private void dismissLoadingOnUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoading.dismiss();
            }
        });
    }

    final class ValidateViewHolder extends TitleActivity.ViewHolder {

        ImageView mIcon;
        TextView mRoomName;
        EllipsizeEndTextView mRoomDesc;
        EditText mNickname;
        LinearLayout mPwdLayout;
        View mPwdBottomLine;
        EditText mPassword;

        ValidateViewHolder(View view) {
            super(view);

            mIcon = findViewById(R.id.id_validate_room_icon);
            mRoomName = findViewById(R.id.id_validate_room_name);
            mRoomDesc = findViewById(R.id.id_validate_room_desc);
            mNickname = findViewById(R.id.id_validate_nickname);
            mPwdLayout = findViewById(R.id.id_validate_passwd_layout);
            mPwdBottomLine = findViewById(R.id.id_validate_passwd_bottom_line);
            mPassword = findViewById(R.id.id_validate_passwd);

            findViewById(R.id.id_validate_room).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goRoom();
                }
            });
        }

        private String getEditTextValue(EditText editText) {
            return editText.getText().toString().trim();
        }

        void goRoom() {
            Tools.hideSoftInput(ValidateActivity.this);
            mNickName = getEditTextValue(mNickname);
            mPwd = getEditTextValue(mPassword);
            if (mRole == TALKER && isNoPwd ) {
                if (TextUtils.isEmpty(mNickName)) {
                    toastOnUiThread("请填写登录信息");
                    return;
                }
            } else if(mRole == INSPECTOR && isNoPwd ){
                if (TextUtils.isEmpty(mNickName)) {
                    toastOnUiThread("请填写登录信息");
                    return;
                }
            } else {
                if (TextUtils.isEmpty(mNickName) || TextUtils.isEmpty(mPwd)) {
                    toastOnUiThread("请填写登录信息");
                    return;
                }
            }
            mLoading.show(mRoot);
            isJump = false;
            CCAtlasClient.getInstance().login(mRoomId,
                    mUserId, mRole, mNickName, mPwd, new CCAtlasCallBack<String>() {
                        @Override
                        public void onSuccess(String sessionid) {
                            saveUserMsg();
                            join(sessionid,mUserId);
                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            dismissLoadingOnUiThread();
                            Tools.showToast(errMsg);
                        }
                    });
        }

    }

    /**
     *
     * @param mSeesionid
     * @param mUserAccount
     */
    private void join(String mSeesionid,String mUserAccount) {
        //saas界面的跳转
        dismissLoadingOnUiThread();
        if (!isJump&&TextUtils.equals(ROOM_TYPE_SAAS,mTemplate)) {
            isJump = true;
            if (mRole == TALKER) {
                if(mLayoutMode==1){
                    Intent intent = new Intent(ValidateActivity.this, StudentRoomSaas1v1Activity.class);
                    intent.putExtra(CCRoomActivity.ScreenDirectionKey, CCClassApplication.sClassDirection);
                    intent.putExtra(CCRoomActivity.SeesionidKey,mSeesionid);
                    intent.putExtra(CCRoomActivity.UserAccountKey,mUserAccount);
                    intent.putExtra(CCRoomActivity.AreaCodeKey, CCClassApplication.mAreaCode);
                    startActivity(intent);
                }else if(mLayoutMode==0){
                    Intent intent = new Intent(ValidateActivity.this, StudentRoomSaasActivity.class);
//                Intent intent = new Intent(ValidateActivity.this, StudentDemoActivity.class);
                    intent.putExtra(CCRoomActivity.ScreenDirectionKey, CCClassApplication.sClassDirection);
                    intent.putExtra(CCRoomActivity.SeesionidKey,mSeesionid);
                    intent.putExtra(CCRoomActivity.UserAccountKey,mUserAccount);
                    intent.putExtra(CCRoomActivity.AreaCodeKey, CCClassApplication.mAreaCode);
                    startActivity(intent);
                }else {
                    toastOnUiThread("此版本暂不支持该样式");
                }

            }else{
                toastOnUiThread("此版本不支持该角色");
            }
            return;
        }

        //原有界面的跳转处理
        CCAtlasClient.getInstance().join(mSeesionid, mUserAccount, CCClassApplication.mAreaCode, false, new
                CCAtlasCallBack<CCInteractBean>() {
            @Override
            public void onSuccess(CCInteractBean ccInteractBean) {
                synchronized (ValidateActivity.this) {
                    if (!isJump) {
                        isJump = true;
                        dismissLoadingOnUiThread();
                        if (mRole == CCAtlasClient.PRESENTER) {
                            Intent intent = new Intent(ValidateActivity.this, TeacherRoomActivity.class);
                            intent.putExtra(CCRoomActivity.ScreenDirectionKey, CCClassApplication.sClassDirection);
                            startActivity(intent);
                        } else if (mRole == TALKER) {
                            Intent intent = new Intent(ValidateActivity.this, StudentRoomActivity.class);
                            intent.putExtra(CCRoomActivity.ScreenDirectionKey, CCClassApplication.sClassDirection);
                            startActivity(intent);
                        } else if (mRole == INSPECTOR) {
                            Intent intent = new Intent(ValidateActivity.this, InspectorRoomActivity.class);
                            intent.putExtra(CCRoomActivity.ScreenDirectionKey, CCClassApplication.sClassDirection);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                dismissLoadingOnUiThread();
                toastOnUiThread(errMsg);
            }
        });
    }

}
