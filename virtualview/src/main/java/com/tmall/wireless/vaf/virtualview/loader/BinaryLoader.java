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

package com.tmall.wireless.vaf.virtualview.loader;

import android.text.TextUtils;
import android.util.Log;

import com.libra.virtualview.common.Common;
import com.tmall.wireless.vaf.framework.VafContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by gujicheng on 16/8/4.
 */
public class BinaryLoader {
    static private final String TAG = "BinaryLoader_TMTEST";

    private StringLoader mStringLoader;
    private UiCodeLoader mUiCodeLoader;
    private ExprCodeLoader mExprCodeLoader;
    @Deprecated
    private int[] mDepPageIds;

    public BinaryLoader() {
    }

    public void destroy() {
        mStringLoader = null;
        mExprCodeLoader = null;
        mUiCodeLoader = null;
    }

    public void setPageContext(VafContext context) {
        mStringLoader = context.getStringLoader();
    }

    public void setExprCodeManager(ExprCodeLoader manager) {
        mExprCodeLoader = manager;
    }

    public void setUiCodeManager(UiCodeLoader manager) {
        mUiCodeLoader = manager;
    }

    public int loadFromFile(String path) {
        int ret = -1;
        try {
            FileInputStream fin = new FileInputStream(path);

            int length = fin.available();

            byte[] buf = new byte[length];
            fin.read(buf);

            ret = loadFromBuffer(buf);

            fin.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        }

        return ret;
    }

    // return pageId
    public int loadFromBuffer(byte[] buf) {
        return loadFromBuffer(buf, false);
    }

    public int loadFromBuffer(byte[] buf, boolean override) {
        int ret = -1;

        if (null != buf) {
            mDepPageIds = null;

            if (buf.length > 27) {
                // check tag
                int tagLen = Common.TAG.length();
                String tag = new String(buf, 0, tagLen);
                if (TextUtils.equals(Common.TAG, tag)) {
                    CodeReader reader = new CodeReader();

                    reader.setCode(buf);
                    reader.seekBy(tagLen);

                    // check version
                    int majorVersion = reader.readShort();
                    int minorVersion = reader.readShort();
                    int patchVersion = reader.readShort();
                    reader.setPatchVersion(patchVersion);
                    if ((Common.MAJOR_VERSION == majorVersion) && (Common.MINOR_VERSION == minorVersion)) {
                        int uiStartPos = reader.readInt();
                        reader.seekBy(4);

                        int strStartPos = reader.readInt();
                        reader.seekBy(4);

                        int exprCodeStartPos = reader.readInt();
                        reader.seekBy(4);

                        int extraStartPos = reader.readInt();
                        reader.seekBy(4);

                        int pageId = reader.readShort();

                        int depPageCount = reader.readShort();
                        if (depPageCount > 0) {
                            mDepPageIds = new int[depPageCount];
                            for (int i = 0; i < depPageCount; ++i) {
                                mDepPageIds[i] = reader.readShort();
                            }
                        }

                        if (reader.seek(uiStartPos)) {
                            // parse ui codes
                            boolean result = false;
                            if (!override) {
                                result = mUiCodeLoader.loadFromBuffer(reader, pageId, patchVersion);
                            } else {
                                result = mUiCodeLoader.forceLoadFromBuffer(reader, pageId, patchVersion);
                            }

                            // parse string
                            if (reader.getPos() == strStartPos) {
                                if (null != mStringLoader) {
                                    result = mStringLoader.loadFromBuffer(reader, pageId);
                                } else {
                                    Log.e(TAG, "mStringManager is null");
                                }
                            } else {
                                Log.e(TAG, "string pos error:" + strStartPos + "  read pos:" + reader.getPos());
                            }

                            // parse expr
                            if (reader.getPos() == exprCodeStartPos) {
                                if (null != mExprCodeLoader) {
                                    result = mExprCodeLoader.loadFromBuffer(reader, pageId);
                                } else {
                                    Log.e(TAG, "mExprCodeStore is null");
                                }
                            } else {
                                Log.e(TAG, "expr pos error:" + exprCodeStartPos + "  read pos:" + reader.getPos());
                            }

                            // load extra data
                            if (reader.getPos() == extraStartPos) {
                            } else {
                                Log.e(TAG, "extra pos error:" + extraStartPos + "  read pos:" + reader.getPos());
                            }

                            if (result) {
                                ret = pageId;
                            }
                        }
                    } else {
                        Log.e(TAG, "version dismatch");
                    }
                } else {
                    Log.e(TAG, "loadFromBuffer failed tag is invalidate:" + tag);
                }
            } else {
                Log.e(TAG, "file len invalidate:" + buf.length);
            }
        } else {
            Log.e(TAG, "buf is null");
        }
        return ret;
    }
}
