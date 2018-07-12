package jebeaudet.flywayvalidator;

import java.util.Collection;

import org.apache.maven.plugin.MojoFailureException;

public class InvalidFlywayMigrationFilenameFormatFailureException extends MojoFailureException
{
    private static final long serialVersionUID = 1L;

    public InvalidFlywayMigrationFilenameFormatFailureException(Object source, Collection<String> filenames)
    {
        super(source, "", getLongErrorMessage(filenames));
    }

    private static String getLongErrorMessage(Collection<String> filenames)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n------------------------------------------------------------------------\n");
        sb.append("Invalid filename format found(It should look like: V1_2__Description.sql or V1.2.3__Description.sql)!\nProblematic filenames : \n");
        for (String filename : filenames) {
            sb.append(filename);
            sb.append("\n");
        }
        sb.append("------------------------------------------------------------------------\n");
        return sb.toString();
    }

}
