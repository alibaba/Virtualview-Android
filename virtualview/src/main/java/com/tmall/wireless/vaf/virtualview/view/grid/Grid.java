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

package com.tmall.wireless.vaf.virtualview.view.grid;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.cm.ContainerService;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.NativeViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gujicheng on 16/10/27.
 */
public class Grid extends NativeViewBase {
    private final static String TAG = "Grid_TMTEST";

    private GridImp mNative;

    public Grid(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mNative = new GridImp(context.forViewConstruction());
        mNative.setVirtualView(this);
        __mNative = mNative;
    }

    @Override
    public void reset() {
        super.reset();

        recycleViews();
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    private void recycleViews() {
        ContainerService cm = mContext.getContainerService();
        int childCount = mNative.getChildCount();
        for(int i = 0; i < childCount; ++i) {
            IContainer c = (IContainer)mNative.getChildAt(i);
            cm.recycle(c);
        }
        mNative.removeAllViews();
    }

    @Override
    public void setData(Object data) {
        super.setData(data);

        if (data instanceof JSONObject) {
            JSONObject obj = (JSONObject)data;
            data = obj.optJSONArray(this.getDataTag());
        }

        recycleViews();

        if (data instanceof JSONArray) {
            JSONArray arr = (JSONArray)data;

            ContainerService cm = mContext.getContainerService();
            int len = arr.length();
            for (int i = 0; i < len; ++i) {
                try {
                    JSONObject obj = arr.getJSONObject(i);
                    String type = obj.optString(TYPE);
                    if (!TextUtils.isEmpty(type)) {
                        View v = cm.getContainer(type);
                        if (null != v) {
                            ViewBase vb = ((IContainer)v).getVirtualView();
                            vb.setVData(obj);

                            mNative.addView(v);

                            if (vb.supportExposure()) {
                                mContext.getEventManager().emitEvent(EventManager.TYPE_Exposure, EventData.obtainData(mContext, vb));
                            }

                            vb.ready();
                        } else {
                            Log.e(TAG, "create view failed");
                        }
                    } else {
                        Log.e(TAG, "get type failed");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "get json object failed:" + e);
                }
            }
        } else {
            Log.e(TAG, "setData not array");
        }
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        mNative.setAutoDimDirection(mAutoDimDirection);
        mNative.setAutoDimX(mAutoDimX);
        mNative.setAutoDimY(mAutoDimY);
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_itemHeight:
                    mNative.setItemHeight(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemHorizontalMargin:
                    mNative.setItemHorizontalMargin(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemVerticalMargin:
                    mNative.setItemVerticalMargin(Utils.dp2px(value));
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
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_colCount:
                mNative.setColumnCount(value);
                break;

            case StringBase.STR_ID_itemHeight:
                mNative.setItemHeight(Utils.dp2px(value));
                break;

            case StringBase.STR_ID_itemHorizontalMargin:
                mNative.setItemHorizontalMargin(Utils.dp2px(value));
                break;

            case StringBase.STR_ID_itemVerticalMargin:
                mNative.setItemVerticalMargin(Utils.dp2px(value));
                break;
            default:
                ret = super.setAttribute(key, value);
                break;
        }
        return ret;
    }

    @Override
    protected boolean setAttribute(int key, String value) {
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_itemHorizontalMargin:
                mViewCache.put(this, StringBase.STR_ID_itemHorizontalMargin, value, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_itemVerticalMargin:
                mViewCache.put(this, StringBase.STR_ID_itemVerticalMargin, value, Item.TYPE_FLOAT);
                break;
            default:
                ret = super.setAttribute(key, value);
                break;
        }
        return ret;
    }

    @Override
    protected boolean setRPAttribute(int key, float value) {
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_itemHeight:
                mNative.setItemHeight(Utils.rp2px(value));
                break;

            case StringBase.STR_ID_itemHorizontalMargin:
                mNative.setItemHorizontalMargin(Utils.rp2px(value));
                break;

            case StringBase.STR_ID_itemVerticalMargin:
                mNative.setItemVerticalMargin(Utils.rp2px(value));
                break;
            default:
                ret = super.setRPAttribute(key, value);
                break;
        }
        return ret;
    }

    @Override
    protected boolean setRPAttribute(int key, int value) {
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_itemHeight:
                mNative.setItemHeight(Utils.rp2px(value));
                break;

            case StringBase.STR_ID_itemHorizontalMargin:
                mNative.setItemHorizontalMargin(Utils.rp2px(value));
                break;

            case StringBase.STR_ID_itemVerticalMargin:
                mNative.setItemVerticalMargin(Utils.rp2px(value));
                break;
            default:
                ret = super.setRPAttribute(key, value);
                break;
        }
        return ret;
    }

    public static class Builder implements IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new Grid(context, viewCache);
        }
    }
}
