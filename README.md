# Flyway validator maven plugin
This plugin will help you avoid duplicate revises when working with [Flyway](https://flywaydb.org/). Duplicate revises sometimes happen when concurrent PR are being merged or lunatic developers forget to validate their old PR against the current codebase.

By using this plugin, the build will fail when problematic version numbers are found in the project.

## How to use
I plan on publishing to maven central soon but in the meantime, clone this project and publish it to your local maven repo. Then, add this to your project/parent pom.xml : 
```
<plugin>
    <groupId>jebeaudet</groupId>
    <artifactId>flyway-validator-maven-plugin</artifactId>
    <version>0.2</version>
    <executions>
        <execution>
            <goals>
               <goal>validate-flyway-revises</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
This will make the plugin run in the default `validate` phase of Maven.

## Configuration
The following properties can be configured : 
* Root path of source files;
* Path for SQL migrations;
* Base package for .java migrations;
* Flag to fail the build if invalid SQL filenames are found.

This is done in the plugin configuration (defaults value shown here) : 
```
<plugin>
    <groupId>com.coveo</groupId>
    <artifactId>flyway-validator-maven-plugin</artifactId>
    <version>0.2</version>
    <configuration>
        <rootPath>/src/main/resources</rootPath>
        <sqlRevisesRootPath>db/migration</sqlRevisesRootPath>
        <javaRevisesPackage>db.migration</javaRevisesPackage>
        <abortBuildOnInvalidFilenames>true</abortBuildOnInvalidFilenames>
    </configuration>
[...]
</plugin>
```

## Example
Here's an example of a failed build : 
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.coveo:flyway-validator-maven-plugin:0.2:validate-flyway-revises (default) on project test-project:
[ERROR] ------------------------------------------------------------------------
[ERROR] Duplicate migration version(s) found : [1.0, 1.0.1].
[ERROR] Details :
[ERROR] FlywayMigration [version=1.0, filename=V1.0__InitialSetup.sql]
[ERROR] FlywayMigration [version=1.0, filename=V1.0__Conflict.sql]
[ERROR] FlywayMigration [version=1.0.1, filename=V1.0.1__AddConstraint.sql]
[ERROR] FlywayMigration [version=1.0.1, filename=V1_0_1__AddAnotherConstraint.java]
[ERROR] ------------------------------------------------------------------------
```

## Question or found a bug? 
Open an issue! PR are also welcome.