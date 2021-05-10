package com.milo.plugins

import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import com.milo.plugins.utils.*

class MyFirstPlugin implements Plugin<Project> {

    private static final String CFIX_DIR = "cfixDir"
    private static final String CFIX_PATCHES = "cfixPatches"

    private static final String MAPPING_TXT = "mapping.txt"
    private static final String HASH_TXT = "hash.txt"

    private static final String DEBUG = "debug"

    private Task cfixJarBeforeDexTask
    private List<File> patchList = []

    private CFixExtension extension

    @Override
    void apply(Project project) {
        //从项目中创建扩展信息
        project.extensions.create("cfix", CFixExtension, project)

        project.afterEvaluate {
            //从项目中获取扩展信息
            extension = project.extensions.findByName("cfix") as CFixExtension

            project.android.applicationVariants.each { variant ->
                if (!variant.name.contains(DEBUG) || (variant.name.contains(DEBUG) && extension.debugOn)) {

                    File cfixDir
                    File patchDir
                    Map hashMap

                    CFixLogger.init(variant)

                    //判断版本，获取到transformClassTask
                    // Gradle 1.5 - 2.x
                    Task dexTask2 = project.tasks.findByName("transformClassesWithDexFor${variant.name.capitalize()}")
                    // Gradle 3.0+
                    Task dexTask3 = project.tasks.findByName("transformClassesWithDexBuilderFor${variant.name.capitalize()}")

                    Task dexTask
                    if (dexTask2 != null) {
                        dexTask = dexTask2
                    } else if (dexTask3 != null) {
                        dexTask = dexTask3
                    } else {
                        CFixLogger.i("Gradle Version not support")
                        return
                    }

                    //获取manifestTask
                    Task manifestTask = project.tasks.findByName("process${variant.name.capitalize()}Manifest")
                    //获取混淆task
                    Task proguardTask = project.tasks.findByName("transformClassesAndResourcesWithProguardFor${variant.name.capitalize()}")
                    Set<File> manifestFiles = manifestTask.outputs.files.files

                    //寻找上一次打包产出的文件
                    File oldCFixDir = CFixFileUtils.getFileFromProperty(project, CFIX_DIR)
                    if(oldCFixDir){
                        File mappingFile = CFixFileUtils.getVariantFile(oldCFixDir, variant, MAPPING_TXT)
                        //应用上次的mapping规则
                        CFixAndroidUtils.applymapping(proguardTask, mappingFile)

                        File hashFile = CFixFileUtils.getVariantFile(oldCFixDir, variant, HASH_TXT)
                        hashMap = CFixMapUtils.parseMap(hashFile)
                    }

                    String dirName = variant.dirName
                    //定义输出路径
                    cfixDir = new File("${project.buildDir}/outputs/cfix")
                    File outputDir = new File("${cfixDir}/${dirName}")
                    File hashFile = new File(outputDir, HASH_TXT)

                    //定义task名称
                    String cfixJarBeforeDex = "cfixJarBeforeDex${variant.name.capitalize()}"
                    project.task(cfixJarBeforeDex){
                        doLast {
                            Set<File> inputFiles = dexTask.inputs.files.files
                            inputFiles.each { file ->
                                CFixLogger.i("transformClassesTask input: ${file.absolutePath}")
                            }


                            Set<File> files = CFixFileUtils.getFileFromProperty(inputFiles)
                            files.each { file ->
                                if(file.name.endsWith(".jar")){
                                    CFixProcessor.processJar(file, hashFile, hashMap, patchDir, extension)
                                } else if(file.name.endsWith(".class")){
                                    //处理class文件
                                    CFixProcessor.processClass(file, hashFile, hashMap, patchDir, extension)
                                }
                            }
                        }
                    }

                    //task赋值
                    cfixJarBeforeDexTask = project.tasks[cfixJarBeforeDex]
                    cfixJarBeforeDexTask.doFirst {
                        CFixLogger.init(variant)

                        String appligionName = CFixAndroidUtils.getApplication(manifestFiles)
                        if(appligionName != null){
                            extension.excludeClass.add(appligionName)
                        }

                        outputDir.deleteDir()
                        outputDir.mkdirs()
                        hashFile.createNewFile()

                        if(oldCFixDir){
                            //如果有有基准包路径，则创建补丁目录
                            patchDir = new File("${cfixDir}/${dirName}/patch")
                            patchDir.mkdirs()
                            patchList.add(patchDir)
                        }
                    }

                    cfixJarBeforeDexTask.doLast {
                        if(proguardTask){
                            //赋值混淆的mapping.txt文件
                            File mapFile = new File("${project.buildDir}/outputs/mapping/${variant.dirName}/${MAPPING_TXT}")
                            File newMapFile = new File("${cfixDir}/${variant.dirName}/${MAPPING_TXT}")
                            FileUtils.copyFile(mapFile, newMapFile)
                        }
                    }

                    //dexTask添加依赖，在cfixJarBeforeDexTask之后执行
                    cfixJarBeforeDexTask.dependsOn dexTask.taskDependencies.getDependencies(dexTask)
                    dexTask.dependsOn cfixJarBeforeDexTask

                    String cfixPatch =  "cfix${variant.name.capitalize()}Patch"
                    project.task(cfixPatch){
                        doLast {
                            if(patchDir){
                                //生成dex文件
                                String patchPatch = CFixAndroidUtils.dex(project, patchDir)
                                //对补丁文件进行签名
                                CFixAndroidUtils.signPatch(patchPatch, extension)
                            }
                        }
                    }

                    //声明补丁task
                    Task cfixPatchTask = project.tasks[cfixPatch]
                    //在打包之前先生成补丁
                    cfixPatchTask.dependsOn cfixJarBeforeDexTask
                }
            }

            project.task(CFIX_PATCHES){
                doLast {
                    patchList.each { patchDir ->
                        String patchPatch = CFixAndroidUtils.dex(project, patchDir)
                        CFixAndroidUtils.signPatch(patchPatch, extension)
                    }
                }
            }

            project.tasks[CFIX_PATCHES].dependsOn cfixJarBeforeDexTask

        }
    }

}
