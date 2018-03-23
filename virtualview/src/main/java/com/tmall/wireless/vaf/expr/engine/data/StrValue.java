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

package com.tmall.wireless.vaf.expr.engine.data;

import android.util.Log;

/**
 * Created by gujicheng on 16/12/14.
 */

public class StrValue extends Value {
    private final static String TAG = "StrValue_TMTEST";
    public String mValue;

    public StrValue(String v) {
        mValue = v;
    }

    @Override
    public void copy(Value v) {
        if (null != v) {
            mValue = new String(((StrValue)v).mValue);
        } else {
            Log.e(TAG, "value is null");
        }
    }

    @Override
    public Value clone() {
        return sValueCache.mallocStrValue(mValue);
    }

    @Override
    public Class<?> getValueClass() {
        return String.class;
    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return "value type:string, value:" + mValue;
    }
}
