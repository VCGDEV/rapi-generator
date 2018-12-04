package com.vcgdev.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class CodeGeneratorUtils {
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

    public CodeGeneratorUtils(String basePackage, String dtoPackage, String repositoryPackage, String exceptionPackage, String servicePackage, String entityPacakge, String exceptionClass) {
        this.basePackage = basePackage;
        this.dtoPackage = dtoPackage;
        this.repositoryPackage = repositoryPackage;
        this.exceptionPackage = exceptionPackage;
        this.servicePackage = servicePackage;
        this.entityPackage = entityPacakge;
        this.exceptionClass = exceptionClass;
    }

    public String readFileTemplate(String fileTemplate, String domainName) throws IOException {
        InputStream stream = CodeGeneratorUtils.class.getResourceAsStream("src/main/resources/"+fileTemplate);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        while((line = reader.readLine())!=null){
            buffer.append(line).append("\n");
        }

        return buffer.toString()
                .replace(BASE_PACKAGE,this.basePackage)
                .replace(DTO_PACKAGE,this.dtoPackage)
                .replace(REPOSITORY_PACKAGE,this.repositoryPackage)
                .replace(EXCEPTION_PACKAGE,this.exceptionPackage)
                .replace(SERVICE_PACKAGE,this.servicePackage)
                .replace(ENTITY_PACKAGE,this.entityPackage)
                .replace(EXCEPTION_CLASS,this.exceptionClass)
                .replace(ENTITY_NAME,domainName)
                .replace(DTO_NAME,domainName.concat("DTO"));
    }

    private Map<String,String> getReplacementValues(String fileTemplate,String domainName){
        return null;
    }
}
