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

package com.tmall.wireless.virtualviewdemo.vs;

import android.util.Log;

import android.widget.Toast;
import com.libra.expr.common.ExprCode;
import com.libra.expr.common.StringSupport;
import com.libra.expr.compiler.ExprCompiler;
import com.tmall.wireless.vaf.expr.engine.DataManager;
import com.tmall.wireless.vaf.expr.engine.ExprEngine;
import com.tmall.wireless.vaf.expr.engine.NativeObjectManager;

/**
 * Created by gujicheng on 17/3/27.
 */

public class VSEngine {
    private final static String TAG = "VSEngine_TMTEST";

    private StringSupport mStringSupport = new StringSupportImp();
    private ExprCompiler mCompiler = new ExprCompiler();
    private ExprEngine mEngine = new ExprEngine();
    private NativeObjectManager mNativeObjectManager = new NativeObjectManager();

    public static String test() {
        Module module = new Module();
        VSEngine engine = new VSEngine();
        Context context = new Context();
        engine.getNativeObjectManager().registerObject("dl", module);
        DataManager dm = engine.getExprEngine().getEngineContext().getDataManager();
        dm.add("time", 10);
        dm.add("name", "hello");
        dm.add("id", 8);

        String vs = "this.name = (data.time > data.id) && (data.time >= 3)";
        engine.run(context, vs);

        Log.d(TAG, "test result:" + context.getName());
        return context.getName();
    }

    public VSEngine() {
        mNativeObjectManager.setStringManager(mStringSupport);
        mCompiler.setStringSupport(mStringSupport);
        mEngine.setStringSupport(mStringSupport);
        mEngine.setNativeObjectManager(mNativeObjectManager);
        mEngine.initFinished();
    }

    public ExprEngine getExprEngine() {
        return mEngine;
    }

    public NativeObjectManager getNativeObjectManager() {
        return mNativeObjectManager;
    }

    public void run(Object context, String str) {
        boolean ret = mCompiler.doCompile(str);
        if (ret) {
//            Log.d(TAG, "compile successful");
            ExprCode ec = mCompiler.getCode();

            ret = mEngine.execute(context, ec);
//            Log.d(TAG, "exe result:" + ret);
        } else {
            Log.e(TAG, "compile failed");
        }
    }
}
