REST API Generator
=================

Development
======

Create a new  based rest project in [start.spring.io](http://start.spring.io)
and add the next dependencies: jpa,hateoas, web, h2 and lombok

Add maven repository to build.gradle file in buildScript 

```
maven {
   url 'http://5.189.184.52:8081/artifactory/libs-snapshot/'
}
```

and also add dependency in buildscript section

```
   classpath("com.vcgdev.plugin:rapi-generator:1.2")
```

Add mapstruct to your project, version used in example: 1.3.0.Final

```
  implementation "org.mapstruct:mapstruct:${mapstructVersion}"
  annotationProcessor "org.mapstruct:mapstruct-processor:${mapstructVersion}"
```

you will need to apply the plugin

```
   apply plugin: 'com.vcgdev.plugin'
```

and in the end add configurations for the task, those configurations are required to generate the files with the correct structure

```
genRest{
	basePackage = "com.vcgdev.pluginexample"
	servicePackage = "service"
	repositoryPackage = "persistence"
	entityPackage = "domain"
	resourcePackage = "rest"
	dtoPackage = "views"
        exceptionClass = "BaseException"
        exceptionPackage = "exception"
        mapperPackage = "mapper"
}
```

With previous configurations just run the next command

```
   ./gradlew -Pargs=User genRest
```
And that's it, plugin going to generate all the structure with:
Repository, Service, RestController, Swagger annotations and views

See the project example in: [plugin-example](https://github.com/VCGDEV/plugin-example)


