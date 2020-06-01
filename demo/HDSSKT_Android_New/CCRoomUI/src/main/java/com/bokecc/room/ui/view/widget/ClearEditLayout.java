package com.bokecc.room.ui.view.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bokecc.room.ui.R;


/**
 * 作者 ${CC视频}.<br/>
 */

public class ClearEditLayout extends FrameLayout {

    private View mContent;
    private ImageView mClear;
    private EditText mInput;

    private Handler mHandler;
    private InputMethodManager imm;

    private OnClearClickListener mOnClearClickListener;
    private OnEditFocusChangeListener mOnEditFocusChangeListener;
    private OnEditTextChangedListener mOnEditTextChangedListener;

    public ClearEditLayout(@NonNull Context context) {
        this(context, null);
    }

    public ClearEditLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearEditLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContent = LayoutInflater.from(context).inflate(R.layout.clear_edit_layout_ui, this, true);
        mClear = (ImageView) findContentChidView(R.id.id_clear_btn);
        mInput = (EditText) findContentChidView(R.id.id_clear_input);
        mInput.requestFocus();
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mHandler = new Handler();
        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mInput.setText("");
                mClear.setVisibility(INVISIBLE);
                if (mOnClearClickListener != null) {
                    mOnClearClickListener.onCloseClick();
                }
            }
        });
        mInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mInput.getText().toString().trim().length() > 0) {
                    mClear.setVisibility(VISIBLE);
                } else {
                    mClear.setVisibility(INVISIBLE);
                }
                if (mOnEditFocusChangeListener != null) {
                    mOnEditFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mClear.setVisibility(VISIBLE);
                } else {
                    mClear.setVisibility(INVISIBLE);
                }
                if (mOnEditTextChangedListener != null) {
                    mOnEditTextChangedListener.onChanged(s);
                }
            }
        });
    }

    private Runnable mShowSoftboardRunnable = new Runnable() {
        @Override
        public void run() {
            imm.showSoftInput(mInput, 0);
        }
    };

    public void showSoftboard() {
        mHandler.postDelayed(mShowSoftboardRunnable, 150);
    }

    public void setInputType(int type) {
        mInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
    }

    public void setOnClearClickListener(OnClearClickListener onClearClickListener) {
        mOnClearClickListener = onClearClickListener;
    }

    public void setOnEditFocusChangeListener(OnEditFocusChangeListener onEditFocusChangeListener) {
        mOnEditFocusChangeListener = onEditFocusChangeListener;
    }

    public void setOnEditTextChangedListener(OnEditTextChangedListener onEditTextChangedListener) {
        mOnEditTextChangedListener = onEditTextChangedListener;
    }

    public void setCloseVisibility(int visibility) {
        mClear.setVisibility(visibility);
    }

    public void setCloseEnabled(boolean enabled) {
        mClear.setEnabled(enabled);
    }

    public void setHint(String value) {
        mInput.setHint(value);
    }

    public void setHintColor(int color) {
        mInput.setHintTextColor(color);
    }

    public void setText(String value) {
        mInput.setText(value);
        mInput.setSelection(value.length()); //光标定位
    }

    public String getText() {
        return mInput.getText().toString().trim();
    }

    private View findContentChidView(int id) {
        return mContent.findViewById(id);
    }

    public interface OnClearClickListener {
        void onCloseClick();
    }

    public interface OnEditFocusChangeListener {
        void onFocusChange(View v, boolean hasFocus);
    }

    public interface OnEditTextChangedListener {
        void onChanged(Editable s);
    }

}
