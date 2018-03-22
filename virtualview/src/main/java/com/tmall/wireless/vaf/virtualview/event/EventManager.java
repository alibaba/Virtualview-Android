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

package com.tmall.wireless.vaf.virtualview.event;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujicheng on 16/12/28.
 */

public class EventManager {
    private final static String TAG = "EventManager_TMTEST";

    public final static int TYPE_Click = 0;
    public final static int TYPE_Exposure = 1;
    public final static int TYPE_Load = 2;
    public final static int TYPE_FlipPage = 3;
    public final static int TYPE_LongCLick = 4;
    public final static int TYPE_Touch = 5;
    public final static int TYPE_COUNT = 6;

    private Object[] mProcessor = new Object[TYPE_COUNT];

    public void register(int type, IEventProcessor processor) {
        if (null != processor && type >= 0 && type < TYPE_COUNT) {
            List<IEventProcessor> pList = (List<IEventProcessor>)mProcessor[type];
            if (null == pList) {
                pList = new ArrayList<>();
                mProcessor[type] = pList;
            }

            pList.add(processor);
        } else {
            Log.e(TAG, "register failed type:" + type + "  processor:" + processor);
        }
    }

    public void unregister(int type, IEventProcessor processor) {
        if (null != processor && type >= 0 && type < TYPE_COUNT) {
            List<IEventProcessor> pList = (List<IEventProcessor>)mProcessor[type];
            if (null != pList) {
                pList.remove(processor);
            }
        } else {
            Log.e(TAG, "unregister failed type:" + type + "  processor:" + processor);
        }
    }

    public boolean emitEvent(int type, EventData data) {
        boolean ret = false;
        if (type >= 0 & type < TYPE_COUNT) {
            List<IEventProcessor> pList = (List<IEventProcessor>)mProcessor[type];
            if (null != pList) {
                for (int i = 0, size = pList.size(); i < size; i++) {
                    IEventProcessor p = pList.get(i);
                    ret = p.process(data);
                }
            }
        }

        if (null != data) {
            data.recycle();
        }

        return ret;
    }
}
