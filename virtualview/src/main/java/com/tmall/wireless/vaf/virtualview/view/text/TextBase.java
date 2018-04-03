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

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.RtlHelper;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;
import com.tmall.wireless.vaf.virtualview.loader.StringLoader;

/**
 * Created by gujicheng on 16/8/15.
 */
public abstract class TextBase extends ViewBase {
    private final static String TAG = "TextBase_TMTEST";

    protected String mText;
    protected int mTextColor;
    protected int mTextSize;
    protected int mTextStyle;
    protected String mTypeface;
    protected int mLines;
    protected int mEllipsize;

    public TextBase(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mLines = -1;
        mEllipsize = -1;
        mText = "";
        mTextColor = Color.BLACK;
        mTextSize = Utils.dp2px(20);
        mDataTag = "title";
        mTextStyle = Typeface.NORMAL;
    }

    public void setText(String text) {
        if (!TextUtils.equals(text, mText)) {
            mText = text;
            refresh();
        }
    }

    public String getText() {
        return mText;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        if (mTextColor != color) {
            mTextColor = color;
            mPaint.setColor(mTextColor);
            refresh();
        }
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();

        if (isRtl()) {
            mGravity = RtlHelper.resolveRtlGravity(mGravity);
        }
    }

    @Override
    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = super.setAttribute(key, stringValue);

        if (!ret) {
            switch (key) {
                case StringBase.STR_ID_text:
                    if (Utils.isEL(stringValue)) {
                        mViewCache.put(this, StringBase.STR_ID_text, stringValue, Item.TYPE_STRING);
                    } else {
                        mText = stringValue;
                    }
                    break;
                case StringBase.STR_ID_typeface:
                    mTypeface = stringValue;
                    break;
                case StringBase.STR_ID_textSize:
                    mViewCache.put(this, StringBase.STR_ID_textSize, stringValue, Item.TYPE_FLOAT);
                    break;
                case StringBase.STR_ID_textColor:
                    mViewCache.put(this, StringBase.STR_ID_textColor, stringValue, Item.TYPE_COLOR);
                    break;
                case StringBase.STR_ID_textStyle:
                    mViewCache.put(this, StringBase.STR_ID_textStyle, stringValue, Item.TYPE_TEXT_STYLE);
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
                case StringBase.STR_ID_textSize:
                    mTextSize = Utils.dp2px(Math.round(value));
                    break;

                default:
                    ret = false;
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
                case StringBase.STR_ID_textSize:
                    mTextSize = Utils.rp2px(value);
                    break;

                default:
                    ret = false;
            }
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_textSize:
                    mTextSize = Utils.dp2px(value);
                    break;

                case StringBase.STR_ID_textColor:
                    mTextColor = value;
                    break;

                case StringBase.STR_ID_textStyle:
                    mTextStyle = value;
                    break;

                case StringBase.STR_ID_lines:
                    mLines = value;
                    break;

                case StringBase.STR_ID_ellipsize:
                    mEllipsize = value;
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
                case StringBase.STR_ID_textSize:
                    mTextSize = Utils.rp2px(value);
                    break;

                default:
                    ret = false;
            }
        }

        return ret;
    }

}
