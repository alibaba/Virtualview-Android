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

package com.tmall.wireless.vaf.framework.tools;

import android.util.Log;

/**
 * Created by gujicheng on 16/11/25.
 */

public class FastIntegerHash {
    private final static String TAG = "FastIntegerHash_TMTEST";

    public final static int DEFAULT_MAX_SLOT_COUNT = 20;

    public final static int DEFAULT_MAX_ITEM_COUNT = 1024;

    private int mMaxSlotCount;
    private int mMaxItemCount;
    private Object[] mSlot;

    public FastIntegerHash() {
        this(DEFAULT_MAX_SLOT_COUNT, DEFAULT_MAX_ITEM_COUNT);
    }

    public FastIntegerHash(int maxSlotCount, int maxItemCount) {
        mMaxSlotCount = maxSlotCount;
        mMaxItemCount = maxItemCount;

        mSlot = new Object[mMaxSlotCount];
    }

    public Object[] getSlot() {
        return mSlot;
    }

    public void clear() {
        for (int i = 0; i < mSlot.length; ++i) {
            mSlot[i] = null;
        }

        mSlot = null;
    }

    public void addSlot(int slotIndex, int maxCount) {
        if (slotIndex >= 0 && slotIndex < mMaxSlotCount && maxCount > 0) {
            mSlot[slotIndex] = new Object[maxCount];
        } else {
            Log.e(TAG, "addSlot failed slotIndex:" + slotIndex + "  maxCount:" + maxCount);
        }
    }

    public boolean put(int k, Object v) {
        boolean ret = true;

        int slotIndex = k / mMaxItemCount;
        int itemIndex = k % mMaxItemCount;

        if (slotIndex >= 0 && slotIndex < mMaxSlotCount) {
            Object[] item = (Object[])mSlot[slotIndex];
            if (null != item) {
                item[itemIndex] = v;
//                Log.d(TAG, "put ok");
            } else {
                ret = false;
                Log.e(TAG, "put failed1 slotIndex:" + slotIndex + "  itemIndex:" + itemIndex);
            }
        } else {
            ret = false;
            Log.e(TAG, "put failed2 slotIndex:" + slotIndex + "  itemIndex:" + itemIndex);
        }

        return ret;
    }

    public Object get(int k) {
        Object ret = null;

        int slotIndex = k / mMaxItemCount;
        int itemIndex = k % mMaxItemCount;

        if (slotIndex >= 0 && slotIndex < mMaxSlotCount) {
            Object[] item = (Object[])mSlot[slotIndex];
            if (null != item && itemIndex < item.length) {
                ret = item[itemIndex];
//                Log.d(TAG, "get ok");
            } else {
                Log.e(TAG, "get failed k:" + k);
            }
        } else {
            Log.e(TAG, "get failed k:" + k);
        }

        return ret;
    }
}
