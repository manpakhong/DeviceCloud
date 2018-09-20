package com.littlecloud.pool.object;

import java.io.Serializable;

public class LocationList implements Serializable{
	private Long timestamp;
	private Float latitude;
	private Float longitude;
	private Float altitude;
	private Float speed;
	private Float h_uncertain;
	private Float v_uncertain;
	private Float status;
	private Integer flag;
	private Float h_dop;

	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}		
	public Float getAltitude() {
		return altitude;
	}
	public void setAltitude(Float altitude) {
		this.altitude = altitude;
	}
	public Float getSpeed() {
		return speed;
	}
	public void setSpeed(Float speed) {
		this.speed = speed;
	}
	public Float getH_uncertain() {
		return h_uncertain;
	}
	public void setH_uncertain(Float h_uncertain) {
		this.h_uncertain = h_uncertain;
	}
	public Float getV_uncertain() {
		return v_uncertain;
	}
	public void setV_uncertain(Float v_uncertain) {
		this.v_uncertain = v_uncertain;
	}
	public Float getStatus() {
		return status;
	}
	public void setStatus(Float status) {
		this.status = status;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Float getH_dop() {
		return h_dop;
	}
	public void setH_dop(Float h_dop) {
		this.h_dop = h_dop;
	}

	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocationList [timestamp=");
		builder.append(timestamp);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", altitude=");
		builder.append(altitude);
		builder.append(", speed=");
		builder.append(speed);
		builder.append(", h_uncertain=");
		builder.append(h_uncertain);
		builder.append(", v_uncertain=");
		builder.append(v_uncertain);
		builder.append(", status=");
		builder.append(status);
		builder.append(", flag=");
		builder.append(flag);
		builder.append(", h_dop=");
		builder.append(h_dop);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
    public boolean equals(Object object)
    {
        boolean same = false;

        if (object != null && object instanceof LocationList)
        {
        	if( this.getTimestamp()!=null && ((LocationList) object).getTimestamp() != null )
        		same = this.getTimestamp().intValue() == ((LocationList) object).getTimestamp().intValue();
        }

        return same;
    }
	
	@Override
	public int hashCode(){
		return (this.getTimestamp() == null? 0 : this.getTimestamp().intValue());			
	}
	
}
	
	