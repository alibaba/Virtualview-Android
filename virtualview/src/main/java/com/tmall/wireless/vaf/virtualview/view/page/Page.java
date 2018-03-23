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

package com.tmall.wireless.vaf.virtualview.view.page;

import android.graphics.Bitmap;
import android.support.annotation.Keep;
import android.util.Log;
import android.view.View;
import com.libra.expr.common.ExprCode;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.expr.engine.ExprEngine;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.IBean;
import com.tmall.wireless.vaf.virtualview.core.NativeViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import org.json.JSONObject;

/**
 * Created by gujicheng on 16/10/26.
 */
public class Page extends NativeViewBase implements PageView.Listener {
    private final static String TAG = "Page_TMTEST";

    protected PageImp mNative;
    protected ExprCode mPageFlipCode;
    protected int mCurPos = 0;
    protected int mPrePos = 0;
    protected int mTotal;

    public Page(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mNative = new PageImp(context);
        __mNative = mNative;
        mNative.setListener(this);
    }

    @Override
    public void reset() {
        super.reset();

        mNative.reset();
    }

    public int getPrePos() {
        return mPrePos;
    }

    public int getCurPos() {
        return mCurPos;
    }

    public int getTotal() {
        return mTotal;
    }

    public int getChildCount() {
        return mNative.size();
    }

    public void callPageFlip() {
        mContext.getEventManager().emitEvent(EventManager.TYPE_FlipPage, new EventData(mContext, this));

        if (null != mPageFlipCode) {
            ExprEngine engine = mContext.getExprEngine();
            try {
                if (null != engine) {
                    engine.getEngineContext().getDataManager().replaceData(
                        (JSONObject)getViewCache().getComponentData());
                }
                if (null != engine && engine.execute(this, mPageFlipCode)) {
                } else {
                    Log.e(TAG, "callPageFlip execute failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    @Override
    protected boolean setAttribute(int key, ExprCode value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_onPageFlip:
                    mPageFlipCode = value;
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
                case StringBase.STR_ID_orientation:
                    mNative.setOrientation(ViewBaseCommon.HORIZONTAL == value ? true : false);
                    break;

                case StringBase.STR_ID_autoSwitch:
                    mNative.setAutoSwitch(value > 0 ? true : false);
                    break;

                case StringBase.STR_ID_canSlide:
                    mNative.setSlide(value > 0 ? true : false);
                    break;

                case StringBase.STR_ID_stayTime:
                    mNative.setStayTime(value);
                    break;

                case StringBase.STR_ID_animatorTime:
                    mNative.setAnimatorTimeInterval(value);
                    break;

                case StringBase.STR_ID_autoSwitchTime:
                    mNative.setAutoSwitchTimeInterval(value);
                    break;

                case StringBase.STR_ID_containerID:
                    mNative.setContainerId(value);
                    break;

                case StringBase.STR_ID_animatorStyle:
                    mNative.setAnimationStyle(value);
                    break;

                case StringBase.STR_ID_layoutOrientation:
                    mNative.setLayoutOrientation(ViewBaseCommon.NORMAL == value ? true : false);

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
                case StringBase.STR_ID_autoSwitch:
                    mViewCache.put(this, StringBase.STR_ID_autoSwitch, stringValue, Item.TYPE_BOOLEAN);
                    break;
                case StringBase.STR_ID_canSlide:
                    mViewCache.put(this, StringBase.STR_ID_canSlide, stringValue, Item.TYPE_BOOLEAN);
                    break;

                case StringBase.STR_ID_stayTime:
                    mViewCache.put(this, StringBase.STR_ID_stayTime, stringValue, Item.TYPE_INT);
                    break;

                case StringBase.STR_ID_animatorTime:
                    mViewCache.put(this, StringBase.STR_ID_animatorTime, stringValue, Item.TYPE_INT);
                    break;

                case StringBase.STR_ID_autoSwitchTime:
                    mViewCache.put(this, StringBase.STR_ID_autoSwitchTime, stringValue, Item.TYPE_INT);
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    public void onPageFlip(int pos, int total) {
        mPrePos = mCurPos;
        mCurPos = pos - 1;
        mTotal = total;
        callBean();
        callPageFlip();
    }

    @Keep
    public void onScroll(int curPos) {
        Log.d(TAG, "page scroll " + curPos);
    }

    private void callBean() {
        IBean bean = getBean();
        if (null != bean) {
            bean.doEvent(EventManager.TYPE_FlipPage, 0, null);
        }
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new Page(context, viewCache);
        }
    }
}
