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

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.maven.plugin.logging.Log;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import com.bachue.compressmavenplugin.dto.ResultFile;

/**
 * Compress util class
 * @author Alejandro Vivas
 * @version 25/09/2017 0.0.1-SNAPSHOT
 * @since 25/09/2017 0.0.1-SNAPSHOT
 */
public final class CompressUtil
{
	/**
	 * Private class to avoid instances
	 * @author Alejandro Vivas
	 * @version 25/09/2017 0.0.1-SNAPSHOT
	 * @since 25/09/2017 0.0.1-SNAPSHOT
	 */
	private CompressUtil()
	{
	}

	public static void compress(Collection<ResultFile> resultFiles, Log log) throws IOException
	{
		for (ResultFile resultFile : resultFiles)
		{
			long originalSize = resultFile.getInputFile().length();
			log.info("Starting compress file:[" + resultFile.getInputFile().getAbsolutePath() + "]");
			for (String format : resultFile.getFormats())
			{
				log.info("Starting compress in format:[" + format + "]");
				switch (format)
				{
					case "br":
						compressBrotli(resultFile, log);
					break;

					default:
						// Use commons compress 
						compress(resultFile,format,format,log);
					break;
				}
				long finalSize = new File(resultFile.getOutputFile() + "." + format).length();
				log.info("End compress in format:[" + format + "] Original Size:" + originalSize + " Final Size:" + finalSize + " Ratio compression:" + ((float)finalSize/(float)originalSize));
			}

		}
	}

	private static void compressBrotli(ResultFile resultFile, Log log) throws IOException
	{
		OutputStream fout = null;
		BrotliStreamCompressor streamCompressor = null;
		try
		{
			File outputFiles = new File(resultFile.getOutputFile().substring(0, resultFile.getOutputFile().lastIndexOf(File.separator) + 1));
			outputFiles.mkdirs();
			
			BrotliLibraryLoader.loadBrotli();
			byte[] inBuf = FileUtil.fileToBytes(resultFile.getInputFile().getAbsolutePath());
			boolean doFlush = true;
			streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
			byte[] compressed = streamCompressor.compressArray(inBuf, doFlush);
			fout = new FileOutputStream(resultFile.getOutputFile() + ".br");
			fout.write(compressed);
		}
		finally
		{
			close(log, fout, streamCompressor);
		}
	}
	
	private static void compress(ResultFile resultFile,String format,String extension,Log log)  throws IOException
	{
		InputStream in = null;
		OutputStream fout = null;
		CompressorOutputStream compressOutput = null;
		try
		{
			in = Files.newInputStream(Paths.get(resultFile.getInputFile().getAbsolutePath()));
			File outputFiles = new File(resultFile.getOutputFile().substring(0, resultFile.getOutputFile().lastIndexOf(File.separator) + 1));
			outputFiles.mkdirs();
			fout = new FileOutputStream(resultFile.getOutputFile() + "." + extension);
			BufferedOutputStream out = new BufferedOutputStream(fout);;
						
			switch (format)
			{
				case "gz":
					compressOutput = new GzipCompressorOutputStream(out);
				break;
			
				case "bzip2":
					compressOutput = new BZip2CompressorOutputStream(out);	
				break;
					
				case "pack":
					compressOutput = new Pack200CompressorOutputStream(out);	
				break;
				
				case "xz":
					compressOutput = new XZCompressorOutputStream(out);
				break;
				
				case "lzma":
					compressOutput = new LZMACompressorOutputStream(out);
				break;
				
				case "deflate":
					compressOutput = new DeflateCompressorOutputStream(out);
				break;	
				
				case "snappy":
					compressOutput = new FramedSnappyCompressorOutputStream(out);
				break;
				
				case "lz4":
					compressOutput = new FramedLZ4CompressorOutputStream(out);
				break;	
				
				default:
				break;
			}			
			
			int buffersize = 1024;
			final byte[] buffer = new byte[buffersize];
			int n = 0;
			while (-1 != (n = in.read(buffer)))
			{
				compressOutput.write(buffer, 0, n);				
			}
		}
		finally
		{
			close(log, in, compressOutput);
		}
	}

	private static void close(Log log, Closeable... closeables)
	{
		for (Closeable closeable : closeables)
		{
			if (closeable != null)
			{
				try
				{
					closeable.close();
				}
				catch (Exception e)
				{
					log.error("Error cerrando recurso", e);
				}
			}
		}
	}
}
