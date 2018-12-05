package com.vcgdev.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class RestGenerator extends DefaultTask {
    private final Logger logger = LoggerFactory.getLogger(RestGenerator.class);
    private String basePackage;
    private String servicePackage;
    private String dtoPackage;
    private String entityPackage;
    private String resourcePackage;
    private String entityName;
    private String repositoryPackage;
    private String exceptionClass;
    private String exceptionPackage;
    private final String SOURCE_PATH = "src/main/java";
    private CodeGeneratorUtils codeGeneratorUtils;
    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public String getDtoPackage() {
        return dtoPackage;
    }

    public void setDtoPackage(String dtoPackage) {
        this.dtoPackage = dtoPackage;
    }

    public String getEntityPackage() {
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getResourcePackage() {
        return resourcePackage;
    }

    public void setResourcePackage(String resourcePackage) {
        this.resourcePackage = resourcePackage;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getRepositoryPackage() {
        return repositoryPackage;
    }

    public void setRepositoryPackage(String repositoryPackage) {
        this.repositoryPackage = repositoryPackage;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionPackage() {
        return exceptionPackage;
    }

    public void setExceptionPackage(String exceptionPackage) {
        this.exceptionPackage = exceptionPackage;
    }

    @TaskAction
    public void generateResources() throws IOException {
        if(null == basePackage || basePackage.trim().isEmpty())
            throw new IllegalArgumentException("Base package must not be empty or null");
        if(null == servicePackage || servicePackage.trim().isEmpty())
            throw new IllegalArgumentException("Service package must not be empty or null");
        if(null == resourcePackage || resourcePackage.trim().isEmpty())
            throw new IllegalArgumentException("Resource package must not be empty or null");
        if(null == repositoryPackage || repositoryPackage.isEmpty())
            throw new IllegalArgumentException("Repository package must not be empty or null");
        if(null == dtoPackage || dtoPackage.trim().isEmpty())
            throw new IllegalArgumentException("DTO package must not be empty or null");
        if(null == entityPackage || entityPackage.trim().isEmpty())
            throw new IllegalArgumentException("Entity package must not be empty or null");
        if(null == entityName || entityName.trim().isEmpty())
            throw new IllegalArgumentException("You must provide the entity name");
        if(null == exceptionClass)
            throw new IllegalArgumentException("You must provide the exception class");
        logger.info("Generate resources for: {}",entityName);
        codeGeneratorUtils = new CodeGeneratorUtils(basePackage,dtoPackage,repositoryPackage,exceptionPackage,servicePackage,entityPackage,exceptionClass);
        String serviceContent = codeGeneratorUtils.readFileTemplate("service.template",entityName);
        logger.info(serviceContent);
        //TODO read from template
        //TODO wirte into file
        //TODO extract attributes and ID from entity
    }

}
