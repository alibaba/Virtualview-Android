/*
 * MIT License
 *
 * Copyright (c) 2018 Alibaba Group
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

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.LineHeightSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.TextBaseCommon;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;

/**
 * Created by gujicheng on 16/8/15.
 */
public class NativeText extends TextBase {

    private final static String TAG = "NativeText_TMTEST";

    protected NativeTextImp mNative;

    protected VVLineHeightSpannableStringBuilder mSpannableStringBuilder;

    protected boolean mSupportHtmlStyle = false;

    protected float mLineSpaceMultipiler = 1.0f;

    protected float mLineSpaceExtra = 0.0f;

    protected float mLineHeight = Float.NaN;

    public NativeText(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mNative = new NativeTextImp(context.forViewConstruction());
    }

    @Override
    public void setText(String text) {
        if (!TextUtils.equals(text, mText)) {
            mText = text;
            setRealText(mText);
        }
    }

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
        super.comLayout(l, t, r, b);
        mNative.comLayout(l, t, r, b);
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
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
        if (0 != (mTextStyle & TextBaseCommon.UNDERLINE)) {
            flag |= Paint.UNDERLINE_TEXT_FLAG;
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
    }

    @Override
    public void setData(Object data) {
        super.setData(data);

        if (data instanceof String) {
            setRealText((String) data);
        }
    }

    protected void setRealText(String str) {
        CharSequence content = mSupportHtmlStyle ? Html.fromHtml(str) : str;
        if (!Float.isNaN(mLineHeight)) {
            if (mSpannableStringBuilder == null) {
                mSpannableStringBuilder = new VVLineHeightSpannableStringBuilder();
            }
            mSpannableStringBuilder.setContent(content, mLineHeight);
            mNative.setText(mSpannableStringBuilder);
        } else {
            mNative.setText(content);
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
                case StringBase.STR_ID_lineHeight:
                    mLineHeight = Utils.dp2px(value);
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
                case StringBase.STR_ID_lineHeight:
                    mLineHeight = Utils.dp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    protected boolean setRPAttribute(int key, int value) {
        boolean ret = super.setRPAttribute(key, value);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_lineHeight:
                    mLineHeight = Utils.rp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }

        }

        return ret;
    }

    @Override
    protected boolean setRPAttribute(int key, float value) {
        boolean ret = super.setRPAttribute(key, value);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_lineHeight:
                    mLineHeight = Utils.rp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }

        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = super.setAttribute(key, stringValue);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_lineHeight:
                    mViewCache.put(this, StringBase.STR_ID_lineHeight, stringValue, Item.TYPE_FLOAT);
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

    public static class VVLineHeightSpannableStringBuilder extends SpannableStringBuilder {

        private VVLineHeightSpan mVVLineHeightSpan;


        public void setContent(CharSequence sequence, float lineHeight) {
            clear();
            clearSpans();
            if (mVVLineHeightSpan == null) {
                mVVLineHeightSpan = new VVLineHeightSpan(lineHeight);
            } else {
                mVVLineHeightSpan.setHeight(lineHeight);
            }
            append(sequence);
            setSpan(mVVLineHeightSpan, 0, sequence.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

    }

    /**
     * It is learned from facebook's ReactiveNative.
     * See <a href="https://github.com/facebook/react-native/blob/master/ReactAndroid/src/main/java/com/facebook/react/views/text/CustomLineHeightSpan.java">here</a>
     */
    public static class VVLineHeightSpan implements LineHeightSpan {

        private int mHeight;

        VVLineHeightSpan(float height) {
            this.mHeight = (int) Math.ceil(height);
        }

        public void setHeight(float height) {
            mHeight = (int) Math.ceil(height);
        }

        public int getHeight() {
            return mHeight;
        }

        @Override
        public void chooseHeight(
            CharSequence text,
            int start,
            int end,
            int spanstartv,
            int v,
            Paint.FontMetricsInt fm) {
            // This is more complicated that I wanted it to be. You can find a good explanation of what the
            // FontMetrics mean here: http://stackoverflow.com/questions/27631736.
            // The general solution is that if there's not enough height to show the full line height, we
            // will prioritize in this order: descent, ascent, bottom, top

            if (fm.descent > mHeight) {
                // Show as much descent as possible
                fm.bottom = fm.descent = Math.min(mHeight, fm.descent);
                fm.top = fm.ascent = 0;
            } else if (-fm.ascent + fm.descent > mHeight) {
                // Show all descent, and as much ascent as possible
                fm.bottom = fm.descent;
                fm.top = fm.ascent = -mHeight + fm.descent;
            } else if (-fm.ascent + fm.bottom > mHeight) {
                // Show all ascent, descent, as much bottom as possible
                fm.top = fm.ascent;
                fm.bottom = fm.ascent + mHeight;
            } else if (-fm.top + fm.bottom > mHeight) {
                // Show all ascent, descent, bottom, as much top as possible
                fm.top = fm.bottom - mHeight;
            } else {
                // Show proportionally additional ascent / top & descent / bottom
                final int additional = mHeight - (-fm.top + fm.bottom);

                // Round up for the negative values and down for the positive values  (arbritary choice)
                // So that bottom - top equals additional even if it's an odd number.
                fm.top -= Math.ceil(additional / 2.0f);
                fm.bottom += Math.floor(additional / 2.0f);
                fm.ascent = fm.top;
                fm.descent = fm.bottom;
            }
        }
    }

}
