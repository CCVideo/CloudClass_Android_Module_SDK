package ccsskt.bokecc.base.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bokecc.sskt.base.CCAtlasCallBack;
import com.bokecc.sskt.base.bean.CCInteractBean;
import com.bokecc.sskt.base.bean.CCUser;

import butterknife.BindView;
import butterknife.OnClick;
import ccsskt.bokecc.base.example.base.TitleActivity;
import ccsskt.bokecc.base.example.base.TitleOptions;
/**
 * 作者 ${CC视频}.<br/>
 */

public class ValidateActivity extends TitleActivity<ValidateActivity.ValidateViewHolder> {

    private static final String KEY_ROOM_ID = "room_id";
    private static final String KEY_USER_ACCOUNT = "user_account";
    private static final String KEY_USER_ROLE = "user_role";

    private static Intent newIntent(Context context, String roomId, String userAccount, String mRole) {
        Intent intent = new Intent(context, ValidateActivity.class);
        intent.putExtra(KEY_ROOM_ID, roomId);
        intent.putExtra(KEY_USER_ACCOUNT, userAccount);
        intent.putExtra(KEY_USER_ROLE, mRole);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static void startSelf(Context context, String roomId, String userAccount, String mRole) {
        context.startActivity(newIntent(context, roomId, userAccount, mRole));
    }

    public static final String TAG = ValidateActivity.class.getSimpleName();

    private String mRoomId;
    private String mUserAccount;
    private String mRole;
    private InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_validate;
    }

    @Override
    protected ValidateViewHolder getViewHolder(View contentView) {
        return new ValidateViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(ValidateViewHolder holder) {

        final TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).
                title("验证").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mRoomId = getIntent().getStringExtra(KEY_ROOM_ID);
        mUserAccount = getIntent().getStringExtra(KEY_USER_ACCOUNT);
        mRole = getIntent().getStringExtra(KEY_USER_ROLE);
//        mAtlasClient.changeServerDomain(Config.ServerUrl,"CHN",new CCAtlasCallBack<String>(){
//
//            @Override
//            public void onSuccess(String s) {
//                Log.i(TAG, "onBindViewHolder: " + s);
//                ServerUrl = s;
//            }
//
//            @Override
//            public void onFailure(int errCode, String errMsg) {
//
//            }
//        });
        showSoftboard();
    }

    private Runnable mShowSoftboardRunnable = new Runnable() {
        @Override
        public void run() {
            imm.showSoftInput(mViewHolder.mNickname, 0);
        }
    };

    public void showSoftboard() {
        mHandler.postDelayed(mShowSoftboardRunnable, 150);
    }

    final class ValidateViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_validate_nickname)
        EditText mNickname;
        @BindView(R.id.id_validate_passwd)
        EditText mPassword;

        ValidateViewHolder(View view) {
            super(view);
        }

        private String getEditTextValue(EditText editText) {
            return editText.getText().toString().trim();
        }

        @OnClick(R.id.id_validate_room)
        void goRoom() {
            showProgress();
            String nickname = getEditTextValue(mNickname);
            String password = getEditTextValue(mPassword);
            ccAtlasClient.login(mRoomId, mUserAccount, Integer.parseInt(mRole.equals("talker") ? "1" : "0"), nickname,
                    password, new CCAtlasCallBack<String>() {
                        @Override
                        public void onSuccess(final String Seesionid) {
                            try {
                                dismissProgress();
                                ccAtlasClient.join(Seesionid, mUserAccount, null, new CCAtlasCallBack<CCInteractBean>() {
                                    @Override
                                    public void onSuccess(CCInteractBean ccBaseBean) {
                                        dismissProgress();
                                        showToast("join room success");
                                        MainActivity.startSelf(ValidateActivity.this, Seesionid, mRoomId, mUserAccount);
                                    }

                                    @Override
                                    public void onFailure(int errCode, String errMsg) {
                                        dismissProgress();
                                        showToast(errMsg);
                                    }
                                });

                            } catch (Exception e) {
                                showToast(e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            dismissProgress();
                            showToast(errMsg);
                        }
                    });
        }

        @OnClick(R.id.id_validate_sort_mic)
        void goSortMicrophone() {
            showProgress();
            String nickname = getEditTextValue(mNickname);
            String password = getEditTextValue(mPassword);
            ccAtlasClient.login(mRoomId, mUserAccount, Integer.parseInt(mRole.equals("talker") ? "1" : "0"), nickname,
                    password, new CCAtlasCallBack<String>() {
                        @Override
                        public void onSuccess(String Seesionid) {
                            try {
                                dismissProgress();
                                SortMicrophoneActivity.startSelf(ValidateActivity.this, Seesionid, mRoomId, mUserAccount);
                            } catch (Exception e) {
                                showToast(e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(int errCode, String errMsg) {
                            dismissProgress();
                            showToast(errMsg);
                        }
                    });
        }
    }

}
