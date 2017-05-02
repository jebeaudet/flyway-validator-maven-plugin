/**
 * Copyright (c) 2011 - 2017, Coveo Solutions Inc.
 */
package jebeaudet.flywayvalidator;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoFailureException;

public class DuplicateVersionFailureException extends MojoFailureException
{
    private static final long serialVersionUID = 1L;

    public DuplicateVersionFailureException(Object object, Collection<FlywayMigration> duplicates)
    {
        super(object, "", getLongErrorMessage(duplicates));
    }

    private static String getLongErrorMessage(Collection<FlywayMigration> duplicates)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------------------------------------------------------\n");
        sb.append(String.format("Duplicate migration version(s) found : %s.\nDetails : \n", duplicates.stream()
                                                                                                      .map(flywayMigration -> flywayMigration.getVersion())
                                                                                                      .collect(Collectors.toSet())));
        for (FlywayMigration duplicate : duplicates) {
            sb.append(duplicate);
            sb.append("\n");
        }
        sb.append("------------------------------------------------------------------------\n");
        return sb.toString();
    }
}
