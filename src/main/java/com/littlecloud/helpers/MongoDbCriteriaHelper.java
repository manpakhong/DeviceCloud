package com.littlecloud.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.littlecloud.dtos.MongoCriteriaAnnotation;
import com.littlecloud.utils.CommonUtils;
import com.mongodb.BasicDBObject;

public class MongoDbCriteriaHelper <T> {
	private static final Logger log = Logger.getLogger(MongoDbCriteriaHelper.class);
	public static final String FIELD_TYPE_INTEGER = "Integer";
	public static final String FIELD_TYPE_STRING = "String";
	public static final String FIELD_TYPE_LONG = "Long";
	public static final String FIELD_TYPE_DATE = "Date";
	public static final String FIELD_TYPE_DOUBLE = "Double";
	
	public BasicDBObject generateSearchJsonSearchBasicDBObject(T object){
		BasicDBObject searchBasicObject = null;
		Class<?> c = object.getClass();
		
		try{
			
			Field [] fields = c.getDeclaredFields();
//			log.debugf("FIFOREPLAY20140918 - MongoDbCriteriaHelper.generateSearchJsonSearchBasicDBObject() - package: %s", c.getPackage().toString());
			searchBasicObject = new BasicDBObject();
			Map<String, BasicDBObject> basicDbObjectElementHashMap = new HashMap<String, BasicDBObject>();
			for (Field field: fields){
//				log.debugf("FIFOREPLAY20140918 - MongoDbCriteriaHelper.generateSearchJsonSearchBasicDBObject() - field: %s, type: %s", field.getName(), field.getType().getName());
				field.setAccessible(true);

				Object value = field.get(object);
				
				if (value != null){		    		
					Annotation [] annotations = field.getAnnotations(); 
					for (Annotation annotation: annotations){
						if(annotation instanceof MongoCriteriaAnnotation){
					    	MongoCriteriaAnnotation mongoCriteriaAnnotation = (MongoCriteriaAnnotation) annotation;
					    	
					    	if (mongoCriteriaAnnotation.rangeType().equals(MongoCriteriaAnnotation.RANGE_TYPE_NONE)){
					    		if (value instanceof List){
				    				List valueList =  (List) value;
				    				if (valueList != null && valueList.size() > 0){
				    					searchBasicObject.put(mongoCriteriaAnnotation.fieldName(), new BasicDBObject("$in", valueList));
				    				}

					    		} else {
						    		searchBasicObject.put(mongoCriteriaAnnotation.fieldName(), value);					    			
					    		}
					    		
					    	} else {
					    		String criteriaFieldType = field.getType().getName();
					    		String mongoFieldType = mongoCriteriaAnnotation.fieldType();

			    				Date dateValue = (Date) value;
					    		if (mongoCriteriaAnnotation.rangeType().equals(MongoCriteriaAnnotation.RANGE_TYPE_FROM)){
					    			if (isCriteriaFromDate2Numeric(criteriaFieldType, mongoFieldType)){
					    				Integer intValue = CommonUtils.convertDate2UnixTime(dateValue);
					    				basicDbObjectElementHashMap.put(mongoCriteriaAnnotation.fieldName(), new BasicDBObject("$gte", intValue));
					    			} else {
					    				basicDbObjectElementHashMap.put(mongoCriteriaAnnotation.fieldName(), new BasicDBObject("$gte", dateValue));
					    			}
						    	} else if (mongoCriteriaAnnotation.rangeType().equals(MongoCriteriaAnnotation.RANGE_TYPE_TO)){
					    			if (isCriteriaFromDate2Numeric(criteriaFieldType, mongoFieldType)){
					    				Integer intValue = CommonUtils.convertDate2UnixTime(dateValue);
					    				BasicDBObject basicDbObject = basicDbObjectElementHashMap.get(mongoCriteriaAnnotation.fieldName());
					    				if (basicDbObject != null && basicDbObject.get("$gte") != null){
					    					Integer intValueFrom = (Integer) basicDbObject.get("$gte");
					    					basicDbObjectElementHashMap.put(mongoCriteriaAnnotation.fieldName(), new BasicDBObject("$gte", intValueFrom).append("$lte", intValue));
					    				} else {
					    					basicDbObjectElementHashMap.remove(mongoCriteriaAnnotation.fieldName());
					    				}

					    			} else {
					    				basicDbObjectElementHashMap.put(mongoCriteriaAnnotation.fieldName(), new BasicDBObject("$lte", dateValue));
					    				
					    				BasicDBObject basicDbObject = basicDbObjectElementHashMap.get(mongoCriteriaAnnotation.fieldName());
					    				if (basicDbObject != null && basicDbObject.get("$gte") != null){
					    					Date dateValueFrom = (Date) basicDbObject.get("$gte");
					    					basicDbObjectElementHashMap.put(mongoCriteriaAnnotation.fieldName(), new BasicDBObject("$gte", dateValueFrom).append("$lte", dateValue));
					    				} else {
					    					basicDbObjectElementHashMap.remove(mongoCriteriaAnnotation.fieldName());
					    				}
					    			}
						    	}						    		

					    	}	
					    }
					}
				}
			} // end for (Field field: fields)
			
			searchBasicObject.putAll(basicDbObjectElementHashMap);
		} catch (Exception e){
			log.error("FIFOREPLAY20140918 - MongoDbCriteriaHelper.generateSearchJsonSearchBasicDBObject() - ", e);
		}
		return searchBasicObject;
	}
	
	private boolean isCriteriaFromDate2Numeric(String criteriaType, String mongoType){
		boolean isFromDate2Integer = false;
		
		boolean isCriteriaTypeDate = false;
		boolean isMongoTypeNumeric = false;
		if (isDateType(criteriaType)){
			isCriteriaTypeDate = true;
		}
		
		if (!isStringType(mongoType) && !isDateType(mongoType)){
			isMongoTypeNumeric = true;
		}
		
		if (isCriteriaTypeDate && isMongoTypeNumeric){
			isFromDate2Integer = true;
		}
		
		return isFromDate2Integer;
	}
	
	private boolean isStringType(String type){
		boolean isStringType = false;
		if (type.indexOf(FIELD_TYPE_STRING) >= 0 ){
			isStringType = true;
		}
		return isStringType;
	}
	private boolean isDateType(String type){
		boolean isDateType = false;
		if (type.indexOf(FIELD_TYPE_DATE) >= 0 ){
			isDateType = true;
		}
		return isDateType;
	}
}
