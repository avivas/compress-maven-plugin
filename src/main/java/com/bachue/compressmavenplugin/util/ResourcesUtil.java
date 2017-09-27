package com.bachue.compressmavenplugin.util;

/*-
 * #%L
 * compress-maven-plugin Maven Plugin
 * %%
 * Copyright (C) 2017 Bachue
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.bachue.compressmavenplugin.dto.Resource;
import com.bachue.compressmavenplugin.dto.ResultFile;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Resource util class
 * @author Alejandro Vivas
 * @version 22/09/2017 0.0.1-SNAPSHOT
 * @since 22/09/2017 0.0.1-SNAPSHOT
 */
public final class ResourcesUtil
{
	/**
	 * Constructor to avoid instances
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 */
	private ResourcesUtil()
	{
	}

	/**
	 * Return a collection file
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param resources Resources with configuration
	 * @param project Maven project
	 * @param log Maven logger
	 * @return Collection with files in resources
	 * @throws MojoExecutionException If files not found or invalid configuration
	 */
	public static final Collection<ResultFile> getFiles(List<Resource> resources, MavenProject project,Log log) throws MojoExecutionException
	{
		Collection<ResultFile> filesResources = new ArrayList<>();
		for(Resource resource : resources)
		{
			filesResources.addAll(getFiles(resource, project, log));
		}
		return filesResources;
	}
	
	/**
	 * Return files by resource configuration
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param resource Resource configuration
	 * @param project Maven Project 
	 * @param log Maven logger
	 * @return Collection with files
	 * @throws MojoExecutionException If files not found o invalid configuration
	 */
	public static final Collection<ResultFile> getFiles(Resource resource, MavenProject project,Log log) throws MojoExecutionException
	{
		if (resource.getDirectory() == null || resource.getDirectory().isEmpty())
		{
			throw new MojoExecutionException("No directory defined in resource");
		}
		
		String resourceDirectory = resource.getDirectory();
		if( !new File(resourceDirectory).isAbsolute() )
		{
			resourceDirectory = project.getBasedir() + File.separator + resourceDirectory;
			resourceDirectory = resourceDirectory.replace('/',File.separatorChar);
		}
		
		if( !new File(resourceDirectory).exists() )
		{
			throw new MojoExecutionException("Invalid resource directory:[" + resourceDirectory +"]");
		}
		
		String outputDirectory = resource.getOutputDirectory() == null ? "" : resource.getOutputDirectory().replace('/',File.separatorChar);
		log.info("Ouput directory:[" + outputDirectory + "]");

		Collection<ResultFile> files = new ArrayList<>();
		// Add include files
		if (resource.getIncludes() != null && !resource.getIncludes().isEmpty())
		{
			for( String include : resource.getIncludes() )
			{
				String path = resource.getDirectory() + File.separator + include;
				File filePath = new File(path); 
				if( filePath.exists() )
				{
					String outputFile = outputDirectory + File.separator + include;
					ResultFile resultFile = new ResultFile(filePath, outputFile,resource.getFormats());
					files.add(resultFile);
				}
				else
				{
					filePath = new File( project.getBasedir() + File.separator + path);
					if( filePath.exists() )
					{
						String outputFile = outputDirectory + File.separator + include;
						ResultFile resultFile = new ResultFile(filePath, outputFile,resource.getFormats());
						files.add(resultFile);
					}
					else
					{
						throw new MojoExecutionException("Invalid include resource:[" + include +"]");
					}
				}
			}
		}		
		else // Search files by extensions
		{
			String [] extensions = null;
			if (resource.getExtensions() != null && !resource.getExtensions().isEmpty())
			{
				extensions = resource.getExtensions().toArray(new String[resource.getExtensions().size()]);
			}
			log.info("Searching file by extensions:" + Arrays.toString(extensions));
			Collection<File> foundFiles = FileUtil.getFiles(resourceDirectory, extensions);
			for(File file : foundFiles)
			{
				String outputFile = file.getAbsolutePath().replace(resourceDirectory, outputDirectory);
				ResultFile resultFile = new ResultFile(file, outputFile,resource.getFormats());
				files.add(resultFile);
			}
		}
		
		// Remove exclude resources
		if( (resource.getExcludes() != null) && !resource.getExcludes().isEmpty())
		{
			Iterator<String> iteratorExcludeFile =  resource.getExcludes().iterator(); 
			while( iteratorExcludeFile.hasNext() )
			{
				String exclude = resourceDirectory  + File.separator + iteratorExcludeFile.next();
				exclude = exclude.replace('/', File.separatorChar);
				File excludeFile = new File(exclude); 
				if( !excludeFile.isAbsolute() )
				{
					exclude = project.getBasedir() + File.separator + exclude;					
					excludeFile = new File(exclude);
				}
								
				boolean result = files.remove( new ResultFile(excludeFile,null,null) );
				if(result)
				{
					log.info("Exclude file:[" + exclude + "]");
				}
			}
		}

		return files;
	}
}
