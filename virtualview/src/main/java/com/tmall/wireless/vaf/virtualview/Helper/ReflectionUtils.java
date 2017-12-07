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

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by gujicheng on 16/9/13.
 */
public class ReflectionUtils {
    private final static String TAG = "ReflectionUtils_TMTEST";

    public static Method getDeclaredMethod(Object object, String methodName, Class<?> ... parameterTypes){
        Method method = null ;

        Class<?> clazz = object.getClass();
        Log.d(TAG, "begin");
        do {
            try {
                Log.d(TAG, "getDeclaredMethod clazz:" + clazz + "  types:" + parameterTypes);
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                break;
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        } while(null == method && null != clazz);
        Log.d(TAG, "end");
//        for(Class<?> clazz = object.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
//            try {
//                method = clazz.getDeclaredMethod(methodName, parameterTypes) ;
//                break;
//            } catch (Exception e) {
//            }
//        }

        if (null == method) {
            Log.e(TAG, "getDeclaredMethod no such method:");
        }

        return method;
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     * @param object : 子类对象
     * @param methodName : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @param parameters : 父类中的方法参数
     * @return 父类中方法的执行结果
     */

    public static Object invokeMethod(Object object, String methodName, Class<?> [] parameterTypes,
                                      Object [] parameters) {
        //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
        Method method = getDeclaredMethod(object, methodName, parameterTypes) ;

        //抑制Java对方法进行检查,主要是针对私有方法而言
        method.setAccessible(true) ;

        try {
            if(null != method) {

                //调用object 的 method 所代表的方法，其方法的参数是 parameters
                return method.invoke(object, parameters) ;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "invokeMethod failed:" + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "invokeMethod failed:" + e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.e(TAG, "invokeMethod failed:" + e);
        }

        return null;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */

    public static Field getDeclaredField(Object object, String fieldName){
        Field field = null ;

        Class<?> clazz = object.getClass() ;

        for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName) ;
                return field ;
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了

            }
        }

        return null;
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value : 将要设置的值
     */

    public static void setFieldValue(Object object, String fieldName, Object value){

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName) ;

        //抑制Java对其的检查
        field.setAccessible(true) ;

        try {
            //将 object 中 field 所代表的值 设置为 value
            field.set(object, value) ;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
     */

    public static Object getFieldValue(Object object, String fieldName){

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName) ;

        //抑制Java对其的检查
        field.setAccessible(true) ;

        try {
            //获取 object 中 field 所代表的属性值
            return field.get(object) ;

        } catch(Exception e) {
            e.printStackTrace() ;
        }

        return null;
    }  }
