package com.littlecloud.control.json;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.jboss.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.littlecloud.control.json.util.JsonUtils;

public class DateSerializer implements JsonSerializer<Date> {

	private static final Logger logger = Logger.getLogger(DateSerializer.class);
	
	private TimeZone timezone;
	
	public DateSerializer(TimeZone timezone) {
		super();
		this.timezone = timezone;
	}

	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		SimpleDateFormat df = new SimpleDateFormat(JsonUtils.jsonUtcDateFormat);
		//df.setTimeZone(TimeZone.getTimeZone("UTC"));
		df.setTimeZone(timezone);

		logger.info("src.toString()=" + src.toString());

		return new JsonPrimitive(df.format(src));
		
	}
}