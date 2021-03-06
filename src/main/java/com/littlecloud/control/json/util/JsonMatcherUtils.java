package com.littlecloud.control.json.util;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;

public class JsonMatcherUtils {

	private static final Logger log = Logger.getLogger(JsonMatcherUtils.class);

	public static String JsonMatcherRemove(String json, String patternstart, String patternend) {
	
		if (json == null) 
			return null;
		
		String jsonString = json.replace(" ", "");;
		Pattern pattern_s = Pattern.compile(patternstart);
		Matcher matcher = pattern_s.matcher(jsonString);
		
		int start=0, end=0;		
		ArrayList<CharSequence> matchedJsonStrings = new ArrayList<CharSequence>();
		
		while(matcher.find())
		{
			start = matcher.start();
		    
		    Pattern pattern_e = Pattern.compile(patternend);
		    Matcher matcher_e = pattern_e.matcher(jsonString);		
			
		    while(matcher_e.find())
		    {
				end = matcher_e.end();
				if (end>start) break;
			}
			CharSequence matchedJsonString = jsonString.subSequence(start, end);
			log.debugf("JsonMatcherUtils - matchedJsonString = %s", matchedJsonString);
			
			matchedJsonStrings.add(matchedJsonString);
		} 
		
		String formatstring = null;
		for (CharSequence mt : matchedJsonStrings)
		{
//			System.out.println(mt);
			formatstring = jsonString.replace(mt, "");
			jsonString = formatstring;
		}
		
		return formatstring;
		
	}
	
	public static ArrayList<String> JsonMatcherGet(String json, String patternstart, String patternend) {
		if (json == null) return null;
		String jsonString = json.replace(" ", "");
		Pattern pattern_s = Pattern.compile(patternstart);
		Matcher matcher = pattern_s.matcher(jsonString);
		int start=0, end=0;
		CharSequence matchedJsonString = null;
		
		while(matcher.find()) {
			start = matcher.end();//matcher.start();
		} 
		
		Pattern pattern_e = Pattern.compile(patternend);
		matcher = pattern_e.matcher(jsonString);		
		while(matcher.find()) {
			end = matcher.end();
			if (end>start) break;
		}
		matchedJsonString = jsonString.subSequence(start+1, end-1);
		log.debugf("JsonMatcherUtils - matchedJsonString = %s", matchedJsonString);
		String[] getstring = ((String) matchedJsonString).split(",");
		ArrayList<String> formatstring = new ArrayList<String>();
		int i =0;
		while (i < getstring.length) {
			formatstring.add(getstring[i]);
			i++;
		}
		return formatstring;
		
	}
	
public static String JsonMatcherRemoveMap(String json, String pattern) {
		
		if (json == null) 
			return null;
		
		String jsonString = json.replace(" ", "");
		Pattern pattern_s = Pattern.compile(pattern);
		Matcher matcher = pattern_s.matcher(jsonString);
		
		int start=0, end=0;		
		String formatstring = null;
		CharSequence matchedJsonString = null;
		while(matcher.find())
		{
			start = matcher.start();
			end = matcher.end();
		   System.out.println(start + "-" + end);
			if (start == 0 )
			{
				if (end == (jsonString.length())) /* map */
					return "none";
				else /* map|... */
				{
					matchedJsonString = jsonString.subSequence(start, end+1);
				}
			}
			
			else /* ...|map... */
			{
				matchedJsonString = jsonString.subSequence(start-1, end);
			}			
			log.debugf("JsonMatcherUtils - matchedJsonString = %s", matchedJsonString);			
		} 
		
		if (matchedJsonString == null)
			return jsonString;
		
		formatstring = jsonString.replace(matchedJsonString, "");		
		return formatstring;
		
	}
	
	public static String JsonMatcherRemovePortal_ic2(String json, String pattern) {
	
		if (json == null)
			return null;
	
		String jsonString = json.replace(" ", "");
		Pattern pattern_s = Pattern.compile(pattern);
		Matcher matcher = pattern_s.matcher(jsonString);
	
		int start = 0, end = 0;
		String formatstring = null;
		CharSequence matchedJsonString = null;
		while (matcher.find()) {
			start = matcher.start();
			end = matcher.end();
			System.out.println(start + "-" + end);
			if (start == 0) {
				if (end == (jsonString.length())) /* portal_ic2 */
					return "none";
				else /* portal_ic2|... */
				{
					matchedJsonString = jsonString.subSequence(start, end + 1);
				}
			}
	
			else /* ...|portal_ic2... */
			{
				matchedJsonString = jsonString.subSequence(start - 1, end);
			}
			log.debugf("JsonMatcherUtils - matchedJsonString = %s",
					matchedJsonString);
		}
	
		if (matchedJsonString == null)
			return jsonString;
	
		formatstring = jsonString.replace(matchedJsonString, "");
		return formatstring;
	
	}
	
	public static boolean isFieldExist(String json, String pattern) {
		
		if (json == null) 
			return false;
		
		String jsonString = json.replace(" ", "");
		Pattern pattern_s = Pattern.compile(pattern);
		Matcher matcher = pattern_s.matcher(jsonString);
	
		while(matcher.find())
		{
			log.debug("isFieldExist = true, " + json);
			return true;	
		} 
		
		log.debug("isFieldExist = false, " + json);		
		return false;		
	}
		
	
	public static void main(String[] args) {
		
		/* **** Test JsonMatcherRemove **** */
		//String TunnelStatJson = "{\"stat\": \"ok\", \"response\": { \"tunnel_order\": [ \"23_43442\" ], \"tunnel\": { \"23_43442\": { \"1\": { \"rtt\": 1, \"stime\": 145193, \"tx\": [ 6913524, null, null, null, null, null, null, 6917288 ], \"nanostime\": 938839666, \"ack_miss\": [ 0, null, null, null, null, null, null, 0 ], \"rx\": [ 3717360, null, null, null, null, null, null, 3718320 ], \"state\": \"HC_OK\", \"name\": \"WAN 1\" }, \"2\": { \"rtt\": 4, \"stime\": 145193, \"tx\": [ 6913156, null, null, null, null, null, null, 6917632 ], \"nanostime\": 938832877, \"ack_miss\": [ 0, null, null, null, null, null, null, 0 ], \"rx\": [ 3717396, null, null, null, null, null, null, 3718748 ], \"state\": \"HC_OK\", \"name\": \"WAN 2\" }}, \"order\": [ 1, 2 ] }}}";//
//		String TunnelStatJson = "\"tunnel_order\": [ \"6_1\", \"4_1\", \"5_1\", \"1_1\", \"3_1\", \"2_1\" ], \"tunnel\": { \"1_1\": { \"1\": { \"tx\": [ 79860 ], \"stime\": 20807, \"nanostime\": 673237097, \"rx\": [ 41216 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 1\", \"state\": \"HC_OK\" }, \"2\": { \"tx\": [ 79372 ], \"stime\": 20807, \"nanostime\": 673232348, \"rx\": [ 41284 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 2\", \"state\": \"HC_OK\" }, \"3\": { \"tx\": [ 79500 ], \"stime\": 20807, \"nanostime\": 673213142, \"rx\": [ 41532 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 3\", \"state\": \"HC_OK\" }, \"order\": [ 1, 2, 3 ] }, \"5_1\": { \"1\": { \"tx\": [ 83360 ], \"stime\": 20807, \"nanostime\": 668249407, \"rx\": [ 43820 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 1\", \"state\": \"HC_OK\" }, \"2\": { \"tx\": [ 83380 ], \"stime\": 20807, \"nanostime\": 668244518, \"rx\": [ 43720 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 2\", \"state\": \"HC_OK\" }, \"3\": { \"tx\": [ 83804 ], \"stime\": 20807, \"nanostime\": 668236836, \"rx\": [ 43948 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 3\", \"state\": \"HC_OK\" }, \"order\": [ 1, 2, 3 ] }, \"6_1\": { \"1\": { \"tx\": [ 80860 ], \"stime\": 20807, \"nanostime\": 657842688, \"rx\": [ 43852 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 1\", \"state\": \"HC_OK\" }, \"2\": { \"tx\": [ 80960 ], \"stime\": 20807, \"nanostime\": 657828091, \"rx\": [ 43996 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 2\", \"state\": \"HC_OK\" }, \"3\": { \"tx\": [ 81008 ], \"stime\": 20807, \"nanostime\": 657813774, \"rx\": [ 43992 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 3\", \"state\": \"HC_OK\" }, \"order\": [ 1, 2, 3 ] }, \"3_1\": { \"1\": { \"tx\": [ null, null, null, null, 77220 ], \"stime\": 20807, \"nanostime\": 678213893, \"rx\": [ null, null, null, null, 39740 ], \"rtt\": 1, \"ack_miss\": [ null, null, null, null, 0 ], \"name\": \"WAN 1\", \"state\": \"HC_OK\" }, \"2\": { \"tx\": [ null, null, null, null, 78360 ], \"stime\": 20807, \"nanostime\": 678209074, \"rx\": [ null, null, null, null, 40208 ], \"rtt\": 4, \"ack_miss\": [ null, null, null, null, 0 ], \"name\": \"WAN 2\", \"state\": \"HC_OK\" }, \"3\": { \"tx\": [ null, null, null, null, 77912 ], \"stime\": 20807, \"nanostime\": 678191543, \"rx\": [ null, null, null, null, 39956 ], \"rtt\": 1, \"ack_miss\": [ null, null, null, null, 0 ], \"name\": \"WAN 3\", \"state\": \"HC_OK\" }, \"order\": [ 1, 2, 3 ] }, \"4_1\": { \"1\": { \"tx\": [ 80952 ], \"stime\": 20807, \"nanostime\": 663116377, \"rx\": [ 41552 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 1\", \"state\": \"HC_OK\" }, \"2\": { \"tx\": [ 80028 ], \"stime\": 20807, \"nanostime\": 663101850, \"rx\": [ 41312 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 2\", \"state\": \"HC_OK\" }, \"3\": { \"tx\": [ 80240 ], \"stime\": 20807, \"nanostime\": 663090326, \"rx\": [ 41656 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 3\", \"state\": \"HC_OK\" }, \"order\": [ 1, 2, 3 ] }, \"2_1\": { \"1\": { \"tx\": [ 82420 ], \"stime\": 20807, \"nanostime\": 683270306, \"rx\": [ 42484 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 1\", \"state\": \"HC_OK\" }, \"2\": { \"tx\": [ 83676 ], \"stime\": 20807, \"nanostime\": 683265487, \"rx\": [ 42600 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 2\", \"state\": \"HC_OK\" }, \"3\": { \"tx\": [ 82960 ], \"stime\": 20807, \"nanostime\": 683257665, \"rx\": [ 42804 ], \"rtt\": 1, \"ack_miss\": [ 0 ], \"name\": \"WAN 3\", \"state\": \"HC_OK\" }, \"order\": [ 1, 2, 3 ] } }";
//		String patternstart = ",\"order\":";		
//		String patternend = "]";
//		ArrayList<String> out = JsonMatcherGet(TunnelStatJson, patternstart, patternend);
//		String formated_data = JsonMatcherRemove(TunnelStatJson, patternstart, patternend);
//		System.out.println(formated_data);
	
		
		/* **** Test JsonMatcherRemoveMap **** */
		String pattern = "map";
		//All the test cases
		String feature1 = "wifi|portal_ic2|map|pepvpn";
		String feature2 = "map|wifi|portal_ic2|pepvpn";
		String feature3 = "wifi|portal_ic2|pepvpn|map";
		String feature4 = "wifi|portal_ic2|pepvpn";
		String feature5 = "map";
		String feature6 = "none";
		String feature7 = "";
		String feature8 = null;
		
		System.out.println(feature1+" =>"+JsonMatcherRemoveMap(feature1, pattern));
		System.out.println(feature2+" =>"+JsonMatcherRemoveMap(feature2, pattern));
		System.out.println(feature3+" =>"+JsonMatcherRemoveMap(feature3, pattern));
		System.out.println(feature4+" =>"+JsonMatcherRemoveMap(feature4, pattern));
		System.out.println(feature5+" =>"+JsonMatcherRemoveMap(feature5, pattern));
		System.out.println(feature6+" =>"+JsonMatcherRemoveMap(feature6, pattern));
		System.out.println(feature7+" =>"+JsonMatcherRemoveMap(feature7, pattern));
		System.out.println(feature8+" =>"+JsonMatcherRemoveMap(feature8, pattern));
	}

}
