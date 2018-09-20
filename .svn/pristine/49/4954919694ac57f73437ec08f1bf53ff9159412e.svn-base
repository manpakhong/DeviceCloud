package com.littlecloud.utils;

import java.io.File;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipArchiver;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.jboss.logging.Logger;

public class ArchiverUtils extends PlexusTestCase{
	private static final String ARCHIVER_FILE_DESC = "config.txt";
	private static final Logger log = Logger.getLogger(ArchiverUtils.class);
	
	
	public void extractTgzFile2(){
		TarArchiveOutputStream out;
	}
	
	
	public void extractTgzFile(File sourceFile, File destFile){
		try{			
			if (sourceFile != null && destFile != null){
				TarGZipUnArchiver ua = (TarGZipUnArchiver) lookup( UnArchiver.ROLE, "tgz" );
				ua.setSourceFile(sourceFile);		
				
				ua.setDestDirectory(destFile);

				ua.extract();
			} else {
				throw new Exception("Source File or Destinated File must be specified.");
			}
		} catch (Exception e){
			log.error("CRYPTO20140313 - string2File ", e);
		}
	} // end extractTgzFile
	
	public void archive2TarFile(File sourceFile, File destFile){
		try{			
			if (sourceFile != null && destFile != null){
		        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
		        tarArchiver.setDestFile(destFile);
		        tarArchiver.addFile(sourceFile, ARCHIVER_FILE_DESC);
		        tarArchiver.createArchive();
				
			} else {
				throw new Exception("Source File or Destinated File must be specified.");
			}
		} catch (Exception e){
			log.error("CRYPTO20140313 - string2File ", e);
		}
	}
	
	public void archive2GzFile(File sourceFile, File destFile){
		try{			
			if (sourceFile != null && destFile != null){
				GZipArchiver gzipArchiver = (GZipArchiver) lookup (Archiver.ROLE, "gzip");
				gzipArchiver.setDestFile(destFile);
				gzipArchiver.addFile(sourceFile, ARCHIVER_FILE_DESC);
				gzipArchiver.createArchive();
				
			} else {
				throw new Exception("Source File or Destinated File must be specified.");
			}
		} catch (Exception e){
			log.error("CRYPTO20140313 - string2File ", e);
		}
	}
	
}
