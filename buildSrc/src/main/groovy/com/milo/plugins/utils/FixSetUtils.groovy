package com.milo.plugins.utils

class FixSetUtils {

    static boolean isExcluded(String path, Set<String> excludeClass) {
        boolean isExcluded = false
        excludeClass.each { exclude ->
            if (path.endsWith(exclude)) {
                isExcluded = true
            }
        }
        return isExcluded
    }

    static boolean isIncluded(String path, Set<String> includePackage) {
        if (includePackage.size() == 0) {
            return true
        }

        boolean isIncluded = false
        includePackage.each { include ->
            if (path.startsWith(include)) {
                isIncluded = true
            }
        }
        return isIncluded
    }
}
