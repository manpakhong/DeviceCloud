package com.littlecloud.control.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class JsonExclusionStrategy implements ExclusionStrategy {

	private final Class<?> typeToExclude;

	public JsonExclusionStrategy(Class<?> typeToExclude) {
		this.typeToExclude = typeToExclude;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return (this.typeToExclude != null && this.typeToExclude == clazz);
				//|| clazz.getAnnotation(GsonExclude.class) != null;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(JsonExclude.class) != null;
	}

}