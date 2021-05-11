package com.milo.plugins

import com.milo.plugins.utils.FixLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class HotFixPlugin implements Plugin<Project> {

    long startTime

    private static final String HOTFIX_DIR = "hotfixDir"
    private static final String HOTFIX_PATCHES = "hotfixPatches"

    private static final String MAPPING_TXT = "mapping.txt"
    private static final String HASH_TXT = "hash.txt"

    private static final String DEBUG = "debug"

    @Override
    void apply(Project project) {
        startTime = System.currentTimeMillis()
        FixLogger.i("HotFixPlugin start")

        project.afterEvaluate {
            String applicationId = project.android.defaultConfig.applicationId
            FixLogger.i("applicationId == ${applicationId}")

            project.android.applicationVariants.each{ variant ->
                FixLogger.i("variant.name == ${variant.name}")

                // Gradle 1.5 - 2.x
                Task dexTask2 = project.tasks.findByName("transformClassesWithDexFor${variant.name.capitalize()}")
                // Gradle 3.0+
                Task dexTask3 = project.tasks.findByName("transformClassesWithDexBuilderFor${variant.name.capitalize()}")

                Task dexTask = null
                if(dexTask2 != null){
                    dexTask = dexTask2
                } else if(dexTask3 != null){
                    dexTask = dexTask3
                } else {
                    throw new NullPointerException("gradle版本不支持，未找到transformClassesWithDexFor, 目前仅支持1.5-3.0+")
                }

                File hotfixFile = new File("${project.buildDir}/outputs/hotfix")
                File hotfixOutputFile = new File("${hotfixFile.getAbsolutePath()}/${variant.dirName}")
                hotfixOutputFile.delete()
                hotfixOutputFile.mkdirs()

                File hashFile = new File(hotfixOutputFile.getAbsolutePath() + "/" + HASH_TXT)
                if(hashFile.exists()){
                    hashFile.delete()
                }
                hashFile.createNewFile()


//                String cfixJarBeforeDex = "cfixJarBeforeDex${variant.name.capitalize()}"
//                project.task(cfixJarBeforeDex) {
//                    doLast {
//                        Set<File> inputFiles = dexTask.inputs.files.files
//                        inputFiles.each { file ->
//                            CFixLogger.i("transformClassesTask input: ${file.absolutePath}")
//                        }
//                        Set<File> files = CFixFileUtils.getFiles(inputFiles)
//                        files.each { file ->
//                            if (file.name.endsWith(".jar")) {
//                                CFixProcessor.processJar(file, hashFile, hashMap, patchDir, extension)
//                            } else if (file.name.endsWith(".class")) {
//                                CFixProcessor.processClass(file, hashFile, hashMap, patchDir, extension)
//                            }
//                        }
//                    }
//                }


                Task hotfixJarBeforeDex = project.task("hotfixBeforeDex${variant.name.capitalize()}"){
                    doLast {
                        Set<File> inputFiles = dexTask.inputs.files.files
                        inputFiles.each { file ->
                            FixLogger.i("dexTask class ${file.getAbsolutePath()}")
                        }
                    }
                }

                dexTask.dependsOn hotfixJarBeforeDex
            }
        }

        FixLogger.i("HotFixPlugin end, 耗时:${System.currentTimeMillis()-startTime}ms")
    }

}