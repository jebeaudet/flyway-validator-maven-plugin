/**
 * Copyright (c) 2011 - 2017, Coveo Solutions Inc.
 */
package jebeaudet.flywayvalidator;

public class FlywayMigration
{
    private String version;
    private String filename;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public FlywayMigration withVersion(String version)
    {
        setVersion(version);
        return this;
    }

    public FlywayMigration withFilename(String filename)
    {
        setFilename(filename);
        return this;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FlywayMigration other = (FlywayMigration) obj;
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "FlywayMigration [version=" + version + ", filename=" + filename + "]";
    }
}
