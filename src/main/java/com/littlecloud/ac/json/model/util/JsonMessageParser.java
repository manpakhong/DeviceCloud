package com.littlecloud.ac.json.model.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.littlecloud.ac.json.model.Json_WanIpInfo;
import com.littlecloud.ac.messagehandler.DdnsMessageHandler;
import com.littlecloud.utils.CommonUtils;

public class JsonMessageParser {
	private static final Logger log = Logger.getLogger(JsonMessageParser.class);
	public static List<Json_WanIpInfo> parseWanIpInfo(String json){
		List<Json_WanIpInfo> jsonWanIpInfoList = null;
		try{
			if (json != null && !json.isEmpty()){
				if (isValidateJsonFormat(json)){
					JsonParser jsonParser = new JsonParser();
					JsonObject jo = (JsonObject) jsonParser.parse(json);
					JsonObject resObject = jo.get("response").getAsJsonObject();
					JsonArray orderArray = resObject.get("order").getAsJsonArray(); // order is the index of inner Json Object
					Gson gson = new Gson();
					
					if (orderArray != null && orderArray.size() > 0){
						jsonWanIpInfoList = new ArrayList<Json_WanIpInfo>();
						for (JsonElement jElement: orderArray){
							String indexName = jElement.toString();
							JsonObject idxObj = resObject.get(indexName).getAsJsonObject();
							Json_WanIpInfo jsonWanIpInfo = gson.fromJson(idxObj.toString(), Json_WanIpInfo.class);
							if (jsonWanIpInfo != null){
								if (CommonUtils.isNumeric(indexName)){
									int id = Integer.parseInt(indexName);
									jsonWanIpInfo.setId(id);									
								}
								jsonWanIpInfoList.add(jsonWanIpInfo);
							}					
						}	
					}
				} // end if (isValidateJsonFormat(json))
			}
		} catch (Exception e){
			log.error("DDNS20140402 - parseWanIpInfo()",e);
		}
		return jsonWanIpInfoList;
	} // end parseWanIpInfo
	
	private static boolean isValidateJsonFormat(String json){
		boolean isValid = false;
		
		int idxOfResponse = json.indexOf("response");
		int idxOfOrder = json.indexOf("order");
		
		if (idxOfResponse > -1 && idxOfOrder > -1){
			isValid = true;
		}
		
		return isValid;
	} // end isValidateJsonFormat
} // end class
