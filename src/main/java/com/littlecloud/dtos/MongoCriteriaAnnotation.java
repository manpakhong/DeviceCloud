package com.littlecloud.dtos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MongoCriteriaAnnotation {
	public String fieldType();
	public String fieldName();
	public String rangeType();
	public static final String RANGE_TYPE_FROM = "from";
	public static final String RANGE_TYPE_TO = "to";
	public static final String RANGE_TYPE_NONE = "none";
	public static final String FIELD_TYPE_INTEGER = "Integer";
	public static final String FIELD_TYPE_STRING = "String";
	public static final String FIELD_TYPE_LONG = "Long";
	public static final String FIELD_TYPE_DATE = "Date";
	public static final String FIELD_TYPE_DOUBLE = "Double";
}
