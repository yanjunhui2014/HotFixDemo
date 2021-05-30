package com.milo.hotfixdemo.asm;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Title：将代码插入方法的工具类
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 5/19/21
 */
public class InsertCodeToMethod {

    public void insertFiled(String name, Object value, String inputClass, String outputClass) {
        System.out.println("insertFiled .. in ..");
        if (inputClass == null || !new File(inputClass).exists()) {
            System.out.println("class文件不存在");
            return;
        }

        InputStream inputClassIs = null;

        try {
            inputClassIs = new FileInputStream(inputClass);
            ClassReader reader = new ClassReader(inputClassIs);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
