package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.object.utils.NetUtils;

public class NetInfoObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private String org_netId;
	private Integer iana_id = 9999;
	private ActiveDevices actDevices;	
	//private InactiveDevLst inactDevLst;	
	private boolean isLoaded = false;
	
	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getOrg_netId() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String org_netId) {
		this.iana_id = iana_id;
		this.org_netId = org_netId;
	}
	
	public NetInfoObject(String orgId, Integer netId) {
		super();
		actDevices = new ActiveDevices();
		//inactDevLst = new InactiveDevLst();
		org_netId = getOrgNetId(orgId, netId);
	}
	
	public ActiveDevices getActDevices() {
		return actDevices;
	}

//	public InactiveDevLst getInactDevLst() {
//		return inactDevLst;
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NetInfoObject [org_netId=");
		builder.append(org_netId);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", actDevLst=");
		builder.append(actDevices);
//		builder.append(", inactDevLst=");
//		builder.append(inactDevLst);
		builder.append(", isLoaded=");
		builder.append(isLoaded);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

//	public CopyOnWriteArrayList<DevicesTrimObject> getAllDevInfoLst()
//	{
//		CopyOnWriteArrayList<DevicesTrimObject> fullLst = new CopyOnWriteArrayList<DevicesTrimObject>();
//		
//		if( getDevInfoLst() != null )
//			fullLst.addAll(getDevInfoLst());
//		
//		if( getInactDevInfoLst() != null )
//			fullLst.addAll(getInactDevInfoLst());
//		
//		return fullLst;
//	}
	
	public Map<Integer, DevicesTrimObject> getDevInfoMap() {
		return actDevices.getDevInfoMap();
	}
	
	public List<Devices> getDevicesLst(String orgId) {
		//return devInfoLst;
		return actDevices.getDevicesLst(orgId);
	}

//	public void setDevInfoMap(Map<Integer, DevicesTrimObject> devInfoMap) {
//		//this.devInfoLst = devInfoLst;
//		actDevLst.setDevInfoMap(devInfoMap);
//	}
	
//	public CopyOnWriteArrayList<DevicesTrimObject> getInactDevInfoLst() {
//		//return devInfoLst;
//		return inactDevLst.getDevInfoLst();
//	}
//
//	public void setInactDevInfoLst(CopyOnWriteArrayList<DevicesTrimObject> devInfoLst) {
//		//this.devInfoLst = devInfoLst;
//		inactDevLst.setDevInfoLst(devInfoLst);
//	}
	
	public Integer getIana_id() {
		return iana_id;
	}

	public String getOrg_netId() {
		return org_netId;
	}

	public void setOrg_netId(String org_netId) {
		this.org_netId = org_netId;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	
//	public List<DevicesTrimObject> getDevInfoLst() {			
//		return infomapToList(actDevLst.getDevInfoMap());
//	}
	
	private List<DevicesTrimObject> infomapToList(Map<Integer, DevicesTrimObject> devInfoMap)
	{
		List<DevicesTrimObject> devInfoLst = new ArrayList<DevicesTrimObject>();
		
		if (devInfoMap!=null && devInfoMap.values()!=null && devInfoMap.values().size()!=0)
			devInfoLst.addAll(devInfoMap.values());
		return devInfoLst;		
	}
	
	public static class ActiveDevices implements Serializable
	{
		//private CopyOnWriteArrayList<DevicesTrimObject> devInfoLst = new CopyOnWriteArrayList<DevicesTrimObject>();
		private Map<Integer, DevicesTrimObject> devInfoMap = new ConcurrentHashMap<Integer, DevicesTrimObject>();
		
		public List<Devices> getDevicesLst(String orgId) 
		{
			List<Devices> devFullLst = new ArrayList<Devices>(NetUtils.getConvertedDevicesObjectMapFromCache(orgId, devInfoMap));
			return devFullLst;
		}		
		
		public Map<Integer, DevicesTrimObject> getDevInfoMap() {
			return devInfoMap;
		}

		public void remove(DevicesTrimObject devTrim) {
			if (devTrim.getId()!=null)
				devInfoMap.remove(devTrim.getId());
		}

		public void putAll(Map<Integer, DevicesTrimObject> m) {
			devInfoMap.putAll(m);
		}

		public DevicesTrimObject put(Integer key, DevicesTrimObject value) {
			return devInfoMap.put(key, value);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ActiveDevLst [devInfoMap=");
			builder.append(devInfoMap);
			builder.append("]");
			return builder.toString();
		}

//		public CopyOnWriteArrayList<DevicesTrimObject> getDevInfoLst() {
//			return devInfoLst;
//		}
//
//		public void setDevInfoLst(CopyOnWriteArrayList<DevicesTrimObject> devInfoLst) {
//			this.devInfoLst = devInfoLst;
//		}
//
//		public boolean add(DevicesTrimObject e) {
//			return devInfoLst.add(e);
//		}
//	
//		public void add(int index, DevicesTrimObject element) {
//			devInfoLst.add(index, element);
//		}
//	
//		public boolean addAll(Collection<? extends DevicesTrimObject> c) {
//			return devInfoLst.addAll(c);
//		}
//	
//		public boolean addAll(int index, Collection<? extends DevicesTrimObject> c) {
//			return devInfoLst.addAll(index, c);
//		}
//	
//		public boolean contains(Object o) {
//			return devInfoLst.contains(o);
//		}
//	
//		public boolean containsAll(Collection<?> c) {
//			return devInfoLst.containsAll(c);
//		}
//	
//		public DevicesTrimObject get(int index) {
//			return devInfoLst.get(index);
//		}
//	
//		public DevicesTrimObject remove(int index) {
//			return devInfoLst.remove(index);
//		}
//	
//		public boolean remove(Object o) {
//			return devInfoLst.remove(o);
//		}
//	
//		public int size() {
//			return devInfoLst.size();
//		}
//		
//		public void clear() {
//			devInfoLst.clear();
//		}
//
//		public String toString() {
//			return devInfoLst.toString();
//		}
		
		
	}
	
//	public static class InactiveDevLst implements Serializable
//	{
//		private CopyOnWriteArrayList<DevicesTrimObject> devInfoInactiveLst = new CopyOnWriteArrayList<DevicesTrimObject>();
//				
//		public CopyOnWriteArrayList<DevicesTrimObject> getDevInfoLst() {
//			return devInfoInactiveLst;
//		}
//
//		public void setDevInfoLst(CopyOnWriteArrayList<DevicesTrimObject> devInfoInactiveLst) {
//			this.devInfoInactiveLst = devInfoInactiveLst;
//		}
//
//		public boolean add(DevicesTrimObject e) {
//			return devInfoInactiveLst.add(e);
//		}
//	
//		public void add(int index, DevicesTrimObject element) {
//			devInfoInactiveLst.add(index, element);
//		}
//	
//		public boolean addAll(Collection<? extends DevicesTrimObject> c) {
//			return devInfoInactiveLst.addAll(c);
//		}
//	
//		public boolean addAll(int index, Collection<? extends DevicesTrimObject> c) {
//			return devInfoInactiveLst.addAll(index, c);
//		}
//	
//		public boolean contains(Object o) {
//			return devInfoInactiveLst.contains(o);
//		}
//	
//		public boolean containsAll(Collection<?> c) {
//			return devInfoInactiveLst.containsAll(c);
//		}
//	
//		public DevicesTrimObject get(int index) {
//			return devInfoInactiveLst.get(index);
//		}
//	
//		public DevicesTrimObject remove(int index) {
//			return devInfoInactiveLst.remove(index);
//		}
//	
//		public boolean remove(Object o) {
//			return devInfoInactiveLst.remove(o);
//		}
//	
//		public int size() {
//			return devInfoInactiveLst.size();
//		}
//		
//		public void clear() {
//			devInfoInactiveLst.clear();
//		}
//
//		public String toString() {
//			return devInfoInactiveLst.toString();
//		}
//	}
	
	private static String getOrgNetId(String orgId, Integer netId)
	{
		return orgId + "_" + String.valueOf(netId);
	}
	
}
