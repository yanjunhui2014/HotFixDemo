package com.milo.plugins

import org.gradle.api.Project

class HotFixExtension {
    HashSet<String> includePackage = []
    HashSet<String> excludeClass = []
    boolean debugOn = true
    boolean showLog = true

    boolean sign = false
    File storeFile = null
    String storePassword = ''
    String keyAlias = ''
    String keyPassword = ''

    HotFixExtension(Project project) {
    }
}
