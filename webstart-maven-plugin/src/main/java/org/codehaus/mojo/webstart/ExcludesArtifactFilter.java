package org.codehaus.mojo.webstart;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.IncludesArtifactFilter;

public class ExcludesArtifactFilter extends IncludesArtifactFilter {

	public ExcludesArtifactFilter(List<String> patterns) {
		super(patterns);
	}

	public boolean include( Artifact artifact )
    {
        String id = artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + (artifact.hasClassifier() ? artifact.getClassifier() : "");

        boolean matched = false;
        for ( Iterator<String> i = getPatterns().iterator(); i.hasNext() & !matched; )
        {
            // TODO what about wildcards? Just specifying groups? versions?
        	String pattern = i.next();
            if ( id.equals( pattern ) )
            {
                matched = true;
            }
        }
        return !matched;
    }
}
