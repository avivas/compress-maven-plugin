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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

/**
 * @author Alejandro Vivas
 * @version 22/09/2017 0.0.1-SNAPSHOT
 * @since 22/09/2017 0.0.1-SNAPSHOT
 */
public final class FileUtil
{
	/**
	 * To avoid instances
	 * @author Alejandro Vivas
	 * @version 22/09/2017 0.0.1-SNAPSHOT
	 * @since 22/09/2017 0.0.1-SNAPSHOT
	 */
	private FileUtil()
	{}
	
	public static Collection<File> getFiles(String directory, String[] extensions)
	{
		File directoryFile = new File(directory);
		return FileUtils.listFiles(directoryFile, extensions, true);
	}
	
	/**
	 * Read file and return content in String
	 * @author Alejandro Vivas
	 * @version 6/09/2017 0.0.1-SNAPSHOT
	 * @since 6/09/2017 0.0.1-SNAPSHOT
	 * @param path Path to file
	 * @return bytes with content
	 * @throws IOException If fail to read file
	 */
	public static byte[] fileToBytes(String path) throws IOException
	{
		return Files.readAllBytes(Paths.get(path));
	}
}
