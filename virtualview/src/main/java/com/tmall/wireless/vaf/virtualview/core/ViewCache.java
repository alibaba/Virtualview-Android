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

package com.tmall.wireless.vaf.virtualview.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import com.libra.TextUtils;
import com.libra.Utils;
import com.libra.virtualview.common.TextBaseCommon;
import com.libra.virtualview.common.ViewBaseCommon;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by gujicheng on 16/12/6.
 */

public class ViewCache {

    private final static String TAG = "ViewCache_TMTEST";

    private List<ViewBase> mCacheView = new ArrayList<>();

    private ArrayMap<ViewBase, List<Item>> mCacheItem = new ArrayMap<>();

    private Object mComponentData;

    private View mHoldView;

    public static class Item {

        private static final Object EMPTY = new Object();

        public static final String FLAG_INVALIDATE = "_flag_invalidate_";

        protected final static String RP = "rp";

        private Parser mParser;

        public ViewBase mView;

        public int mKey;

        public String mAttrEL;

        private ArrayMap<Integer, Object> cacheTargetValue = new ArrayMap<>();

        private int mValueType = TYPE_INT;

        public static final int TYPE_INT = 0;

        public static final int TYPE_FLOAT = 1;

        public static final int TYPE_STRING = 2;

        public static final int TYPE_COLOR = 3;

        public static final int TYPE_BOOLEAN = 4;

        public static final int TYPE_VISIBILITY = 5;

        public static final int TYPE_GRAVITY = 6;

        public static final int TYPE_OBJECT = 7;

        public static final int TYPE_TEXT_STYLE = 8;

        public Item(ViewBase v) {
            this(v, 0);
        }

        public Item(ViewBase v, int p) {
            mView = v;
            mKey = p;
        }

        public Item(ViewBase v, int key, String attrEL, int valueType) {
            mView = v;
            mKey = key;
            mAttrEL = attrEL;
            mValueType = valueType;
            if (mAttrEL != null) {
                if (Utils.isThreeUnknown(attrEL)) {
                    mParser = new ThreeUnknownELParser();
                    mParser.compile(mAttrEL);
                } else {
                    mParser = new SimpleELParser();
                    mParser.compile(mAttrEL);
                }
            }
        }

        public void bind(Object jsonObject, boolean isAppend) {
            Object value = cacheTargetValue.get(jsonObject.hashCode());
            if (value == null) {
                value = mParser.getValueFromEL(jsonObject);
                if (value == null) {
                    value = EMPTY;
                } else {
                    String string = Utils.toString(value);
                    switch (mValueType) {
                        case TYPE_COLOR:
                            value = Utils.parseColor(string);
                            break;
                        case TYPE_VISIBILITY:
                            if ("invisible".equals(string)) {
                                value = Integer.valueOf(ViewBaseCommon.INVISIBLE);
                            } else if ("gone".equals(string)) {
                                value = Integer.valueOf(ViewBaseCommon.GONE);
                            } else {
                                value = Integer.valueOf(ViewBaseCommon.VISIBLE);
                            }
                            break;
                        case TYPE_GRAVITY:
                            int tempValue = 0;
                            String[] strArr = string.split("\\|");
                            for (String str : strArr) {
                                str = str.trim();
                                if (TextUtils.equals("left", str)) {
                                    tempValue |= ViewBaseCommon.LEFT;
                                } else if (TextUtils.equals("right", str)) {
                                    tempValue |= ViewBaseCommon.RIGHT;
                                } else if (TextUtils.equals("h_center", str)) {
                                    tempValue |= ViewBaseCommon.H_CENTER;
                                } else if (TextUtils.equals("top", str)) {
                                    tempValue |= ViewBaseCommon.TOP;
                                } else if (TextUtils.equals("bottom", str)) {
                                    tempValue |= ViewBaseCommon.BOTTOM;
                                } else if (TextUtils.equals("v_center", str)) {
                                    tempValue |= ViewBaseCommon.V_CENTER;
                                } else if (TextUtils.equals("center", str)) {
                                    tempValue |= ViewBaseCommon.H_CENTER;
                                    tempValue |= ViewBaseCommon.V_CENTER;
                                } else {
                                    break;
                                }
                            }
                            value = Integer.valueOf(tempValue);
                            break;
                        case TYPE_TEXT_STYLE:
                            int styleValue = 0;
                            String[] strs = string.split("\\|");
                            for (String str : strs) {
                                str = str.trim();
                                if (TextUtils.equals("bold", str)) {
                                    styleValue |= TextBaseCommon.BOLD;
                                } else if (TextUtils.equals("italic", str)) {
                                    styleValue |= TextBaseCommon.ITALIC;
                                } else if (TextUtils.equals("strike", str)) {
                                    styleValue |= TextBaseCommon.STRIKE;
                                }
                            }
                            value = Integer.valueOf(styleValue);
                            break;
                        default:
                            break;
                    }
                }
                cacheTargetValue.put(jsonObject.hashCode(), value);
            }
            if (value != EMPTY) {
                switch (mValueType) {
                    case TYPE_INT:
                        if (value instanceof Number) {
                            Integer integer = Utils.toInteger(value);
                            if (integer != null) {
                                mView.setAttribute(mKey, integer.intValue());
                            }
                        } else {
                            String stringValue = value.toString();
                            if (stringValue.endsWith(RP)) {
                                stringValue = stringValue.substring(0, stringValue.length() - 2);
                                Integer integer = Utils.toInteger(stringValue);
                                if (integer != null) {
                                    mView.setRPAttribute(mKey, integer.intValue());
                                }
                            } else {
                                Integer integer = Utils.toInteger(value);
                                if (integer != null) {
                                    mView.setAttribute(mKey, integer.intValue());
                                }
                            }
                        }
                        break;
                    case TYPE_COLOR:
                    case TYPE_GRAVITY:
                    case TYPE_VISIBILITY:
                    case TYPE_TEXT_STYLE:
                        Integer enumValue = Utils.toInteger(value);
                        if (enumValue != null) {
                            mView.setAttribute(mKey, enumValue.intValue());
                        }
                        break;
                    case TYPE_FLOAT:
                        if (value instanceof Number) {
                            Float floater = Utils.toFloat(value);
                            if (floater != null) {
                                mView.setAttribute(mKey, floater.floatValue());
                            }
                        } else {
                            String stringValue = value.toString();
                            if (stringValue.endsWith(RP)) {
                                stringValue = stringValue.substring(0, stringValue.length() - 2);
                                Float floater = Utils.toFloat(stringValue);
                                if (floater != null) {
                                    mView.setRPAttribute(mKey, floater.floatValue());
                                }
                            } else {
                                Float floater = Utils.toFloat(value);
                                if (floater != null) {
                                    mView.setAttribute(mKey, floater.floatValue());
                                }
                            }
                        }



                        break;
                    case TYPE_STRING:
                        mView.setAttribute(mKey, Utils.toString(value));
                        break;
                    case TYPE_BOOLEAN:
                        Boolean booleaner = Utils.toBoolean(value);
                        if (booleaner != null) {
                            mView.setAttribute(mKey, booleaner.booleanValue() ? 1 : 0);
                        } else {
                            mView.setAttribute(mKey, 0);
                        }
                        break;
                    case TYPE_OBJECT:
                        boolean ret = mView.setAttribute(mKey, value);
                        if (!ret) {
                            if (isAppend) {
                                mView.appendData(value);
                            } else {
                                mView.setData(value);
                            }
                        }
                        break;
                    default:
                        //Log.e(TAG, "can not set value to " + (mParser != null
                        //    ? mParser.getValue() : "") + " valueType " + mValueType);
                        break;
                }
            } else {
                //Log.e(TAG, "can not load value from " + (mParser != null
                //    ? mParser.getValue() : "") + " valueType " + mValueType);
            }
        }

        public void clear() {
            cacheTargetValue.clear();
        }

        public void invalidate(int hashCode) {
            cacheTargetValue.remove(hashCode);
        }

    }

    public void setHoldView(View v) {
        mHoldView = v;
    }

    public View getHolderView() {
        return mHoldView;
    }

    public void setComponentData(Object data) {
        mComponentData = data;
    }

    public Object getComponentData() {
        return mComponentData;
    }

    public List<ViewBase> getCacheView() {
        return mCacheView;
    }

    public void destroy() {
        if (mCacheView != null) {
            mCacheView.clear();
            mCacheView = null;
        }
        if (mCacheItem != null) {
            for (int i = 0, size = mCacheItem.size(); i < size; i++) {
                List<Item> items = mCacheItem.valueAt(i);
                if (items != null) {
                    for (int j = 0, length = items.size(); j < length; j++) {
                        Item item = items.get(j);
                        item.clear();
                    }
                }
            }
            mCacheItem.clear();
            mCacheItem = null;
        }
    }

    public void put(ViewBase v) {
        put(v, 0, null, Item.TYPE_INT);
    }

    public void put(ViewBase v, int key, String attrELIndex, int valueType) {
        List<Item> attrs = mCacheItem.get(v);
        if (null == attrs) {
            attrs = new ArrayList<>();
            mCacheItem.put(v, attrs);
            mCacheView.add(v);
        }

        attrs.add(new Item(v, key, attrELIndex, valueType));
    }

    public List<Item> getCacheItem(ViewBase viewBase) {
        return mCacheItem.get(viewBase);
    }

    private interface Parser {

        String getValue();

        boolean compile(String el);

        Object getValueFromEL(Object object);

    }

    private static class SimpleELParser implements Parser {

        private static final char DOLLAR = '$';
        private static final char LEFT_BRACE = '{';
        private static final char RIGHT_BRACE = '}';

        private static final int STATE_COMMON = 2;
        private static final int STATE_ARRAY_START = 3;
        private static final int STATE_ARRAY_END = 4;

        private static final char DOT = '.';
        private static final char ARRAY_START = '[';
        private static final char ARRAY_END = ']';

        private List<Object> exprFragment = new LinkedList<Object>();

        private String value;

        private int state;

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public boolean compile(String path) {
            if (path == null || path.length() == 0) {
                return false;
            }
            value = path;
            int length = path.length();
            exprFragment.clear();
            if (path.charAt(0) == DOLLAR && path.charAt(1) == LEFT_BRACE && path.charAt(length - 1) == RIGHT_BRACE) {
                StringBuilder sb = new StringBuilder();
                state = STATE_COMMON;
                for (int i = 2; i < length - 1; i++) {
                    char c = path.charAt(i);
                    switch (c) {
                        case DOT:
                            if (state == STATE_ARRAY_START) {
                                sb.append(c);
                                break;
                            } else if (state == STATE_ARRAY_END) {
                                state = STATE_COMMON;
                                break;
                            } else {
                                String value = sb.toString();
                                try {
                                    int intValue = Integer.parseInt(value);
                                    exprFragment.add(Integer.valueOf(intValue));
                                } catch (NumberFormatException e) {
                                    exprFragment.add(value);
                                }
                                sb.delete(0, sb.length());
                            }
                            break;
                        case ARRAY_START:
                            if (state == STATE_COMMON) {
                                if (sb.length() > 0) {
                                    String value = sb.toString();
                                    try {
                                        int intValue = Integer.parseInt(value);
                                        exprFragment.add(Integer.valueOf(intValue));
                                    } catch (NumberFormatException e) {
                                        exprFragment.add(value);
                                    }
                                    sb.delete(0, sb.length());
                                }
                                state = STATE_ARRAY_START;
                            } else {
                                //error
                                return false;
                            }
                            break;
                        case ARRAY_END:
                            if (state == STATE_ARRAY_START) {
                                String value = sb.toString();
                                try {
                                    int intValue = Integer.parseInt(value);
                                    exprFragment.add(Integer.valueOf(intValue));
                                } catch (NumberFormatException e) {
                                    exprFragment.add(value);
                                }
                                sb.delete(0, sb.length());
                                state = STATE_ARRAY_END;
                            } else {
                                //error
                                return false;
                            }
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
                if (state == STATE_COMMON) {
                    String value = sb.toString();
                    try {
                        int intValue = Integer.parseInt(value);
                        exprFragment.add(Integer.valueOf(intValue));
                    } catch (NumberFormatException e) {
                        exprFragment.add(value);
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getValueFromEL(Object jsonObject) {
            Object result = null;
            if (exprFragment.size() > 0) {
                if (jsonObject != null) {
                    for (int i = 0, size = exprFragment.size(); i < size; i++) {
                        Object node = exprFragment.get(i);
                        if (node instanceof String) {
                            String key = node.toString();
                            if (key.equalsIgnoreCase("this")) {
                                result = jsonObject;
                            } else {
                                if (jsonObject instanceof JSONObject) {
                                    result = ((JSONObject)jsonObject).opt(key);
                                } else {
                                    break;
                                }
                            }
                        } else if (node instanceof Integer) {
                            if (jsonObject instanceof JSONArray) {
                                result = ((JSONArray)jsonObject).opt(((Integer)node).intValue());
                            } else {
                                break;
                            }
                        }
                        jsonObject = result;
                    }
                }
            } else {
                result = value;
            }
            return result;
        }

    }

    private static class ThreeUnknownELParser implements Parser {

        private static final char AT = '@';
        private static final char QUESTION = '?';
        private static final char COLON = ':';

        private static final int STATE_CONDITION = 1;
        private static final int STATE_RESULT_1 = 2;
        private static final int STATE_RESULT_2 = 3;

        private static final char LEFT_BRACE = '{';
        private static final char RIGHT_BRACE = '}';

        private int state;

        private SimpleELParser condition;

        private SimpleELParser result1;

        private SimpleELParser result2;

        private String value;

        @Override
        public boolean compile(String el) {
            if (el == null || el.length() == 0) {
                return false;
            }
            value = el;
            int length = el.length();
            if (el.charAt(0) == AT && el.charAt(1) == LEFT_BRACE && el.charAt(length - 1) == RIGHT_BRACE) {
                StringBuilder sb = new StringBuilder();
                state = STATE_CONDITION;
                for (int i = 2; i < length - 1; i++) {
                    char c = el.charAt(i);
                    switch (c) {
                        case QUESTION:
                            if (state == STATE_CONDITION) {
                                condition = new SimpleELParser();
                                condition.compile(sb.toString().trim());
                                sb.delete(0, sb.length());
                                state = STATE_RESULT_1;
                            }
                            break;
                        case COLON:
                            if (state == STATE_RESULT_1) {
                                result1 = new SimpleELParser();
                                result1.compile(sb.toString().trim());
                                sb.delete(0, sb.length());
                                state = STATE_RESULT_2;
                            }
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
                if (state == STATE_RESULT_2) {
                    result2 = new SimpleELParser();
                    result2.compile(sb.toString().trim());
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public Object getValueFromEL(Object jsonObject) {
            Object result = null;
            if (condition != null && result1 != null && result2 != null) {
                if (jsonObject != null) {
                    Object conditionValue = condition.getValueFromEL(jsonObject);
                    boolean determine = true;
                    if (conditionValue == null) {
                        determine = false;
                    } else if (conditionValue instanceof Boolean) {
                        determine = ((Boolean)conditionValue).booleanValue();
                    } else if (conditionValue instanceof String) {
                        if (TextUtils.isEmpty((CharSequence)conditionValue)) {
                            determine = false;
                        } else {
                            if (TextUtils.equals((CharSequence)conditionValue, "null")) {
                                determine = false;
                            } else if (TextUtils.equals(((String)conditionValue).toLowerCase(), "false")) {
                                determine = false;
                            }
                        }
                    } else if (conditionValue instanceof JSONObject) {
                        if (((JSONObject)conditionValue).length() == 0) {
                            determine = false;
                        }
                    } else if (conditionValue instanceof JSONArray) {
                        if (((JSONArray)conditionValue).length() == 0) {
                            determine = false;
                        }
                    } else if (conditionValue.equals(JSONObject.NULL)) {
                        determine = false;
                    }
                    if (determine) {
                        result = result1.getValueFromEL(jsonObject);
                    } else {
                        result = result2.getValueFromEL(jsonObject);
                    }
                }
            } else {
                result = value;
            }
            return result;
        }

    }

}
