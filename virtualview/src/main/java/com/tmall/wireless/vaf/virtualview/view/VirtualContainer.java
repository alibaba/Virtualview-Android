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

package com.tmall.wireless.vaf.virtualview.view;

import android.view.View;
import android.view.ViewGroup;

import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.cm.ContainerService;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by gujicheng on 16/12/20.
 */

public class VirtualContainer extends ViewBase {
    private final static String TAG = "VContainer_TMTEST";

    private int mOrder = -1;
    private IView mRealVB;

    public VirtualContainer(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
    }

    public int getOrder() {
        return mOrder;
    }

    @Override
    public void reset() {
        super.reset();

        if (null != mRealVB) {
            ContainerService cs = mContext.getContainerService();
            cs.recycle((IContainer)mRealVB);
            ((ViewGroup)mViewCache.getHolderView()).removeView((View)mRealVB);
            mRealVB = null;
        }
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (null != mRealVB) {
            mRealVB.onComMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        if (null != mRealVB) {
            mRealVB.onComLayout(changed, l, t, r, b);
        }
    }

    @Override
    public int getComMeasuredWidth() {
        if (null != mRealVB) {
            return mRealVB.getComMeasuredWidth();
        }
        return 0;
    }

    @Override
    public int getComMeasuredHeight() {
        if (null != mRealVB) {
            return mRealVB.getComMeasuredHeight();
        }
        return 0;
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        if (null != mRealVB) {
            mRealVB.measureComponent(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        if (null != mRealVB) {
            mRealVB.comLayout(l, t, r, b);
        }
    }

    protected void attachViews(ViewBase view) {
        if (view instanceof Layout) {
            Layout layout = (Layout) view;
            List<ViewBase> subViews = layout.getSubViews();
            if (null != subViews) {
                for (ViewBase com : subViews) {
                    attachViews(com);
                }
            }
        } else {
            View v = view.getNativeView();
            if (null != v) {
                ((ViewGroup) mViewCache.getHolderView()).addView(v);
            }
        }
    }

    protected void detachViews(ViewBase view) {
        if (view instanceof Layout) {
            Layout layout = (Layout) view;
            List<ViewBase> subViews = layout.getSubViews();
            if (null != subViews) {
                for (ViewBase com : subViews) {
                    detachViews(com);
                }
            }
        } else {
            View v = view.getNativeView();
            if (null != v) {
                ((ViewGroup) mViewCache.getHolderView()).removeView(v);
            }
        }
    }

    @Override
    public void setData(Object data) {
        super.setData(data);
        ContainerService cs = mContext.getContainerService();

        if (null != mRealVB) {
            cs.recycle((IContainer)mRealVB);
            ((ViewGroup)mViewCache.getHolderView()).removeView((View)mRealVB);
        }

        if (data instanceof JSONArray) {
            JSONArray arr = (JSONArray) data;
            if (mOrder < arr.length()) {
                JSONObject obj = arr.optJSONObject(mOrder);
                if (null != obj) {
                    mRealVB = (IView)cs.getContainer(obj.optString(ViewBase.TYPE));
                    if (null != mRealVB) {
                        ViewBase vb = ((IContainer) mRealVB).getVirtualView();
                        vb.setVData(obj);
                        ((ViewGroup)mViewCache.getHolderView()).addView((View)mRealVB);
                        if (vb.supportExposure()) {
                            mContext.getEventManager().emitEvent(EventManager.TYPE_Exposure, EventData.obtainData(mContext, vb));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_order:
                    mOrder = value;
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
            return new VirtualContainer(context, viewCache);
        }
    }

}
