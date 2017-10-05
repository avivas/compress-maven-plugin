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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import com.bachue.compressmavenplugin.dto.Archive;
import com.bachue.compressmavenplugin.dto.ArchiveFormat;
import com.bachue.compressmavenplugin.dto.CompressFormat;
import com.bachue.compressmavenplugin.dto.ResultFile;

/**
 * Compress util class
 * @author Alejandro Vivas
 * @version 05/10/2017 0.0.1-SNAPSHOT
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
	public static void createArchive(Archive archive, Collection<ResultFile> resultFiles, Log log) throws IOException, MojoExecutionException
	{
		for (ArchiveFormat format : archive.getFormats())
		{
			createArchive(archive, resultFiles, log, format);
		}
	}

	/**
	 * Create a ar archive
	 * @author Alejandro Vivas
	 * @version 05/10/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param archive Archive to process
	 * @param resultFiles ResultFiles in Archive
	 * @param log Maven loger
	 * @throws IOException If fail to create archive
	 */
	public static void createArchive(Archive archive, Collection<ResultFile> resultFiles, Log log, ArchiveFormat format) throws IOException, MojoExecutionException
	{
		ArchiveOutputStream archiveOutputStream = null;
		try
		{
			String defaultEntension = format.getExtension();
			String outputFileName = archive.getOutputFile().endsWith("." + defaultEntension) ? archive.getOutputFile() : archive.getOutputFile() + "." + defaultEntension;
			log.info("Creating archive:[" + outputFileName  +"]");
			OutputStream fout = null;
			if (format.equals(ArchiveFormat.sevenz))
			{
				archiveOutputStream = new InternalSevenZOutputFile(new File(outputFileName));
			}
			else
			{
				fout = new FileOutputStream(outputFileName);
			}

			switch (format)
			{
				case ar:
					archiveOutputStream = new ArArchiveOutputStream(fout);
				break;

				case cpio:
					archiveOutputStream = new CpioArchiveOutputStream(fout);
				break;

				case tar:
					archiveOutputStream = new TarArchiveOutputStream(fout);
				break;

				case zip:
					archiveOutputStream = new ZipArchiveOutputStream(fout);
				break;

				case jar:
					archiveOutputStream = new JarArchiveOutputStream(fout);
				break;

				default:
					if (!format.equals(ArchiveFormat.sevenz))
					{
						throw new MojoExecutionException("Format: " + format + " is invalid");
					}
			}

			for (ResultFile resultFile : resultFiles)
			{
				String archiveEntryName = resultFile.getOutputFile().substring(1);
				log.info("Adding file:[" + archiveEntryName + "]");

				ArchiveEntry entry = null;

				switch (format)
				{
					case ar:
						entry = new ArArchiveEntry(archiveEntryName, resultFile.getInputFile().length());
					break;

					case cpio:
						entry = new CpioArchiveEntry(archiveEntryName, resultFile.getInputFile().length());
					break;

					case tar:
						entry = new TarArchiveEntry(resultFile.getInputFile(), archiveEntryName);
					break;

					case zip:
						entry = new ZipArchiveEntry(resultFile.getInputFile(), archiveEntryName);
					break;

					case jar:
						entry = new ZipArchiveEntry(resultFile.getInputFile(), archiveEntryName);
					break;

					case sevenz:
						entry = archiveOutputStream.createArchiveEntry(resultFile.getInputFile(), archiveEntryName);
					break;

					default:
						throw new MojoExecutionException("Format: " + format + " is invalid");
				}

				archiveOutputStream.putArchiveEntry(entry);
				archiveOutputStream.write(FileUtil.fileToBytes(resultFile.getInputFile().getAbsolutePath()));
				archiveOutputStream.closeArchiveEntry();
			}
		}
		finally
		{
			close(log, archiveOutputStream);
		}
	}

	/**
	 * Compress each ResultFile.
	 * @author Alejandro Vivas
	 * @version 05/10/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param resultFiles List of ResultFile to compress
	 * @param log Maven logger
	 * @throws IOException If fail to compress a file
	 */
	public static void compress(Collection<ResultFile> resultFiles, Log log) throws IOException, MojoExecutionException
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
				long finalSize = new File(resultFile.getOutputFile() + "." + format.getExtension()).length();
				log.info("End compress in format:[" + format + "] Original Size:" + originalSize + " Final Size:" + finalSize + " Ratio compression:" + ((float) finalSize / (float) originalSize));
			}
		}
	}

	/**
	 * Compress file with brotli
	 * @author Alejandro Vivas
	 * @version 05/10/2017 0.0.1-SNAPSHOT
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
			fout = new FileOutputStream(resultFile.getOutputFile() + "." +  CompressFormat.br.getExtension());
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
	 * @version 05/10/2017 0.0.1-SNAPSHOT
	 * @since 27/09/2017 0.0.1-SNAPSHOT
	 * @param resultFile Object with result file
	 * @param format Format to compress
	 * @param log Maven logger
	 * @throws IOException If fail to compress file
	 */
	private static void compress(ResultFile resultFile, CompressFormat format, Log log) throws IOException, MojoExecutionException
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

	/**
	 * Internal SevenZOutputFile class but this class extends ArchiveOutputStream
	 * @author Alejandro Vivas
	 * @version 5/10/2017 0.0.1-SNAPSHOT
	 * @since 5/10/2017 0.0.1-SNAPSHOT
	 */
	static class InternalSevenZOutputFile extends ArchiveOutputStream
	{
		/** SevenZOutputFile class */
		private SevenZOutputFile sevenZOutputFile;

		/**
		 * @author Alejandro Vivas
		 * @version 5/10/2017 0.0.1-SNAPSHOT
		 * @throws IOException
		 * @since 5/10/2017 0.0.1-SNAPSHOT
		 */
		public InternalSevenZOutputFile(File file) throws IOException
		{
			this.sevenZOutputFile = new SevenZOutputFile(file);
		}

		/*
		 * (non-Javadoc)
		 * @author Alejandro Vivas
		 * @version 5/10/2017 0.0.1-SNAPSHOT
		 * @since 5/10/2017 0.0.1-SNAPSHOT
		 * @see org.apache.commons.compress.archivers.ArchiveOutputStream#putArchiveEntry(org.apache.commons.compress.archivers.ArchiveEntry)
		 */
		@Override
		public void putArchiveEntry(ArchiveEntry entry) throws IOException
		{
			this.sevenZOutputFile.putArchiveEntry(entry);
		}

		/*
		 * (non-Javadoc)
		 * @author Alejandro Vivas
		 * @version 5/10/2017 0.0.1-SNAPSHOT
		 * @since 5/10/2017 0.0.1-SNAPSHOT
		 * @see org.apache.commons.compress.archivers.ArchiveOutputStream#closeArchiveEntry()
		 */
		@Override
		public void closeArchiveEntry() throws IOException
		{
			this.sevenZOutputFile.closeArchiveEntry();
		}

		/*
		 * (non-Javadoc)
		 * @author Alejandro Vivas
		 * @version 5/10/2017 0.0.1-SNAPSHOT
		 * @since 5/10/2017 0.0.1-SNAPSHOT
		 * @see org.apache.commons.compress.archivers.ArchiveOutputStream#finish()
		 */
		@Override
		public void finish() throws IOException
		{
			this.sevenZOutputFile.finish();
		}

		/*
		 * (non-Javadoc)
		 * @author Alejandro Vivas
		 * @version 5/10/2017 0.0.1-SNAPSHOT
		 * @since 5/10/2017 0.0.1-SNAPSHOT
		 * @see org.apache.commons.compress.archivers.ArchiveOutputStream#createArchiveEntry(java.io.File, java.lang.String)
		 */
		@Override
		public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException
		{
			return this.sevenZOutputFile.createArchiveEntry(inputFile, entryName);
		}

		/*
		 * (non-Javadoc)
		 * @author Alejandro Vivas
		 * @version 5/10/2017 0.0.1-SNAPSHOT
		 * @since 5/10/2017 0.0.1-SNAPSHOT
		 * @see java.io.OutputStream#write(byte[])
		 */
		@Override
		public void write(byte[] b) throws IOException
		{
			this.sevenZOutputFile.write(b);
		}

		/*
		 * (non-Javadoc)
		 * @author Alejandro Vivas
		 * @version 5/10/2017 0.0.1-SNAPSHOT
		 * @since 5/10/2017 0.0.1-SNAPSHOT
		 * @see java.io.OutputStream#close()
		 */
		@Override
		public void close() throws IOException
		{
			this.sevenZOutputFile.close();
		}
	}
}
