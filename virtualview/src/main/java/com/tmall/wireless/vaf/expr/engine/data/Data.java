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

package com.tmall.wireless.vaf.expr.engine.data;

import android.util.Log;

import static com.tmall.wireless.vaf.expr.engine.executor.ArithExecutor.TYPE_String;

/**
 * Created by gujicheng on 16/9/10.
 */
public class Data {
    private final static String TAG = "Data_TMTEST";

    public final static byte TYPE_NONE = 0;
    public final static byte TYPE_INT = 1;
    public final static byte TYPE_FLOAT = 2;
    public final static byte TYPE_STR = 3;
    public final static byte TYPE_OBJECT = 4;

    public Value mValue;
    public int mType;

    private static ValueCache sValueCache = ValueCache.getInstance();

    public Data() {
        reset();
    }

    public void reset() {
        mType = TYPE_NONE;
    }


    private void freeValue(int type, Value v) {
        if (null != v) {
            switch (type) {
                case TYPE_INT:
                    sValueCache.freeIntValue((IntValue) v);
                    break;

                case TYPE_FLOAT:
                    sValueCache.freeFloatValue((FloatValue) v);
                    break;

                case TYPE_STR:
                    sValueCache.freeStrValue((StrValue) v);
                    break;

                case TYPE_OBJECT:
                    sValueCache.freeObjValue((ObjValue) v);
                    break;
            }
        }
    }

    public boolean set(Object val) {
        boolean ret = true;

        if (val instanceof Integer) {
            setInt((Integer) val);
        } else if (val instanceof Float) {
            setFloat((Float) val);
        } else if (val instanceof String) {
            setString((String) val);
        } else {
            setObject(val);
        }

        return ret;
    }

    public int getInt() {
        if (TYPE_INT == mType) {
            return ((IntValue)mValue).mValue;
        }
        return 0;
    }

    public float getFloat() {
        if (TYPE_FLOAT == mType) {
            return ((FloatValue)mValue).mValue;
        }
        return 0;
    }

    public String getString() {
        if (TYPE_String == mType) {
            return ((StrValue)mValue).mValue;
        }
        return null;
    }

    public Object getObject() {
        if (TYPE_OBJECT == mType) {
            return ((ObjValue)mValue).mValue;
        }
        return null;
    }

    public void setInt(int value) {
        if (TYPE_INT != mType) {
            freeValue(mType, mValue);
            mType = TYPE_INT;
//            mValue = new IntValue(value);
            mValue = sValueCache.mallocIntValue(value);
        } else {
            ((IntValue) mValue).mValue = value;
        }
    }

    public void setFloat(float value) {
        if (TYPE_FLOAT != mType) {
            freeValue(mType, mValue);
            mType = TYPE_FLOAT;
//            mValue = new FloatValue(value);
            mValue = sValueCache.mallocFloatValue(value);

        } else {
            ((FloatValue) mValue).mValue = value;
        }
    }

    public void setString(String value) {
        if (TYPE_STR != mType) {
            freeValue(mType, mValue);
            mType = TYPE_STR;
//            mValue = new StrValue(str);
            mValue = sValueCache.mallocStrValue(value);
        } else {
            ((StrValue) mValue).mValue = value;
        }
    }

    public void setObject(Object value) {
        if (TYPE_OBJECT != mType) {
            freeValue(mType, mValue);
            mType = TYPE_OBJECT;
//            mValue = new ObjValue(obj);
            mValue = sValueCache.mallocObjValue(value);
        } else {
            ((ObjValue) mValue).mValue = value;
        }
    }

    public void copy(Data data) {
        if (null != data) {
            if (data.mType == mType) {
                mValue.copy(data.mValue);
            } else {
                mType = data.mType;
                mValue = data.mValue.clone();
            }
        } else {
            Log.e(TAG, "copy failed");
        }
    }

    @Override
    public String toString() {
        String ret = "type:none";

        switch (mType) {
            case TYPE_INT:
                ret = String.format("type:int value:" + mValue);
                break;
            case TYPE_FLOAT:
                ret = String.format("type:float value:" + mValue);
                break;
            case TYPE_STR:
                ret = String.format("type:string value:" + mValue);
                break;
            case TYPE_OBJECT:
                ret = String.format("type:object value:" + mValue);
                break;
        }

        return ret;
    }
}
