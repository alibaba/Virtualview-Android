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

package com.tmall.wireless.vaf.virtualview.event;

import android.app.Activity;

import android.view.MotionEvent;
import android.view.View;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gujicheng on 16/12/28.
 */

public class EventData {
    protected static List<EventData> sCache = new LinkedList<>();

    public ViewBase mVB;
    public Activity mActivity;
    public VafContext mContext;
    /**
     * only not null in touch event
     */
    public View mView;
    /**
     * only not null in touch event
     */
    public MotionEvent mMotionEvent;

    public HashMap<String, Object> paramMap;

    public static void clear() {
        sCache.clear();
    }

    public EventData(VafContext context, ViewBase vb) {
        mContext = context;
        mActivity = context.getCurActivity();
        mVB = vb;
        paramMap = new HashMap<>();
    }

    public EventData(VafContext context, ViewBase vb, View view, MotionEvent motionEvent) {
        mContext = context;
        mActivity = context.getCurActivity();
        mVB = vb;
        mView = view;
        mMotionEvent = motionEvent;
        paramMap = new HashMap<>();
    }

    public void recycle() {
        recycleData(this);
        mVB = null;
        mActivity = null;
        mContext = null;
        mView = null;
        mMotionEvent = null;
    }

    public static EventData obtainData(VafContext context, ViewBase vb) {
        View view = null;
        if (vb != null) {
            view = vb.getNativeView();

            if (view == null  && vb.getViewCache() != null) {
                view = vb.getViewCache().getHolderView();
            }
        }
        return obtainData(context, vb, view, null);
    }

    public static EventData obtainData(VafContext context, ViewBase vb, View view, MotionEvent motionEvent) {
        EventData ret;
        if (sCache.size() > 0) {
            ret = sCache.remove(0);

            ret.mVB = vb;
            ret.mView = view;
            ret.mContext = context;
            ret.mActivity = context.getCurActivity();
        } else {
            ret = new EventData(context, vb, view, motionEvent);
        }
        return ret;
    }

    protected static void recycleData(EventData data) {
        if (null != data) {
            sCache.add(data);
        }
    }
}
