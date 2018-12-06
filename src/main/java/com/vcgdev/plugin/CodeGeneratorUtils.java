package com.vcgdev.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String JAVA_SOURCE_PATH = "src/main/java/";

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
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line;
        while((line = reader.readLine())!=null){
            buffer.append(line).append("\n");
        }
        reader.close();
        String entityVar =  domainName.substring(0,1).toLowerCase() + domainName.substring(1);
        String dtoVar = "DTO";
        String result = buffer.toString()
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
        return result;
    }

    public void generateDTO(String domainName) throws IOException {
        String basePath = JAVA_SOURCE_PATH+
                basePackage.replace(".","/");
        String entityPath = basePath        +"/"+entityPackage.replace(".","/");
        String dtoPath = basePath        +"/"+dtoPackage.replace(".","/");
        File dtoFile = new File(dtoPath+"/"+domainName+"DTO.java");
        if(!dtoFile.exists()){
            File domainJavaFile = new File(entityPath+"/"+domainName+".java");
            logger.info("File from: {}",domainJavaFile.getAbsolutePath());
            if(!domainJavaFile.exists()){
                throw new IllegalArgumentException("Entity not found: "+domainName+".java");
            }
            FileInputStream fos = new FileInputStream(domainJavaFile);
                logger.info("read java attributes and imports");
                BufferedReader reader = new BufferedReader(new InputStreamReader(fos));
                String line;
                StringBuffer importContent = new StringBuffer();
                StringBuffer attributesContent = new StringBuffer();
                Pattern attributesPattern = Pattern.compile("private\\p{Space}[a-zA-Z]*\\p{Space}*.*;");
                Boolean searchId = Boolean.FALSE;
                while ((line = reader.readLine())!=null){
                    line = line.trim().replaceAll("\\s{2,}"," ");//replace by on single space
                    Matcher matcher = attributesPattern.matcher(line);
                    if(line.contains("@Id")){
                        searchId = Boolean.TRUE;
                    }
                    if(matcher.matches()){
                        attributesContent.append("\t").append(line)
                        .append("\n");
                        if(searchId){
                            classId = line.split(" ")[1];//private Class id;
                            logger.info("ClassID: {}",classId);
                            searchId = Boolean.FALSE;
                        }
                    }
                    if(line.contains("import") &&
                            !line.contains("javax.persistence")&&
                            !line.contains("lombok")){
                        importContent.append(line);//imports
                    }

                }
                reader.close();
                //here classId is obtained from JavaFile
                String dtoTemplate = readFileTemplate("dto.template",domainName)
                        .replace("{imports}",importContent.toString())
                        .replace("{attributes}",attributesContent.toString());
                if (!new File(dtoPath).exists()){
                    logger.info("Generate new dir");
                    new File(dtoPath).mkdir();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(dtoFile));
                writer.write(dtoTemplate);
                writer.close();

        }else{
            logger.info("File for DTO already exists");
        }

    }
}
