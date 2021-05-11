package com.milo.plugins.utils

import com.android.build.gradle.api.BaseVariant

class FixLogger {
    private static String TAG = "HotFix"

    static void init(BaseVariant variant) {
        TAG = "> CFix-${variant.name.capitalize()}"
    }

    static void i(String log) {
        println("${TAG}: ${log}")
    }

}