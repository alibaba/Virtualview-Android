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

package com.tmall.wireless.vaf.virtualview.Helper;

import com.libra.virtualview.common.Common;
import com.libra.virtualview.common.DataItem;
import com.tmall.wireless.vaf.virtualview.loader.StringLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gujicheng on 16/12/26.
 */

public class DataOpt {
    private static StringLoader sStringLoader;

    public static void setStringLoader(StringLoader sl) {
        sStringLoader = sl;
    }

    private static void doFilterSub(JSONArray vData, boolean style) {
        if (null != vData) {
            int size = vData.length();
            for (int i = 0; i < size; ++i) {
                JSONObject jObj = vData.optJSONObject(i);
                if (null != jObj) {
                    String tag = jObj.optString("tag");
                    if (null != tag) {
                        int id = sStringLoader.getStringId(tag, false);
                        jObj.remove("tag");
                        try {
                            jObj.put("tag", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    String key = jObj.optString("key");
                    if (null != key) {
                        int id = sStringLoader.getStringId(key, false);
                        jObj.remove("key");
                        try {
                            jObj.put("key", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Object v = jObj.opt("value");
                    if (null != v) {
                        if (v instanceof JSONArray || v instanceof JSONObject) {
                            filter(v);
                        } else if (v instanceof String) {
                            String str = (String) v;
                            DataItem item = new DataItem(str);
                            if (Common.parseColor(item)) {
                                jObj.remove("value");
                                try {
                                    jObj.put("value", item.mIntValue);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void doFilter(JSONObject data) {
        if (null != data) {
            doFilterSub(data.optJSONArray("vData"), false);
            doFilterSub(data.optJSONArray("vStyle"), true);
        }
    }

    public static void filter(Object obj) {
        JSONArray vData = null;
        if (obj instanceof JSONObject) {
            vData = ((JSONObject) obj).optJSONArray("data");
        } else if (obj instanceof JSONArray) {
            vData = (JSONArray) obj;
        }

        if (null != vData) {
            int size = vData.length();
            for (int i = 0; i < size; ++i) {
                JSONObject jObj = vData.optJSONObject(i);
                doFilter(jObj);
            }
        }
    }

}
