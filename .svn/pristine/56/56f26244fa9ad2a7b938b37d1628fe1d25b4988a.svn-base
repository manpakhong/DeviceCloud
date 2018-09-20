package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Date;

import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.json.JsonExclude;

public class JsonDeviceLocations extends PoolObject implements Serializable{
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
	private boolean isExist;
	private Boolean expired;
	private Boolean isStatic;
	private Boolean isNewTrack;
	
	public JsonDeviceLocations() {

	}
	public JsonDeviceLocations(DeviceGpsLocations loc) {
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
	public float getLatitude() {
		return la;
	}
	public void setLatitude(float latitude) {
		this.la = latitude;
	}
	public float getLongitude() {
		return lo;
	}
	public void setLongitude(float longitude) {
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
	public boolean isExist() {
		return isExist;
	}
	public void setExist(boolean isExist) {
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonDeviceLocations [loc=");
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
		builder.append(", isExist=");
		builder.append(isExist);
		builder.append(", expired=");
		builder.append(expired);
		builder.append(", isStatic=");
		builder.append(isStatic);
		builder.append(", isNewTrack=");
		builder.append(isNewTrack);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
