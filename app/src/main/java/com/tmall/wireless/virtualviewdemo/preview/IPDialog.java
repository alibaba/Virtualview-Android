package com.tmall.wireless.virtualviewdemo.preview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.tmall.wireless.virtualviewdemo.R;
import com.tmall.wireless.virtualviewdemo.preview.util.HttpUtil;

/**
 * IP setting dialog.
 *
 * @author SunQiang
 * @since 2019/5/21
 */
public class IPDialog extends Dialog {

    private EditText mIPEditText;

    private View.OnClickListener mOnOKClickListener;

    public IPDialog setOnOKClickListener(View.OnClickListener onOKClickListener) {
        mOnOKClickListener = onOKClickListener;
        return this;
    }

    public IPDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ip);
        setCanceledOnTouchOutside(false);
        mIPEditText = findViewById(R.id.et_ip);
        mIPEditText.setText(HttpUtil.getHostIp(getContext()));
        findViewById(R.id.tv_ok).setOnClickListener(v -> {
            HttpUtil.setHostIp(getContext(), mIPEditText.getText().toString());
            if (mOnOKClickListener != null) {
                mOnOKClickListener.onClick(v);
            }
            dismiss();
        });
        findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
