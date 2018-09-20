package com.littlecloud.control.json.model;

import java.util.ArrayList;
import java.util.List;

import com.littlecloud.pool.object.DevNeighborListObject.Neighbor;

public class Json_Device_Neighbor_List {
	private Integer iana_id;
	private String sn;
	private List<Json_Device_Neighbor> neighbors;

	public Integer getIana_id() {
		return iana_id;
	}
	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Json_Device_Neighbor_List(){
		neighbors = new ArrayList<Json_Device_Neighbor>();
	}
	public List<Json_Device_Neighbor> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Json_Device_Neighbor> neighbors) {
		this.neighbors = neighbors;
	}
	
	public boolean addJsonDeviceNeighbor(Json_Device_Neighbor jsonDeviceNeighbor){
		boolean added = false;
		if (jsonDeviceNeighbor != null){
			neighbors.add(jsonDeviceNeighbor);
			added = true;
		}
		return added;
	}
	
	public boolean addDevNeighbor(Neighbor devNeighbor){
		boolean added = false;
		if (devNeighbor != null){
			Json_Device_Neighbor jsonDeviceNeighbor = new Json_Device_Neighbor();
			jsonDeviceNeighbor.setChannel(devNeighbor.getChannel());
			jsonDeviceNeighbor.setLast(devNeighbor.getLast());
			jsonDeviceNeighbor.setMac(devNeighbor.getMac());
			jsonDeviceNeighbor.setSecurity(devNeighbor.getSecurity());
			jsonDeviceNeighbor.setSignal(devNeighbor.getSignal());
			jsonDeviceNeighbor.setSsid_hex(devNeighbor.getSsid_hex());
			jsonDeviceNeighbor.setStatus(devNeighbor.getStatus());
			
			neighbors.add(jsonDeviceNeighbor);
			added = true;
		}
		return added;
	}
	@Override
	public String toString() {
		return "Json_Device_Neighbor_List [iana_id=" + iana_id + ", sn=" + sn
				+ ", neighbors=" + neighbors + "]";
	}
	

	
}
