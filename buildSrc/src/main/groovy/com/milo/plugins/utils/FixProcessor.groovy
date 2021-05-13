package com.milo.plugins.utils

import com.milo.plugins.HotFixExtension
import org.apache.commons.io.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.objectweb.asm.*

class FixProcessor {

    static processJar(File jarFile, File hashFile, Map hashMap, File patchDir, HotFixExtension extension) {
        if (shouldProcessJar(jarFile)) {
            File optDirFile = new File(jarFile.absolutePath.substring(0, jarFile.absolutePath.length() - 4))
            FixFileUtils.unZipJar(jarFile, optDirFile)

            File metaInfoDir = new File(optDirFile, "META-INF")
            if (metaInfoDir.exists()) {
                metaInfoDir.deleteDir()
            }

            int counter = 0
            optDirFile.eachFileRecurse { file ->
                if (file.isFile()) {
                    boolean result = processClass(file, hashFile, hashMap, patchDir, extension)
                    if (result) {
                        counter++
                    }
                }
            }

            if (counter == 0) {
                optDirFile.deleteDir()
                return
            }

            File optJar = new File(jarFile.parent, jarFile.name + ".opt")
            FixFileUtils.zipJar(optDirFile, optJar)
            jarFile.delete()
            optJar.renameTo(jarFile)
            optDirFile.deleteDir()
        }
    }

    static boolean processClass(File classFile, File hashFile, Map hashMap, File patchDir, HotFixExtension extension) {
        if (shouldProcessClass(classFile, extension)) {
            referHackWhenInit(classFile, hashFile, hashMap, patchDir)
            return true
        }
        return false
    }

    //对.class文件进行字节处理（插入参数为Hack的构造方法）
    private static void referHackWhenInit(File classFile, File hashFile, Map hashMap,
                                          File patchDir) {
        File optClass = new File(classFile.parent, classFile.name + ".opt")
        FileInputStream inputStream = new FileInputStream(classFile)
        FileOutputStream outputStream = new FileOutputStream(optClass)

        ClassReader cr = new ClassReader(inputStream)
        String className = cr.className
        ClassWriter cw = new ClassWriter(cr, 0)
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
            @Override
            MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
                mv = new MethodVisitor(Opcodes.ASM4, mv) {
                    @Override
                    void visitInsn(int opcode) {
                        if ("<init>".equals(name) && opcode == Opcodes.RETURN) {
                            super.visitLdcInsn(Type.getType("Lme/wcy/cfix/Hack;"))
                        }
                        super.visitInsn(opcode)
                    }
                }
                return mv
            }
        }
        cr.accept(cv, 0)

        outputStream.write(cw.toByteArray())
        inputStream.close()
        outputStream.close()
        if (classFile.exists()) {
            classFile.delete()
        }
        optClass.renameTo(classFile)

        // save hash
        FileInputStream is = new FileInputStream(classFile)
        String hash = DigestUtils.sha1Hex(is)
        is.close()
        hashFile.append(FixMapUtils.format(className, hash))

        if (FixMapUtils.notSame(hashMap, className, hash)) {
            FileUtils.copyFile(classFile, FixFileUtils.touchFile(patchDir, className + ".class"))
        }
    }

    private static boolean shouldProcessJar(File jarFile) {
        if (!jarFile.exists() || !jarFile.name.endsWith(".jar")) {
            return false
        }

        String jarPath = FixFileUtils.formatPath(jarFile.absolutePath)
        return jarPath.contains("/build/intermediates/")
    }

    private static boolean shouldProcessClass(File classFile, HotFixExtension extension) {
        if (!classFile.exists() || !classFile.name.endsWith(".class")) {
            return false
        }

        FileInputStream inputStream = new FileInputStream(classFile)
        ClassReader cr = new ClassReader(inputStream)
        String className = cr.className
        inputStream.close()

        final boolean shouldProcess = !className.startsWith("me/wcy/cfix/lib/") &&
                !className.contains("android/support/") &&
                !className.contains("/R\$") &&
                !className.endsWith("/R") &&
                !className.endsWith("/BuildConfig") &&
                FixSetUtils.isIncluded(className, extension.includePackage) &&
                !FixSetUtils.isExcluded(className, extension.excludeClass)
        if(shouldProcess){
            FixLogger.d("shouldProcess class name == ${className}")
        }
        return shouldProcess;
    }
}
