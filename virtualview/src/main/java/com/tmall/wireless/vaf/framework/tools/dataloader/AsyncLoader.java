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

package com.tmall.wireless.vaf.framework.tools.dataloader;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gujicheng on 16/10/19.
 */
public class AsyncLoader {
    private final static String TAG = "AsyncLoader_TMTEST";

    private HashSet<Integer> mTaskIds = new HashSet<>();
    private boolean mIsAddFinish;
    private int mId;
    private boolean mLoaderFailed;

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;
    private static final long KEEP_ALIVE = 10L;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncLoader#" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), sThreadFactory);

    private final static int MSG_MULTI_RESULT = 1;
    private final static int MSG_TASK_RESULT = 2;

    private Handler mMainHandler;

    private Listener mListener;
    private RunListener mRunListener;

    private boolean mIsDestroy = false;

    public interface Listener {
        void onAsyncLoaderFinished(boolean successful);
        void onBeforeLoader(int id);
        void onAfterLoader(int id, int result);
        void onAsyncLoaderFailed(int id, int result);
    }

    public interface RunListener {
        void onAsyncRunResult(int id, int result);
    }

    public interface TaskRunnable {
        int run();
    }

    public AsyncLoader() {
        init();

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!mIsDestroy) {
                    switch (msg.what) {
                        case MSG_MULTI_RESULT:
                            if (mTaskIds.remove(msg.arg1) ) {
                                if (msg.arg2 < 0) {
                                    mListener.onAsyncLoaderFailed(msg.arg1, msg.arg2);
                                    mLoaderFailed = true;
                                }

                                if (mIsAddFinish && 0 == mTaskIds.size()) {
                                    // all task finish
                                    if (null != mListener) {
                                        mListener.onAsyncLoaderFinished(!mLoaderFailed);
                                    } else {
                                        Log.e(TAG, "listener is null");
                                    }
                                }
                            } else {
                                Log.e(TAG, "remove task failed id:" + msg.arg1);
                            }
                            break;

                        case MSG_TASK_RESULT:
                            if (null != mRunListener) {
                                mRunListener.onAsyncRunResult(msg.arg1, msg.arg2);
                            }
                            break;

                        default:
                            Log.e(TAG, "can not deal msg:" + msg.what);
                    }
                }
            }
        };
    }

    public void setRunListener(RunListener lis) {
        mRunListener = lis;
    }

    public void setListener(Listener lis) {
        mListener = lis;
    }

    public void destroy() {
        mIsDestroy = true;
        mListener = null;
        mRunListener = null;
        mMainHandler.removeMessages(MSG_TASK_RESULT);
        mMainHandler.removeMessages(MSG_MULTI_RESULT);
    }

    public void init() {
        mTaskIds.clear();
        mIsAddFinish = false;
        mId = 0;
        mLoaderFailed = false;
    }

    public boolean runTask(int id, TaskRunnable r) {
        if (!mIsDestroy && null != r) {
            THREAD_POOL_EXECUTOR.execute(new ExeRunnable(r, id));
            return true;
        }
        return false;
    }

    // return task id
    public int addTask(TaskRunnable r) {
        int ret = -1;

        if (!mIsDestroy && null != r && !mIsAddFinish) {
            ret = mId++;
            mTaskIds.add(ret);
            THREAD_POOL_EXECUTOR.execute(new MyRunnable(r, ret));
        } else {
            Log.e(TAG, "r is null or add finished:" + mIsAddFinish);
        }

        return ret;
    }

    public void addFinish() {
        mIsAddFinish = true;
        if (0 == mTaskIds.size()) {
            if (null != mListener) {
                mListener.onAsyncLoaderFinished(!mLoaderFailed);
            } else {
                Log.e(TAG, "listener is null");
            }
        }
    }

    class ExeRunnable implements Runnable {
        private TaskRunnable mRealR;
        private int mId;

        public ExeRunnable(TaskRunnable r, int id) {
            mRealR = r;
            mId = id;
        }

        @Override
        public void run() {
            if (null != mRealR) {
                int ret = mRealR.run();
                Message m = mMainHandler.obtainMessage(MSG_TASK_RESULT, mId, ret);
                mMainHandler.sendMessage(m);
            }
        }
    }

    class MyRunnable implements Runnable {
        private TaskRunnable mRealR;
        private int mId;

        MyRunnable(TaskRunnable r, int id) {
            mRealR = r;
            mId = id;
        }

        @Override
        public void run() {
//            Log.d(TAG, "start run");
            if (null != mRealR && null != mListener) {

                mListener.onBeforeLoader(mId);

                int result = mRealR.run();

                mListener.onAfterLoader(mId, result);

                // run finish
                Message m = mMainHandler.obtainMessage(MSG_MULTI_RESULT, mId, result);
                mMainHandler.sendMessage(m);
            } else {
                Log.e(TAG, "real runnable is null or listener:" + mListener);
            }

            mRealR = null;
        }
    }
}
