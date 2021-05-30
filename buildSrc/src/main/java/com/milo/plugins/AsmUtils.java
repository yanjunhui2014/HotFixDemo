package com.milo.plugins;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 5/14/21
 */
public class AsmUtils {

    public static void writeToDesktop(String classPath, ClassWriter classWriter) {
        File classFile = new File("/Users/junhui/Desktop/class/" + getFileName(classPath));
        try {
            classFile.delete();
            classFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(classFile);
            fos.write(classWriter.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileName(String classPath) {
        return classPath.substring(classPath.lastIndexOf("/"), classPath.length());
    }

}
