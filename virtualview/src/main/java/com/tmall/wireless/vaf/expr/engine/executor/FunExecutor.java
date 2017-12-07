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

package com.tmall.wireless.vaf.expr.engine.executor;

import android.util.Log;

import com.tmall.wireless.vaf.expr.engine.data.Data;
import com.tmall.wireless.vaf.expr.engine.data.Value;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by gujicheng on 16/10/18.
 */
public class FunExecutor extends ArithExecutor {
    private final static String TAG = "FunExecutor_TMTEST";

    @Override
    public int execute(Object com) {
        int ret = super.execute(com);

//        Log.d(TAG, "execute -------");
        // read fun name ids
        Set<Object> objs = findObject();
        if (null != objs) {
            int funNameId = mCodeReader.readInt();
//            Log.d(TAG, "execute funNameId:" + this.mAppContext.getStringLoader().getString(funNameId));

            Value[] params = readParam();
            if (null != params) {
                int resultRegId = mCodeReader.readByte();
                if (call(funNameId, resultRegId, params, objs)) {
                    ret = RESULT_STATE_SUCCESSFUL;
                }
            }
        } else {
            Log.e(TAG, "execute findObject failed");
        }

        return ret;
    }

    protected boolean call(int funNameId, int resultRegId, Value[] params, Set<Object> objs) {
        boolean ret = false;

        int size = params.length;
        Class[] xx = new Class[size];
        Object[] exeParams = new Object[size];
        for (int i = 0; i < size; ++i) {
            xx[i] = params[i].getValueClass();
            exeParams[i] = params[i].getValue();
        }

        String funName = mStringSupport.getString(funNameId);
        for (Object obj : objs) {
            try {
//                Log.d(TAG, "funName:" + funName);
                Method method = obj.getClass().getMethod(funName, xx);
                if (null != method) {
//                    Log.d(TAG, "call exe fun:" + obj + " param:" + exeParams.length + "  param v:" + exeParams[0]);
                    Object returnValue = method.invoke(obj, exeParams);
//                    Log.d(TAG, "returnValue:" + returnValue);
                    Data result = mRegisterManger.get(resultRegId);
                    if (null != returnValue) {
                        if (!result.set(returnValue)) {
                            Log.e(TAG, "call set return value failed:" + returnValue);
                        } else {
//                        Log.d(TAG, "call set return value ok:" + obj);
                        }
                    } else {
                        result.reset();
                    }
//                    Log.d(TAG, "returnValue:" + returnValue);
                    ret = true;
                } else {
                    Log.e(TAG, "get method failed:" + obj.getClass());
                }
            } catch (NoSuchMethodException e) {
//            e.printStackTrace();
                Log.e(TAG, "call get method failed:" + e + obj);
            } catch (InvocationTargetException e) {
//            e.printStackTrace();
//                Log.e(TAG, "call InvocationTargetException:" + e.getMessage() + obj);
            } catch (IllegalAccessException e) {
//            e.printStackTrace();
                Log.e(TAG, "call get method failed:" + e + obj);
            }
        }

        return ret;
    }

    protected Value[] readParam() {
        // read param
        int paramCount = mCodeReader.readByte();
        Value[] ret = new Value[paramCount];
//        Log.d(TAG, "readParam count:" + paramCount);
        for (int i = 0; i < paramCount; ++i) {
            int type = mCodeReader.readByte();
//            Log.d(TAG, "read param type:" + type);
            Data d = readData(type);
            if (null != d) {
//                Log.d(TAG, "readParam data:" + d);
                ret[i] = d.mValue;
            } else {
                Log.e(TAG, "read param failed:" + type);
                ret = null;
            }
        }

        return ret;
    }
}
