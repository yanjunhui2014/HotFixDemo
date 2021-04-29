package com.milo.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyFirstPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("helloworld").doLast {
            print("this is helloworld task")
        }
    }

}