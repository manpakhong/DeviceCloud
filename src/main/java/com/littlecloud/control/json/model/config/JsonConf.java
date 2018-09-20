package com.littlecloud.control.json.model.config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.config.JsonConf_RadioSettings.Modules;
import com.littlecloud.control.json.model.config.util.ProductConfigConversionUtils;

public abstract class JsonConf {

	private static final Logger log = Logger.getLogger(JsonConf.class);

	public enum CONFIG_TYPE { MAX, AP, UNKNOWN };
	
	protected static final int GLOBAL_DEV_INDEX = -1;

	protected boolean traverseConfig(ConcurrentHashMap<Integer, String> confJsonMap, String prefix, Object parent, Class cls, Integer devId, CONFIG_TYPE configType)
			throws Exception
	{
		//log.debugf("******************* Parent = " + parent + " (prefix: %s) *******************", prefix);
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields)
		{
			/* only process ConfigValue annotation */
			if (field.isAnnotationPresent(ConfigValue.class))
			{
				ConfigValue config = field.getAnnotation(ConfigValue.class);
				String configName = config.name();

				//log.debugf("ConfigValue=%s, name=%s, valueMap=%s", config, config.name(), config.valueMap());

				if (field.getType() == List.class)
				{
					//log.debug("processing ANNOTATED List field " + field.getName());

					/* traverse list */
					Type type = field.getGenericType();
					if (type instanceof ParameterizedType)
					{
						/* get list type. e.g. List<Modules> */
						ParameterizedType pType = (ParameterizedType) type;
						if (pType.getActualTypeArguments()[0] == Modules.class)
						{
							/* sort Modules object by deviceId */
							List<Modules> objLst = (ArrayList<Modules>) PropertyUtils.getNestedProperty(parent, field.getName());
							if (objLst!=null)
							{
								/* sort by device id */
								Collections.sort(objLst);
								int pDevId = -99;
								int numRadio = 0;
								for (Modules m : objLst)
								{
									if (pDevId == m.getDevice_id())
										numRadio++;
									else
										numRadio = 1;
									//log.debug("********* " + m.getDevice_id() + " radio " + numRadio);
	
									if (numRadio == 1)
										traverseConfig(confJsonMap, "", m, m.getClass(), m.getDevice_id(), configType);
									else
										traverseConfig(confJsonMap, configName + numRadio + "_", m, m.getClass(), m.getDevice_id(), configType);
	
									pDevId = m.getDevice_id();
	
								}
							}
						}
						else
						{
							List<Object> objLst = (List<Object>) PropertyUtils.getNestedProperty(parent, field.getName());
							if (objLst != null)
							{
								if (pType.getActualTypeArguments()[0] == String.class)
								{
									/* String list e.g. ACL */
									
									/* load dev config */
									StringBuilder sbDev = new StringBuilder();
									String devConfig = confJsonMap.get(devId);
									if (devConfig != null)
										sbDev.append(devConfig);
									
									/* suppose no value mapping for nestedproperty */
									ProductConfigConversionUtils.ConfigKeyValue keyValue = ProductConfigConversionUtils.convertKeyValue(configName, null, configType);
									if (keyValue!=null)
									{
										String lookupConfigName = keyValue.getKey();
										String newConfig = prefix + lookupConfigName + "=\"";
										for (Object s: objLst)
										{
											if (config.isCapitalValue()) 
												newConfig += String.valueOf(s).toUpperCase()+" ";
											else
												newConfig += s+" ";
										}
										
										newConfig = newConfig.trim() + "\"\n";
										
										sbDev.append(newConfig);
										// log.debugf("adding (devId, config)=(%s,%s)", devId, sbDev.toString());
										//log.debugf("adding string list (devId, config)=(%s,%s)", devId, newConfig.trim());
	
										confJsonMap.put(devId, sbDev.toString());
									}
									else
									{
										log.debugf("configName %s is not required for the configType %s (1)", configName, configType);
									}
								}
								else
								{
									for (Object s : objLst)
									{
										/* handle list by type */
										if (pType.getActualTypeArguments()[0] == JsonConf_SsidProfiles.class)
											traverseConfig(confJsonMap, configName + ((JsonConf_SsidProfiles) s).getSsid_id() + "_", s, s.getClass(), GLOBAL_DEV_INDEX, configType);
										else if (pType.getActualTypeArguments()[0] == JsonConf_SsidProfiles.RadiusServer.class)
											traverseConfig(confJsonMap, prefix + configName + ((JsonConf_SsidProfiles.RadiusServer) s).getId() + "_", s, s.getClass(), GLOBAL_DEV_INDEX, configType);
										else if (pType.getActualTypeArguments()[0] == JsonConf_PepvpnProfilesNew.Profile.class)
											traverseConfig(confJsonMap, configName + ((JsonConf_PepvpnProfilesNew.Profile) s).getId() + "_", s, s.getClass(), GLOBAL_DEV_INDEX, configType);
									}
								}
							}
						}
					} else {
						// log.debug("Non-ParameterizedType: " + field.getType());
					}
				}
				else
				{
					//log.debugf("processing ANNOTATED field %s (isMemberClass=%s)", field.getName(), field.getType().isMemberClass());

					if (PropertyUtils.getNestedProperty(parent, field.getName()) != null)
					{
						Type type = field.getGenericType();
						//log.debug("Type.toString()=" + type.toString());

						if (!field.getType().isEnum() && field.getType().isMemberClass())
						{
							/* parse member. e.g. wep, ssid_profiles, modules */
							//log.debug("doing MemberClass " + field.getType());
							Object obj = PropertyUtils.getNestedProperty(parent, field.getName());
							traverseConfig(confJsonMap, prefix + (configName.length() != 0 ? configName + "_" : ""), obj, obj.getClass(), GLOBAL_DEV_INDEX, configType);
						}
						else
						{
							/* use getter and process final output */
							//log.debug("SECURITY doing Non-MemberClass " + field.getType());

							/* load dev config */
							StringBuilder sbDev = new StringBuilder();
							String devConfig = confJsonMap.get(devId);
							if (devConfig != null)
							{
								//log.debugf("SECURITY append devConfig = %s", devConfig);
								sbDev.append(devConfig);
							}
							
							String newConfig = null;							
							String lookupConfigName = null;
							String lookupConfigValue = null;							
							
							String result = PropertyUtils.getNestedProperty(parent, field.getName()).toString();
							if (config.valueMap() != null && config.valueMap().length() != 0)
							{
//								log.debugf("resultB4=%s", result);
//								log.debugf("SECURITY config.name=%s, config.valueMap=%s, result=%s", config.name(), config.valueMap(), result);
//								log.debugf("SECURITY properties=%s", valueMapToProperties(config.valueMap()));
								String lookup = valueMapToProperties(config.valueMap()).getProperty(result);
								
								ProductConfigConversionUtils.ConfigKeyValue keyValue = ProductConfigConversionUtils.convertKeyValue(configName, lookup, configType);
								if (keyValue == null)
								{
									if (log.isDebugEnabled())
										log.debugf("configName %s is not required for the configType %s (2)", configName, configType);
									result = null;
								}
								else
								{
									lookupConfigName = keyValue.getKey();
									lookupConfigValue = keyValue.getValue();
									lookup = lookupConfigValue;
									
									newConfig = prefix + lookupConfigName + "=\"";
									
									if (lookup!=null && !lookup.isEmpty())
									{
										lookup = config.isCapitalValue() == true ? lookup.toUpperCase() : lookup;
										result = lookup;									
									}
									else
									{
										result = "";
									}
								}
								//log.debugf("SECURITY result=%s", result);
							}
							else
							{
								/* no value lookup for nestedproperty */
								ProductConfigConversionUtils.ConfigKeyValue keyValue = ProductConfigConversionUtils.convertKeyValue(configName, null, configType);
								if (keyValue == null)
								{
									if (log.isDebugEnabled())
										log.debugf("configName %s is not required for the configType %s (3)", configName, configType);
									result = null;
								}
								else
								{
									lookupConfigName = keyValue.getKey();
									newConfig = prefix + lookupConfigName + "=\"";
									
									result = String.valueOf(PropertyUtils.getNestedProperty(parent, field.getName()));
								}
							}
							
							if (result != null)
							{
								if (config.isCapitalValue()) {
									result = result.toUpperCase();
								}

								newConfig += result + "\"\n";
								
								if (result.compareToIgnoreCase("${setnull}")!=0)
									sbDev.append(newConfig);
							}
//							else
//							{
//								newConfig += "\"\n";
//								sbDev.append(newConfig);
//							}
							
							//log.debugf("SECURITY adding (devId, config)=(%s,%s)", devId, newConfig.trim());

							confJsonMap.put(devId, sbDev.toString());

						}
					}
				}
			}
			else
			{
				// log.debug("processing Non-ANNOTATED List field " + field.getName());
			}
		}

		return true;
	}

	public boolean updateWith(Object update) {

		if (!this.getClass().isAssignableFrom(update.getClass())) {
			log.warn("update class is not assignable");
			return false;
		}

		Method[] methods = this.getClass().getMethods();

		for (Method fromMethod : methods) {
			if ( (fromMethod.getDeclaringClass().equals(this.getClass())
					|| fromMethod.getDeclaringClass()==this.getClass().getSuperclass())
					&& (fromMethod.getName().matches("^get[A-Z].*$") || fromMethod.getName().matches("^is[A-Z].*$"))) {
								
				String fromName = fromMethod.getName();

				if (fromMethod.getReturnType() == List.class && fromMethod.isAnnotationPresent(JsonConfList.class))
				//if (fromMethod.getReturnType() == List.class)
				{
					log.debug("merge list field found " + fromMethod);

					/* handle List merge */
					String mergeName = fromName.replace("get", "merge");
					try {						
						if (fromMethod.getDeclaringClass()==this.getClass().getSuperclass())
						{
							Method mergeMethod = this.getClass().getMethod(mergeName, update.getClass().getSuperclass());
							mergeMethod.invoke(this, update.getClass().getSuperclass().cast(update));
						} else {
							Method mergeMethod = this.getClass().getMethod(mergeName, update.getClass());
							mergeMethod.invoke(this, update);
						}
					} catch (NoSuchMethodException | SecurityException e) {
						log.warn("Method invoke - NoSuchMethodException | SecurityException ", e);
						return false;
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						log.warn("Method invoke - IllegalAccessException | IllegalArgumentException | InvocationTargetException", e);
						return false;
					}
				}
				else
				{
					/* handle get set copy */
					String toName;
					if (fromName.matches("^get[A-Z].*")) {
						toName = fromName.replace("get", "set");
					} else {
						toName = fromName.replace("is", "set");
					}

					try {
						Method toMethod = this.getClass().getMethod(toName, fromMethod.getReturnType());
						Object value = fromMethod.invoke(update, (Object[]) null);
						if (value != null) {
							toMethod.invoke(this, value);
						}
					} catch (Exception e) {
						log.error("Method invoke exception ", e);
						return false;
					}
				}
			}
		}

		return true;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

	protected Properties valueMapToProperties(String valueMap) {
		Properties p = new Properties();

		String[] v = valueMap.split("[|]");
		for (String s : v)
		{
			// log.debugf("s="+s);
			String[] k = s.split("=");
			if (k.length > 1)
				p.setProperty(k[0], k[1]);
		}
		return p;
	}
}
