package com.bokecc.room.ui.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bokecc.sskt.base.bean.BrainStom;
import com.bokecc.common.utils.Tools;
import com.bokecc.common.dialog.CustomDialog;
import com.bokecc.room.ui.R;

/**
 * 头脑风暴视图
 * @author wy
 */
public class BrainStomDialog extends CustomDialog {

    private OnCommitClickListener mOnCommitClickListener;
    private Button mCommit;
    private TextView mTip;
    private TextView mContent;
    private EditText mEditText;

    private BrainStom brainStom;

    public BrainStomDialog(Context context, BrainStom brainStom,OnCommitClickListener mOnCommitClickListener) {
        super(context);
        this.brainStom = brainStom;
        this.mOnCommitClickListener = mOnCommitClickListener;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_brain_stom_layout);
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
    }


    /**
     * 初始化视图
     */
    private void initView(){
        findViewById(R.id.id_brain_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTip = findViewById(R.id.id_brain_tip);
        mContent = findViewById(R.id.id_brain_content);

        mEditText = findViewById(R.id.text_info);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    mCommit.setEnabled(true);
                } else {
                    mCommit.setEnabled(false);
                }
            }
        });

        mCommit = findViewById(R.id.id_brain_commit);
        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEditText.getText()) || mEditText.getText().toString().trim().isEmpty()) {
                    Tools.showToast("禁止发送空消息");
                    return;
                }
                mOnCommitClickListener.onCommit(mEditText.getText().toString());
                dismiss();
            }
        });

        mTip.setText("主题：" + brainStom.getTileName());
        mContent.setText(brainStom.getContent());
        if (mEditText.getText() != null) {
            mEditText.setText("");
        }
        mCommit.setEnabled(false);
    }
    public void disableInteractivie(){
        mEditText.setEnabled(false);
        mCommit.setClickable(false);
    }

    /**
     * 监听
     */
    public interface OnCommitClickListener {
        void onCommit(String content);
    }


}