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
 * Class with archive data
 * @author Alejandro Vivas
 * @version 19/09/2017 0.0.1-SNAPSHOT
 * @since 19/09/2017 0.0.1-SNAPSHOT
 */
public class Archive
{
	/** Path to output file */
	private String			outputFile;
	/** List of resources */
	private List<Resource>	resources;
	/** Output formats */
	private List<String> formats;

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @return the outputFile
	 */
	public String getOutputFile()
	{
		return outputFile;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
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
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param formats the formats to set
	 */
	public void setFormats(List<String> formats)
	{
		this.formats = formats;
	}
	
	/**
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @return the formats
	 */
	public List<String> getFormats()
	{
		return formats;
	}
}
