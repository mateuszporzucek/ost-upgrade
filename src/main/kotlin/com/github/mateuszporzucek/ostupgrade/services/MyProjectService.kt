package com.github.mateuszporzucek.ostupgrade.services

import com.github.mateuszporzucek.ostupgrade.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
