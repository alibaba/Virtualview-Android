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

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gujicheng on 16/12/14.
 */

public class ValueCache {
    private static ValueCache sThis = null;

    private List<IntValue> mIntCache = new LinkedList<>();
    private List<FloatValue> mFloatCache = new LinkedList<>();
    private List<StrValue> mStrCache = new LinkedList<>();
    private List<ObjValue> mObjCache = new LinkedList<>();

    public static ValueCache getInstance() {
        if (null == sThis) {
            sThis = new ValueCache();
        }

        return sThis;
    }

    private ValueCache() {
    }

    public IntValue mallocIntValue(int v) {
        if (mIntCache.size() > 0) {
            IntValue iv = mIntCache.remove(0);
            iv.mValue = v;
            return iv;
        } else {
            return new IntValue(v);
        }
    }

    public void freeIntValue(IntValue v) {
        mIntCache.add(v);
    }

    public FloatValue mallocFloatValue(float v) {
        if (mFloatCache.size() > 0) {
            FloatValue iv = mFloatCache.remove(0);
            iv.mValue = v;
            return iv;
        } else {
            return new FloatValue(v);
        }
    }

    public void freeFloatValue(FloatValue v) {
        mFloatCache.add(v);
    }

    public StrValue mallocStrValue(String v) {
        if (mStrCache.size() > 0) {
            StrValue iv = mStrCache.remove(0);
            iv.mValue = v;
            return iv;
        } else {
            return new StrValue(v);
        }
    }

    public void freeStrValue(StrValue v) {
        mStrCache.add(v);
    }

    public ObjValue mallocObjValue(Object v) {
        if (mObjCache.size() > 0) {
            ObjValue iv = mObjCache.remove(0);
            iv.mValue = v;
            return iv;
        } else {
            return new ObjValue(v);
        }
    }

    public void freeObjValue(ObjValue v) {
        mObjCache.add(v);
    }
}

