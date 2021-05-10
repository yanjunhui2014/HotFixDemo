package com.milo.plugins.utils

import com.android.build.gradle.internal.pipeline.TransformTask
import com.android.build.gradle.internal.transforms.ProGuardTransform
import com.milo.plugins.CFixExtension
import groovy.xml.Namespace
import org.apache.tools.ant.taskdefs.condition.Os
import org.apache.tools.ant.util.JavaEnvUtils
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

class CFixAndroidUtils {

    private static final String PATCH_NAME = "patch.jar"

    static String getApplication(Set<File> manifestFiles) {
        manifestFiles = CFixFileUtils.getFiles(manifestFiles)
        File manifestFile = null
        for (File file : manifestFiles) {
            if (file.exists() && file.absolutePath.endsWith("AndroidManifest.xml")) {
                manifestFile = file
                break
            }
        }
        Node manifest = new XmlParser().parse(manifestFile)
        Namespace androidTag = new Namespace("http://schemas.android.com/apk/res/android", 'android')
        String applicationName = manifest.application[0].attribute(androidTag.name)

        if (applicationName != null) {
            return applicationName.replace(".", "/")
        }
        return null
    }

    static String dex(Project project, File classDir) {
        String patchPath = classDir.parent + "/" + PATCH_NAME
        if (classDir.listFiles().size()) {
            String sdkDir = getSdkDir(project)
            if (sdkDir == null) {
                throw new InvalidUserDataException('$ANDROID_HOME is not defined')
            }

            String buildToolsVersion = project.android.buildToolsVersion
            String cmdExt = Os.isFamily(Os.FAMILY_WINDOWS) ? '.bat' : ''
            ByteArrayOutputStream stdout = new ByteArrayOutputStream()
            project.exec {
                commandLine "${sdkDir}/build-tools/${buildToolsVersion}/dx${cmdExt}",
                        '--dex',
                        "--output=${patchPath}",
                        "${classDir.absolutePath}"
                standardOutput = stdout
            }
            String error = stdout.toString().trim()
            if (error) {
                CFixLogger.i("dex error: ${error}")
            }
        }
        return patchPath
    }

    static String getSdkDir(Project project) {
        Properties properties = new Properties()
        File localProps = project.rootProject.file("local.properties")
        if (localProps.exists()) {
            properties.load(localProps.newDataInputStream())
            return properties.getProperty("sdk.dir")
        } else {
            return System.getenv("ANDROID_HOME")
        }
    }

    static applymapping(TransformTask proguardTask, File mappingFile) {
        if (proguardTask) {
            ProGuardTransform transform = (ProGuardTransform) proguardTask.getTransform()
            if (mappingFile.exists()) {
                transform.applyTestedMapping(mappingFile)
            } else {
                CFixLogger.i("${mappingFile} does not exist")
            }
        }
    }

    static signPatch(String patchPath, CFixExtension extension) {
        File patchFile = new File(patchPath)
        if (!patchFile.exists() || !extension.sign) {
            return
        }

        if (extension.storeFile == null || !extension.storeFile.exists()) {
            throw new IllegalArgumentException("> cfix: store file not exists")
        }

        CFixLogger.i("sign patch")

        List<String> command = [JavaEnvUtils.getJdkExecutable('jarsigner'),
                                '-verbose',
                                '-sigalg', 'MD5withRSA',
                                '-digestalg', 'SHA1',
                                '-keystore', extension.storeFile.absolutePath,
                                '-keypass', extension.keyPassword,
                                '-storepass', extension.storePassword,
                                patchFile.absolutePath,
                                extension.keyAlias]
        Process proc = command.execute()

        Thread outThread = new Thread(new Runnable() {
            @Override
            void run() {
                int b
                while ((b = proc.inputStream.read()) != -1) {
                    System.out.write(b)
                }
            }
        })
        Thread errThread = new Thread(new Runnable() {
            @Override
            void run() {
                int b
                while ((b = proc.errorStream.read()) != -1) {
                    System.out.write(b)
                }
            }
        })

        outThread.start()
        errThread.start()

        int result = proc.waitFor()
        outThread.join()
        errThread.join()

        if (result != 0) {
            throw new GradleException('> cfix: sign failed')
        }
    }

}