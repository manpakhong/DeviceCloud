package com.littlecloud.control.json.model;

import java.util.List;

import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.model.pepvpn.PeerDetail;

public class Json_PepvpnInfo
{
	/* *** PeerDetailObject request and response from hub to endpoints ***  */
	private String sid;
	private int foundcache;
	private Devices selectedHub;
	private List<Devices> endpoints_lst;
	private List<PeerDetail> newpepconnLst;
	
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public int getFoundcache() {
		return foundcache;
	}
	public void setFoundcache(int foundcache) {
		this.foundcache = foundcache;
	}
	public Devices getSelectedHub() {
		return selectedHub;
	}
	public void setSelectedHub(Devices selectedHub) {
		this.selectedHub = selectedHub;
	}
	public List<Devices> getEndpoints_lst() {
		return endpoints_lst;
	}
	public void setEndpoints_lst(List<Devices> endpoints_lst) {
		this.endpoints_lst = endpoints_lst;
	}
	public List<PeerDetail> getNewpepconnLst() {
		return newpepconnLst;
	}
	public void setNewpepconnLst(List<PeerDetail> newpepconnLst) {
		this.newpepconnLst = newpepconnLst;
	}
}
