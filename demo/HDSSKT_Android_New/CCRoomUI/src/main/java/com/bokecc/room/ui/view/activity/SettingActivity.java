package com.bokecc.room.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bokecc.ccdocview.SPUtil;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.config.Config;
import com.bokecc.room.ui.model.MyEBEvent;
import com.bokecc.room.ui.view.base.CCRoomActivity;
import com.bokecc.room.ui.view.base.TitleActivity;
import com.bokecc.room.ui.view.base.TitleOptions;
import com.bokecc.room.ui.view.pop.BottomCancelPopup;
import com.bokecc.room.ui.view.widget.ItemLayout;
import com.bokecc.room.ui.view.widget.ToggleButton;
import com.bokecc.sskt.base.CCAtlasClient;
import com.example.ccchatlibrary.CCChatCallBack;
import com.example.ccchatlibrary.CCChatManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import static com.bokecc.room.ui.config.Config.INTERACT_EVENT_WHAT_UPDATE_LIANMAI_MODE;
import static com.bokecc.room.ui.config.Config.INTERACT_EVENT_WHAT_UPDATE_MEDIA_MODE;
import static com.bokecc.sskt.base.CCAtlasClient.Bitrate_Level240;
import static com.bokecc.sskt.base.CCAtlasClient.Bitrate_Level480;
import static com.bokecc.sskt.base.CCAtlasClient.Bitrate_Level720;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_AUTO;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_FREE;
import static com.bokecc.sskt.base.CCAtlasClient.LIANMAI_MODE_NAMED;
import static com.bokecc.sskt.base.CCAtlasClient.MEDIA_MODE_AUDIO;

public class SettingActivity extends TitleActivity<SettingActivity.SettingViewHolder> {

    private static final String TAG = SettingActivity.class.getSimpleName();

   private String mMediaTypeBoth;
   private String mMediaTypeAudio;
   private String mLianmaiTypeFree;
   private String mLianmaiTypeNamed;
   private String mLianmaiTypeAuto;

   private String mBitrateTip;

   private String super_definition;
   private String high_definition;
   private String standard_definition;

   private String kickTip;

    public static final int LIANMAI_MODE = 10;
    public static final int LIANMAI_AUDIO_VIDEO = 11;
    private int mMediaMode;
    private int mLianmaiMode;
    private int mode_position = 0;
    private int mode_media_position = 0;
    private Map<Integer, String> mBitrateTips;
    private int mPickIndex = 0;

    private int mLoopTime = 10;
    public static final String STUDENT_RESOLUTION = "student_resolution";
    private boolean isTeacher = true;
    private ArrayList<String> mirrorModeList;
    private int mirrorMode;

    private CCChatCallBack<Void> mGagCallBack = new CCChatCallBack<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            dismissLoading();
        }

        @Override
        public void onFailure(String err) {
            dismissLoading();
            Tools.showToast(err);
            mViewHolder.mGag.setChecked(CCChatManager.getInstance().isRoomGag());
        }
    };

    private CCChatCallBack<Void> mAudioCallBack = new CCChatCallBack<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            dismissLoading();
        }

        @Override
        public void onFailure(String err) {
            dismissLoading();
            Tools.showToast(err);
            mViewHolder.mMic.setChecked(CCChatManager.getInstance().isRoomGag());
        }
    };
    private boolean isChangeResolution;
    private EventBus mEventBus;
    private OptionsPickerView mMirrorPickerView;
    private CCAtlasClient mCCAtlasClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_setting_ui;
    }

    @Override
    protected SettingViewHolder getViewHolder(View contentView) {

        return new SettingViewHolder(contentView);
    }

    @Override
    protected void beforeSetContentView() {
        if (CCRoomActivity.sClassDirection == 1) {
            //取消标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            //取消状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mEventBus = EventBus.getDefault();
        mMediaTypeBoth = getString(R.string.setting_media_both);
        mMediaTypeAudio =getString(R.string.setting_media_audio);
        mLianmaiTypeFree =getString(R.string.setting_lianmai_free);
         mLianmaiTypeNamed =getString(R.string.setting_lianmai_named);
        mLianmaiTypeAuto=getString(R.string.setting_lianmai_auto);

        mBitrateTip=getString(R.string.setting_bitrate);


        super_definition=getString(R.string.setting_bitrate_720p);
        high_definition=getString(R.string.setting_bitrate_480p);
        standard_definition=getString(R.string.setting_bitrate_240p);

        kickTip=getString(R.string.kick_down_mai_tip);

    }

    @Override
    protected void onBindViewHolder(final SettingViewHolder holder) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCCAtlasClient = CCAtlasClient.getInstance();
        isTeacher = CCAtlasClient.getInstance().getRole()== CCAtlasClient.PRESENTER;
        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.mipmap.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("设置").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        mBitrateTips = new HashMap<>();

        //设置分辨率
        int[] resolutionValues = mCCAtlasClient.getResolution();
        if(isTeacher){
            for(int i=0;i<resolutionValues.length;i++){
                int resolution = resolutionValues[i];
                if(resolution == CCAtlasClient.Resolution_720P){
                    mBitrateTips.put(Bitrate_Level720, super_definition);
                }else if(resolution == CCAtlasClient.Resolution_480P){
                    mBitrateTips.put(Bitrate_Level480, high_definition);
                }else if(resolution == CCAtlasClient.Resolution_240P){
                    mBitrateTips.put(Bitrate_Level240, standard_definition);
                }
            }
            //处理越界数据
            int relolution = SPUtil.getIntsance().getInt(TEACHER_RESOLUTION, mCCAtlasClient.getDefaultResolution());
            int index = 0;
            for(int i=0;i< resolutionValues.length;i++){
                if(relolution == resolutionValues[i]){
                    index = i;
                    break;
                }
            }
            //避免越界
            holder.mTBitrate.setValue(bitOptions.get(index));
            mMediaMode = mCCAtlasClient.getInteractBean().getMediaMode();
            mode_media_position = mMediaMode == MEDIA_MODE_AUDIO ? 0 : 1;
            mLianmaiMode = mCCAtlasClient.getInteractBean().getLianmaiMode();
            mode_position = mLianmaiMode == LIANMAI_MODE_FREE ? 0 :
                    mLianmaiMode == LIANMAI_MODE_NAMED ? 1 : 2;
            holder.mMediaModeSetting.setValue(mMediaMode == MEDIA_MODE_AUDIO ? mMediaTypeAudio :
                    mMediaTypeBoth);
            holder.mLianmaiSetting.setValue(mLianmaiMode == LIANMAI_MODE_FREE ? mLianmaiTypeFree :
                    mLianmaiMode == LIANMAI_MODE_AUTO ? mLianmaiTypeAuto : mLianmaiTypeNamed);
            final CCChatManager mCCChatManager = CCChatManager.getInstance();
            holder.mGag.setChecked(mCCChatManager.isRoomGag());
            holder.mGag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    showLoading();
                    if (isChecked) {
                        mCCChatManager.gagAll(mGagCallBack);
                    } else {
                        mCCChatManager.cancelGagAll(mGagCallBack);
                    }
                }
            });

            holder.mMic.setChecked(!mCCAtlasClient.getInteractBean().isAllAllowAudio());
            holder.mMic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    showLoading();
                    mCCChatManager.changeRoomAudioState(!isChecked, mAudioCallBack);
                }
            });
        }else {

            int relolution = SPUtil.getIntsance().getInt(STUDENT_RESOLUTION, mCCAtlasClient.getDefaultResolution());
            int index = 0;
            for(int i=0;i< resolutionValues.length;i++){
                if(relolution == resolutionValues[i]){
                    index = i;
                    break;
                }
            }
            holder.mSBitrate.setValue(bitOptions.get(index));
            holder.mBottomLayout.setVisibility(View.GONE);
            holder.mMiddleLayout.setVisibility(View.GONE);
            holder.mTopLayout.setVisibility(View.GONE);
            holder.mSBitrate.setVisibility(View.VISIBLE);
            holder.mSBitrate.setValue(bitOptions.get(index));
//            holder.mSBitrate.setValue(mBitrateTips.get(mCCAtlasClient.getInteractBean().getTalkerBitrate()));
        }
        
        
        
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(MyEBEvent event) {
        switch (event.what) {
            case INTERACT_EVENT_WHAT_UPDATE_MEDIA_MODE:
                mMediaMode = (int) event.obj;
                mode_media_position = mMediaMode == MEDIA_MODE_AUDIO ?
                        0 : 1;
                mViewHolder.mMediaModeSetting.setValue(mMediaMode == MEDIA_MODE_AUDIO ? mMediaTypeAudio :
                        mMediaTypeBoth);
                break;
            case INTERACT_EVENT_WHAT_UPDATE_LIANMAI_MODE:
                mLianmaiMode = (int) event.obj;
                mode_position = mLianmaiMode == LIANMAI_MODE_FREE ? 0 :
                        mLianmaiMode == LIANMAI_MODE_NAMED ? 1 : 2;
                mViewHolder.mLianmaiSetting.setValue(mLianmaiMode == LIANMAI_MODE_FREE ? mLianmaiTypeFree :
                        mLianmaiMode == LIANMAI_MODE_AUTO ? mLianmaiTypeAuto : mLianmaiTypeNamed);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInteractEvent(Integer time) {
        mLoopTime = time;
    }

    private final ArrayList<String> bitOptions = new ArrayList<>();
    public static final String TEACHER_RESOLUTION = "teacher_resolution";

    final class SettingViewHolder extends TitleActivity.ViewHolder implements View.OnClickListener {
        private ToggleButton mGag;
        private ToggleButton mMic;
        private  ItemLayout mMediaModeSetting;
        private  ItemLayout mLianmaiSetting;
        private  ItemLayout mLoopPlayTime;
        private  ItemLayout mSBitrate;
        private  ItemLayout mTBitrate,mMirrorMode;
        private View mTopLayout,mMiddleLayout,mBottomLayout;
         private final int[] bitValues = new int[]{Bitrate_Level720, Bitrate_Level480, Bitrate_Level240};
        private final int[] resolutionValues = CCAtlasClient.getInstance().getResolution();

        private boolean isTeacher = true;
        private OptionsPickerView mPickerView;

        private final ArrayList<String> datas = new ArrayList<>();
        private BottomCancelPopup mConfirm;

        SettingViewHolder(View view) {
            super(view);

            for(int i=0;i<resolutionValues.length;i++){
                int resolution = resolutionValues[i];
                if(resolution == CCAtlasClient.Resolution_720P){
                    bitOptions.add(super_definition);
                }else if(resolution == CCAtlasClient.Resolution_480P){
                    bitOptions.add(high_definition);
                }else if(resolution == CCAtlasClient.Resolution_240P){
                    bitOptions.add(standard_definition);
                }
            }

            mGag = view.findViewById(R.id.id_item_gag);
            mMic = view.findViewById(R.id.id_item_close_mic);
            mMediaModeSetting = view.findViewById(R.id.id_setting_media_mode);
            mLianmaiSetting = view.findViewById(R.id.id_setting_lianmai_mode);
            mLoopPlayTime = view.findViewById(R.id.id_setting_loop_play_time);
            mSBitrate = view.findViewById(R.id.id_setting_sbitrate);
            mTBitrate = view.findViewById(R.id.id_setting_tbitrate);
            mTopLayout = view.findViewById(R.id.id_setting_top_layout);
            mMiddleLayout = view.findViewById(R.id.id_setting_middle_layout);
            mBottomLayout = view.findViewById(R.id.id_setting_bottom_layout);
            mMirrorMode = view.findViewById(R.id.id_setting_video_mirror);

//            bitOptions.add(super_definition);
//            bitOptions.add(high_definition);
//            bitOptions.add(standard_definition);
            view.findViewById(R.id.id_setting_down_mai).setOnClickListener(this);
            view.findViewById(R.id.id_setting_media_mode).setOnClickListener(this);
            view.findViewById(R.id.id_setting_lianmai_mode).setOnClickListener(this);
            view.findViewById(R.id.id_setting_loop_play_time).setOnClickListener(this);
            view.findViewById(R.id.id_setting_tbitrate).setOnClickListener(this);
            view.findViewById(R.id.id_setting_sbitrate).setOnClickListener(this);
            mMirrorMode.setOnClickListener(this);
            initOptionsPicker();
            initMirrorPicker();
            datas.add("确定");
            initConfirmPopup();
        }

        private void initConfirmPopup() {
            mConfirm = new BottomCancelPopup(SettingActivity.this);
            mConfirm.setOutsideCancel(true);
            mConfirm.setKeyBackCancel(true);
            mConfirm.setChooseDatas(datas);
            mConfirm.setTip(kickTip);
            mConfirm.setIndexColor(0, Color.parseColor("#ff0000"));
            mConfirm.setOnChooseClickListener(new BottomCancelPopup.OnChooseClickListener() {
                @Override
                public void onClick(int index) {
                    allDown();
                }
            });
        }

        private void allDown() {
            showLoading();
            CCChatManager.getInstance().allKickDownMai(new CCChatCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                }

                @Override
                public void onFailure(String err) {
                    dismissLoading();
                    Tools.showToast(err);
                }
            });
        }
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id==R.id.id_setting_down_mai){
                allDownMai();
            }else if(id ==R.id.id_setting_media_mode){
                mediaSetting();
            }else if(id ==R.id.id_setting_lianmai_mode){
                lianmaiSetting();
            }else if(id==R.id.id_setting_loop_play_time){
                maxLianmai();
            }else if(id ==R.id.id_setting_tbitrate){
                tBitrate();
            }else if(id ==R.id.id_setting_sbitrate){
                sBitrate();
            }else if(id==R.id.id_setting_video_mirror){
                mMirrorPickerView.show();
            }
        }
        void allDownMai() {
            if (CCAtlasClient.getInstance().isRoomLive()) {
                View mRoot = getWindow().getDecorView().findViewById(android.R.id.content);
                mConfirm.show(mRoot);
            } else {
                Tools.showToast("直播未开始");
            }
        }

        void mediaSetting() {
            if (CCAtlasClient.getInstance().isRoomLive()) {
                Tools.showToast("直播中不支持该操作");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("position", mode_media_position);
            bundle.putInt("mode", SetSettingActivity.SETTING_MODE_MEDIA);
            go(SetSettingActivity.class, LIANMAI_AUDIO_VIDEO, bundle);
        }

        void lianmaiSetting() {
            if (CCAtlasClient.getInstance().isRoomLive()) {
                Tools.showToast("直播中不支持该操作");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("position", mode_position);
            bundle.putInt("mode", SetSettingActivity.SETTING_MODE_LIANMAI);
            go(SetSettingActivity.class, LIANMAI_MODE, bundle);
        }
        /**
         * 设置视频镜像模式，这几种模式只针对使用前置摄像头的时候，要么预览和拉流都是镜像的，要么预览和拉流都不镜像
         * 主播端和观众端都是镜像效果，设置对应枚举值为：1
         * 主播端镜像，观众端非镜像效果，设置对应枚举值为 0
         * 主播和观众端都非镜像效果，设置对应枚举值为：2
         * 主播非镜像，观众镜像效果，设置对应枚举值为：3
         */
        private void initMirrorPicker() {
            mMirrorPickerView = new OptionsPickerView.Builder(SettingActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    SPUtil.getIntsance().put(Config.MIRRORING_MODE, options1);
                    mCCAtlasClient.setVideoMirrorMode(options1);
                    mViewHolder.mMirrorMode.setValue(mirrorModeList.get(options1));
                }
            })
                    .setTitleText(mBitrateTip)
                    .setContentTextSize(20)//设置滚轮文字大小
                    .setDividerColor(getResources().getColor(R.color.colorPrimary))//设置分割线的颜色
                    .setBgColor(Color.BLACK)
                    .setTitleBgColor(Color.DKGRAY)
                    .setTitleColor(Color.LTGRAY)
                    .setCancelColor(getResources().getColor(R.color.colorPrimary))
                    .setSubmitColor(getResources().getColor(R.color.colorPrimary))
                    .setTextColorCenter(Color.LTGRAY)
                    .setBackgroundId(0x66000000) //设置外部遮罩颜色
                    .build();


            mirrorMode = SPUtil.getIntsance().getInt(Config.MIRRORING_MODE, 0);
            String[] mirrorStrings = getResources().getStringArray(R.array.mirror_mode);
            mirrorModeList = new ArrayList(mirrorStrings.length);
            Collections.addAll(mirrorModeList,mirrorStrings);
            mMirrorPickerView.setPicker(mirrorModeList);
            mMirrorPickerView.setSelectOptions(mirrorMode);
            mMirrorMode.setValue(mirrorModeList.get(mirrorMode));

        }
        void maxLianmai() {
            go(LoopTimeActivity.class);
        }


        private void writeValue2PickIndex(int bitrate) {
            switch (bitrate) {
                case Bitrate_Level720:
                    mPickIndex = 0;
                    break;
                case Bitrate_Level480:
                    mPickIndex = 1;
                    break;
                case Bitrate_Level240:
                    mPickIndex = 2;
                    break;
            }
        }

        private void showPick(int bitrate) {
            writeValue2PickIndex(bitrate);
            mPickerView.setSelectOptions(bitrate);
            mPickerView.show();
        }

        void tBitrate() {
            if (CCAtlasClient.getInstance().isRoomLive()) {
                Tools.showToast("直播中不支持该操作");
            } else {
                isTeacher = true;

//            showPick(mCCAtlasClient.getInteractBean().getPresenterBitrate());
//                showPick(SPUtil.getIntsance().getInt(TEACHER_RESOLUTION, Resolution_480P));

                //处理越界数据
                int relolution = SPUtil.getIntsance().getInt(TEACHER_RESOLUTION, CCAtlasClient.getInstance().getDefaultResolution());
                int index = 0;
                for(int i=0;i< resolutionValues.length;i++){
                    if(relolution == resolutionValues[i]){
                        index = i;
                        break;
                    }
                }
                //避免越界
                if(index > resolutionValues.length){
                    index = resolutionValues.length -1;
                }
                showPick(index);
            }
        }

        void sBitrate() {
            isTeacher = false;
            int relolution = SPUtil.getIntsance().getInt(STUDENT_RESOLUTION, mCCAtlasClient.getDefaultResolution());
            int index = 0;
            for(int i=0;i< resolutionValues.length;i++){
                if(relolution == resolutionValues[i]){
                    index = i;
                    break;
                }
            }
            if(index > resolutionValues.length){
                index = resolutionValues.length -1;
            }
            showPick(index);
//            showPick(CCAtlasClient.getInstance().getInteractBean().getTalkerBitrate());
        }

        private void initOptionsPicker() {
            mPickerView = new OptionsPickerView.Builder(SettingActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                   /* if (isTeacher) {
                        SPUtil.getIntsance().put(TEACHER_RESOLUTION, resolutionValues[options1]);
                        mViewHolder.mTBitrate.setValue(bitOptions.get(options1));
                        CCAtlasClient.getInstance().setResolution(SPUtil.getIntsance().getInt(TEACHER_RESOLUTION,CCAtlasClient.getInstance().getDefaultResolution()));
                        isChangeResolution = true;
//                        commitTeacherbit(bitValues[options1]);
                    } else {
                        commitStudentbit(bitValues[options1]);
                    }*/

                    if (isTeacher) {
                        SPUtil.getIntsance().put(TEACHER_RESOLUTION, resolutionValues[options1]);
                        mViewHolder.mTBitrate.setValue(bitOptions.get(options1));
                        mCCAtlasClient.setResolution(SPUtil.getIntsance().getInt(TEACHER_RESOLUTION,mCCAtlasClient.getDefaultResolution()));
                        isChangeResolution = true;
                    } else {
                        SPUtil.getIntsance().put(STUDENT_RESOLUTION, resolutionValues[options1]);
                        mViewHolder.mSBitrate.setValue(bitOptions.get(options1));
                    }
                }
            })
                    .setTitleText(mBitrateTip)
                    .setContentTextSize(20)//设置滚轮文字大小
                    .setDividerColor(getResources().getColor(R.color.colorPrimary))//设置分割线的颜色
                    .setBgColor(Color.BLACK)
                    .setTitleBgColor(Color.DKGRAY)
                    .setTitleColor(Color.LTGRAY)
                    .setCancelColor(getResources().getColor(R.color.colorPrimary))
                    .setSubmitColor(getResources().getColor(R.color.colorPrimary))
                    .setTextColorCenter(Color.LTGRAY)
                    .setBackgroundId(0x66000000) //设置外部遮罩颜色
                    .build();
            mPickerView.setPicker(bitOptions);
        }

        private void commitStudentbit(int level) {
            showLoading();
            CCChatManager.getInstance().changeRoomStudentBitrate(level, new CCChatCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                    mViewHolder.mSBitrate.setValue(mBitrateTips.get(CCAtlasClient.getInstance().getInteractBean().getTalkerBitrate()));
                }

                @Override
                public void onFailure(String err) {
                    dismissLoading();
                    Tools.showToast(err);
                }
            });
        }

        private void commitTeacherbit(int level) {
            showLoading();
            CCChatManager.getInstance().changeRoomTeacherBitrate(level, new CCChatCallBack<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismissLoading();
                    mViewHolder.mTBitrate.setValue(mBitrateTips.get
                            (CCAtlasClient.getInstance().getInteractBean().getPresenterBitrate()));
                }

                @Override
                public void onFailure(String err) {
                    dismissLoading();
                    Tools.showToast(err);
                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LIANMAI_MODE:
                if (resultCode == Activity.RESULT_OK) {
                    mode_position = data.getIntExtra("mode_set", 0);
                    updateLianMaiMode(mode_position);
                } else {

                }
                break;
            case LIANMAI_AUDIO_VIDEO:
                if (resultCode == Activity.RESULT_OK) {
                    mode_media_position = data.getIntExtra("mode_set", 0);
                    updateLianMaiMedia(mode_media_position);
                } else {

                }
                break;
            default:
                break;
        }
    }

    private void updateLianMaiMode(int lianmaiMode) {
        String name = lianmaiMode == LIANMAI_MODE_FREE ? mLianmaiTypeFree :
                lianmaiMode == LIANMAI_MODE_NAMED ? mLianmaiTypeNamed : mLianmaiTypeAuto;
        mViewHolder.mLianmaiSetting.setValue(name);
    }

    private void updateLianMaiMedia(int lianmaiModeMedia) {
        mMediaMode = CCAtlasClient.getInstance().getInteractBean().getMediaMode();
        String name = mMediaMode == MEDIA_MODE_AUDIO ? mMediaTypeAudio :
                mMediaTypeBoth;

        mViewHolder.mMediaModeSetting.setValue(name);
    }
    @Override
    public void onBackPressed() {
        if(isChangeResolution){
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        if(isChangeResolution){
            setResult(RESULT_OK);
        }
        super.finish();
    }
}
