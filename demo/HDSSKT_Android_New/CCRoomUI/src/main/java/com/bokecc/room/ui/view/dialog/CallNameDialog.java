package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bokecc.common.utils.Tools;
import com.bokecc.common.dialog.CustomDialog;
import com.bokecc.room.ui.R;
import com.bokecc.common.utils.TimeUtil;

/**
 * 点名对话框
 * @author wy
 */
public class CallNameDialog extends CustomDialog {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mTime = -1;
    private boolean flag = false;

    private Context mContext;

    private ImageButton id_named_close;
    private TextView id_named_time_tip;
    private Button id_named_ok;

    private OnCallNameClickListener callNameClickListener;

    public CallNameDialog(Context context,int time,OnCallNameClickListener callNameClickListener) {
        super(context);
        this.mContext = context;
        this.mTime = time;
        this.callNameClickListener = callNameClickListener;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_call_name_layout);
        setCanceledOnTouchOutside(false);
        super.onCreateWrapContent(savedInstanceState);

        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    return true;
                }
                return false;
            }
        });
        initView();
        id_named_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        id_named_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                if (callNameClickListener != null) {
                    callNameClickListener.onAnswer();
                }
            }
        });



        startCountDown();
    }

    public void disableInteractive() {
        id_named_ok.setEnabled(false);
    }

    /**
     * 关闭
     */
    private void close(){
        dismiss();
        stopCountDown();
    }

    /**
     * 初始化视图和适配器
     */
    private void initView(){
        id_named_close = findViewById(R.id.id_named_close);
        id_named_time_tip = findViewById(R.id.id_named_time_tip);
        id_named_ok = findViewById(R.id.id_named_ok);
    }

    /**
     * 计时线程
     */
    private Runnable mCountTask = new Runnable() {
        @Override
        public void run() {
            if (!flag) {
                return;
            }
            mTime -= 1;
            if (mTime <= 0) {
                dismiss();
                stopCountDown();
                return;
            }
            updateTimeTip();
            mHandler.postDelayed(this, 1000);
        }
    };

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        flag = true;
        mHandler.postDelayed(mCountTask, 1000);
    }

    /**
     * 结束倒计时
     */
    private void stopCountDown() {
        flag = false;
        mHandler.removeCallbacks(mCountTask);
    }

    /**
     * 更新时间
     */
    private void updateTimeTip() {
        String timeStr = TimeUtil.formatNamed(mTime);
        String tip = Tools.getString(R.string.call_name_time);
        timeStr = tip + timeStr;
        SpannableString spannableString = new SpannableString(timeStr);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary));
        spannableString.setSpan(colorSpan, tip.length(), timeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        id_named_time_tip.setText(spannableString);
    }


    /**监听*/
    public interface OnCallNameClickListener {
        void onAnswer();
    }

}
