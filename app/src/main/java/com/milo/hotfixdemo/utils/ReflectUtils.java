package com.milo.hotfixdemo.utils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Title：反射工具
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 2021/4/5
 */
class ReflectUtils {

    public static Object getField(Object obj, Class<?> clazz, String field) throws Exception{
        Field localField = clazz.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    public static void setField(Object obj, Class<?> clazz, Object value) throws Exception{
//        Field field =  clazz.getDeclaredField("delElements");
        Field field =  clazz.getDeclaredFields()[1];
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static Object getPathList(Object baseDexClassLoader) throws Exception {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    //获取dex元素
    public static Object getDelElements(Object paramObject) throws Exception{
        return  getField(paramObject, paramObject.getClass(), "dexElements");
    }

}
