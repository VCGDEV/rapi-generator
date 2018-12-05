package com.vcgdev.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CodeGeneratorUtils {
    private static final Logger logger = LoggerFactory.getLogger(CodeGeneratorUtils.class);
    //packages
    private static final String  BASE_PACKAGE = "{basePackage}";
    private static final String DTO_PACKAGE = "{dtoPackage}";
    private static final String REPOSITORY_PACKAGE = "{repositoryPackage}";
    private static final String EXCEPTION_PACKAGE = "{exceptionPackage}";
    private static final String SERVICE_PACKAGE = "{servicePackage}";
    private static final String ENTITY_PACKAGE = "{domainPackage}";
    //names and attributes
    private static final String ENTITY_NAME = "{entityName}";
    private static final String ENTITY_VAR = "{entityVar}";

    private static final String DTO_NAME = "{dtoName}";
    private static final String DTO_VAR= "{dtoVar}";
    private static final String CLASS_ID = "{classId}";
    private static final String EXCEPTION_CLASS = "{Exception}";

    private String basePackage;
    private String dtoPackage;
    private String repositoryPackage;
    private String exceptionPackage;
    private String servicePackage;
    private String entityPackage;
    private String exceptionClass;
    private String classId;

    public CodeGeneratorUtils(String basePackage, String dtoPackage, String repositoryPackage, String exceptionPackage,
                              String servicePackage, String entityPackage, String exceptionClass) {
        this.basePackage = basePackage;
        this.dtoPackage = dtoPackage;
        this.repositoryPackage = repositoryPackage;
        this.exceptionPackage = exceptionPackage;
        this.servicePackage = servicePackage;
        this.entityPackage = entityPackage;
        this.exceptionClass = exceptionClass;
    }

    public String readFileTemplate(String fileTemplate, String domainName) throws IOException {
        logger.info("Read file template: {}", fileTemplate);
        InputStream stream = CodeGeneratorUtils.class
        .getResourceAsStream("/templates/"+fileTemplate);
        if(stream==null)
            logger.info("Read file is null");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        if(reader==null)
            logger.info("Reader is null");
        StringBuffer buffer = new StringBuffer();
        String line;
        while((line = reader.readLine())!=null){
            buffer.append(line).append("\n");
        }
        logger.info("{}",buffer);
        String entityVar =  domainName.substring(0,1).toLowerCase() + domainName.substring(1);
        String dtoVar = "DTO";
        return buffer.toString()
                .replace(BASE_PACKAGE,this.basePackage)
                .replace(DTO_PACKAGE,this.dtoPackage)
                .replace(REPOSITORY_PACKAGE,this.repositoryPackage)
                .replace(EXCEPTION_PACKAGE,this.exceptionPackage)
                .replace(SERVICE_PACKAGE,this.servicePackage)
                .replace(ENTITY_PACKAGE,this.entityPackage)
                .replace(EXCEPTION_CLASS,this.exceptionClass)
                .replace(ENTITY_NAME,domainName)
                .replace(DTO_VAR,dtoVar)
                .replace(ENTITY_VAR,entityVar)
                .replace(CLASS_ID,this.classId)
                .replace(DTO_NAME,domainName.concat("DTO"));
    }

}
