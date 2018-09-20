package com.littlecloud.control.json.model.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* Need further lookup class for device other than BR1 */

@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {
	String name();		// empty name is often used for parsing list structure 
	String valueMap();	// Use to assign value mapping (empty indicates no mapping is required). e.g. property format K1=V1|K2=V2. 
	boolean isArray() default false;
	boolean isCapitalValue() default false;
}