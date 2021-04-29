package com.milo.hotfixdemo.utils;

import java.lang.reflect.Array;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 2021/4/5
 */
public class ArrayUtils {

    public static Object combine(Object arrayLhs, Object arrayRhs) {
        Class<?> clazz = arrayLhs.getClass().getComponentType();

        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);

        Object result = Array.newInstance(clazz, j);

        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }

        return result;
    }

}
