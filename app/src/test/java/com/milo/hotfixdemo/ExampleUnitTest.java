package com.milo.hotfixdemo;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test() {
        AsmTest asmTest = new AsmTest();
        asmTest.showClassInfoByAsm(DemoApplication.class);
    }

    private void writeDescToClass(){
        String output = "/Users/junhui/android/opensource/HotFixDemo/app/build/asmTest";
        String classDir = "/Users/junhui/android/opensource/HotFixDemo/app/build/intermediates/javac/debug/classes/com/milo/hotfixdemo/ui/MainActivity.class";

        if (!new File(classDir).exists()) {
            System.out.println("class file not found, program termination");
            return;
        }

        boolean fieldAddSuc = false;

        try {

            String desc = Type.getDescriptor(String.class);
            System.out.println("desc == " + desc);

            ClassReader classReader = new ClassReader(new FileInputStream(classDir));
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
//            ClassVisitor addField = new AddField(classWriter,
//                    "field",
//                    Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
//                    Type.getDescriptor(String.class),
//                    "value"
//            );
//            classReader.accept(addField, ClassReader.EXPAND_FRAMES);
            byte[] newClass = classWriter.toByteArray();
            File newFile = new File(output, "MainActivity.class");

            if (createNewFileIfNeed(newFile.getAbsolutePath())) {
                new FileOutputStream(newFile).write(newClass);
                fieldAddSuc = true;
            } else {
                System.out.println("outoutFile create failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(fieldAddSuc){
            System.out.println("congratulation, field add suc");
        }
    }

    private boolean createNewFileIfNeed(String filePath) {
        if (new File(filePath).exists()) {
            return true;
        } else {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}