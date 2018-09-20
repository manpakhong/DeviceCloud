package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.mina.util.CopyOnWriteMap;

import com.littlecloud.control.entity.Networks;
import com.littlecloud.pool.object.NetInfoObject.ActiveDevices;
import com.peplink.api.db.util.DBUtil;

public class OrgInfoObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private String orgId;
	private Integer iana_id = 9999;
	private ActiveNetLst actNetLst;
	private ConcurrentHashMap<NetIdMapKey, Integer> netIdMap = new ConcurrentHashMap<NetIdMapKey, Integer>();

	private boolean isLoaded = false;
	
	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}		

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getOrgId() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String orgId) {
		this.iana_id = iana_id;
		this.orgId = orgId;
	}
	
	private String printNetIdMap() {
		StringBuilder sb = new StringBuilder();
		/* debug */
		for (NetIdMapKey key:netIdMap.keySet())
		{							
			sb.append("|dev ");
			sb.append(key.getIanaId());
			sb.append(",");
			sb.append(key.getSn());
			sb.append("="+netIdMap.get(key));
		}
		return sb.toString();
	}
	
	public ConcurrentHashMap<NetIdMapKey, Integer> getNetIdMap() {
		return netIdMap;
	}

//	public Integer put(Integer ianaId, String sn, Integer netId) {
//		return netIdMap.put(new NetIdMapKey(ianaId, sn), netId);
//	}

	public ActiveNetLst getActNetLst() {
		/* cache sync may override a null */
		if (actNetLst==null)
			actNetLst = new ActiveNetLst();
		return actNetLst;
	}

	public OrgInfoObject(String orgId) {
		super();
		this.actNetLst = new ActiveNetLst();
		this.orgId = orgId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public void saveOrUpdateDevices(Integer devId, Integer ianaId, String sn, Integer netId) {		
		netIdMap.put(new NetIdMapKey(ianaId, sn), netId);
		netIdMap.put(new NetIdMapKey(devId), netId);
	}

	public void deleteDevices(Integer devId, Integer ianaId, String sn)
	{
		netIdMap.remove(new NetIdMapKey(ianaId, sn));
		netIdMap.remove(new NetIdMapKey(devId));
	}

	public Integer lookupDevices(Integer ianaId, String sn)
	{
		return netIdMap.get(new NetIdMapKey(ianaId, sn));
	}
	
	public Integer lookupDevices(Integer devId)
	{
		return netIdMap.get(new NetIdMapKey(devId));
	}

	public static class NetIdMapKey implements Serializable {
		Integer ianaId;
		String sn;

		public NetIdMapKey(Integer ianaId, String sn) {
			super();
			this.ianaId = ianaId;
			this.sn = sn;
		}
		
		public NetIdMapKey(Integer devId) {
			super();
			this.ianaId = devId;
			this.sn = "device_id";
		}

		public Integer getIanaId() {
			return ianaId;
		}

		public void setIanaId(Integer ianaId) {
			this.ianaId = ianaId;
		}

		public String getSn() {
			return sn;
		}

		public void setSn(String sn) {
			this.sn = sn;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("NetIdMapKey [ianaId=");
			builder.append(ianaId);
			builder.append(", sn=");
			builder.append(sn);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ianaId == null) ? 0 : ianaId.hashCode());
			result = prime * result + ((sn == null) ? 0 : sn.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NetIdMapKey other = (NetIdMapKey) obj;
			if (ianaId == null) {
				if (other.ianaId != null)
					return false;
			} else if (!ianaId.equals(other.ianaId))
				return false;
			if (sn == null) {
				if (other.sn != null)
					return false;
			} else if (!sn.equals(other.sn))
				return false;
			return true;
		}
	}

	public static class ActiveNetLst implements Serializable
	{	
		private CopyOnWriteArrayList<Networks> netInfoLst = new CopyOnWriteArrayList<Networks>();
		
		public CopyOnWriteArrayList<Networks> getNetInfoLst() {
			return netInfoLst;
		}
		
		public void setNetInfoLst(CopyOnWriteArrayList<Networks> netInfoLst) {
			this.netInfoLst = netInfoLst;
		}
		
		public boolean add(Networks e) {
			return netInfoLst.add(e);
		}
	
		public void add(int index, Networks element) {
			netInfoLst.add(index, element);
		}
	
		public boolean addAll(Collection<? extends Networks> c) {
			return netInfoLst.addAll(c);
		}
	
		public boolean addAll(int index, Collection<? extends Networks> c) {
			return netInfoLst.addAll(index, c);
		}
	
		public boolean contains(Object o) {
			return netInfoLst.contains(o);
		}
	
		public boolean containsAll(Collection<?> c) {
			return netInfoLst.containsAll(c);
		}
	
		public boolean equals(Object o) {
			return netInfoLst.equals(o);
		}
	
		public Networks get(int index) {
			return netInfoLst.get(index);
		}
	
		public int indexOf(Object o) {
			return netInfoLst.indexOf(o);
		}
	
		public int indexOf(Networks e, int index) {
			return netInfoLst.indexOf(e, index);
		}
	
		public boolean isEmpty() {
			return netInfoLst.isEmpty();
		}
	
		public Networks remove(int index) {
			return netInfoLst.remove(index);
		}
	
		public boolean remove(Object o) {
			return netInfoLst.remove(o);
		}
	
		public boolean removeAll(Collection<?> c) {
			return netInfoLst.removeAll(c);
		}
	
		public boolean retainAll(Collection<?> c) {
			return netInfoLst.retainAll(c);
		}
	
		public int size() {
			return netInfoLst.size();
		}
	
		public void clear() {
			netInfoLst.clear();
		}

		public String toString() {
			return netInfoLst.toString();
		}	
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrgInfoObject [orgId=");
		builder.append(orgId);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", actNetLst=");
		builder.append(actNetLst);
		builder.append(", netIdMap=");
		builder.append(netIdMap);
		builder.append(", isLoaded=");
		builder.append(isLoaded);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
