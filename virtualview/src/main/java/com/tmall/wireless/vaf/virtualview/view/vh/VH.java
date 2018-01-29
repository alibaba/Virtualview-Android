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

package com.tmall.wireless.vaf.virtualview.view.vh;

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
 * Created by gujicheng on 16/11/10.
 */

public class VH extends NativeViewBase {
    private final static String TAG = "VH_TMTEST";

    private VHImp mNative;

    public VH(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
        mNative = new VHImp(context.getContext());
        __mNative = mNative;
    }

    @Override
    public ViewBase getChild(int index) {
        View v = mNative.getChildAt(index);
        ViewBase vb = ((IContainer)v).getVirtualView();
        return vb;
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

    public void setData(int count, String itemType) {
        recycleViews();

        ContainerService cm = mContext.getContainerService();
        while(count > 0) {
            View v = cm.getContainer(itemType);
            mNative.addView(v);
            --count;
        }
    }

    @Override
    public void setData(Object data) {
        super.setData(data);

        if (data instanceof JSONObject) {
            JSONObject obj = (JSONObject)data;
            data = obj.optJSONArray(this.getDataTag());
        }

        if (data instanceof JSONArray) {
            JSONArray arr = (JSONArray)data;

            int len = arr.length();
            recycleViews();

            ContainerService cm = mContext.getContainerService();
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
                            vb.ready();
                            if (vb.supportExposure()) {
                                mContext.getEventManager().emitEvent(EventManager.TYPE_Exposure, EventData.obtainData(mContext, vb));
                            }
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
            Log.e(TAG, "setData not array:" + data);
        }

    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemHeight:
                    mNative.setItemHeight(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemMargin:
                    mNative.setItemMargin(Utils.dp2px(value));
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
                case StringBase.STR_ID_orientation:
                    mNative.setOrientation(value);
                    break;

                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemHeight:
                    mNative.setItemHeight(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_itemMargin:
                    mNative.setItemMargin(Utils.dp2px(value));
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
                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.rp2px(value));
                    break;

                case StringBase.STR_ID_itemHeight:
                    mNative.setItemHeight(Utils.rp2px(value));
                    break;

                case StringBase.STR_ID_itemMargin:
                    mNative.setItemMargin(Utils.rp2px(value));
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
                case StringBase.STR_ID_itemWidth:
                    mNative.setItemWidth(Utils.rp2px(value));
                    break;

                case StringBase.STR_ID_itemHeight:
                    mNative.setItemHeight(Utils.rp2px(value));
                    break;

                case StringBase.STR_ID_itemMargin:
                    mNative.setItemMargin(Utils.rp2px(value));
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
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_itemWidth:
                mViewCache.put(this, StringBase.STR_ID_itemWidth, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_itemHeight:
                mViewCache.put(this, StringBase.STR_ID_itemHeight, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_itemMargin:
                mViewCache.put(this, StringBase.STR_ID_itemMargin, stringValue, Item.TYPE_FLOAT);
                break;
            default:
                ret = super.setAttribute(key, stringValue);
                break;
        }
        return ret;
    }

    public static class Builder implements IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new VH(context, viewCache);
        }
    }
}
