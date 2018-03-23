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

package com.tmall.wireless.vaf.expr.engine;

import android.util.Log;

import com.libra.expr.common.ExprCode;
import com.libra.expr.common.StringSupport;
import com.tmall.wireless.vaf.expr.engine.executor.AddEqExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.AddExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.AndExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.ArrayExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.DivEqExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.DivExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.EqEqExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.EqualExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.Executor;
import com.tmall.wireless.vaf.expr.engine.executor.FunExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.GEExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.GTExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.JmpExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.JmpcExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.LEExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.LTExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.MinusExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.ModEqExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.ModExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.MulEqExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.MulExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.NotEqExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.NotExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.OrExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.SubEqExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.SubExecutor;
import com.tmall.wireless.vaf.expr.engine.executor.TerExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujicheng on 16/9/8.
 */
public class ExprEngine {
    private final static String TAG = "ExprEngine_TMTEST";

    private List<Executor> mExecutors = new ArrayList<>();
    private int mInstructionCount;
//    private CodeReader mCodeReader;
//    private RegisterManager mRegisterManager = new RegisterManager();
//    private DataManager mDataManager = new DataManager();

    private EngineContext mEngineContext = new EngineContext();

    public ExprEngine() {
//        mCodeReader = new CodeReader();

        mExecutors.add(new AddExecutor());
        mExecutors.add(new SubExecutor());
        mExecutors.add(new MulExecutor());
        mExecutors.add(new DivExecutor());
        mExecutors.add(new ModExecutor());
        mExecutors.add(new EqualExecutor());
        mExecutors.add(new TerExecutor());
        mExecutors.add(new MinusExecutor());
        mExecutors.add(new NotExecutor());
        mExecutors.add(new GTExecutor());
        mExecutors.add(new LTExecutor());
        mExecutors.add(new NotEqExecutor());
        mExecutors.add(new EqEqExecutor());
        mExecutors.add(new GEExecutor());
        mExecutors.add(new LEExecutor());
        mExecutors.add(new FunExecutor());
        mExecutors.add(new AddEqExecutor());
        mExecutors.add(new SubEqExecutor());
        mExecutors.add(new MulEqExecutor());
        mExecutors.add(new DivEqExecutor());
        mExecutors.add(new ModEqExecutor());
        mExecutors.add(new JmpExecutor());
        mExecutors.add(new JmpcExecutor());
        mExecutors.add(new AndExecutor());
        mExecutors.add(new OrExecutor());
		mExecutors.add(new ArrayExecutor());
        mInstructionCount = mExecutors.size();
    }

    public EngineContext getEngineContext() {
        return mEngineContext;
    }

    public void destroy() {
        for (Executor executor : mExecutors) {
            executor.destroy();
        }
        mExecutors.clear();

        mEngineContext.destroy();
//        mCodeReader = null;
//
//        mRegisterManager.destroy();
//        mRegisterManager = null;
    }

//    public DataManager getDataManager() {
//        return mDataManager;
//    }

    public void initFinished() {
        for (Executor executor : mExecutors) {
            executor.setEngineContext(mEngineContext);
        }
    }

    public void setNativeObjectManager(NativeObjectManager nom) {
        mEngineContext.setNativeObjectManager(nom);
//        for (Executor executor : mExecutors) {
//            executor.setNativeObjectManager(nom);
//        }
    }

    public void setStringSupport(StringSupport ss) {
        mEngineContext.setStringSupport(ss);
//        for (Executor executor : mExecutors) {
//            executor.setStringSupport(ss);
//        }
    }

    public boolean execute(Object obj, ExprCode code) {
        boolean ret = false;

        CodeReader cr = mEngineContext.getCodeReader();
        if (null != code) {
            cr.setCode(code);

//            Log.d(TAG, "execute code:" + code);
            int exeRlt = Executor.RESULT_STATE_ERROR;
            do {
                byte opCode = cr.readByte();
//                Log.d(TAG, "opCode:" + opCode + "  curPos:" + mCodeReader.curPos());
                if (opCode > -1 && opCode < mInstructionCount) {
                    Executor executor = mExecutors.get(opCode);
//                    Log.d(TAG, "init:");
                    executor.init();
//                    Log.d(TAG, "exe:");
                    exeRlt = executor.execute(obj);
//                    Log.d(TAG, "exe end:" + exeRlt);
                } else {
                    Log.e(TAG, "operator code error:" + opCode);
                    break;
                }
//                Log.d(TAG, "isEndOfCode:" + mCodeReader.isEndOfCode());
            } while (Executor.RESULT_STATE_SUCCESSFUL == exeRlt && !cr.isEndOfCode());

//            Log.d(TAG, "end execute");
            if (Executor.RESULT_STATE_SUCCESSFUL == exeRlt) {
                ret = true;
            }
        }

        return ret;
    }

}
