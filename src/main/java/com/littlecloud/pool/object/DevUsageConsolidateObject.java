package com.littlecloud.pool.object;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DevUsageConsolidateObject 
{
	private static Logger logger = Logger.getLogger(DevUsageConsolidateObject.class);
	
	private ConsolidateObject hourly;
	private ConsolidateObject daily;
	private ConsolidateObject monthly;
	private Integer tz_offset;
	
	public Integer getTz_offset() {
		return tz_offset;
	}

	public void setTz_offset(Integer tz_offset) {
		this.tz_offset = tz_offset;
	}

	public ConsolidateObject getHourly() {
		return hourly;
	}

	public void setHourly(ConsolidateObject hourly) {
		this.hourly = hourly;
	}

	public ConsolidateObject getDaily() {
		return daily;
	}

	public void setDaily(ConsolidateObject daily) {
		this.daily = daily;
	}

	public ConsolidateObject getMonthly() {
		return monthly;
	}

	public void setMonthly(ConsolidateObject monthly) {
		this.monthly = monthly;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevUsageConsolidateObject [hourly=");
		builder.append(hourly);
		builder.append(", daily=");
		builder.append(daily);
		builder.append(", monthly=");
		builder.append(monthly);
		builder.append(", tz_offset=");
		builder.append(tz_offset);
		builder.append("]");
		return builder.toString();
	}

	public class Usages
	{
		private String date;
		private float up;
		private float down;
		private String date_end;
		private String ref_time;
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public float getUp() {
			return up;
		}
		public void setUp(float up) {
			this.up = up;
		}
		public float getDown() {
			return down;
		}
		public void setDown(float down) {
			this.down = down;
		}
		public String getDate_end() {
			return date_end;
		}
		public void setDate_end(String date_end) {
			this.date_end = date_end;
		}
		public String getRef_time() {
			return ref_time;
		}
		public void setRef_time(String ref_time) {
			this.ref_time = ref_time;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Usages [date=");
			builder.append(date);
			builder.append(", up=");
			builder.append(up);
			builder.append(", down=");
			builder.append(down);
			builder.append(", date_end=");
			builder.append(date_end);
			builder.append(", ref_time=");
			builder.append(ref_time);
			builder.append("]");
			return builder.toString();
		}
	}
	
	public class ConsolidateUsages
	{
		Integer id;
		String name;
		List<Usages> usage;
		
		public Integer getId() 
		{
			return id;
		}
		
		public void setId(Integer id) 
		{
			this.id = id;
		}
		
		public String getName() 
		{
			return name;
		}
		
		public void setName(String name) 
		{
			this.name = name;
		}
		
		public List<Usages> getUsage() 
		{
			return usage;
		}
		
		public void setUsages(List<Usages> usage) 
		{
			this.usage = usage;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ConsolidateUsages [id=");
			builder.append(id);
			builder.append(", name=");
			builder.append(name);
			builder.append(", usage=");
			builder.append(usage);
			builder.append("]");
			return builder.toString();
		}
	}
	
	public static class ConsolidateObject
	{
		String unit;
		List<ConsolidateUsages> wanUsages;
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public List<ConsolidateUsages> getWanUsages() {
			return wanUsages;
		}
		public void setWanUsages(List<ConsolidateUsages> wanUsages) {
			this.wanUsages = wanUsages;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ConsolidateObject [unit=");
			builder.append(unit);
			builder.append(", wanUsages=");
			builder.append(wanUsages);
			builder.append("]");
			return builder.toString();
		}
	}
	
	public static DevUsageConsolidateObject getInstance(JSONObject response)
	{
		DevUsageConsolidateObject result = null;
		
		try
		{
			Integer tzOffset = response.getInt("tz_offset");
			JSONObject hour = response.getJSONObject("hourly");
			ConsolidateObject hourly = convertFromJson(hour);
			JSONObject day = response.getJSONObject("daily");
			ConsolidateObject daily = convertFromJson(day);
			JSONObject mth = response.getJSONObject("monthly");
			ConsolidateObject monthly = convertFromJson(mth);
			result = new DevUsageConsolidateObject();
			result.setHourly(hourly);
			result.setDaily(daily);
			result.setMonthly(monthly);
			result.setTz_offset(tzOffset);
		}
		catch(Exception e)
		{
			logger.error("Device Consolidate object get instance error -"+e,e);
		}
		
		return result;
	}
	
	private static ConsolidateObject convertFromJson(JSONObject item)
	{
		ConsolidateObject result = null;
		if(item != null)
		{
			result = new ConsolidateObject();
			JSONArray array = item.getJSONArray("order");
			String unit = item.getString("unit");
			List<ConsolidateUsages> wanUsages = new ArrayList<ConsolidateUsages>();
			Gson gson = new Gson();
			for( Object s : array.toArray() )
			{
				int id = 0;
				ConsolidateUsages consolidateUsage = null;
				
				if(!s.toString().equals("overall"))
				{
					id = (int)(Float.parseFloat(s.toString()));
					consolidateUsage = (ConsolidateUsages)gson.fromJson(item.getJSONObject(""+id).toString(), ConsolidateUsages.class);
				}
				else
				{
					consolidateUsage = (ConsolidateUsages)gson.fromJson(item.getJSONObject(s.toString()).toString(), ConsolidateUsages.class);
				}
				
				logger.info("s:"+s + " " + consolidateUsage);
				if( s.toString().equals("overall") )
				{
					consolidateUsage.setId(0);
					consolidateUsage.setName("all_wan");
				}
				else
				{
					consolidateUsage.setId(id);
				}
				wanUsages.add(consolidateUsage);
			}
			result.setUnit(unit);
			result.setWanUsages(wanUsages);
		}
		
		return result;
	}
	
	
	public static void main(String[] args)
	{
//		String json = "{\"order\":[1,2,3,4,\"overall\"]}";
//		String json = "{\"data\":{\"stat\":\"ok\",\"response\":{\"monthly\":{\"overall\":{\"usage\":[{\"up\":239,\"down\":481,\"date_end\":\"2014-05-31\",\"date\":\"2014-05-01\",\"ref_time\":\"201405\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"WAN3\",\"usage\":[{\"up\":76,\"down\":176,\"date_end\":\"2014-05-23\",\"date\":\"2014-05-01\"}]},\"order\":[1,2,3,4,\"overall\"],\"1\":{\"name\":\"wan1_test_name\",\"usage\":[{\"up\":50,\"down\":132,\"date_end\":\"2014-05-23\",\"date\":\"2014-05-01\"}]},\"2\":{\"name\":\"wan2ChangeName\",\"usage\":[{\"up\":113,\"down\":172,\"date_end\":\"2014-05-23\",\"date\":\"2014-05-01\"}]},\"4\":{\"name\":\"Mobile Internet\",\"usage\":[{\"up\":0,\"down\":0,\"date_end\":\"2014-05-23\",\"date\":\"2014-05-01\"}]}},\"tz_offset\":28800,\"ref_key\":\"2014052317\",\"daily\":{\"overall\":{\"usage\":[{\"up\":7,\"down\":27,\"date\":\"2014-05-23\",\"ref_time\":\"20140523\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"WAN3\",\"usage\":[{\"up\":1,\"down\":7,\"date\":\"2014-05-23\"}]},\"order\":[1,2,3,4,\"overall\"],\"1\":{\"name\":\"wan1_test_name\",\"usage\":[{\"up\":2,\"down\":13,\"date\":\"2014-05-23\"}]},\"2\":{\"name\":\"wan2ChangeName\",\"usage\":[{\"up\":3,\"down\":6,\"date\":\"2014-05-23\"}]},\"4\":{\"name\":\"Mobile Internet\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-23\"}]}},\"hourly\":{\"overall\":{\"usage\":[{\"up\":0,\"down\":1,\"date\":\"2014-05-23-17\",\"ref_time\":\"2014052317\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"WAN3\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-23-17\"}]},\"order\":[1,2,3,4,\"overall\"],\"1\":{\"name\":\"wan1_test_name\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-23-17\"}]},\"2\":{\"name\":\"wan2ChangeName\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-23-17\"}]},\"4\":{\"name\":\"Mobile Internet\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-23-17\"}]}}}},\"sid\":\"\",\"iana_id\":23695,\"status\":200,\"type\":\"PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE\",\"sn\":\"1824-C29B-8C5C\",\"qid\":2000000003,\"timestamp\":1400836929}";
//		String json = "{\"data\":{\"stat\":\"ok\",\"response\":{\"monthly\":{\"overall\":{\"usage\":[{\"up\":189,\"down\":107,\"date_end\":\"2014-05-31\",\"date\":\"2014-05-01\",\"ref_time\":\"201405\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"Wi-Fi WAN\",\"usage\":[{\"up\":0,\"down\":0,\"date_end\":\"2014-05-26\",\"date\":\"2014-05-01\"}]},\"order\":[1,2,3,\"overall\"],\"1\":{\"name\":\"WAN\",\"usage\":[{\"up\":189,\"down\":107,\"date_end\":\"2014-05-26\",\"date\":\"2014-05-01\"}]},\"2\":{\"name\":\"Cellular\",\"usage\":[{\"up\":0,\"down\":0,\"date_end\":\"2014-05-26\",\"date\":\"2014-05-01\"}]}},\"tz_offset\":28800,\"ref_key\":\"2014052609\",\"daily\":{\"overall\":{\"usage\":[{\"up\":16,\"down\":3,\"date\":\"2014-05-26\",\"ref_time\":\"20140526\"},{\"up\":39,\"down\":7,\"date\":\"2014-05-25\",\"ref_time\":\"20140525\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"Wi-Fi WAN\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-26\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25\"}]},\"order\":[1,2,3,\"overall\"],\"1\":{\"name\":\"WAN\",\"usage\":[{\"up\":16,\"down\":3,\"date\":\"2014-05-26\"},{\"up\":39,\"down\":7,\"date\":\"2014-05-25\"}]},\"2\":{\"name\":\"Cellular\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-26\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25\"}]}},\"hourly\":{\"overall\":{\"usage\":[{\"up\":1,\"down\":0,\"date\":\"2014-05-26-09\",\"ref_time\":\"2014052609\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-08\",\"ref_time\":\"2014052608\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-07\",\"ref_time\":\"2014052607\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-06\",\"ref_time\":\"2014052606\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-05\",\"ref_time\":\"2014052605\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-04\",\"ref_time\":\"2014052604\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-03\",\"ref_time\":\"2014052603\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-02\",\"ref_time\":\"2014052602\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-01\",\"ref_time\":\"2014052601\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-00\",\"ref_time\":\"2014052600\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-23\",\"ref_time\":\"2014052523\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-22\",\"ref_time\":\"2014052522\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-21\",\"ref_time\":\"2014052521\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-20\",\"ref_time\":\"2014052520\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-19\",\"ref_time\":\"2014052519\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-18\",\"ref_time\":\"2014052518\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-17\",\"ref_time\":\"2014052517\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-16\",\"ref_time\":\"2014052516\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-15\",\"ref_time\":\"2014052515\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-14\",\"ref_time\":\"2014052514\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-13\",\"ref_time\":\"2014052513\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-12\",\"ref_time\":\"2014052512\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-11\",\"ref_time\":\"2014052511\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-10\",\"ref_time\":\"2014052510\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"Wi-Fi WAN\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-26-09\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-08\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-07\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-06\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-05\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-04\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-03\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-02\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-01\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-00\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-23\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-22\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-21\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-20\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-19\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-18\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-17\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-16\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-15\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-14\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-13\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-12\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-11\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-10\"}]},\"order\":[1,2,3,\"overall\"],\"1\":{\"name\":\"WAN\",\"usage\":[{\"up\":1,\"down\":0,\"date\":\"2014-05-26-09\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-08\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-07\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-06\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-05\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-04\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-03\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-02\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-01\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-26-00\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-23\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-22\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-21\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-20\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-19\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-18\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-17\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-16\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-15\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-14\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-13\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-12\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-11\"},{\"up\":1,\"down\":0,\"date\":\"2014-05-25-10\"}]},\"2\":{\"name\":\"Cellular\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-26-09\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-08\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-07\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-06\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-05\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-04\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-03\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-02\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-01\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-00\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-23\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-22\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-21\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-20\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-19\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-18\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-17\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-16\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-15\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-14\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-13\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-12\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-11\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25-10\"}]}}}},\"sid\":\"\",\"iana_id\":23695,\"status\":200,\"type\":\"PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE\",\"sn\":\"2830-E418-5433\",\"qid\":2000036377,\"timestamp\":1401068180}";
		String json = "{\"data\":{\"stat\":\"ok\",\"response\":{\"monthly\":{\"overall\":{\"usage\":[{\"up\":269,\"down\":535,\"date_end\":\"2014-05-31\",\"date\":\"2014-05-01\",\"ref_time\":\"201405\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"WAN3\",\"usage\":[{\"up\":82,\"down\":191,\"date_end\":\"2014-05-27\",\"date\":\"2014-05-01\"}]},\"order\":[1,2,3,4,\"overall\"],\"1\":{\"name\":\"wan1_test_name\",\"usage\":[{\"up\":62,\"down\":153,\"date_end\":\"2014-05-27\",\"date\":\"2014-05-01\"}]},\"2\":{\"name\":\"wan2ChangeName\",\"usage\":[{\"up\":124,\"down\":190,\"date_end\":\"2014-05-27\",\"date\":\"2014-05-01\"}]},\"4\":{\"name\":\"Mobile Internet\",\"usage\":[{\"up\":0,\"down\":0,\"date_end\":\"2014-05-27\",\"date\":\"2014-05-01\"}]}},\"tz_offset\":28800,\"ref_key\":\"2014052704\",\"daily\":{\"overall\":{\"usage\":[{\"up\":1,\"down\":1,\"date\":\"2014-05-27\",\"ref_time\":\"20140527\"},{\"up\":10,\"down\":28,\"date\":\"2014-05-26\",\"ref_time\":\"20140526\"},{\"up\":6,\"down\":9,\"date\":\"2014-05-25\",\"ref_time\":\"20140525\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"WAN3\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27\"},{\"up\":2,\"down\":8,\"date\":\"2014-05-26\"},{\"up\":1,\"down\":2,\"date\":\"2014-05-25\"}]},\"order\":[1,2,3,4,\"overall\"],\"1\":{\"name\":\"wan1_test_name\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27\"},{\"up\":3,\"down\":10,\"date\":\"2014-05-26\"},{\"up\":2,\"down\":3,\"date\":\"2014-05-25\"}]},\"2\":{\"name\":\"wan2ChangeName\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27\"},{\"up\":4,\"down\":9,\"date\":\"2014-05-26\"},{\"up\":2,\"down\":3,\"date\":\"2014-05-25\"}]},\"4\":{\"name\":\"Mobile Internet\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-25\"}]}},\"hourly\":{\"overall\":{\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27-04\",\"ref_time\":\"2014052704\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-03\",\"ref_time\":\"2014052703\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-02\",\"ref_time\":\"2014052702\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-01\",\"ref_time\":\"2014052701\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-00\",\"ref_time\":\"2014052700\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-23\",\"ref_time\":\"2014052623\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-22\",\"ref_time\":\"2014052622\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-21\",\"ref_time\":\"2014052621\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-20\",\"ref_time\":\"2014052620\"},{\"up\":0,\"down\":2,\"date\":\"2014-05-26-19\",\"ref_time\":\"2014052619\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-18\",\"ref_time\":\"2014052618\"},{\"up\":0,\"down\":3,\"date\":\"2014-05-26-17\",\"ref_time\":\"2014052617\"},{\"up\":0,\"down\":2,\"date\":\"2014-05-26-16\",\"ref_time\":\"2014052616\"},{\"up\":0,\"down\":2,\"date\":\"2014-05-26-15\",\"ref_time\":\"2014052615\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-14\",\"ref_time\":\"2014052614\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-13\",\"ref_time\":\"2014052613\"},{\"up\":0,\"down\":4,\"date\":\"2014-05-26-12\",\"ref_time\":\"2014052612\"},{\"up\":0,\"down\":2,\"date\":\"2014-05-26-11\",\"ref_time\":\"2014052611\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-10\",\"ref_time\":\"2014052610\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-09\",\"ref_time\":\"2014052609\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-08\",\"ref_time\":\"2014052608\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-07\",\"ref_time\":\"2014052607\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-06\",\"ref_time\":\"2014052606\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-05\",\"ref_time\":\"2014052605\"}]},\"unit\":\"MB\",\"3\":{\"name\":\"WAN3\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27-04\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-03\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-02\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-01\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-00\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-23\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-22\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-21\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-20\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-19\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-18\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-17\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-16\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-15\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-14\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-13\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-12\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-11\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-10\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-09\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-08\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-07\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-06\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-05\"}]},\"order\":[1,2,3,4,\"overall\"],\"1\":{\"name\":\"wan1_test_name\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27-04\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-03\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-02\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-01\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-00\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-23\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-22\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-21\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-20\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-19\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-18\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-17\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-16\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-15\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-14\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-13\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-12\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-11\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-10\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-09\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-08\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-07\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-06\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-05\"}]},\"2\":{\"name\":\"wan2ChangeName\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27-04\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-03\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-02\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-01\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-00\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-23\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-22\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-21\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-20\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-19\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-18\"},{\"up\":0,\"down\":1,\"date\":\"2014-05-26-17\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-16\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-15\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-14\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-13\"},{\"up\":0,\"down\":2,\"date\":\"2014-05-26-12\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-11\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-10\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-09\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-08\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-07\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-06\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-05\"}]},\"4\":{\"name\":\"Mobile Internet\",\"usage\":[{\"up\":0,\"down\":0,\"date\":\"2014-05-27-04\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-03\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-02\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-01\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-27-00\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-23\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-22\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-21\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-20\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-19\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-18\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-17\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-16\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-15\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-14\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-13\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-12\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-11\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-10\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-09\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-08\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-07\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-06\"},{\"up\":0,\"down\":0,\"date\":\"2014-05-26-05\"}]}}}},\"sid\":\"\",\"iana_id\":23695,\"status\":200,\"type\":\"PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE\",\"sn\":\"2830-E24C-0774\",\"qid\":2000000073,\"timestamp\":1401134566}";
		JSONObject object = JSONObject.fromObject(json);
		JSONObject data = object.getJSONObject("data");
		JSONObject response = data.getJSONObject("response");
		DevUsageConsolidateObject deviceConso = DevUsageConsolidateObject.getInstance(response);
		System.out.println(""+deviceConso);
	}
}
