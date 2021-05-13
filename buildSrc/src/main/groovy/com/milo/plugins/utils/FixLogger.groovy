package com.milo.plugins.utils

import com.android.build.gradle.api.BaseVariant

class FixLogger {
    private static boolean sShowLog = true

    static void init(boolean showLog) {
        sShowLog = showLog;
    }

    static void i(String log) {
        i("HotFix", log)
    }

    static void i(String tag, String log) {
        println("${tag}: ${log}")
    }

    static void d(String log) {
        i("HotFix", log)
    }

    static void d(String tag, String log) {
        if (sShowLog)
            println("${tag}: ${log}")
    }
}