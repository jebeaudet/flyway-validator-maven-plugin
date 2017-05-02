package jebeaudet.flywayvalidator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.reflect.ClassPath;

@Mojo(name = "validate-flyway-revises", defaultPhase = LifecyclePhase.VALIDATE)
public class FlywayValidator extends AbstractMojo
{
    private static final String ERROR_MESSAGE = "Error while resolving java migration filenames!";
    private static final String SLASH = "/";

    @Parameter(defaultValue = "${session.currentProject}", required = true, readonly = true)
    protected MavenProject mavenProject;

    /**
     * Root path of resources. Defaults to <b>src/main/resources</b>.
     */
    @Parameter(property = "validate-flyway-revises.rootPath", defaultValue = "src/main/resources", required = true)
    private String rootPath;

    /**
     * Root path of SQL revises. Defaults to <b>db/migration</b>.
     */
    @Parameter(property = "validate-flyway-revises.sqlRevisesRootPath", defaultValue = "db/migration", required = true)
    private String sqlRevisesRootPath;

    /**
     * Package for Java revises. Defaults to <b>db.migration</b>.
     */
    @Parameter(property = "validate-flyway-revises.javaRevisesPackage", defaultValue = "db.migration", required = true)
    private String javaRevisesPackage;

    @Override
    public void execute() throws DuplicateVersionFailureException, MojoExecutionException
    {
        List<FlywayMigration> sqlFlywayMigrations = getSqlFlywayMigrationFromPath(String.join(SLASH,
                                                                                              rootPath,
                                                                                              sqlRevisesRootPath));

        List<FlywayMigration> javaFlywayMigrations = getJavaFlywayMigrationFromBasePackage(javaRevisesPackage);

        List<FlywayMigration> flywayMigrationList = new ArrayList<>();
        flywayMigrationList.addAll(mapMigrationFilenamesToVersionList(sqlFlywayMigrations));
        flywayMigrationList.addAll(mapMigrationFilenamesToVersionList(javaFlywayMigrations));

        Set<FlywayMigration> flywayMigrationSet = new HashSet<>(flywayMigrationList);

        if (flywayMigrationSet.size() != flywayMigrationList.size()) {
            List<FlywayMigration> duplicateVersions = getDuplicateFlywayMigrations(flywayMigrationList,
                                                                                   flywayMigrationSet);
            throw new DuplicateVersionFailureException(this, duplicateVersions);
        }
    }

    private List<FlywayMigration> getDuplicateFlywayMigrations(List<FlywayMigration> flywayMigrationList,
                                                               Set<FlywayMigration> flywayMigrationSet)
    {
        ArrayList<FlywayMigration> duplicates = new ArrayList<>(flywayMigrationList);
        flywayMigrationSet.stream().forEach(version -> duplicates.remove(version));
        return flywayMigrationList.stream()
                                  .filter(migration -> duplicates.contains(migration))
                                  .collect(Collectors.toList());
    }

    private List<FlywayMigration> getJavaFlywayMigrationFromBasePackage(String basePackage)
            throws MojoExecutionException
    {
        try {
            URL[] urls = mavenProject.getCompileClasspathElements().stream().map(element -> {
                try {
                    return new File(element).toURI().toURL();
                } catch (MalformedURLException e) {
                    return null;
                }
            }).filter(Objects::nonNull).toArray(URL[]::new);

            try (URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls,
                                                                            Thread.currentThread()
                                                                                  .getContextClassLoader())) {

                return ClassPath.from(urlClassLoader)
                                .getTopLevelClasses(basePackage)
                                .stream()
                                .map(clazz -> clazz.getSimpleName())
                                .filter(className -> className.startsWith("V"))
                                .map(className -> new FlywayMigration().withFilename(className + ".java"))
                                .collect(Collectors.toList());
            }
        } catch (IOException | DependencyResolutionRequiredException e) {
            getLog().error(ERROR_MESSAGE, e);
            throw new MojoExecutionException(ERROR_MESSAGE, e);
        }
    }

    private List<FlywayMigration> mapMigrationFilenamesToVersionList(List<FlywayMigration> migrationFilenames)
    {
        for (FlywayMigration flywayMigration : migrationFilenames) {
            String filename = flywayMigration.getFilename();

            //Strip comments if present
            int beginCommentIndex = filename.indexOf("__");
            String version = beginCommentIndex == -1 ? filename.substring(1, filename.length())
                                                     : filename.substring(1, beginCommentIndex);

            //Transform java revise nomenclature V1_0_1 into V1.0.1
            version = version.replaceAll("_", ".");

            flywayMigration.setVersion(version);
        }
        return migrationFilenames;
    }

    private List<FlywayMigration> getSqlFlywayMigrationFromPath(String path)
    {
        File folder = new File(String.join(SLASH, mavenProject.getBasedir().getPath(), path));
        Optional<File[]> filesArray = Optional.ofNullable(folder.listFiles());

        return filesArray.map(fileArray -> Arrays.stream(fileArray)
                                                 .filter(file -> file.isFile())
                                                 .map(filename -> filename.getName())
                                                 .filter(filename -> filename.endsWith(".sql")
                                                         && filename.startsWith("V"))
                                                 .map(filename -> new FlywayMigration().withFilename(filename))
                                                 .collect(Collectors.toList()))
                         .orElseGet(() -> new ArrayList<>());
    }
}
