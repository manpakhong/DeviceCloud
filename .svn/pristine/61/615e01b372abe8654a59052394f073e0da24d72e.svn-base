package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StationUsageHistoryObject extends PoolObject implements PoolObjectIf, Serializable
{

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private CopyOnWriteArrayList<Timelist> hourly;
	private CopyOnWriteArrayList<Timelist> daily;
	private CopyOnWriteArrayList<Timelist> monthly;
	
	public class Timelist
	{
		List<ClientList> client_list;
		String time;
		
		public class ClientList
		{
			float rx;
			float tx;
			String ip;
			String type;
			public float getRx() {
				return rx;
			}
			public void setRx(float rx) {
				this.rx = rx;
			}
			public float getTx() {
				return tx;
			}
			public void setTx(float tx) {
				this.tx = tx;
			}
			public String getIp() {
				return ip;
			}
			public void setIp(String ip) {
				this.ip = ip;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
			
		}

		public List<ClientList> getClient_list() {
			return client_list;
		}

		public void setClient_list(List<ClientList> client_list) {
			this.client_list = client_list;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}
	}
	
	@Override
	public String getKey() {
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

	public CopyOnWriteArrayList<Timelist> getHourly() {
		return hourly;
	}

	public void setHourly(CopyOnWriteArrayList<Timelist> hourly) {
		this.hourly = hourly;
	}

	public CopyOnWriteArrayList<Timelist> getDaily() {
		return daily;
	}

	public void setDaily(CopyOnWriteArrayList<Timelist> daily) {
		this.daily = daily;
	}

	public CopyOnWriteArrayList<Timelist> getMonthly() {
		return monthly;
	}

	public void setMonthly(CopyOnWriteArrayList<Timelist> monthly) {
		this.monthly = monthly;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationUsageHistoryObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", hourly=");
		builder.append(hourly);
		builder.append(", daily=");
		builder.append(daily);
		builder.append(", monthly=");
		builder.append(monthly);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
