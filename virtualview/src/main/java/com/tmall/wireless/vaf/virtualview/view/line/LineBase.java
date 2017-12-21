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

package com.tmall.wireless.vaf.virtualview.view.line;

import android.graphics.Color;
import android.util.Log;

import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;

import static com.libra.virtualview.common.LineBaseCommon.STYLE_SOLID;

/**
 * Created by gujicheng on 16/11/2.
 */

public abstract class LineBase extends ViewBase {
    private final static String TAG = "LineBase_TMTEST";

    protected boolean mIsHorizontal;
    protected int mLineColor;
    protected int mLineWidth;
    protected int mStyle;
    protected float[] mDashEffect = {3, 5, 3, 5};

    public LineBase(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mLineColor = Color.BLACK;
        mLineWidth = 1;
        mIsHorizontal = true;
        mStyle = STYLE_SOLID;
    }

    public boolean horizontal() {
        return mIsHorizontal;
    }

    public int getPaintWidth() {
        return mLineWidth;
    }

    public int getStyle() {
        return mStyle;
    }

    public int getColor() {
        return mLineColor;
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = true;
        switch (key) {
            case StringBase.STR_ID_paintWidth:
                mLineWidth = Utils.dp2px(value);
                if (mLineWidth <= 0) {
                    mLineWidth = 1;
                }
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = super.setAttribute(key, stringValue);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_color:
                    mViewCache.put(this, StringBase.STR_ID_color, stringValue, Item.TYPE_COLOR);
                    break;
                case StringBase.STR_ID_dashEffect:
                    if (null != stringValue) {
                        stringValue = stringValue.trim();
                        if (stringValue.startsWith("[") && stringValue.endsWith("]")) {
                            stringValue = stringValue.substring(1, stringValue.length() - 1);
                            String[] arr = stringValue.split(",");
                            if (arr.length > 0 && (0 == (arr.length & 1))) {
                                float[] d = new float[arr.length];
                                int i = 0;
                                try {
                                    for (i = 0; i < arr.length; ++i) {
                                        d[i] = Float.parseFloat(arr[i]);
                                    }
                                } catch(NumberFormatException e) {
                                }

                                if (i == arr.length) {
                                    mDashEffect = d;
                                }
                            } else {
                                Log.e(TAG, "length invalidate:" + arr.length);
                            }
                        } else {
                            Log.e(TAG, "no match []");
                        }
                    }
                    break;
                default:
                    ret = false;
                    break;
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
                case StringBase.STR_ID_color:
                    mLineColor = value;
                    break;

                case StringBase.STR_ID_orientation:
                    mIsHorizontal = (0 == value) ? false : true;
                    break;

                case StringBase.STR_ID_paintWidth:
                    mLineWidth = Utils.dp2px(value);
                    if (mLineWidth <= 0) {
                        mLineWidth = 1;
                    }
                    break;

                case StringBase.STR_ID_style:
                    mStyle = value;
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
                case StringBase.STR_ID_paintWidth:
                    mLineWidth = Utils.rp2px(value);
                    if (mLineWidth <= 0) {
                        mLineWidth = 1;
                    }
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
                case StringBase.STR_ID_paintWidth:
                    mLineWidth = Utils.rp2px(value);
                    if (mLineWidth <= 0) {
                        mLineWidth = 1;
                    }
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }
}
