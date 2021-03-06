package com.bachue.compressmavenplugin;

/*-
 * #%L
 * compress-maven-plugin Maven Plugin
 * %%
 * Copyright (C) 2017 - 2018 Bachue
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


import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
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
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.bachue.compressmavenplugin.dto.Archive;
import com.bachue.compressmavenplugin.dto.Resource;
import com.bachue.compressmavenplugin.dto.ResultFile;
import com.bachue.compressmavenplugin.util.CompressUtil;
import com.bachue.compressmavenplugin.util.ResourcesUtil;

/**
 * Class with Mojo compress goal
 * @author Alejandro Vivas
 * @version 08/01/2018 0.0.1-SNAPSHOT
 * @since 22/09/2017 0.0.1-SNAPSHOT
 */
@Mojo(name = "compress", defaultPhase = LifecyclePhase.COMPILE)
public class CompressMojo extends AbstractMojo
{
	/** Object to get project path */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject		project;
	
	/** List of resources */
	@Parameter(property = "resources", required = false)
	private List<Resource>	resources;
	/** List of archives */
	@Parameter(property = "archives", required = false)
	private List<Archive>	archives;

	/**
	 * Execute compress goal
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 */
	public void execute() throws MojoExecutionException
	{
		processResources();
	}

	/**
	 * Process resources
	 * @author Alejandro Vivas
	 * @version 08/01/2018 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 */
	private void processResources() throws MojoExecutionException
	{
		if (getResources() != null)
		{
			Collection<ResultFile> resultFiles = ResourcesUtil.getFiles(getResources(),getProject(),getLog());
			Iterator<ResultFile> iterator = resultFiles.iterator();
			getLog().info("Number of files found:" + resultFiles.size());
			while (iterator.hasNext())
			{
				ResultFile resultFile = iterator.next();
				getLog().debug("Input file:" +resultFile.getInputFile().getAbsolutePath());
				getLog().debug("output file:" +resultFile.getOutputFile() + "." + (resultFile.getFormats()) );
			}
			
			try
			{
				CompressUtil.compress(resultFiles,getLog());
			}
			catch (IOException e)
			{
				throw new MojoExecutionException("Error to compress files");
			}
		}
		
		if(getArchives() != null)
		{
			for(Archive archive : getArchives()) 
			{
				Collection<ResultFile> resultFiles = ResourcesUtil.getFiles(archive.getResources(),getProject(),getLog());
				try
				{
					CompressUtil.createArchive(archive, resultFiles, getLog());
				}
				catch (IOException e)
				{
					throw new MojoExecutionException("Error to create archive",e);
				}
			}
		}
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the resources
	 */
	public List<Resource> getResources()
	{
		return resources;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param resources the resources to set
	 */
	public void setResources(List<Resource> resources)
	{
		this.resources = resources;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the archives
	 */
	public List<Archive> getArchives()
	{
		return archives;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param archives the archives to set
	 */
	public void setArchives(List<Archive> archives)
	{
		this.archives = archives;
	}
	
	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param project the project to set
	 */
	public void setProject(MavenProject project)
	{
		this.project = project;
	}
	
	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the project
	 */
	public MavenProject getProject()
	{
		return project;
	}
}
