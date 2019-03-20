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

import java.util.Set;

import android.util.Log;

import com.tmall.wireless.vaf.expr.engine.DataManager;
import com.tmall.wireless.vaf.expr.engine.data.Data;
import com.tmall.wireless.vaf.expr.engine.data.Value;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gujicheng on 17/3/28.
 */

public class ArrayExecutor extends ArithExecutor {
    private final static String TAG = "ArrayExecutor_TMTEST";

    @Override
    public int execute(Object com) {
        int ret = super.execute(com);

//        Log.d(TAG, "execute -------");
        // read fun name ids
        Set<Object> objs = findObject();
        if (null != objs) {
            int arrNameId = -1;
            if (mItemCount > 0) {
                arrNameId = mCodeReader.readInt();
//                Log.d(TAG, "execute ArrayNameId:" + mStringSupport.getString(arrNameId));
            }

            Value param = readParam();
            if (null != param) {
                int resultRegId = mCodeReader.readByte();
//                Log.d(TAG, "param:" + param + "  resultRegId:" + resultRegId);
                if (call(arrNameId, resultRegId, param, objs)) {
                    ret = RESULT_STATE_SUCCESSFUL;
                } else {
                    Log.e(TAG, "call array failed");
                }
            } else {
                Log.e(TAG, "param is null");
            }
        } else {
            Log.e(TAG, "execute findObject failed");
        }

        return ret;
    }

    protected boolean call(int arrNameId, int resultRegId, Value param, Set<Object> objs) {
        boolean ret = false;

        String arrName = mStringSupport.getString(arrNameId);
        Object p = param.getValue();
        if (p instanceof Integer) {
            ret = true;
            int index = (Integer) p;
            for (Object obj : objs) {
                Object returnValue = null;
                try {
                    if (obj instanceof DataManager) {
                        if (mDataManager.getData(arrName) instanceof JSONArray) {
                            JSONArray arr = (JSONArray) mDataManager.getData(arrName);
                            returnValue = arr.get(index);
                        } else if (mDataManager.getData(arrName) instanceof com.alibaba.fastjson.JSONArray) {
                            com.alibaba.fastjson.JSONArray arr = (com.alibaba.fastjson.JSONArray) mDataManager.getData(arrName);
                            returnValue = arr.get(index);
                        }
                    } else if (obj instanceof JSONObject) {
                        JSONArray arr = ((JSONObject) obj).optJSONArray(arrName);
                        returnValue = arr.get(index);
                    } else if (obj instanceof JSONArray) {
                        JSONArray arr = (JSONArray) obj;
                        returnValue = arr.get(index);
                    } else if (obj instanceof com.alibaba.fastjson.JSONArray) {
                        com.alibaba.fastjson.JSONArray arr = (com.alibaba.fastjson.JSONArray) obj;
                        returnValue = arr.get(index);
                    } else if (obj instanceof com.alibaba.fastjson.JSONObject) {
                        com.alibaba.fastjson.JSONArray arr = ((com.alibaba.fastjson.JSONObject) obj).getJSONArray(arrName);
                        returnValue = arr.get(index);
                    } else {
                        Log.e(TAG, "error object:" + obj);
                        ret = false;
                        break;
                    }

//                    Log.d(TAG, "returnValue:" + returnValue);
                    Data result = mRegisterManger.get(resultRegId);
                    if (null != returnValue) {
                        if (!result.set(returnValue)) {
                            Log.e(TAG, "call set return value failed:" + returnValue);
                        } else {
//                            Log.d(TAG, "call set return value ok:" + result);
                        }
                    } else {
                        result.reset();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "set value failed");
                    ret = false;
                }
            }
        } else {
            Log.e(TAG, "param not integer");
        }

        return ret;
    }

    protected Value readParam() {
        // read param
        Value ret;
//        Log.d(TAG, "readParam count:" + paramCount);
        int type = mCodeReader.readByte();
//            Log.d(TAG, "read param type:" + type);
        Data d = readData(type);
        if (null != d) {
//                Log.d(TAG, "readParam data:" + d);
            ret = d.mValue;
        } else {
            Log.e(TAG, "read param failed:" + type);
            ret = null;
        }

        return ret;
    }
}
