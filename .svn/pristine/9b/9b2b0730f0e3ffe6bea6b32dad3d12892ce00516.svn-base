package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.littlecloud.ac.json.model.Json_StationList.IfiList;
import com.littlecloud.ac.json.model.Json_StationList.StationStatusList;
import com.littlecloud.ac.json.model.Json_StationList.StationWirelessList;
import com.littlecloud.ac.json.model.Json_StationListDelta;

import org.jboss.logging.Logger;

public class StationListDeltaObject extends PoolObject implements PoolObjectIf, Serializable {
	
	private static Logger log = Logger.getLogger(StationListDeltaObject.class);	
	
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference

	private CopyOnWriteArrayList<StationList> station_list;

	private Integer timestamp;
	
	private CopyOnWriteArrayList<MacIpList> del_station_list;
	
	public static StationListDeltaObject convertStationLst(Json_StationListDelta stationList)
	{
		StationListDeltaObject slo = new StationListDeltaObject();
		if (stationList == null) {
			return null;
		}
		slo.setSn(stationList.getSn());
		slo.setDevice_id(stationList.getDevice_id());
		slo.setNetwork_id(stationList.getNetwork_id());
		slo.setOrganization_id(stationList.getOrganization_id());
		slo.setSid(stationList.getSid());

		List<StationList> lan_list = new ArrayList<StationList>();

		List<StationStatusList> JSSList = stationList.getStation_status_list();
		List<StationWirelessList> JSWList = stationList.getStation_wireless_list();

		if (JSSList == null && JSWList == null) {
			return null;
		}

		List<IfiList> JIfiList = stationList.getIfi_list();
		slo.setTimestamp(stationList.getTimestamp());
		int curTimestamp = (int)(new Date().getTime()/1000);

		if(JSSList !=null) {
			for (int i = 0; i < JSSList.size(); i++) {
				StationStatusList JSS = JSSList.get(i);
				StationList sl = new StationList();
				sl.setMac(JSS.getMac());
				sl.setIp(JSS.getIp());
				sl.setName(JSS.getName());
				sl.setStatus(JSS.getStatus());
				sl.setType(JSS.getType());
				lan_list.add(sl);
			}
		}
		
		if (JSWList != null) {
			for (int i = 0; i < JSWList.size(); i++) {
				StationWirelessList JSW = JSWList.get(i);
				if(JSW.getMac() == null)
					continue;
				
				// check by mac if the station is already in ethernet station list
				boolean FoundInList = false;
				// since delta list has less clients, use forloop is more effective than hashmap (than in StationList)
				for (int j = 0; j < lan_list.size(); j++) {
					StationList sl = lan_list.get(j);
					if (JSW.getMac().compareToIgnoreCase(sl.getMac()) == 0) {
						if(log.isDebugEnabled())
							log.debug("compare result match JSW "+JSW.getMac()+" sl " + sl.getMac());
						sl.setRadio_mode(JSW.getRadio_mode());
						sl.setConn_len(JSW.getConn_len());
						sl.setRssi(JSW.getRssi());
						sl.setIfi_id(JSW.getIfi_id());
						
						if (JIfiList != null) {
							for (int k = 0; k < JIfiList.size(); k++) {
								IfiList il = JIfiList.get(k);
								if (il.getIfi_id() == sl.getIfi_id()) {
									sl.setBssid(il.getBssid());
									sl.setEssid(il.getEssid());
									sl.setSecurity(il.getSecurity());
									sl.setChannel(il.getChannel());
									sl.setCh_width(il.getCh_width());
									break;
								}
							}
						}
						FoundInList = true;
						break;
					}
				}
	
				if (!FoundInList) {
					if(log.isDebugEnabled())
						log.debug("FoundInList is false " + JSW.getMac());
					StationList new_sl = new StationList();
					new_sl.setMac(JSW.getMac());
					new_sl.setIp(JSW.getIp());
					new_sl.setStatus(JSW.getStatus());
					new_sl.setRadio_mode(JSW.getRadio_mode());
					new_sl.setConn_len(JSW.getConn_len());
					new_sl.setRssi(JSW.getRssi());
					new_sl.setIfi_id(JSW.getIfi_id());
					for (int k = 0; k < JIfiList.size(); k++) {
						IfiList il = JIfiList.get(k);
						if (il.getIfi_id() == new_sl.getIfi_id()) {
							new_sl.setBssid(il.getBssid());
							new_sl.setEssid(il.getEssid());
							new_sl.setSecurity(il.getSecurity());
							new_sl.setChannel(il.getChannel());
							new_sl.setCh_width(il.getCh_width());
							break;
						}
					}
					new_sl.setClient_id(PoolObjectDAO.convertToClientId(new_sl.getMac(), new_sl.getIp()));
					if (new_sl.getStatus().equalsIgnoreCase("active")){
						new_sl.setFirst_appear_time(curTimestamp);
					}
					lan_list.add(new_sl);
				}
			}
		}

		slo.setStation_list(new CopyOnWriteArrayList<StationList>(lan_list));
		
		if (stationList.getDel_station_list() != null){
			List<MacIpList> mIList = new ArrayList<MacIpList>();
			mIList.addAll(stationList.getDel_station_list());
			slo.setDel_station_list(new CopyOnWriteArrayList<MacIpList>(mIList));
		}

		return slo;
	}
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
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

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public CopyOnWriteArrayList<StationList> getStation_list() {
		return station_list;
	}

	public void setStation_list(CopyOnWriteArrayList<StationList> station_list) {
		this.station_list = station_list;
	}

	public CopyOnWriteArrayList<MacIpList> getDel_station_list() {
		return del_station_list;
	}

	public void setDel_station_list(CopyOnWriteArrayList<MacIpList> del_station_list) {
		this.del_station_list = del_station_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationListDeltaObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", station_list=");
		builder.append(station_list);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", del_station_list=");
		builder.append(del_station_list);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
