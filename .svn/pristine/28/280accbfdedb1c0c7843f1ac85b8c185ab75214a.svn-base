package com.littlecloud.ac.logparser;

import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.PropertyService;

/* parse capwap.log and save to tokudb for data recovery */
public class AcLogParserMain {
	
	private static final Logger log = Logger.getLogger(AcLogParserMain.class);
	
	private static PropertyService<AcLogParserMain> ps = new PropertyService<AcLogParserMain>(AcLogParserMain.class);
	private static final String logpath = ps.getString("logpath");

	public static void main(String[] args) {
		
		log.info(AcLogUtils.readFileNames(logpath));
		
		
		
		AcLogUtils.readGzipFile(logpath+"/"+"capwap.log.test.gz");
	}

}
