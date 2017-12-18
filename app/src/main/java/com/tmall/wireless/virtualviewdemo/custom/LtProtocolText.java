package com.tmall.wireless.virtualviewdemo.custom;

import android.util.TypedValue;
import android.view.View;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.view.text.TextBase;

/**
 * Created by luanxuan on 2017/12/14.
 */

public class LtProtocolText extends TextBase {
    private LtProtocolImpl mNative;


    public LtProtocolText(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
        mNative = new LtProtocolImpl(context.getContext());
    }


    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        //String[] paths = mText.split("/");
        //if (paths.length == 3) {
        //    if ("local".equals(paths[0])) {
        //        if ("login".equals(paths[1])) {
        //            ILtaoLogin login = BeanFactory.getBean(ILtaoLogin.class);
        //            if ("nick".equals(paths[2])) {
        //                mNative.setText(login.getNick());
        //            } else if ("phone".equals(paths[2])) {
        //                mNative.setText(login.getPhone());
        //            }
        //        }
        //    }
        //} else {
            mNative.setText(mText);
        //}
        mNative.setTextColor(mTextColor);
        mNative.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mNative.onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        mNative.onComLayout(changed, l, t, r, b);
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        mNative.measureComponent(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        mNative.comLayout(l, t, r, b);
    }

    @Override
    public int getComMeasuredHeight() {
        return mNative.getComMeasuredHeight();
    }

    @Override
    public int getComMeasuredWidth() {
        return mNative.getComMeasuredWidth();
    }

    @Override
    public View getNativeView() {
        return mNative;
    }

    public static class Builder implements IBuilder {

        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new LtProtocolText(context, viewCache);
        }
    }
}
