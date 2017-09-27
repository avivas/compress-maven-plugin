package com.bachue.compressmavenplugin.dto;

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

import java.util.List;

/**
 * Class with data resource information data
 * @author Alejandro Vivas
 * @version 19/09/2017 0.0.1-SNAPSHOT
 * @since 19/09/2017 0.0.1-SNAPSHOT
 */
public class Resource
{
	/** Include files, if empty all files in directory with extension in extensions */
	private List<String>	includes;
	/** Exclude files */
	private List<String>	excludes;
	/** Extension files to search */
	private List<String>	extensions;
	/** Input directoroty to find files */
	private String			directory;
	/** Output directory */
	private String			outputDirectory;
	/** Output formats */
	private List<CompressFormat> formats;

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the includes
	 */
	public List<String> getIncludes()
	{
		return includes;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param includes the includes to set
	 */
	public void setIncludes(List<String> includes)
	{
		this.includes = includes;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the excludes
	 */
	public List<String> getExcludes()
	{
		return excludes;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param excludes the excludes to set
	 */
	public void setExcludes(List<String> excludes)
	{
		this.excludes = excludes;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the extensions
	 */
	public List<String> getExtensions()
	{
		return extensions;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param extensions the extensions to set
	 */
	public void setExtensions(List<String> extensions)
	{
		this.extensions = extensions;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the directory
	 */
	public String getDirectory()
	{
		return directory;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory)
	{
		this.directory = directory;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the outputDirectory
	 */
	public String getOutputDirectory()
	{
		return outputDirectory;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param outputDirectory the outputDirectory to set
	 */
	public void setOutputDirectory(String outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}
	
	/**
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param formats the formats to set
	 */
	public void setFormats(List<CompressFormat> formats)
	{
		this.formats = formats;
	}
	
	/**
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @return the formats
	 */
	public List<CompressFormat> getFormats()
	{
		return formats;
	}
}
