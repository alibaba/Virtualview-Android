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

package com.tmall.wireless.vaf.virtualview.loader;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import android.util.SparseIntArray;
import com.libra.expr.common.ExprCode;
import com.libra.virtualview.common.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujicheng on 16/9/12.
 */
public class ExprCodeLoader {
    private final static String TAG = "CodeManager_TMTEST";

    private List[] mCodeTab = new ArrayList[Common.MAX_TAB_SIZE];

    private ArrayMap<Integer, ExprCode> mCodeMap = new ArrayMap();

    public void reset() {
        mCodeTab = null;
    }

    public ExprCode get(int index) {
        return mCodeMap.get(index);
    }

    public boolean loadFromBuffer(CodeReader reader, int pageId) {
        boolean ret = true;

        //int tabIndex = pageId;
        //List<ExprCode> arr = mCodeTab[tabIndex];
        //if (null == arr) {
        //    arr = new ArrayList<>(30);
        //    mCodeTab[tabIndex] = arr;
        //}

        int totalSize = reader.getMaxSize();
        int count = reader.readInt();
        for (int k = 0; k < count; ++k) {
            int index = reader.readInt();
            int len = reader.readShort();
            int pos = reader.getPos();
            if (pos + len <= totalSize) {
                byte[] codes = reader.getCode();
                ExprCode c = new ExprCode(codes, pos, len);
                mCodeMap.put(index, c);
                reader.seekBy(len);
            } else {
                Log.e(TAG, "read string over");
                ret = false;
                break;
            }
        }

        return ret;
    }
}
