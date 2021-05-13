package com.milo.hotfixdemo;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 5/13/21
 */
public class AsmTest {

    public void showClassInfoByAsm(Class clazz) {
        //使用class的名称
        try {
            ///
            System.out.println("showClassInfoByAsm .. in ..");

//            final String classPath = "/Users/junhui/android/github/HotFixDemo/app/build/intermediates/classes/release/com/milo/hotfixdemo/DemoApplication.class";
//            File classFile = new File(classPath);
//            ClassReader classReader = new ClassReader(new FileInputStream(classFile));

            ClassReader classReader = new ClassReader(clazz.getName());
            String[] interfaces = classReader.getInterfaces();

            System.out.println("superName == " + classReader.getSuperName());

            for (String anInterface : interfaces) {
                System.out.println("anInterface == " + anInterface);
            }

            System.out.println("showClassInfoByAsm .. out ..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
