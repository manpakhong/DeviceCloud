package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.List;

import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.pool.object.utils.BranchUtils;

import java.util.concurrent.ConcurrentHashMap;

public class BranchInfoObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private String orgId = "Branch";
	private Integer iana_id = 9999;
	private SnsOrg snsOrg = new SnsOrg();		
	
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
	
	public BranchInfoObject() {
		super();
//		snsOrg = new SnsOrg();
	}

	public SnsOrg getSnsOrg() {
		return snsOrg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BranchInfoObject [orgId=");
		builder.append(orgId);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", snsOrg=");
		builder.append(snsOrg);
		builder.append(", isLoaded=");
		builder.append(isLoaded);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	
	public void setSnsOrgMap(ConcurrentHashMap<String, SnsOrganizations> snsOrgMap) {
		this.snsOrg.setSnsOrgMap(snsOrgMap);
	}
	
	public ConcurrentHashMap<String, SnsOrganizations> getSnsOrgMap() {
		return this.snsOrg.getSnsOrgMap();
	}
	
	public CopyOnWriteArraySet<String> getOrgIdSet() {
		return this.snsOrg.getOrgIdSet();
	}
	
	public SnsOrganizations getSnsOrgObj(int iana, String sn) {
		return this.snsOrg.get(BranchUtils.createKey(iana, sn));		
	}
	
	public static class SnsOrg implements Serializable
	{
		private CopyOnWriteArraySet<String> orgIdSet = new CopyOnWriteArraySet<String>();
		private ConcurrentHashMap<String, SnsOrganizations> snsOrgMap = new ConcurrentHashMap<String, SnsOrganizations>();
		
		public ConcurrentHashMap<String, SnsOrganizations> getSnsOrgMap() {
			return snsOrgMap;
		}

		public void setSnsOrgMap(ConcurrentHashMap<String, SnsOrganizations> snsOrgMap) {
			this.snsOrgMap = snsOrgMap;
		}
		
		public CopyOnWriteArraySet<String> getOrgIdSet() {
			return orgIdSet;
		}

		public void setOrgIdSet(CopyOnWriteArraySet<String> orgIdSet) {
			this.orgIdSet = orgIdSet;
		}

		public SnsOrganizations add(SnsOrganizations e) {
			return snsOrgMap.put(BranchUtils.createKey(e.getIanaId(), e.getSn()), e);
		}
	
		public void addAll(List<SnsOrganizations> list) {
			snsOrgMap.clear();
			for (SnsOrganizations snsOrg : list) {
				snsOrgMap.put(BranchUtils.createKey(snsOrg.getIanaId(), snsOrg.getSn()), snsOrg);
			}
		}

		public boolean contains(Object o) {
			return snsOrgMap.contains(o);
		}
	
		public SnsOrganizations get(String sn) {
			return snsOrgMap.get(sn);
		}
	
		public SnsOrganizations remove(String sn) {
			return snsOrgMap.remove(sn);
		}
	
		public SnsOrganizations remove(Object o) {
			return snsOrgMap.remove(o);
		}
	
		public int size() {
			return snsOrgMap.size();
		}
		
		public String toString() {
			return snsOrgMap.toString();
		}
	}
}
