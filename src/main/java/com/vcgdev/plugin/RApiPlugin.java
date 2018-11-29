package com.vcgdev.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class RApiPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("genRest",RestGenerator.class,(task) -> {
            if (project.getProject().hasProperty("args")) {
                task.setEntityName(project.getProperties().get("args").toString());
            }
        });
    }
}
