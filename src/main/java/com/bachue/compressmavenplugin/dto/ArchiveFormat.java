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

/**
 * Enum with archive formats
 * @author Alejandro Vivas
 * @version 05/10/2017 0.0.1-SNAPSHOT
 * @since 05/10/2017 0.0.1-SNAPSHOT
 */
public enum ArchiveFormat
{
	ar("ar"), cpio("cpio"), tar("tar"), zip("zip"), jar("jar"), sevenz("7z");
	
	/** Default extension */
	private String extension;
	
	/**
	 * Create enum
	 * @author Alejandro Vivas
	 * @version 05/10/2017 0.0.1-SNAPSHOT
	 * @since 05/10/2017 0.0.1-SNAPSHOT
	 */
	private ArchiveFormat(String extension)
	{
		this.extension = extension;
	}
	
	/**
	 * @author Alejandro Vivas
	 * @version 05/10/2017 0.0.1-SNAPSHOT
	 * @since 05/10/2017 0.0.1-SNAPSHOT
	 * @return the extension
	 */
	public String getExtension()
	{
		return extension;
	}
}
