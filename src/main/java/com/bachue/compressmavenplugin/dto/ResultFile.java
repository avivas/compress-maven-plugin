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

import java.io.File;
import java.util.List;

/**
 * Class with input file and Output file path without extension file
 * @author Alejandro Vivas
 * @version 27/09/2017 0.0.1-SNAPSHOT
 * @since 25/09/2017 0.0.1-SNAPSHOT
 */
public class ResultFile
{
	/** Input file */
	private File			inputFile;
	/** Output file */
	private String			outputFile;
	/** Output formats */
	private List<CompressFormat>	formats;

	/**
	 * Defult constructor
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 */
	public ResultFile()
	{
	}

	/**
	 * Constructor to define init values
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param inputFile Input file
	 * @param outputFile Output file
	 */
	public ResultFile(File inputFile, String outputFile,List<CompressFormat> formats)
	{
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.formats = formats;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @return the inputFile
	 */
	public File getInputFile()
	{
		return inputFile;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(File inputFile)
	{
		this.inputFile = inputFile;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @return the outputFile
	 */
	public String getOutputFile()
	{
		return outputFile;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @param formats the formats to set
	 */
	public void setFormats(List<CompressFormat> formats)
	{
		this.formats = formats;
	}

	/**
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * @return the formats
	 */
	public List<CompressFormat> getFormats()
	{
		return formats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @author Alejandro Vivas
	 * 
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * 
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof ResultFile))
		{
			return false;
		}
		ResultFile resultFile = (ResultFile) obj;
		return resultFile.getInputFile().equals(this.inputFile);
	}
}
