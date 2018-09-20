package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DevTimelyUsageObject extends PoolObject implements PoolObjectIf, Serializable
{

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private CopyOnWriteArrayList<WanList> wan; 

	public CopyOnWriteArrayList<WanList> getWan() {
		return wan;
	}

	public void setWan(CopyOnWriteArrayList<WanList> wan) {
		this.wan = wan;
	}

	@Override
	public String getKey() 
	{
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) 
	{
		// TODO Auto-generated method stub
		this.iana_id = iana_id;
		this.sn = sn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	
	public class WanList
	{
		private Integer id;
		private String wan_name;
		private List<UsageList> hourly;
		private List<UsageList> daily;
		private List<UsageList> monthly;
		
		public List<UsageList> getHourly() 
		{
			return hourly;
		}

		public void setHourly(List<UsageList> hourly) 
		{
			this.hourly = hourly;
		}

		public List<UsageList> getDaily() 
		{
			return daily;
		}

		public void setDaily(List<UsageList> daily) 
		{
			this.daily = daily;
		}

		public List<UsageList> getMonthly() 
		{
			return monthly;
		}

		public void setMonthly(List<UsageList> monthly) 
		{
			this.monthly = monthly;
		}
		
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return wan_name;
		}

		public void setName(String name) {
			this.wan_name = name;
		}

		public class UsageList implements Serializable
		{
			String time;
			Float up;
			Float down;
			
			public String getTime() 
			{
				return time;
			}
			
			public void setTime(String time) 
			{
				this.time = time;
			}
			
			public Float getUp() 
			{
				return up;
			}
			
			public void setUp(Float up) 
			{
				this.up = up;
			}
			
			public Float getDown() 
			{
				return down;
			}
			
			public void setDown(Float down) 
			{
				this.down = down;
			}

			@Override
			public String toString() {
				StringBuilder builder = new StringBuilder();
				builder.append("UsageList [time=");
				builder.append(time);
				builder.append(", up=");
				builder.append(up);
				builder.append(", down=");
				builder.append(down);
				builder.append("]");
				return builder.toString();
			}		
			
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevTimelyUsageObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", wan=");
		builder.append(wan);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
	
}
