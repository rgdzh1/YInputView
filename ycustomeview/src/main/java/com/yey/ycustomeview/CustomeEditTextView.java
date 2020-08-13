package com.yey.ycustomeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class CustomeEditTextView extends FrameLayout {
    private static final String TAG = "CustomeEditTextView 日志";
    // 控件高度
    private EditText mEtContent;
    private TextView mTvErr;
    private TextView mTvHint;
    private String mErrStr;
    private String mHintStr;
    private String mContentStr;
    private int mErrColor;
    // 失去焦点时候提示颜色
    private int mLoseFocusColor;
    // 获取焦点时候提示颜色
    private int mGetFocusColor;
    private int mEtContentColor;
    private View mLineView;

    // 是否获取焦点
    private boolean etHasFocus;

    // 是否是错误状态
    private boolean hasErrStatus;

    public CustomeEditTextView(@NonNull Context context) {
        this(context, null);
    }

    public CustomeEditTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomeEditTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initXmlParams(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }


    private void initXmlParams(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomeEditTextView, defStyleAttr, 0);
        mErrStr = typedArray.getString(R.styleable.CustomeEditTextView_y_err_desc);
        mHintStr = typedArray.getString(R.styleable.CustomeEditTextView_y_hint_desc);
        mContentStr = typedArray.getString(R.styleable.CustomeEditTextView_y_content_desc);
        mErrColor = typedArray.getColor(R.styleable.CustomeEditTextView_y_tv_err_color, Color.RED);
        mLoseFocusColor = typedArray.getColor(R.styleable.CustomeEditTextView_y_lose_focus, Color.GRAY);
        mGetFocusColor = typedArray.getColor(R.styleable.CustomeEditTextView_y_get_focus, Color.BLUE);
        mEtContentColor = typedArray.getColor(R.styleable.CustomeEditTextView_y_et_content_color, Color.BLACK);
        typedArray.recycle();
    }

    // 初始化View
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_custome_edite_textview, this);
        // 设置内容
        mEtContent = (EditText) findViewById(R.id.et_y_content);
        mEtContent.setText(mContentStr);
        mEtContent.setTextColor(mEtContentColor);
        mEtContent.setHintTextColor(mLoseFocusColor);
        mEtContent.setHint(mHintStr);

        mTvHint = (TextView) findViewById(R.id.tv_y_hint);
        mTvHint.setText(mHintStr);
        mTvHint.setTextColor(mLoseFocusColor);

        mTvErr = (TextView) findViewById(R.id.tv_y_err);
        mTvErr.setText(mErrStr);
        mTvErr.setTextColor(mErrColor);

        mLineView = (View) findViewById(R.id.v_y_line);
        mLineView.setBackgroundColor(mLoseFocusColor);

        // 初始隐藏
        if (TextUtils.isEmpty(mContentStr)) {
            mTvHint.setVisibility(View.INVISIBLE);
        } else {
            mTvHint.setVisibility(View.VISIBLE);
        }
        mTvErr.setVisibility(View.INVISIBLE);
    }


    private void initListener() {
        /**
         * 为EditText设置焦点改变的监听
         */
        mEtContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etHasFocus = hasFocus;
                if (hasFocus) {
                    // 获取焦点
                    // 1. EditText hint提示取消
                    // 2. TextView提示控件显示,文字色高亮,内容为XML中设置的提示文字
                    // 3. 分割线背景色高亮
                    mEtContent.setHint("");
                    mTvHint.setVisibility(VISIBLE);
                    mTvHint.setTextColor(mGetFocusColor);
                    mTvHint.setText(mHintStr);
                    mLineView.setBackgroundColor(mGetFocusColor);
                    // Log.e(TAG, "onFocusChange: ");
                } else {
                    /**
                     * 失去焦点时候
                     * 1. EditText中有内容
                     *    a. TextView提示控件不隐藏,文字色置灰
                     *    b. 分割线背景色置灰
                     * 2. EditText中没有内容
                     *    a. EditText hint提示显示
                     *    b. TextView提示控件隐藏,清除该控件中的内容
                     */
                    String mContentStr = mEtContent.getText().toString();
                    mTvHint.setTextColor(mLoseFocusColor);
                    mLineView.setBackgroundColor(mLoseFocusColor);
                    if (TextUtils.isEmpty(mContentStr.trim())) {
                        // 如果没有输入数据
                        mEtContent.setText("");
                        mEtContent.setHint(mHintStr);
                        mTvHint.setVisibility(INVISIBLE);
                        mTvHint.setText("");
                    }
                }

                if (hasErrStatus) {
                    // 如果处于错误状态
                    // 1. TextView提示控件字体颜色显示错误色
                    // 2. 分割线背景色显示错误色
                    mTvHint.setTextColor(mErrColor);
                    mLineView.setBackgroundColor(mErrColor);
                }
            }
        });
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击整个控件为EditText获取焦点
                mEtContent.requestFocus();
            }
        });
    }

    /**
     * 显示错误信息
     */
    public void setErr() {
        if (!TextUtils.isEmpty(mErrStr)) {
            hasErrStatus = true;
            mTvErr.setText(mErrStr);
            setErrStatus();
        }
    }
    /**
     * 显示错误信息
     */
    public void setErr(String err) {
        hasErrStatus = true;
        mTvErr.setText(err);
        setErrStatus();
    }

    /**
     * 清除错误信息
     */
    public void clearErr() {
        hasErrStatus = false;
        mTvErr.setVisibility(View.INVISIBLE);
        if (etHasFocus) {
            mTvHint.setTextColor(mGetFocusColor);
            mLineView.setBackgroundColor(mGetFocusColor);
        } else {
            mTvHint.setTextColor(mLoseFocusColor);
            mLineView.setBackgroundColor(mLoseFocusColor);
        }
    }

    /**
     * 设置控件为错误状态
     */
    private void setErrStatus() {
        // Tv Err
        mTvErr.setTextColor(mErrColor);
        mTvErr.setVisibility(View.VISIBLE);
        // line
        mLineView.setBackgroundColor(mErrColor);
        // hint
        mTvHint.setTextColor(mErrColor);
    }



}
