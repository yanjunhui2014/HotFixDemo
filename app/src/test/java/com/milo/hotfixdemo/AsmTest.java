package com.milo.hotfixdemo;

import com.milo.hotfixdemo.asm.LifecycleOnCreateMethodVisitor;
import com.milo.hotfixdemo.hotfixtools.FileUtils;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;

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

    public void showClassInfoByAsm(String classPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        //使用class的名称
        try {
            File classFile = new File(classPath);
            if (!classFile.exists()) {
                println("您要查询的class文件不存在");
                return;
            }

            fis = new FileInputStream(classPath);
            ClassReader classReader = new ClassReader(fis);

            println("类名 == " + classReader.getClassName());
            println("父类名 == " + classReader.getSuperName());

            String[] interfaces = classReader.getInterfaces();
            if (interfaces.length == 0) {
                println("实现接口 == 无");
            } else {
                StringBuilder builder = new StringBuilder();
                for (String anInterface : interfaces) {
                    builder.append(anInterface);
                    builder.append(",");
                }
                println("实现接口 == " + builder.toString());
            }

            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, classWriter) {

                private String className;
                private String superName;

                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, name, signature, superName, interfaces);
                    this.className = name;
                    this.superName = superName;
                    println("visit, name == " + name + ", superName == " + superName);
                }

                @Override
                public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                    println("visitField");
                    println("visitField : " + formatString("access", access + "") + "," + formatString("name", name) + "," + formatString("desc", desc) +
                            formatString("signature", signature));
                    return super.visitField(access, name, desc, signature, value);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//                    println("visitMethod");
//                    println("visitMethod : " + formatString("access", access + "") + "," + formatString("name", name) + "," + formatString("desc", desc) +
//                            formatString("signature", signature));
                    if("androidx/appcompat/app/AppCompatActivity".equals(superName)) {
                        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                        if ("<init>".equals(name) && "()V".equals(desc)) {
                            println("visitInsn, 成功访问构造方法");
                        } else if("onCreate".equals(name) && "(Landroid/os/Bundle;)V".equals(desc)){
                            println("visitInsn, 成功f访问onCreate方法");
                            return new LifecycleOnCreateMethodVisitor(className, mv);
                        }
                    }

                    return super.visitMethod(access, name, desc, signature, exceptions);
                }

                @Override
                public void visitEnd() {
                    super.visitEnd();
                    println("visitEnd");
                }
            };
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

            byte[] newClass = classWriter.toByteArray();
            fos = new FileOutputStream(new File(classPath.replace(".class", "2.class")));
            fos.write(newClass);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            flush(fos);
            close(fos);
            close(fis);
        }
    }

    public void addFieldToClass(String classPath, String outputDir, final String fieldName, final Object value, String descriptor, int access) {
        try {
            System.out.println("addFieldToClass .. in ..");
            if (classPath == null || !new File(classPath).exists()) {
                System.out.println("class文件不存在");
                return;
            }
            InputStream is = new FileInputStream(classPath);
            ClassReader classReader = new ClassReader(is);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

            ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, classWriter) {

                boolean repeatField = false;

                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if (name.equals(fieldName)) {
                        repeatField = true;
                    }

                    return super.visitField(access, name, descriptor, signature, value);
                }

                @Override
                public void visitEnd() {
                    if (!repeatField) {
                        super.visitField(access, fieldName, descriptor, null, value);
                    }
                    super.visitEnd();
                }
            };

            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            byte[] newClass = classWriter.toByteArray();
            File newFile = new File(outputDir, getFileName(classPath));
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(newClass);

            close(is);
            close(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hackClass(String classPath, String outputDir) {
        System.out.println("hackClass .. in ..");
        if (classPath == null || !new File(classPath).exists()) {
            System.out.println("class文件不存在");
            return;
        }

        File classFile = new File(classPath);
        File optClass = new File(outputDir, getFileName(classPath));

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            if (optClass.exists()) {
                optClass.delete();
            }
            optClass.createNewFile();

            inputStream = new FileInputStream(classFile);
            outputStream = new FileOutputStream(optClass);

            ClassReader cr = new ClassReader(inputStream);
            String className = cr.getClassName();
            ClassWriter cw = new ClassWriter(cr, ClassReader.EXPAND_FRAMES);

            ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                    mv = new MethodVisitor(Opcodes.ASM4, mv) {
                        @Override
                        public void visitInsn(int opcode) {
                            if ("<init>".equals(name) && opcode == Opcodes.RETURN) {
                                super.visitLdcInsn(Type.getType(Type.getDescriptor(String.class)));
                            }
                            super.visitInsn(opcode);
                        }
                    };
                    return mv;
                }
            };
            cr.accept(cv, 0);

            outputStream.write(cw.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
        }

    }

    private static String getFileName(String classPath) {
        return classPath.substring(classPath.lastIndexOf("/"), classPath.length());
    }

    private static void close(Closeable closeable) {
        try {
            closeable.close();
            closeable = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void flush(Flushable flushable) {
        try {
            flushable.flush();
            flushable = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void print(String msg) {
        System.out.print(msg);
    }

    private static void println(String msg) {
        System.out.println(msg);
    }

    private static String formatString(String key, String value){
        return String.format("[%s == %s]", key, value);
    }

}
