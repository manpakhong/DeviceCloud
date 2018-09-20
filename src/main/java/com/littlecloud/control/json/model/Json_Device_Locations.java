package com.littlecloud.control.json.model;

import java.util.Date;

import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceLocations;
import com.littlecloud.control.json.JsonExclude;

public class Json_Device_Locations {
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Json_Device_Locations [loc=");
		builder.append(loc);
		builder.append(", la=");
		builder.append(la);
		builder.append(", lo=");
		builder.append(lo);
		builder.append(", sp=");
		builder.append(sp);
		builder.append(", at=");
		builder.append(at);
		builder.append(", hu=");
		builder.append(hu);
		builder.append(", vu=");
		builder.append(vu);
		builder.append(", ts=");
		builder.append(ts);
		builder.append(", cp=");
		builder.append(cp);
		builder.append(", isExist=");
		builder.append(isExist);
		builder.append(", expired=");
		builder.append(expired);
		builder.append(", isStatic=");
		builder.append(isStatic);
		builder.append(", isNewTrack=");
		builder.append(isNewTrack);
		builder.append("]");
		return builder.toString();
	}
	@JsonExclude
	private DeviceGpsLocations loc;
	
	public static final String dateFormat = "yyyyMMddHHmm";
	
	//@JsonExclude
	//private Integer id;
	//private int device_id;
	private Float la;
	private Float lo;
	private Float sp;
	private Float at;
	private Float hu;
	private Float vu;
	private Date ts;
	private Integer cp;	/* cut-off point - type in db , flag in wtp */
	private Boolean isExist;
	private Boolean expired;
	private Boolean isStatic;
	private Boolean isNewTrack;
	
	public Json_Device_Locations() {

	}
	public Json_Device_Locations(DeviceGpsLocations loc) {
		this.loc = loc;

		if (loc == null)
			return;

		//id = loc.getId();
		la = loc.getLatitude();
		lo = loc.getLongitude();
		sp = loc.getSpeed();
		at = loc.getAltitude();
		ts = loc.getDatetime();
		hu = loc.getHUncertain();
	}
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
	public Float getLatitude() {
		return la;
	}
	public void setLatitude(Float latitude) {
		this.la = latitude;
	}
	public Float getLongitude() {
		return lo;
	}
	public void setLongitude(Float longitude) {
		this.lo = longitude;
	}
	public Float getSpeed() {
		return sp;
	}
	public void setSpeed(Float sp) {
		this.sp = sp;
	}
	public Float getAltitude() {
		return at;
	}
	public void setAltitude(Float at) {
		this.at = at;
	}
	public Date getTimestamp() {
		return ts;
	}
	public void setTimestamp(Date timestamp) {
		this.ts = timestamp;
	}
	public Boolean isExist() {
		return isExist;
	}
	public void setExist(Boolean isExist) {
		this.isExist = isExist;
	}
	public Boolean getExpired() {
		return expired;
	}
	public void setExpired(Boolean expired) {
		this.expired = expired;
	}
	public Float getHu() {
		return hu;
	}
	public void setHu(Float hu) {
		this.hu = hu;
	}
	public Float getVu() {
		return vu;
	}
	public void setVu(Float vu) {
		this.vu = vu;
	}
	public Integer getType() {
		return cp;
	}
	public void setType(Integer cp) {
		this.cp = cp;
	}		
	public static String getDateformat() {
		return dateFormat;
	}
	public Boolean getIsStatic() {
		return isStatic;
	}
	public void setIsStatic(Boolean isStatic) {
		this.isStatic = isStatic;
	}
	public Boolean getIsNewTrack() {
		return isNewTrack;
	}
	public void setIsNewTrack(Boolean isNewTrack) {
		this.isNewTrack = isNewTrack;
	}
}
