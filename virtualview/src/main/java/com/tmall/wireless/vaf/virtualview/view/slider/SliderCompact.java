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

package com.tmall.wireless.vaf.virtualview.view.slider;

import android.util.Log;
import com.libra.Utils;
import com.libra.expr.common.ExprCode;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.expr.engine.ExprEngine;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.NativeViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import org.json.JSONObject;

/**
 * Created by gujicheng on 16/12/15.
 * The new Slide is implemented with Scroller and controlled with {@link android.support.v7.widget.LinearSnapHelper}, see {@link Slider}. <br />
 * This is reserved for app build with old support library.
 */
public class SliderCompact extends NativeViewBase implements SliderView.Listener {
    private final static String TAG = "Slider_TMTEST";

    protected SliderCompactImp mNative;
    protected ExprCode mScrollCode;
    protected int mCur;
    protected int mTotal;

    public SliderCompact(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mNative = new SliderCompactImp(context);
        __mNative = mNative;
        mNative.setListener(this);
    }

    @Override
    public void reset() {
        super.reset();

        mNative.reset();
    }

    public int getCur() {
        return mCur;
    }

    public int getTotal() {
        return mTotal;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void setData(Object data) {
        mNative.setData(data);

        super.setData(data);
    }

    public void callScroll() {
        if (null != mScrollCode) {
            ExprEngine engine = mContext.getExprEngine();
            if (null != engine) {
                engine.getEngineContext().getDataManager().replaceData(
                        getViewCache().getComponentData());
            }
            if (null != engine && engine.execute(this, mScrollCode) ) {
            } else {
                Log.e(TAG, "callPageFlip execute failed");
            }
        }
    }

    @Override
    protected boolean setAttribute(int key, ExprCode value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_onScroll:
                    mScrollCode = value;
                    break;

                default:
                    ret = false;
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
                case StringBase.STR_ID_span:
                    mNative.setSpan(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.dp2px(value));
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
                case StringBase.STR_ID_span:
                    mNative.setSpan(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_orientation:
                    mNative.setOrientation(value);
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
                case StringBase.STR_ID_span:
                    mNative.setSpan(Utils.rp2px(value));
                    break;

                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.rp2px(value));
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
                case StringBase.STR_ID_span:
                    mNative.setSpan(Utils.rp2px(value));
                    break;

                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.rp2px(value));
                    break;

                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    public void onScroll(int pos, int total) {
        mCur = pos;
        mTotal = total;
        callScroll();
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new SliderCompact(context, viewCache);
        }
    }

}
