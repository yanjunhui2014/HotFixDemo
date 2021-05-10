package com.milo.hotfixdemo.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 2021/4/5
 */
public class FileDexUtils {
    static final String tag = "FileDexUtils";

    private static HashSet<File> loadDex = new HashSet<File>();

    static {
        loadDex.clear();
    }

    /**
     *  加载修复包的dex文件
     *
     * @param context
     */
    public static void loadFixedDex(Context context) {
        Log.d(tag, "loadFixedDex");
        File fileDir = context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE);
        File[] files = fileDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.getAbsolutePath().endsWith(".dex") && !"classes.dex".equalsIgnoreCase(file.getAbsolutePath())) {
                //将需要热修的dex文件汇集到一起
                loadDex.add(file);
            }
        }

        createDexClassLoader(context, fileDir);
    }

    private static void createDexClassLoader(Context context, File fileDir) {
        String optimDir = fileDir.getAbsolutePath() + "/" + "opt_dex";
        File fopt = new File(optimDir);
        if (!fopt.exists()) {
            fopt.mkdirs();
        }

        for (File file : loadDex) {
            Log.d(tag, "createDexClassLoader, file == " + file.getAbsolutePath());

            //创建类加载起
            DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath(), optimDir,
                    null, context.getClassLoader());
            //开始修复
            hotFix(context, dexClassLoader);
        }
    }

    private static void hotFix(Context context, DexClassLoader classLoader) {
        Log.d(tag, "hotFix");
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();

        try {
            //获取修复包里的dexElements
            Object myElements = ReflectUtils.getDelElements(ReflectUtils.getPathList(classLoader));

            //获取原包里的dexElements
            Object sysElements = ReflectUtils.getDelElements(ReflectUtils.getPathList(pathClassLoader));

            //得到合并后的dexElements
            Object combineElements = ArrayUtils.combine(myElements, sysElements);

            //得到原包里的 pathList
            Object sysPathList = ReflectUtils.getPathList(pathClassLoader);

            //利用反射，将合并后的dexElements 赋值到原包 pathList中
            ReflectUtils.setField(sysPathList, sysPathList.getClass(), combineElements);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(tag, "e == " + e.getMessage());
        }
    }

}
