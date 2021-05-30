package com.milo.hotfixdemo.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 5/30/21
 */
public class LifecycleOnCreateMethodVisitor extends MethodVisitor {

    private String className;

    public LifecycleOnCreateMethodVisitor(String className, MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
        this.className = className;
    }

    @Override
    public void visitCode() {
        // 加载常量 load constant
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("abcd");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(className + " >> onCreate");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);

        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, 3);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitLdcInsn("1");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn("2");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitLdcInsn("3");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        super.visitCode();
        //方法执行后插入
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc);
        System.out.println("name ==  " + name + ", desc == " + desc);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    //        mv.visitLdcInsn("TAG");
    //        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
    //        mv.visitInsn(Opcodes.DUP);
    //        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
    //        mv.visitLdcInsn("-------> onCreate : ");
    //        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    //        mv.visitVarInsn(Opcodes.ALOAD, 0);
    //        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
    //        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
    //        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    //        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    //        mv.visitInsn(Opcodes.POP);
}
