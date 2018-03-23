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

package com.tmall.wireless.vaf.expr.engine.executor;

import android.util.Log;

import com.tmall.wireless.vaf.expr.engine.data.Data;

/**
 * Created by gujicheng on 16/10/24.
 */
public class CompositeEqExecutor extends EqualExecutor {
    private final static String TAG = "ComEqExecutor_TMTEST";

    protected void calcIntInt(Data result, int l, int r) {
    }

    protected void calcIntFloat(Data result, int l, float r) {

    }

    protected void calcIntString(Data result, int l, String r) {

    }

    protected void calcFloatInt(Data result, float l, int r) {

    }

    protected void calcFloatFloat(Data result, float l, float r) {

    }

    protected void calcFloatString(Data result, float l, String r) {

    }

    protected void calcStringInt(Data result, String l, int r) {

    }

    protected void calcStringFloat(Data result, String l, float r) {

    }

    protected void calcStringString(Data result, String l, String r) {

    }

    @Override
    protected int eqDeal(Data resultReg, Data data2) {
        int ret = RESULT_STATE_ERROR;

//        Log.d(TAG, "eqDeal obj count" + mMyObjs.size());
//        NativeObjectManager cm = mAppContext.getNativeObjectManager();
        if (mMyObjs.size() > 0) {
            for (Object obj : mMyObjs) {
                Object result = mNativeObjectManager.getPropertyImp(obj, mPropertyNameId);
                if (null != result) {
                    Data o = new Data();
                    if (result instanceof Integer) {
                        switch (data2.mType) {
                            case Data.TYPE_INT:
                                calcIntInt(o, (Integer)result, data2.getInt());
                                break;
                            case Data.TYPE_FLOAT:
                                calcIntFloat(o, (Integer)result, data2.getFloat());
                                break;
                            case Data.TYPE_STR:
                                calcIntString(o, (Integer)result, data2.getString());
                                break;
                        }
                    } else if (result instanceof Float) {
                        switch (data2.mType) {
                            case Data.TYPE_INT:
                                calcFloatInt(o, (Float)result, data2.getInt());
                                break;
                            case Data.TYPE_FLOAT:
                                calcFloatFloat(o, (Float)result, data2.getFloat());
                                break;
                            case Data.TYPE_STR:
                                calcFloatString(o, (Float)result, data2.getString());
                                break;
                        }
                    } else if (result instanceof String) {
                        switch (data2.mType) {
                            case Data.TYPE_INT:
                                calcStringInt(o, (String)result, data2.getInt());
                                break;
                            case Data.TYPE_FLOAT:
                                calcStringFloat(o, (String)result, data2.getFloat());
                                break;
                            case Data.TYPE_STR:
                                calcStringString(o, (String)result, data2.getString());
                                break;
                        }
                    } else {
                        Log.e(TAG, "var type invalidate:" + result);
                    }

                    if (null != o) {
//                        Log.d(TAG, "set value:" + o);
                        mNativeObjectManager.setPropertyImp(obj, mPropertyNameId, o);
                    } else {
                        Log.e(TAG, "calc failed");
                    }
                } else {
                    Log.e(TAG, "result value is empty:" + result);
                }
            }
            ret = RESULT_STATE_SUCCESSFUL;
        } else {
            Log.e(TAG, "obj is empty");
        }

        return ret;
    }
}
