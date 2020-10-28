package org.codehaus.mojo.webstart;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.junit.Assert;
import org.junit.Test;

public class JnlpDownloadServletMojoTest {

	@Test
	public void testExclude() {
		
		List<String> excludesList = new ArrayList<>();
		excludesList.add("groupId1:artifactId1");
		excludesList.add("groupId2:artifactId2:classifier2");
		
    	
    	JnlpDownloadServletMojo jnlpDownloadServletMojo = new JnlpDownloadServletMojo();
    	
    	ExcludesArtifactFilter artifactFilter = jnlpDownloadServletMojo.prepareExcludesArtifactFilter(excludesList);
    	
    	ArtifactHandler artifactHandler = new DefaultArtifactHandler() {
			
			@Override
			public String getClassifier() {
				return "";
			}
		};
    	
    	Assert.assertFalse(artifactFilter.include(new DefaultArtifact("groupId1", "artifactId1", "1.0", "compile", "jar", null, artifactHandler)));
    	Assert.assertFalse(artifactFilter.include(new DefaultArtifact("groupId2", "artifactId2", "1.0", "compile", "jar", "classifier2", artifactHandler)));
    	Assert.assertTrue(artifactFilter.include(new DefaultArtifact("groupId3", "artifactId3", "1.0", "compile", "jar", null, artifactHandler)));
    	
	}
}
