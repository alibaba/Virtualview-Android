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

package com.tmall.wireless.vaf.virtualview.layout;

import android.util.Log;

import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

import static com.libra.virtualview.common.VH2LayoutCommon.DIRECTION_BOTTOM;
import static com.libra.virtualview.common.VH2LayoutCommon.DIRECTION_LEFT;
import static com.libra.virtualview.common.VH2LayoutCommon.DIRECTION_RIGHT;
import static com.libra.virtualview.common.VH2LayoutCommon.DIRECTION_TOP;

/**
 * Created by gujicheng on 16/10/11.
 */
public class VH2Layout extends VHLayout {
    private final static String TAG = "VH2Layout_TMTEST";

    public VH2Layout(VafContext context, ViewCache vc) {
        super(context, vc);
    }

    @Override
    public Params generateParams() {
        return new Params();
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        switch (mOrientation) {
            case ViewBaseCommon.HORIZONTAL: {
                int left = 0;
                int leftStart = l + mPaddingLeft;
                int rightStart = r - mPaddingRight;

                for (ViewBase view : mSubViews) {
                    if (view.isGone()) {
                        continue;
                    }

                    Params childP = (Params) view.getComLayoutParams();
                    int w = view.getComMeasuredWidth();
                    int h = view.getComMeasuredHeight();

                    if (0 != (childP.mLayoutDirection & DIRECTION_LEFT)) {
                        leftStart += childP.mLayoutMarginLeft;
                        left = leftStart;
                        leftStart += w + childP.mLayoutMarginRight;
                    } else if (0 != (childP.mLayoutDirection & DIRECTION_RIGHT)) {
                        rightStart -= childP.mLayoutMarginRight + w;
                        left = rightStart;
                        rightStart -= childP.mLayoutMarginLeft;
                    } else {
                        Log.e(TAG, "onComLayout HORIZONTAL direction invalidate:" + childP.mLayoutDirection);
                    }

                    int tt;
                    if (0 != (childP.mLayoutGravity & ViewBaseCommon.V_CENTER)) {
                        tt = (b + t - h) >> 1;
                    } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.BOTTOM)) {
                        tt = b - h - mPaddingBottom - childP.mLayoutMarginBottom;
                    } else {
                        tt = t + mPaddingTop + childP.mLayoutMarginTop;
                    }
                    view.comLayout(left, tt, left + w, tt + h);
                }
                break;
            }

            case ViewBaseCommon.VERTICAL: {
                int top = 0;
                int topStart = t + mPaddingTop;
                int bottomStart = b - mPaddingBottom;

                for (ViewBase view : mSubViews) {
                    if (view.isGone()) {
                        continue;
                    }

                    Params childP = (Params) view.getComLayoutParams();
                    int w = view.getComMeasuredWidth();
                    int h = view.getComMeasuredHeight();

                    if (0 != (childP.mLayoutDirection & DIRECTION_TOP)) {
                        topStart += childP.mLayoutMarginTop;
                        top = topStart;
                        topStart += h + childP.mLayoutMarginBottom;
                    } else if (0 != (childP.mLayoutDirection & DIRECTION_BOTTOM)) {
                        bottomStart -= childP.mLayoutMarginBottom + h;
                        top = bottomStart;
                        bottomStart -= childP.mLayoutMarginTop;
                    } else {
                        Log.e(TAG, "onComLayout VERTICAL direction invalidate:" + childP.mLayoutDirection);
                    }

                    int ll;
                    if (0 != (childP.mLayoutGravity & ViewBaseCommon.H_CENTER)) {
                        ll = (r + l - w) >> 1;
                    } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.RIGHT)) {
                        ll = r - mPaddingRight - childP.mLayoutMarginRight - w;
                    } else {
                        ll = l + mPaddingLeft + childP.mLayoutMarginLeft;
                    }

                    view.comLayout(ll, top, ll + w, top + h);
                }
                break;
            }
        }
    }

    public static class Params extends VHLayout.Params {
        public int mLayoutDirection;

        public Params() {
            mLayoutDirection = DIRECTION_LEFT;
        }

        @Override
        public boolean setAttribute(int key, int value) {
            boolean ret = super.setAttribute(key, value);

            if (!ret) {
                ret = true;
                switch (key) {
                    case StringBase.STR_ID_layoutDirection:
                        mLayoutDirection = value;
                        break;

                    default:
                        ret = false;
                }
            }

            return ret;
        }
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new VH2Layout(context, viewCache);
        }
    }

}
