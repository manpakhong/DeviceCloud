package com.littlecloud.ac.logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.PropertyService;

public class AcLogUtils {
	
	private static final Logger log = Logger.getLogger(AcLogUtils.class);	
	
	private static PropertyService<AcLogUtils> ps = new PropertyService<AcLogUtils>(AcLogUtils.class);
	private static final List<String> AcWtpPattern = initAcWtpPattern();
	private static final List<String> WtpAcPattern = initWtpAcPattern();
	
	private static List<String> initAcWtpPattern() {
		List<String> result = Arrays.asList(ps.getString("AcWtpPattern").split(","));		
		return result;
	}
	
	private static List<String> initWtpAcPattern() {
		List<String> result = Arrays.asList(ps.getString("WtpAcPattern").split(","));		
		return result;
	}
	
	private static boolean isMatchAcWtpPattern(String content, List<String> patternLst) {
		for (String pattern:patternLst)
		{
			if (content.contains(pattern))
				return true;
		}
		return false;
	}
			
	public static void readGzipFile(String filepath)
	{			
		BufferedReader br = null;
		GZIPInputStream gzip = null;
		String content = null;
		
		if (filepath==null || filepath.isEmpty())
			return;
				
		try {
			gzip = new GZIPInputStream(new FileInputStream(filepath));
			br = new BufferedReader(new InputStreamReader(gzip));
			while ((content = br.readLine()) != null)
			{
				if (isMatchAcWtpPattern(content, AcWtpPattern) || isMatchAcWtpPattern(content, WtpAcPattern))
				{
					log.info(content);
					
					Pattern pattern = Pattern.compile("\\[(.*)\\].*");
					Matcher matcher = pattern.matcher(content);
					if (matcher.find()){
						log.debugf("matched %s", matcher.group(1));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						try {
							Date date = sdf.parse(matcher.group(1));							
							log.debugf("date %s", date);
						} catch (ParseException e) {
							log.error("ParseException", e);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br!=null)
					br.close();
			} catch (IOException e) {
				log.error("IOException", e);
			}
		}
	}
	
	/* read dir */
	public static List<String> readFileNames(String filepath)
	{
		List<String> fileLst = new ArrayList<String>();
		
		File dir = new File(filepath);
		if (!dir.isDirectory())
			return fileLst;
		
		List<File> files = (List<File>) FileUtils.listFiles(dir, null, true);
		if (files!=null && !files.isEmpty())
		{
			for (File file:files)
				fileLst.add(file.getName());
		}
		
		return fileLst;
	}

}
