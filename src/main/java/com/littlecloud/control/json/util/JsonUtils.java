package com.littlecloud.control.json.util;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.health.info.DBInfo;
import com.littlecloud.control.json.DateDeserializer;
import com.littlecloud.control.json.DateSerializer;
import com.littlecloud.control.json.JsonExclusionStrategy;
import com.littlecloud.control.json.JsonRequest;
import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.model.config.util.ConfigUpdatePerNetworkTask;

public class JsonUtils<T> {

	private static final Logger log = Logger.getLogger(JsonUtils.class);

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	
	public static final String jsonRequestDateFormat = "yyyy-MM-dd";
	public static final String jsonRequestDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	//public static final String jsonUtcDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	//public static final String jsonUtcDateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";	// not used because javascript i18n support issue
	public static final String jsonUtcDateFormat = "yyyy-MM-dd'T'HH:mm:ss";

	private List<T> list = new ArrayList<T>();

	private static Gson gsonToJson = createGsonToBuilder(new JsonExclusionStrategy(null));
	private static Gson gsonToJsonCompact = createGsonToBuilderCompact(new JsonExclusionStrategy(null));
	private static Gson gsonToJsonPretty = createGsonToBuilderPretty(null, new JsonExclusionStrategy(null));
	private static Gson gsonFromJson = createGsonFromBuilderCompact(null, new JsonExclusionStrategy(null));
	private static Gson gsonFromJsonConfigDate = createGsonFromBuilderConfigDateCompact(null, new JsonExclusionStrategy(null));
	
	public static void setPROD_MODE(boolean pROD_MODE) {
		PROD_MODE = pROD_MODE;
		
		gsonToJson = createGsonToBuilder(new JsonExclusionStrategy(null));
		gsonToJsonCompact = createGsonToBuilderCompact(new JsonExclusionStrategy(null));
		gsonFromJson = createGsonFromBuilderCompact(null, new JsonExclusionStrategy(null));
		gsonFromJsonConfigDate = createGsonFromBuilderConfigDateCompact(null, new JsonExclusionStrategy(null));
	}

	public static String toJson(Object src) {
		return gsonToJson.toJson(src);
	}

	public static String toJson(Object src, String timezone) {
		return createGsonToBuilder(new DateSerializer(TimeZone.getTimeZone(timezone)), false, new JsonExclusionStrategy(null)).toJson(src);
	}

	public static String toJsonCompact(Object src)
	{
		return gsonToJsonCompact.toJson(src);
	}
	
	public static String toJsonPretty(Object src)
	{
		return gsonToJsonPretty.toJson(src);
	}

	/* generate server reference number */
	public static String genServerRef() {
		// SimpleDateFormat smf = new java.text.SimpleDateFormat(JsonUtils.dateFormat);
		// String randomPrefix = smf.format(new Date());
		//
		// Random rand = new Random(System.nanoTime());
		//
		// DecimalFormat df = new DecimalFormat("0000000000");
		// String output = df.format(rand.nextDouble());
		// return df.format(Double.parseDouble(randomPrefix + Math.abs(rand.nextInt(8999999)+1000000)));

		SimpleDateFormat smf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		String randomPrefix = smf.format(new Date());

		Random rand = new Random(System.nanoTime());
		return randomPrefix + (Math.abs(rand.nextInt(89999999) + 10000000));
	}
	
	public static String relateServerRef(String serverRef)
	{
		if (serverRef==null)
			return "";
		
		return serverRef.substring(0, serverRef.length()-2) + String.valueOf(Integer.valueOf(serverRef.substring(serverRef.length()-2, serverRef.length())) + 1);
	}
	
//	public static <T> T fromJsonList(String json)
//	{
//		Type listType = new TypeToken<T>() {}.getType();
//		
//		try {
//			return gsonFromJson.fromJson(json, listType);
//		} catch (com.google.gson.JsonSyntaxException e)
//		{
//			try {
//				return gsonFromJsonConfigDate.fromJson(json, listType);
//			} catch (Exception e1) {
//				log.error("INVALID INPUT "+json, e1);
//				return null;
//			}
//		} catch (Exception e) {
//			log.error("INVALID INPUT "+json, e);
//			return null;
//		}
//	}
	
	public static <T> T fromJson(String json, Type type)
	{
		try {
			return gsonFromJson.fromJson(json, type);
		} catch (com.google.gson.JsonSyntaxException e)
		{
			try {
				return gsonFromJsonConfigDate.fromJson(json, type);
			} catch (Exception e1) {
				log.error("INVALID INPUT "+json, e1);
				return null;
			}
		} catch (Exception e) {
			log.error("INVALID INPUT "+json, e);
			return null;
		}
	}
	
	public static <T> T fromJson(String json, Class<T> clazz)
	{
		try {
			return gsonFromJson.fromJson(json, clazz);
		} catch (com.google.gson.JsonSyntaxException e)
		{
			try {
				return gsonFromJsonConfigDate.fromJson(json, clazz);
			} catch (Exception e1) {
				log.error("INVALID INPUT "+json, e1);
				return null;
			}
		} catch (Exception e) {
			log.error("INVALID INPUT "+json, e);
			return null;
		}
	}

	public static <T> T fromJson(TimeZone timezone, String json, Class<T> clazz)
	{
		return createGsonFromBuilderCompact(new DateDeserializer(timezone), new JsonExclusionStrategy(null)).fromJson(json, clazz);
	}

	public static <T> T getRequestObject(String json, Class<T> clazz)
	{
		if (json == null)
		{
			log.warn("input JSON is empty");
			return null;
		}

		T request = null;
		try {
			request = gsonFromJson.fromJson(json, clazz);
		} catch (com.google.gson.JsonSyntaxException e)
		{
			try {
				request = gsonFromJsonConfigDate.fromJson(json, clazz);
			} catch (Exception e1) {
				log.error("INVALID INPUT "+json, e1);
				return null;
			}
		} catch (Exception e) {
			log.error("INVALID INPUT "+json, e);
			return null;
		}

		JsonRequest req = (JsonRequest) request;
		if (!req.isValidRequest()) {
			log.error("MISSING INPUT PARAM: " + json.substring(100));
			return null;
		}

		if (req.getServer_ref().compareTo("0") == 0)
		{
			String server_ref = genServerRef();
			req.setServer_ref(server_ref);
		}

		return request;
	}

	public static <RESPONSE_ENTITY, REQUEST> JsonResponse<RESPONSE_ENTITY> getResponseObject(REQUEST request)
	{
		JsonResponse<RESPONSE_ENTITY> response = new JsonResponse<RESPONSE_ENTITY>();

		if (request != null)
		{
			JsonRequest req = (JsonRequest) request;
			response.setCaller_ref(req.getCaller_ref());
			response.setServer_ref(req.getServer_ref());
		}

		return response;
	}

	public static boolean isValidJsonRequest(JsonRequest request, String json)
	{
		try {
			Gson gsonReq = new Gson();
			request = gsonReq.fromJson(json, JsonRequest.class);
		} catch (Exception e) {
			return false;
		}

		if (request.getCaller_ref() == null || request.getVersion() == null)
			return false;

		if (!request.isValidRequest()) {
			return false;
		}

		return true;
	}

	public static Gson createGsonToBuilder(ExclusionStrategy... exs) {
		return createGsonToBuilder(null, false, exs);
	}
	
	public static Gson createGsonToBuilder(DateSerializer ds, boolean bCompact, ExclusionStrategy... exs) {
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setDateFormat(jsonUtcDateFormat);
		if (ds != null)
			gsonbuilder.registerTypeAdapter(Date.class, ds);
		gsonbuilder.setExclusionStrategies(exs);
		if (!PROD_MODE/* || !bCompact // force compact */)	
			gsonbuilder.setExclusionStrategies(exs).setPrettyPrinting();
		return gsonbuilder.disableHtmlEscaping().create();
	}
	
	public static Gson createGsonToBuilderCompact(ExclusionStrategy... exs) {
		return createGsonToBuilder(null, true, exs);
	}

	public static Gson createGsonFromBuilderCompact(DateDeserializer dd, ExclusionStrategy... exs) {
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setDateFormat(jsonRequestDateTimeFormat); // "yyyy-MM-dd HH:mm:ssX"
		if (dd != null)
			gsonbuilder.registerTypeAdapter(Date.class, dd);
		gsonbuilder.setExclusionStrategies(exs);
		if (!PROD_MODE)
			gsonbuilder.setExclusionStrategies(exs).setPrettyPrinting();
		// return gsonbuilder.serializeNulls().create();
		return gsonbuilder.create();
	}
	
	private static Gson createGsonToBuilderPretty(DateSerializer ds, JsonExclusionStrategy exs) {
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setDateFormat(jsonUtcDateFormat);
		if (ds != null)
			gsonbuilder.registerTypeAdapter(Date.class, ds);
		gsonbuilder.setExclusionStrategies(exs);
		gsonbuilder.setExclusionStrategies(exs).setPrettyPrinting();
		return gsonbuilder.disableHtmlEscaping().create();
	}
	
	private static Gson createGsonFromBuilderConfigDateCompact(DateDeserializer dd, JsonExclusionStrategy jsonExclusionStrategy) {
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setDateFormat(jsonUtcDateFormat); // "yyyy-MM-dd'T'HH:mm:ssX"
		if (dd != null)
			gsonbuilder.registerTypeAdapter(Date.class, dd);
		gsonbuilder.setExclusionStrategies(jsonExclusionStrategy);
		if (!PROD_MODE)
			gsonbuilder.setExclusionStrategies(jsonExclusionStrategy).setPrettyPrinting();
		// return gsonbuilder.serializeNulls().create();
		return gsonbuilder.create();
	}

	public List<T> add(T obj)
	{
		list.add(obj);
		return list;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public static Map<String, String> toPropertyFormat(JSONObject json, Map<String, String> out, String upperLayer) {
		if (json.isEmpty())
		{
			log.warn("json object is null!");
			return null;
		}

		log.debug("======================= upperLayer " + upperLayer + " ========================");

		String layer = upperLayer;

		Iterator<String> keys = json.keys();

		boolean findObject = false;
		while (keys.hasNext()) {
			String key = keys.next();
			log.debug("key=" + key);
			String val = null;
			try {
				/* do JSONObject */
				JSONObject value = json.getJSONObject(key);
				log.debug("------------- jsonObject --------------");
				layer = layer + "_" + key;
				log.debug("jsonObject layer=" + layer);
				toPropertyFormat(value, out, layer);

				findObject = true;
			} catch (Exception e) {
				// /logger.debug("##### e=" + e);
				findObject = false;
			}

			if (!findObject)
			{
				try {
					/* do JSONArray */
					JSONArray array = json.getJSONArray(key);
					log.debug("------------- jsonArray --------------");
					// layer = layer + "_" + key;
					for (int i = 0; i < array.size(); i++) {
						log.debug("jsonArray layer=" + layer);

						try {
							JSONObject value = array.getJSONObject(i);
							toPropertyFormat(value, out, layer + "_" + key + (i + 1));
						} catch (Exception e2) {
							// /logger.debug("##### e2=" + e2);
							// e2.printStackTrace();

							val = json.getString(key);
							out.put(layer + "_" + key, val);
							log.debugf("store2 (%s,%s)\n", layer + "_" + key, val);
						}
					}

					findObject = true;
				} catch (Exception e1) {
					// /logger.debug("##### e1=" + e1);
				}
			}

			if (!findObject)
			{
				log.debugf("------------- value of key %s is a value --------------\n", key);
				val = json.getString(key);

				if (val != null) {
					out.put(layer + "_" + key, val);
					log.debugf("store1 (%s,%s)\n", key, val);
				}
			}
		}
		return out;
	}
	
	public static String getConvertedSsidEncryption(String encryptionFromDevice){
		String ret = "";
		switch (encryptionFromDevice){
			case "WPAMIX_PSK":
				ret = "WPA/WPA2 Personal";
				break;
			case "WPAMIX_EP":
				ret = "WPA/WPA2 Enterprise";
				break;
			case "none":
			case "OPEN":
				ret = "OPEN";
				break;
			default:
				ret = encryptionFromDevice;
				break;
		}
		return ret;
	}
	
	public static void main(String args[]) 
	{
		String in = "20131030020805428352013103002080615027366";
		System.out.printf("result=\n%s \n=> \n%s\n", in, relateServerRef(in));
		
		System.out.println(JsonUtils.genServerRef());
	}
}
