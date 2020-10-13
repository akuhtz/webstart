package org.codehaus.mojo.webstart.generator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.webstart.ResolvedJarResource;
import org.junit.Test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.Assert;

/**
 * Tests the {@link JarResourcesGenerator} class.
 *
 * @author Kevin Stembridge
 * @version $Revision$
 */
public class JarResourcesGeneratorTest
{
    public static final String EOL = System.getProperty( "line.separator" );

    @Test
    public void testGetDependenciesText()
        throws Exception
    {

        MavenProject mavenProject = new MavenProject();
        File resourceLoaderPath = new File( System.getProperty( "java.io.tmpdir" ) );
        File outputFile = File.createTempFile( "bogus", "jnlp" );
        outputFile.deleteOnExit();

        File templateFile = File.createTempFile( "bogusTemplate", ".vm" );
        templateFile.deleteOnExit();

        List<ResolvedJarResource> jarResources = new ArrayList<>();
        String mainClass = "fully.qualified.ClassName";

        GeneratorTechnicalConfig generatorTechnicalConfig =
            new GeneratorTechnicalConfig( mavenProject, resourceLoaderPath, "default-jnlp-template.vm",
                                          outputFile, templateFile.getName(), mainClass,
                                          "jar:file:/tmp/path/to/webstart-plugin.jar", "utf-8" );
        JarResourceGeneratorConfig jarResourceGeneratorConfig = new JarResourceGeneratorConfig( jarResources, null, null, null, null );
        JarResourcesGenerator generator  =
            new JarResourcesGenerator( new SystemStreamLog(), generatorTechnicalConfig, jarResourceGeneratorConfig );

//        JarResourcesGenerator generator =
//            new JarResourcesGenerator( new SystemStreamLog(), mavenProject, resourceLoaderPath,
//                                       "default-jnlp-template.vm", outputFile, templateFile.getName(), jarResources,
//                                       mainClass, "jar:file:/tmp/path/to/webstart-plugin.jar", null, "utf-8" );

        //The list of jarResources is empty so the output text should be an empty string
        assertEquals( "", generator.getDependenciesText() );

        //Add some JarResources and confirm the correct output
        ResolvedJarResource jarResource1 = buildJarResource( "href1", "1.1", null, "bogus.Class", true, true );
        ResolvedJarResource jarResource2 = buildJarResource( "href2", "1.2", null, null, true, true );
        ResolvedJarResource jarResource3 = buildJarResource( "href3", "1.3", null, null, false, true );
        ResolvedJarResource jarResource4 = buildJarResource( "href4", "1.4", null, null, false, false );

        jarResources.add( jarResource1 );
        jarResources.add( jarResource2 );
        jarResources.add( jarResource3 );
        jarResources.add( jarResource4 );

        String expectedText =EOL + "<jar href=\"href1\" version=\"1.1\" main=\"true\"/>" + 
        		EOL + "<jar href=\"href2\" version=\"1.2\"/>" +
        		EOL + "<jar href=\"href3-1.3\"/>" + EOL;

        String actualText = generator.getDependenciesText();

        Assert.assertEquals( expectedText, actualText );

        JarResourceGeneratorConfig jarResourceGeneratorConfig2 = new JarResourceGeneratorConfig( jarResources, "myLib", null, null, null );
        JarResourcesGenerator generator2  =
            new JarResourcesGenerator( new SystemStreamLog(), generatorTechnicalConfig, jarResourceGeneratorConfig2 );

        String expectedText2 = EOL + "<jar href=\"myLib/href1\" version=\"1.1\" main=\"true\"/>" +
        		EOL + "<jar href=\"myLib/href2\" version=\"1.2\"/>" + 
        		EOL + "<jar href=\"myLib/href3-1.3\"/>" + EOL;

        String actualText2 = generator2.getDependenciesText();

        Assert.assertEquals( expectedText2, actualText2 );

    }
    
    @Test
    public void testGetDependenciesNativeWin32Text()
            throws Exception
        {

            MavenProject mavenProject = new MavenProject();
            File resourceLoaderPath = new File( System.getProperty( "java.io.tmpdir" ) );
            File outputFile = File.createTempFile( "bogus", "jnlp" );
            outputFile.deleteOnExit();

            File templateFile = File.createTempFile( "bogusTemplate", ".vm" );
            templateFile.deleteOnExit();

            List<ResolvedJarResource> jarResources = new ArrayList<>();
            String mainClass = "fully.qualified.ClassName";

            GeneratorTechnicalConfig generatorTechnicalConfig =
                new GeneratorTechnicalConfig( mavenProject, resourceLoaderPath, "default-jnlp-win32-template.vm",
                                              outputFile, templateFile.getName(), mainClass,
                                              "jar:file:/tmp/path/to/webstart-plugin.jar", "utf-8" );
            JarResourceGeneratorConfig jarResourceGeneratorConfig = new JarResourceGeneratorConfig( jarResources, null, null, null, null );
            JarResourcesGenerator generator  =
                new JarResourcesGenerator( new SystemStreamLog(), generatorTechnicalConfig, jarResourceGeneratorConfig );

            //The list of jarResources is empty so the output text should be an empty string
            assertEquals( "", generator.getDependenciesText() );

            //Add some JarResources and confirm the correct output
            ResolvedJarResource jarResource1 = buildJarResource( "href1", "1.1", null, "bogus.Class", true, true );
            ResolvedJarResource jarResource2 = buildJarResource( "groupId1", "artifactId1", "1.2", "win32", null, true, true );
            ResolvedJarResource jarResource3 = buildJarResource( "groupId2", "artifactId2", "1.3", "win32", null, false, true );
            ResolvedJarResource jarResource4 = buildJarResource( "href3", "1.3", "win32", null, true, true );
            ResolvedJarResource jarResource5 = buildJarResource( "href4", "1.4", null, null, false, false );

            jarResources.add( jarResource1 );
            jarResources.add( jarResource2 );
            jarResources.add( jarResource3 );
            jarResources.add( jarResource4 );
            jarResources.add( jarResource5 );

            String expectedDependenciesText =EOL + "<jar href=\"href1\" version=\"1.1\" main=\"true\"/>" + EOL;

            String actualText = generator.getDependenciesText();

            Assert.assertEquals( expectedDependenciesText, actualText );
            
            String expectedDependenciesNativeWin32Text =EOL + "<resources os=\"Windows\" arch=\"x86\">" + 
            		EOL +"\t<nativelib href=\"artifactId1-win32.jar\" version=\"1.2\"/>" + 
            		EOL +"\t<nativelib href=\"artifactId2-1.3-win32.jar\"/>" + 
            		EOL +"\t<nativelib href=\"href3\" version=\"1.3\"/>" + 
            		EOL + "</resources>" + EOL;

            String actualNativeWin32Text = generator.getDependenciesNativeWin32Text();

            Assert.assertEquals( expectedDependenciesNativeWin32Text, actualNativeWin32Text );
        }    

    private static ResolvedJarResource buildJarResource( final String hrefValue, final String version, final String classifier, final String mainClass,
                                                  final boolean outputJarVersion, final boolean includeInJnlp )
    {

        return new ResolvedJarResource( new ArtifactStub() )
        {

            @Override
            public String getHrefValue()
            {
                return hrefValue;
            }
            
            @Override
    		public String getArtifactId()
    		{
    			return hrefValue;
    		}

            @Override
            public String getMainClass()
            {
                return mainClass;
            }

            @Override
            public String getVersion()
            {
                return version;
            }

            @Override
            public boolean isIncludeInJnlp()
            {
                return includeInJnlp;
            }

            @Override
            public boolean isOutputJarVersion()
            {
                return outputJarVersion;
            }

            @Override
            public String getClassifier()
            {
                return classifier;
            }
            
        };

    }

    private static ResolvedJarResource buildJarResource( final String groupId, final String artifactId, final String version, final String classifier, final String mainClass,
    		final boolean outputJarVersion, final boolean includeInJnlp )
    {

    	return new ResolvedJarResource( new ArtifactStub() )
    	{

            @Override
            public String getHrefValue()
            {
            	if (outputJarVersion) {
            		return artifactId + /*"-" + version +*/ "-" + classifier + "." + getType();
            	}
                return artifactId + "-" + version + "-" + classifier + "." + getType();
            }

    		@Override
    		public String getGroupId()
    		{
    			return groupId;
    		}

    		@Override
    		public String getArtifactId()
    		{
    			return artifactId;
    		}

    		@Override
    		public String getMainClass()
    		{
    			return mainClass;
    		}

    		@Override
    		public String getVersion()
    		{
    			return version;
    		}

    		@Override
    		public boolean isIncludeInJnlp()
    		{
    			return includeInJnlp;
    		}

    		@Override
    		public boolean isOutputJarVersion()
    		{
    			return outputJarVersion;
    		}

    		@Override
    		public String getClassifier()
    		{
    			return classifier;
    		}

    		@Override
    		public String getType()
    		{
    			return "jar";
    		}

    	};

    }
    
}
