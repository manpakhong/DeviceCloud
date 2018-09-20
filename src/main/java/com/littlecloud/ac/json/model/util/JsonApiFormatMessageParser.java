package com.littlecloud.ac.json.model.util;

import org.jboss.logging.Logger;

import com.google.gson.Gson;

public class JsonApiFormatMessageParser<T> {
	private static final Logger log = Logger
			.getLogger(JsonApiFormatMessageParser.class);
	private Class<T> reference;
	
	public JsonApiFormatMessageParser(Class<T> classRef){
		reference = classRef;
	}
	
	public T getNewInstance(){
		try{
			return reference.newInstance();
		} catch (Exception e){
			log.errorf(e, "JsonApiFormatMessageParser - generic parser error!");
		}
		return null;
	}
	
	
	public <T> T parseResponse(String json) {
		Gson gson = new Gson();
		Object obj = getNewInstance();
		
		return (T) gson.fromJson(json.toString(), reference);			
	}
}
