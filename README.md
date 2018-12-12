REST API Generator
=================
Install
======

Run gradlew command in linux terminal

```
$ ./gradlew publish
```

Development
======

Create a new  based rest project in [start.spring.io](http://start.spring.io)
and add the next dependencies: jpa,hateoas, web, h2 and lombok

once you download the project add next lines on build.gradle file in the repositories section:

```
    maven {
		url uri('../rapi-generator/build/repos/snapshots')
    }
```

and also add dependency 

```
   classpath("com.vcgdev.plugin:rapi-generator:1.0")
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
}
```

exceptionClass must have the next code: 

```
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class BaseException extends Exception {
    private HttpStatus status;
    private String message;

    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.status = HttpStatus.NOT_FOUND;
    }
}
```
you can put the previous class in any package but you have to give the package name in the plugin configuration

With previous configurations just run the next command

```
   ./gradlew -Pargs=User genRest
```
And that's it, plugin going to generate all the structure with:
Repository, Service, RestController, Swagger annotations and views

See the project example in: [plugin-example](https://github.com/VCGDEV/plugin-example)


