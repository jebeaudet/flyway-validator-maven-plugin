# Flyway validator maven plugin
This plugin will help you avoid duplicate revises when working with [Flyway](https://flywaydb.org/). Duplicate revises sometimes happen when concurrent PR are being merged or lunatic developers forget to validate their old PR against the current codebase.

By using this plugin, the build will fail when problematic version numbers are found in the project.

## How to use
I plan on publishing to maven central soon but in the meantime, clone this project and publish it to your local maven repo. Then, add this to your project/parent pom.xml : 
```
<plugin>
    <groupId>jebeaudet</groupId>
    <artifactId>flyway-validator-maven-plugin</artifactId>
    <version>0.1</version>
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
The root path, SQL migration path for .sql migrations and base package for .java migrations can be configured like this (defaults value shown here) : 
```
<plugin>
    <groupId>com.coveo</groupId>
    <artifactId>flyway-validator-maven-plugin</artifactId>
    <version>0.0.5-SNAPSHOT</version>
    <configuration>
        <rootPath>/src/main/resources</rootPath>
        <sqlRevisesRootPath>db/migration</sqlRevisesRootPath>
        <javaRevisesPackage>db.migration</javaRevisesPackage>
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
[ERROR] Failed to execute goal com.coveo:flyway-validator-maven-plugin:0.0.5-SNAPSHOT:validate-flyway-revises (default) on project test-project:
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