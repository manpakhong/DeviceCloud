package com.littlecloud.control.json;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.jboss.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.littlecloud.control.json.util.JsonUtils;

public class DateDeserializer implements JsonDeserializer<Date> {

	private static final Logger log = Logger.getLogger(DateDeserializer.class);
	
	private TimeZone timezone;
	
	public DateDeserializer(TimeZone timezone) {
		super();
		this.timezone = timezone;
	}

	@Override
	public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		String date = element.getAsString();

		SimpleDateFormat formatter = new SimpleDateFormat(JsonUtils.jsonUtcDateFormat);
		//formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		formatter.setTimeZone(timezone);

		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			log.error("Failed to parse Date due to:", e);
		}
		return null;
	}
}
