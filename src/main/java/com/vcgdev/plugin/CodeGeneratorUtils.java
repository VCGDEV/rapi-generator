package com.vcgdev.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class CodeGeneratorUtils {
    private static final Logger logger = LoggerFactory.getLogger(CodeGeneratorUtils.class);
    //packages
    private static final String  BASE_PACKAGE = "{basePackage}";
    private static final String DTO_PACKAGE = "{dtoPackage}";
    private static final String REPOSITORY_PACKAGE = "{repositoryPackage}";
    private static final String EXCEPTION_PACKAGE = "{exceptionPackage}";
    private static final String SERVICE_PACKAGE = "{servicePackage}";
    private static final String ENTITY_PACKAGE = "{domainPackage}";
    private static final String RESOURCE_PACKAGE = "{resourcePackage}";
    private static final String MAPPER_PACKAGE = "{mapperPackage}";
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
    private String resourcePackage;
    private String mapperPackage;
    private List<String> attributeNames = new ArrayList<>();
    CodeGeneratorUtils(String basePackage, String dtoPackage, String repositoryPackage, String exceptionPackage,
                              String servicePackage, String entityPackage,
                       String exceptionClass,String resourcePackage,
                       String mapperPackage) {
        this.basePackage = basePackage;
        this.dtoPackage = dtoPackage;
        this.repositoryPackage = repositoryPackage;
        this.exceptionPackage = exceptionPackage;
        this.servicePackage = servicePackage;
        this.entityPackage = entityPackage;
        this.exceptionClass = exceptionClass;
        this.resourcePackage = resourcePackage;
        this.mapperPackage = mapperPackage;
    }

    private String readFileTemplate(String fileTemplate, String domainName) throws IOException {
        logger.info("Read file template: {}", fileTemplate);
        InputStream stream = CodeGeneratorUtils.class
        .getResourceAsStream("/templates/"+fileTemplate);
        if(stream==null)
            throw new IllegalArgumentException("Error to load stream");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        if(reader==null)
            throw new IllegalArgumentException("Template does not exists");
        StringBuffer buffer = new StringBuffer();
        String line;
        while((line = reader.readLine())!=null){
            buffer.append(line).append("\n");
        }
        reader.close();
        String entityVar =  domainName.substring(0,1).toLowerCase() + domainName.substring(1);
        String dtoVar = entityVar.concat("DTO");

        String templateString = buffer.toString();

        String dtoMapping;
        String entityMapping;
        if(fileTemplate.contains("service-impl")){
            dtoMapping = generateMapping(DTO_VAR,ENTITY_VAR);
            logger.info(dtoMapping);
            templateString = templateString.replace("{dtoMapping}",dtoMapping);
            entityMapping = generateMapping(ENTITY_VAR,DTO_VAR);
            logger.info(entityMapping);
            templateString = templateString.replace("{entityMapping}",entityMapping);
        }
        templateString = templateString.replace(BASE_PACKAGE,this.basePackage)
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
                .replace(DTO_NAME,domainName.concat("DTO"))
                .replace(MAPPER_PACKAGE, this.mapperPackage)
                .replace("{mapping}",domainName.toLowerCase()+"s")//TODO add better way to generate mapping
                .replace(RESOURCE_PACKAGE,this.resourcePackage);
        return  templateString;
    }

    private void generateDTO(String domainName) throws IOException {
        String basePath = JAVA_SOURCE_PATH+
                basePackage.replace(".","/");
        String entityPath = basePath        +"/"+entityPackage.replace(".","/");
        String dtoPath = basePath        +"/"+dtoPackage.replace(".","/");
        File dtoFile = new File(dtoPath+"/"+domainName+"DTO.java");
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
                String []attributeParts =line.split(" ");//private Class id;
                String attributeName = attributeParts[2].replace(";","");
                attributeNames.add(attributeName);
                if(searchId){
                    classId = attributeParts[1];
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
        if(!dtoFile.exists()){
            //here classId is obtained from JavaFile
            String dtoTemplate = readFileTemplate("dto.template",domainName)
                    .replace("{imports}",importContent.toString())
                    .replace("{attributes}",attributesContent.toString());
            writeFile(dtoFile,dtoPath,dtoTemplate);

        }else{
            logger.info("File for DTO already exists");
        }

    }

    void createDomainStructure(String domainName) throws IOException{
        //generate DTO first to extract classId
        generateDTO(domainName);
        fileNames().parallelStream()
                .forEach(fileName -> generateFile(fileName, domainName));

    }

    private List<String> fileNames() {
        return Arrays.asList(              
                "repository.template",
                "service.template",
                "service-impl.template",
                "resource.template",
                "mapper.template"
        );
    }

    private String generateMapping(String from,String to){
        logger.info("{}",attributeNames);
        return attributeNames.stream()
                .map(s ->{
                    String attribute = s.substring(0,1).toUpperCase()
                               +s.substring(1);
                    StringBuffer setter = new StringBuffer();
                    setter.append("\t\t")
                            .append(to)
                            .append(".set")
                            .append(attribute)
                            .append("(")
                            .append(from)
                            .append(".get")
                            .append(attribute)
                            .append("())");
                    return setter.toString();
                }).collect(Collectors.joining(";\n")).concat(";");
    }

    private void writeFile(File file,String path, String content) throws IOException{
        if (!new File(path).exists()){
            logger.info("Generate new dir");
            new File(path).mkdir();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
    }

    private void generateFile(String fileTemplate,String domainName) {
        String fileName = domainName.concat("Service.java");
        String basePath = JAVA_SOURCE_PATH.concat(basePackage.replace(".","/"))
                            .concat("/");
        String filePath = basePath.concat(servicePackage.replace(".","/"))
                            .concat("/");
        //TODO use switch instead
        if(fileTemplate.contains("service-impl")){
            fileName = domainName.concat("ServiceImpl.java");
            filePath = filePath.concat("impl/");
        }else if(fileTemplate.contains("resource")){
            fileName = domainName.concat("Controller.java");
            filePath = basePath.concat(resourcePackage.replace(".","/"))
                    .concat("/");
        }else if(fileTemplate.contains("repository")){
            filePath = basePath.concat(repositoryPackage.replace(".","/"))
                    .concat("/");
            fileName = domainName.concat("Repository.java");
        }else if(fileTemplate.contains("exception")){
            filePath = basePath.concat(exceptionPackage.replace(".","/"))
                    .concat("/");
            fileName = exceptionClass.concat(".java");
        }else if(fileTemplate.contains("handling")){
            fileName = "ExceptionHandling.java";
            filePath = basePath.concat(resourcePackage.replace(".","/"))
                    .concat("/");
        } else if(fileTemplate.contains("mapper")) {
            fileName = domainName.concat("Mapper.java");
            filePath = basePath.concat(mapperPackage.replace(".","/"))
                    .concat("/");
        }
        File fileToGenerate = new File(filePath.concat(fileName));
        if(!fileToGenerate.exists()){
            try {
                String fileContent = readFileTemplate(fileTemplate,domainName);
                writeFile(fileToGenerate, filePath, fileContent);
            }catch (IOException io) {
                throw new RuntimeException(String.format("Could not generate file, from template %s", fileTemplate));
            }
        }else{
            logger.info("File: {} already exists",fileName);
        }

    }
}
