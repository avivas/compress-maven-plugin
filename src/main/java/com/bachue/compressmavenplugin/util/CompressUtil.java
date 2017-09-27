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

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
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

import com.bachue.compressmavenplugin.dto.Archive;
import com.bachue.compressmavenplugin.dto.CompressFormat;
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

	/**
	 * Create a archive
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param archive Archive to create
	 * @param resultFiles Resultfiles
	 * @param log maven logger
	 * @throws IOException If fail operation
	 */
	public static void createArchive(Archive archive, Collection<ResultFile> resultFiles, Log log) throws IOException
	{
		createAr(archive, resultFiles,log);
	}

	/**
	 * Create a ar archive
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param archive Archive to process
	 * @param resultFiles ResultFiles in Archive
	 * @param log Maven loger
	 * @throws IOException If fail to create archive
	 */
	public static void createAr(Archive archive, Collection<ResultFile> resultFiles, Log log) throws IOException
	{
		ArArchiveOutputStream arArchiveOutputStream = null;
		try
		{
			OutputStream fout = new FileOutputStream(archive.getOutputFile() + ".ar");
			arArchiveOutputStream = new ArArchiveOutputStream(fout);
			for (ResultFile resultFile : resultFiles)
			{
				ArArchiveEntry entry = new ArArchiveEntry(resultFile.getInputFile().getName(),resultFile.getInputFile().length());//new ArArchiveEntry(resultFile.getInputFile(), resultFile.getInputFile().getName());
				arArchiveOutputStream.putArchiveEntry(entry);
				arArchiveOutputStream.write(FileUtil.fileToBytes(resultFile.getInputFile().getAbsolutePath()));
				arArchiveOutputStream.closeArchiveEntry();
			}
		}
		finally
		{
			close(log, arArchiveOutputStream);
		}
	}

	/**
	 * Compress each ResultFile.
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param resultFiles List of ResultFile to compress
	 * @param log Maven logger
	 * @throws IOException If fail to compress a file
	 */
	public static void compress(Collection<ResultFile> resultFiles, Log log) throws IOException
	{
		for (ResultFile resultFile : resultFiles)
		{
			long originalSize = resultFile.getInputFile().length();
			log.info("Starting compress file:[" + resultFile.getInputFile().getAbsolutePath() + "]");
			for (CompressFormat format : resultFile.getFormats())
			{
				log.info("Starting compress in format:[" + format + "]");
				switch (format)
				{
					case br:
						compressBrotli(resultFile, log);
					break;

					default:
						// Use commons compress
						compress(resultFile, format, log);
					break;
				}
				long finalSize = new File(resultFile.getOutputFile() + "." + format).length();
				log.info("End compress in format:[" + format + "] Original Size:" + originalSize + " Final Size:" + finalSize + " Ratio compression:" + ((float) finalSize / (float) originalSize));
			}
		}
	}

	/**
	 * Compress file with brotli
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param resultFile File to compress
	 * @param log Maven logger
	 * @throws IOException
	 */
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
			fout = new FileOutputStream(resultFile.getOutputFile() + CompressFormat.br.getExtension());
			fout.write(compressed);
		}
		finally
		{
			close(log, fout, streamCompressor);
		}
	}

	/**
	 * Compress a file using apache commons compress
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param resultFile Object with result file
	 * @param format Format to compress
	 * @param log Maven logger
	 * @throws IOException If fail to compress file
	 */
	private static void compress(ResultFile resultFile, CompressFormat format, Log log) throws IOException
	{
		InputStream in = null;
		OutputStream fout = null;
		CompressorOutputStream compressOutput = null;
		try
		{
			in = Files.newInputStream(Paths.get(resultFile.getInputFile().getAbsolutePath()));
			File outputFiles = new File(resultFile.getOutputFile().substring(0, resultFile.getOutputFile().lastIndexOf(File.separator) + 1));
			outputFiles.mkdirs();
			fout = new FileOutputStream(resultFile.getOutputFile() + "." + format.getExtension());
			BufferedOutputStream out = new BufferedOutputStream(fout);

			switch (format)
			{
				case gz:
					compressOutput = new GzipCompressorOutputStream(out);
				break;

				case bzip2:
					compressOutput = new BZip2CompressorOutputStream(out);
				break;

				case pack:
					compressOutput = new Pack200CompressorOutputStream(out);
				break;

				case xz:
					compressOutput = new XZCompressorOutputStream(out);
				break;

				case lzma:
					compressOutput = new LZMACompressorOutputStream(out);
				break;

				case deflate:
					compressOutput = new DeflateCompressorOutputStream(out);
				break;

				case snappy:
					compressOutput = new FramedSnappyCompressorOutputStream(out);
				break;

				case lz4:
					compressOutput = new FramedLZ4CompressorOutputStream(out);
				break;

				default:
					log.error("Invalid format:" + format);
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

	/**
	 * Util metethod to close resources.
	 * @author Alejandro Vivas
	 * @version 27/09/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param log Maven logger
	 * @param closeables Objects to close
	 */
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
					log.error("Error to close a resource", e);
				}
			}
		}
	}
}
