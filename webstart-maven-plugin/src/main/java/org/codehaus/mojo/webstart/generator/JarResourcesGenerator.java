package org.codehaus.mojo.webstart.generator;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.ArtifactUtils;

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

import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.webstart.ResolvedJarResource;
import org.codehaus.plexus.util.StringUtils;

/**
 * Generates a JNLP deployment descriptor.
 *
 * @author ngc
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 * @author Kevin Stembridge
 */
public class JarResourcesGenerator
        extends AbstractGenerator<JarResourceGeneratorConfig>
{
    
    private static final String NATIVE_ARCH_WIN32 = "win32";
    
    private static final String NATIVE_ARCH_WIN64 = "win64";
    
    private static final String NATIVE_ARCH_WINAMD64 = "winamd64";
    
    private final List<String> NATIVE_ARCH_CLASSIFIER_MARKERS = Arrays.asList(NATIVE_ARCH_WIN32, NATIVE_ARCH_WIN64, NATIVE_ARCH_WINAMD64);
	
    public JarResourcesGenerator( Log log, GeneratorTechnicalConfig technicalConfig,
                                  JarResourceGeneratorConfig extraConfig )
    {
        super( log, technicalConfig, extraConfig );
    }

    @Override
    protected String getArgumentsText()
    {
        StringBuilder buffer = new StringBuilder();

        if ( getExtraConfig().getArguments() != null )
        {
            for ( String argument : getExtraConfig().getArguments() )
            {
                buffer.append( "<argument>" ).append( argument ).append( "</argument>" ).append( EOL );
            }
        }

        return buffer.toString();
    }

    @Override
    protected String getDependenciesText()
    {

        String jarResourcesText = "";

        String libPath = getExtraConfig().getLibPath();

        Set<ResolvedJarResource> jarResources = new LinkedHashSet<>();
        jarResources.addAll( getExtraConfig().getJarResources() );

        if ( !jarResources.isEmpty() )
        {
            final int multiplier = 100;
            StringBuilder buffer = new StringBuilder( multiplier * jarResources.size() );
            buffer.append( EOL );

            for ( ResolvedJarResource jarResource : jarResources )
            {

                if ( !jarResource.isIncludeInJnlp() || (jarResource.getClassifier() != null && NATIVE_ARCH_CLASSIFIER_MARKERS.contains(jarResource.getClassifier().toLowerCase())))            	
                {
                    continue;
                }

                // snapshot version should be transfered every time if the timestamp is not the same
                boolean snapshotVersion = ArtifactUtils.isSnapshot( jarResource.getVersion() );

                buffer.append( "<jar href=\"" );
                if ( StringUtils.isNotEmpty( libPath ) )
                {
                    buffer.append( libPath );
                    buffer.append( '/' );
                }

                if ( jarResource.isOutputJarVersion() && !snapshotVersion )
                {
                	getLog().debug( "Current jarResource.Href: " + jarResource.getHrefValue() ); 
                
                    buffer.append( jarResource.getHrefValue() );
                    buffer.append( "\"" );

                    buffer.append( " version=\"" ).append( jarResource.getVersion() ).append( "\"" );
                }
                else 
                {
                    String baseName = FilenameUtils.getBaseName(jarResource.getHrefValue());
                    String extension = FilenameUtils.getExtension(jarResource.getHrefValue());
                    
                    if (baseName.endsWith(jarResource.getVersion()))
                    {
                    	int endIndex = baseName.length() - jarResource.getVersion().length() - 1;
                    	baseName = baseName.substring(0, endIndex);
                    }
                    
                    getLog().debug( "Current baseName: " + baseName + ", extension: " + extension + ", version: " + jarResource.getVersion() );
                    
                    buffer.append(baseName).append("-").append(jarResource.getVersion());
                    if (extension != null && extension.length() > 0) {
                        buffer.append(".").append(extension);
                    }
                    buffer.append( "\"" );
                }
                
                if ( jarResource.isOutputDownload() && !snapshotVersion ) 
                {
                    getLog().debug( "Set the download attribute '" + jarResource.getOutputDownload() + "' for resource: " + 
                    		jarResource.getGroupId() + ":" + jarResource.getArtifactId() );

                    buffer.append(" download=\"").append(jarResource.getOutputDownload()).append("\"");
                }                

                if ( jarResource.getMainClass() != null )
                {
                    buffer.append( " main=\"true\"" );
                }

                buffer.append( "/>" ).append( EOL );
            }
            jarResourcesText = buffer.toString();
        }
        return jarResourcesText;
    }

    @Override
    protected String getDependenciesNativeLibrariesText() 
    {
    	String jarResourcesText = "";
    	
    	Set<ResolvedJarResource> jarResources = new LinkedHashSet<>();
        jarResources.addAll( getExtraConfig().getJarResources() );
        
    	if ( jarResources.size() != 0 )
        {
            final StringBuilder buffer = new StringBuilder( 10 * jarResources.size() );
            
            appendDependenciesNativeWin32Text(buffer);
            
            appendDependenciesNativeWin64Text(buffer);
            
            appendDependenciesNativeWinAmd64Text(buffer);
            
            jarResourcesText = buffer.toString();
        }
    	
    	return jarResourcesText;
    }
    
    protected void appendDependenciesNativeWin32Text(final StringBuilder buffer) 
    {
	    appendDependenciesNativeText(buffer, NATIVE_ARCH_WIN32, "x86");
	}
	
	protected void appendDependenciesNativeWin64Text(final StringBuilder buffer) 
	{
	    appendDependenciesNativeText(buffer, NATIVE_ARCH_WIN64, "x86_64");
	}
	
	protected void appendDependenciesNativeWinAmd64Text(final StringBuilder buffer) 
	{
	    appendDependenciesNativeText(buffer, NATIVE_ARCH_WINAMD64, "amd64");
	}
	
	protected void appendDependenciesNativeText(final StringBuilder buffer, String classifier, String arch) 
	{
    	
        String libPath = getExtraConfig().getLibPath();
        Collection<ResolvedJarResource> jarResources = getExtraConfig().getJarResources();
        
        if ( jarResources.size() != 0 )
        {
//        	StringBuilder buffer = new StringBuilder( 100 * jarResources.size() );
//            buffer.append( EOL );

            boolean nativeDependencyAdded = false;
            for ( ResolvedJarResource jarResource : jarResources )
            {
                
                if ( !jarResource.isIncludeInJnlp() || !(classifier.equalsIgnoreCase(jarResource.getClassifier())))
                {
                    continue;
                }    

                if (!nativeDependencyAdded) {
                    buffer.append( "<resources os=\"Windows\" arch=\"");
                    buffer.append(arch);
                    buffer.append("\">" ).append( EOL );
                    nativeDependencyAdded = true;
                }
                
                // snapshot version should be transfered every time if the timestamp is not the same
                boolean snapshotVersion = ArtifactUtils.isSnapshot(jarResource.getVersion());
                if (!jarResource.isOutputJarVersion() || snapshotVersion) {
                    
                    String extension = FilenameUtils.getExtension(jarResource.getHrefValue());
                    buffer.append( "\t<nativelib href=\"" );
                    if ( StringUtils.isNotEmpty( libPath ) )
                    {
                        buffer.append( libPath );
                        buffer.append( '/' );
                    }

                    buffer.append(jarResource.getArtifactId()).append("-").append(jarResource.getVersion()).
                    append("-").append(jarResource.getClassifier()).append(".").append(extension).append( "\"" );
                    buffer.append( "/>" ).append( EOL );
                }
                else {

                    buffer.append( "\t<nativelib href=\"" );
                    if ( StringUtils.isNotEmpty( libPath ) )
                    {
                        buffer.append( libPath );
                        buffer.append( '/' );
                    }

                    buffer.append( jarResource.getHrefValue() ).append( "\"" );
                    if ( jarResource.isOutputJarVersion() && !snapshotVersion ) 
                    {
                        buffer.append(" version=\"").append(jarResource.getVersion()).append("\"");
                    }
                    buffer.append( "/>" ).append( EOL );
                }
            }
            if (nativeDependencyAdded) {
                buffer.append( "</resources>" ).append( EOL );
            }
//            jarResourcesText = buffer.toString();
        }
        
//        return jarResourcesText;
    }
}
