/*
 * MIT License
 *
 * Copyright (c) 2017 Alibaba Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tmall.wireless.vaf.virtualview.view.text;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.TextBaseCommon;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

/**
 * Created by gujicheng on 16/8/15.
 */
public class NativeText extends TextBase {
    private final static String TAG = "NativeText_TMTEST";
    protected NativeTextImp mNative;
    protected boolean mSupportHtmlStyle = false;
    protected float mLineSpaceMultipiler = 1.0f;
    protected float mLineSpaceExtra = 0.0f;

    public NativeText(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mNative = new NativeTextImp(context.getContext());
    }

    @Override
    public void setText(String text) {
        if (!TextUtils.equals(text, mText)) {
            mText = text;
            setRealText(mText);
        }
    }

//    @Override
//    protected void changeVisibility() {
//        switch (mVisibility) {
//            case INVISIBLE:
//                mNative.setVisibility(View.INVISIBLE);
//                break;
//
//            case VISIBLE:
//                mNative.setVisibility(View.VISIBLE);
//                break;
//
//            case GONE:
//                mNative.setVisibility(View.GONE);
//                break;
//        }
//    }
//
    @Override
    public void setTextColor(int color) {
        if (mTextColor != color) {
            mTextColor = color;
            mNative.setTextColor(mTextColor);
        }
    }

    @Override
    public void reset() {
        super.reset();
        mSupportHtmlStyle = false;
        mLineSpaceMultipiler = 1.0f;
        mLineSpaceExtra = 0.0f;
        mText = null;

    }

    @Override
    public View getNativeView() {
        return mNative;
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
    public int getComMeasuredWidth() {
        return mNative.getComMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return mNative.getComMeasuredHeight();
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        mNative.comLayout(l, t, r, b);
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();

        mNative.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        mNative.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mNative.setBorderColor(mBorderColor);
        mNative.setBorderWidth(mBorderWidth);
        mNative.setBorderTopLeftRadius(mBorderTopLeftRadius);
        mNative.setBorderTopRightRadius(mBorderTopRightRadius);
        mNative.setBorderBottomLeftRadius(mBorderBottomLeftRadius);
        mNative.setBorderBottomRightRadius(mBorderBottomRightRadius);
        mNative.setBackgroundColor(mBackground);
        mNative.setTextColor(mTextColor);

        int flag = Paint.ANTI_ALIAS_FLAG;
        if (0 != (mTextStyle & TextBaseCommon.BOLD)) {
            flag |= Paint.FAKE_BOLD_TEXT_FLAG;
        }
        if (0 != (mTextStyle & TextBaseCommon.STRIKE)) {
            flag |= Paint.STRIKE_THRU_TEXT_FLAG;
        }
        mNative.setPaintFlags(flag);

        if (0 != (mTextStyle & TextBaseCommon.ITALIC)) {
            mNative.setTypeface(null, Typeface.BOLD_ITALIC);
        }

        if (mLines > 0) {
            mNative.setLines(mLines);
        }

        if (mEllipsize >= 0) {
            mNative.setEllipsize(TextUtils.TruncateAt.values()[mEllipsize]);
        }

        int gravity = 0;
        if (0 != (mGravity & ViewBaseCommon.LEFT)) {
            gravity |= Gravity.LEFT;
        } else if (0 != (mGravity & ViewBaseCommon.RIGHT)) {
            gravity |= Gravity.RIGHT;
        } else if (0 != (mGravity & ViewBaseCommon.H_CENTER)) {
            gravity |= Gravity.CENTER_HORIZONTAL;
        }

        if (0 != (mGravity & ViewBaseCommon.TOP)) {
            gravity |= Gravity.TOP;
        } else if (0 != (mGravity & ViewBaseCommon.BOTTOM)) {
            gravity |= Gravity.BOTTOM;
        } else if (0 != (mGravity & ViewBaseCommon.V_CENTER)) {
            gravity |= Gravity.CENTER_VERTICAL;
        }
        mNative.setGravity(gravity);

        mNative.setLineSpacing(mLineSpaceExtra, mLineSpaceMultipiler);

        if (!TextUtils.isEmpty(mText)) {
            setRealText(mText);
        } else {
            setRealText("");
        }

        if (isClickable()) {
            mNative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NativeText.this.click(0, 0, false);
                }
            });
        }
        if (isLongClickable()) {
            mNative.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    NativeText.this.click(0, 0, true);
                    return false;
                }
            });
        }
    }

    @Override
    public void setData(Object data) {
        super.setData(data);

        if (data instanceof String) {
            setRealText((String) data);
        }
    }

    protected void setRealText(String str) {
        if (mSupportHtmlStyle) {
            mNative.setText(Html.fromHtml(str));
        } else {
            mNative.setText(str);
        }
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_supportHTMLStyle:
                    mSupportHtmlStyle = (value > 0) ? true : false;
                    break;

                case StringBase.STR_ID_lineSpaceMultiplier:
                    mLineSpaceMultipiler = value;
                    break;

                case StringBase.STR_ID_lineSpaceExtra:
                    mLineSpaceExtra = value;
                    break;

                case StringBase.STR_ID_maxLines:
                    mNative.setMaxLines(value);
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_supportHTMLStyle:
                    mSupportHtmlStyle = (value > 0) ? true : false;
                    break;

                case StringBase.STR_ID_lineSpaceMultiplier:
                    mLineSpaceMultipiler = value;
                    break;

                case StringBase.STR_ID_lineSpaceExtra:
                    mLineSpaceExtra = value;
                    break;

                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new NativeText(context, viewCache);
        }
    }
}
