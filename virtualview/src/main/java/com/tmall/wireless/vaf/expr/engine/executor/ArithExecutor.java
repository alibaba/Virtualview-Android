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

import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.expr.engine.data.Data;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gujicheng on 16/9/10.
 */
public abstract class ArithExecutor extends Executor {
    private static final String TAG = "ArithExecutor_TMTEST";

    public static final byte TYPE_None = -1;
    public static final byte TYPE_Var = 0;
    public static final byte TYPE_Int = 1;
    public static final byte TYPE_Float = 2;
    public static final byte TYPE_String = 3;
    public static final byte TYPE_Register = 4;

    protected int mItemCount;
    protected int mAriResultRegIndex;
    protected Set<Object> mTempObjs;

    @Override
    public void init() {
        super.init();

        mAriResultRegIndex = 256;
    }

    private void ensureTempObjs() {
        if (mTempObjs == null) {
            mTempObjs = new HashSet<>(10);
        }
    }

    protected Data readData(int type) {
        Data ret = new Data();

        switch (type) {
            case TYPE_Var:
                if (!readVar(ret)) {
                    ret = null;
                }
                break;

            case TYPE_Int:
                ret.setInt(mCodeReader.readInt());
                break;

            case TYPE_Float:
                ret.setFloat(Float.intBitsToFloat(mCodeReader.readInt()));
                break;

            case TYPE_String:
                ret.setString(mStringSupport.getString(mCodeReader.readInt()));
                break;

            case TYPE_Register:
                if (!readRegister(ret)) {
                    ret = null;
                }
                break;

            default:
                Log.e(TAG, "can not read this type:" + type);
                ret = null;
                break;
        }

        return ret;
    }

    protected Set<Object> findObject() {
        ensureTempObjs();
        boolean successful = false;

        Set<Object> ret = new HashSet<>(10);
//        mObjs.clear();
        mTempObjs.clear();

        int registerId = mCodeReader.readInt();
//        Log.d(TAG, "findObject registerId:" + registerId);
        if (registerId > -1) {
//            Log.d(TAG, "read obj from register id:" + registerId);
            Data d = mRegisterManger.get(registerId);
            if (null != d) {
                if (Data.TYPE_OBJECT == d.mType) {
//                Log.d(TAG, "object:" + d.mValue);
                    ret.add(d.getObject());
                } else {
                    Log.e(TAG, "read obj from register failed obj:" + d);
//                return ret;
                }
            } else {
                Log.e(TAG, "read obj from register failed  registerId:" + registerId);
            }
        }

        int count = mCodeReader.readByte();
        mItemCount = count;
//        Log.d(TAG, "findObject count:" + count + "  registerId:" + registerId);
        if (count >= 1) {
//            NativeObjectManager cm = mAppContext.getNativeObjectManager();
//            StringLoader sm = mAppContext.getStringLoader();
            if (ret.size() > 0) {
            } else {
                // find first
                int id = mCodeReader.readInt();
                if (StringBase.STR_ID_module == id) {
                    // module
                    if (3 == count) {
                        int moduleNameId = mCodeReader.readInt();
                        String moduleName = mStringSupport.getString(moduleNameId);
                        Object obj = mNativeObjectManager.getModule(moduleName);
                        if (null != obj) {
                            ret.add(obj);
                            successful = true;
                        } else {
                            Log.e(TAG, "findObject can not find module:" + moduleName);
                        }
                    } else {
                        Log.e(TAG, "findObject count invalidate:" + count);
                    }
                    return ret;
                } else if (StringBase.STR_ID_data == id) {
                    ret.add(mDataManager);
                    return ret;
                } else {
                    // component
                    if (StringBase.STR_ID_this == id) {
                        ret.add(mCom);
                    } else if (!mStringSupport.isSysString(id)) {
                        Object cur = null;
                        if (registerId > -1) {
                        } else {
                            cur = mNativeObjectManager.findCom(id);
                        }

                        if (null == cur) {
                            Log.e(TAG, "findObject can not find com id:" + id);
                        } else {
                            ret.add(cur);
                        }
                    } else {
                        Log.e(TAG, "findObject first token invalidate id:" + id);
                    }
                }
            }

            if (ret.size() > 0) {
                successful = true;

                for (int i = 0; i < count - 2; ++i) {
                    int id = mCodeReader.readInt();
                    if (mStringSupport.isSysString(id)) {
                        // is
                        switch (id) {
                            case StringBase.STR_ID_this:
                                // skip
                                break;

                            case StringBase.STR_ID_parent:
                                mTempObjs.clear();

                                for (Object v : ret) {
                                    if (v instanceof ViewBase) {
                                        ViewBase p = ((ViewBase) v).getParent();
                                        if (null != p) {
                                            mTempObjs.add(p);
                                        }
                                    } else {
                                        Log.w(TAG, "warning");
                                    }
                                }

                                // swap
                                ret.clear();
                                ret.addAll(mTempObjs);
//                                swapObjArr();
                                break;

                            case StringBase.STR_ID_ancestor:
                                mTempObjs.clear();

                                for (Object v : ret) {
                                    if (v instanceof ViewBase) {
                                        ViewBase p = ((ViewBase) v).getParent();
                                        while (null != p) {
                                            mTempObjs.add(p);
                                            p = p.getParent();
                                        }
                                    }
                                }

                                // swap
                                ret.clear();
                                ret.addAll(mTempObjs);
                                break;

                            case StringBase.STR_ID_siblings:
                                mTempObjs.clear();
                                for (Object v : ret) {
                                    if (v instanceof ViewBase) {
                                        ViewBase p = ((ViewBase) v).getParent();
                                        if (null != p && p instanceof Layout) {
                                            List<ViewBase> subs = ((Layout)p).getSubViews();
                                            mTempObjs.addAll(subs);

                                            // remove self
                                            mTempObjs.remove(v);
                                        }
                                    }
                                }

                                // swap
                                ret.clear();
                                ret.addAll(mTempObjs);
                                break;

                            case StringBase.STR_ID_children:
                                break;

                            default:
                                successful = false;
                                Log.e(TAG, "findObject invalidate system id:" + id);
                        }
                    } else {
                        mTempObjs.clear();

                        for (Object v : ret) {
                            Object obj = ((ViewBase) v).findViewBaseByName(mStringSupport.getString(id));
                            if (null != obj) {
                                mTempObjs.add(obj);
                            } else {
                                Log.e(TAG, "can not find obj:" + mStringSupport.getString(id));
                            }
                        }

                        // swap
                        ret.clear();
                        ret.addAll(mTempObjs);
                    }

                    if (!successful) {
                        break;
                    }
                }
            }
        } else {
            Log.e(TAG, "findObject count invalidate:" + count);
        }

        if (!successful) {
            ret = null;
        }
        return ret;
    }

    private void swapObjArr(Set<Object> objs) {
        ensureTempObjs();
        Set<Object> t = objs;
        objs = mTempObjs;
        mTempObjs = t;
    }

    protected boolean readVar(Data data) {
        boolean ret = false;

        Set<Object> objs = findObject();
        if (null != objs) {
            ret = true;
            int propertyNameId = mCodeReader.readInt();
//            Log.d(TAG, "readVar name:" + mAppContext.getStringLoader().getString(propertyNameId));
//            NativeObjectManager nm = mAppContext.getNativeObjectManager();
            for (Object obj : objs) {
                if (obj == mDataManager) {
                    Class[] xx = new Class[1];
                    Object[] exeParams = new Object[1];
                    xx[0] = String.class;
                    exeParams[0] = mStringSupport.getString(propertyNameId);
//                    Log.d(TAG, "getData exeParams:" + exeParams[0]);
                    try {
                        Method method = obj.getClass().getMethod("getData", xx);
                        obj = method.invoke(obj, exeParams);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        Log.e(TAG, "getData NoSuchMethodException:");
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        Log.e(TAG, "getData InvocationTargetException:");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Log.e(TAG, "getData IllegalAccessException:");
                    }
//                    Log.d(TAG, "getData result:" + obj + obj.getClass());

                } else {
                    obj = mNativeObjectManager.getPropertyImp(obj, propertyNameId);
//                Log.d(TAG, "readVar value:" + obj);
                }

                if (null != obj) {
                    ret = true;
                    if (obj instanceof Integer) {
                        data.setInt((Integer) obj);
                    } else if (obj instanceof Float) {
                        data.setFloat((Float) obj);
                    } else if (obj instanceof String) {
                        data.setString((String) obj);
                    } else {
                        data.setObject(obj);
//                        ret = false;
//                        Log.e(TAG, "invalidate obj type:" + obj);
                    }
                    break;
                } else {
                    Log.e(TAG, "getProperty failed");
                }
            }
        }

        return ret;
    }

    private boolean readRegister(Data data) {
        boolean ret = false;

        int regId = mCodeReader.readInt();
        if (regId < mAriResultRegIndex) {
            mAriResultRegIndex = regId;
        }

        Data d = mRegisterManger.get(regId);
        if (null != d) {
            data.copy(d);
            ret = true;
        }

        return ret;
    }
}
