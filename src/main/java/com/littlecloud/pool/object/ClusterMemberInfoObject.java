package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClusterMemberInfoObject extends PoolObject implements Serializable
{
	private Set<String> objkeys;
	private Integer totalobj;
	
	public Set<String> getObjkeys() {
		return objkeys;
	}
	
	public void setObjkeys(Set<String> objkeys) {
		this.objkeys = objkeys;
	}
	
	public Integer getTotalobj() {
		return totalobj;
	}
	
	public void setTotalobj(Integer totalobj) {
		this.totalobj = totalobj;
	}
	
	@Override
	public String toString() {
		return "ClusterMemberInfoObject [objkeys=" + objkeys + ", totalobj=" + totalobj + "]";
	}
}
