package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.GeoFencePoints;
import com.littlecloud.control.entity.GeoFences;

public class DevGeoFencesObject extends PoolObject implements PoolObjectIf, Serializable{
	private static final Logger logger = Logger.getLogger(DevGeoFencesObject.class);
	/* key */
	private String sn;
	private Integer iana_id;
	
	/* values */
	private CopyOnWriteArrayList<GeoFencesObject> geoFencesObjectList;
	private Integer device_id;
	private Integer network_id;
	private boolean hasRetrievedOnceByTag;
	private boolean hasRetrievedOnceByNetwork;
	
	public DevGeoFencesObject(){
		super();
		this.hasRetrievedOnceByNetwork = false;
		this.hasRetrievedOnceByTag = false;
	}
	
	public List<GeoFencesObject> getGeoFencesObjectList() {
		return geoFencesObjectList;
	}
	
//	public void setGeoFencesObjectList(List<GeoFencesObject> geoFencesObjectList) {
//		this.geoFencesObjectList = geoFencesObjectList;
//	}

	public int updateGeoFencesObject(GeoFencesObject geoFencesObject){
		int noOfUpdate = 0;
		int noOfDelete = 0;

		
		noOfDelete = removeGeoFencesObjectByGeoId(geoFencesObject.getId());
		boolean addResult = addFromGeoFencesObject(geoFencesObject);
		
		if(noOfDelete > 0 && addResult){
			noOfUpdate = 1;
		}
		
		return noOfUpdate;
	}
	
	public int removeGeoFencesObjectByGeoId(Integer geoId){
		int noOfRemoval = 0;
		try{
			if (this.geoFencesObjectList != null && this.geoFencesObjectList.size() > 0){
				for (GeoFencesObject geoFencesObject: this.geoFencesObjectList){
					if (geoFencesObject.getId().equals(geoId)){
						this.geoFencesObjectList.remove(geoFencesObject);
						noOfRemoval++;
					}
				}
			}
		} catch (Exception e){
			logger.error("GEO20140204 - removeGeoFencesObjectByGeoId", e);
		}
		
		return noOfRemoval;
	}
	
	public Integer getNetwork_id() {
		return network_id;
	}

	public boolean getHasRetrievedOnceByTag() {
		return hasRetrievedOnceByTag;
	}

	public void setHasRetrievedOnceByTag(boolean hasRetrievedOnceByTag) {
		this.hasRetrievedOnceByTag = hasRetrievedOnceByTag;
	}

	public boolean getHasRetrievedOnceByNetwork() {
		return hasRetrievedOnceByNetwork;
	}

	public void setHasRetrievedOnceByNetwork(boolean hasRetrievedOnceByNetwork) {
		this.hasRetrievedOnceByNetwork = hasRetrievedOnceByNetwork;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
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
	
	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
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

	public class GeoFencesObject implements Serializable{
		private List<GeoFencePointsObject> geoFencePointsObjectList;
		private Integer id;
		private Integer networkId;
		private Integer deviceId;
		private String zoneName;
		private String type;
		private Integer speedLimit;
		private Float overSpeedFigure;
		private Float maxOverSpeedFigure;
		private String deviceTag;
		private Boolean emailNotify;
		private Integer createdAt;
		private Boolean enabled;
		private Integer inZoneTime;
		private Integer offZoneTime;
		private Integer overSpeedTime;
		private Integer normalSpeedTime;
		private Boolean isInZone;
		private Boolean isOverSpeedLimit;


		
		public Float getOverSpeedFigure() {
			return overSpeedFigure;
		}
		public void setOverSpeedFigure(Float overSpeedFigure) {
			this.overSpeedFigure = overSpeedFigure;
		}
		public Float getMaxOverSpeedFigure() {
			return maxOverSpeedFigure;
		}
		public void setMaxOverSpeedFigure(Float maxOverSpeedFigure) {
			this.maxOverSpeedFigure = maxOverSpeedFigure;
		}
		public String getDeviceTag() {
			return deviceTag;
		}
		public void setDeviceTag(String deviceTag) {
			this.deviceTag = deviceTag;
		}
		public Boolean getEmailNotify() {
			return emailNotify;
		}
		public void setEmailNotify(Boolean emailNotify) {
			this.emailNotify = emailNotify;
		}
		public Integer getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(Integer createdAt) {
			this.createdAt = createdAt;
		}
		public Boolean getEnabled() {
			return enabled;
		}
		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}
		public Integer getInZoneTime() {
			return inZoneTime;
		}
		public void setInZoneTime(Integer inZoneTime) {
			this.inZoneTime = inZoneTime;
		}
		public Integer getOffZoneTime() {
			return offZoneTime;
		}
		public void setOffZoneTime(Integer offZoneTime) {
			this.offZoneTime = offZoneTime;
		}
		public Integer getOverSpeedTime() {
			return overSpeedTime;
		}
		public void setOverSpeedTime(Integer overSpeedTime) {
			this.overSpeedTime = overSpeedTime;
		}
		public Integer getNormalSpeedTime() {
			return normalSpeedTime;
		}
		public void setNormalSpeedTime(Integer normalSpeedTime) {
			this.normalSpeedTime = normalSpeedTime;
		}
		public Boolean getIsInZone() {
			return isInZone;
		}
		public void setIsInZone(Boolean isInZone) {
			this.isInZone = isInZone;
		}
		public Boolean getIsOverSpeedLimit() {
			return isOverSpeedLimit;
		}
		public void setIsOverSpeedLimit(Boolean isOverSpeedLimit) {
			this.isOverSpeedLimit = isOverSpeedLimit;
		}
		public List<GeoFencePointsObject> getGeoFencePointsObjectList() {
			return geoFencePointsObjectList;
		}
		public void setGeoFencePointsObjectList(
				List<GeoFencePointsObject> geoFencePointsObjectList) {
			this.geoFencePointsObjectList = geoFencePointsObjectList;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getNetworkId() {
			return networkId;
		}
		public void setNetworkId(Integer networkId) {
			this.networkId = networkId;
		}
		public Integer getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(Integer deviceId) {
			this.deviceId = deviceId;
		}
		public String getZoneName() {
			return zoneName;
		}
		public void setZoneName(String zoneName) {
			this.zoneName = zoneName;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Integer getSpeedLimit() {
			return speedLimit;
		}
		public void setSpeedLimit(Integer speedLimit) {
			this.speedLimit = speedLimit;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("GeoFencesObject [geoFencePointsObjectList=");
			builder.append(geoFencePointsObjectList);
			builder.append(", id=");
			builder.append(id);
			builder.append(", networkId=");
			builder.append(networkId);
			builder.append(", deviceId=");
			builder.append(deviceId);
			builder.append(", zoneName=");
			builder.append(zoneName);
			builder.append(", type=");
			builder.append(type);
			builder.append(", speedLimit=");
			builder.append(speedLimit);
			builder.append(", overSpeedFigure=");
			builder.append(overSpeedFigure);
			builder.append(", maxOverSpeedFigure=");
			builder.append(maxOverSpeedFigure);
			builder.append(", deviceTag=");
			builder.append(deviceTag);
			builder.append(", emailNotify=");
			builder.append(emailNotify);
			builder.append(", createdAt=");
			builder.append(createdAt);
			builder.append(", enabled=");
			builder.append(enabled);
			builder.append(", inZoneTime=");
			builder.append(inZoneTime);
			builder.append(", offZoneTime=");
			builder.append(offZoneTime);
			builder.append(", overSpeedTime=");
			builder.append(overSpeedTime);
			builder.append(", normalSpeedTime=");
			builder.append(normalSpeedTime);
			builder.append(", isInZone=");
			builder.append(isInZone);
			builder.append(", isOverSpeedLimit=");
			builder.append(isOverSpeedLimit);
			builder.append("]");
			return builder.toString();
		}
		
	} // end class GeoFencesObject
	
	public class GeoFencePointsObject implements Serializable{
		private Integer Id;
		private Integer geoId;
		private Integer pointGroupId;
		private Integer pointSeq;
		private Float longitude;
		private Float latitude;
		private Integer radius;
		public Integer getId() {
			return Id;
		}
		public void setId(Integer id) {
			Id = id;
		}
		public Integer getGeoId() {
			return geoId;
		}
		public void setGeoId(Integer geoId) {
			this.geoId = geoId;
		}
		
		public Integer getPointGroupId() {
			return pointGroupId;
		}
		public void setPointGroupId(Integer pointGroupId) {
			this.pointGroupId = pointGroupId;
		}
		public Integer getPointSeq() {
			return pointSeq;
		}
		public void setPointSeq(Integer pointSeq) {
			this.pointSeq = pointSeq;
		}
		public Float getLongitude() {
			return longitude;
		}
		public void setLongitude(Float longitude) {
			this.longitude = longitude;
		}
		public Float getLatitude() {
			return latitude;
		}
		public void setLatitude(Float latitude) {
			this.latitude = latitude;
		}
		public Integer getRadius() {
			return radius;
		}
		public void setRadius(Integer radius) {
			this.radius = radius;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("GeoFencePointsObject [Id=");
			builder.append(Id);
			builder.append(", geoId=");
			builder.append(geoId);
			builder.append(", pointGroupId=");
			builder.append(pointGroupId);
			builder.append(", pointSeq=");
			builder.append(pointSeq);
			builder.append(", longitude=");
			builder.append(longitude);
			builder.append(", latitude=");
			builder.append(latitude);
			builder.append(", radius=");
			builder.append(radius);
			builder.append("]");
			return builder.toString();
		}
		
		
	} // end class GeoFencePointsObject
	
	public boolean addFromGeoFencesList(List<GeoFences> geoFencesList){
		return addFromGeoFencesList(null, null, geoFencesList);
	} // end addFromGeoFencesList(List<GeoFences> geoFencesList)
	public boolean addFromGeoFencesList(String sn, Integer ianaId, List<GeoFences> geoFencesList){
		if (sn != null && ianaId != null){
			this.setSn(sn);
			this.setIana_id(ianaId);
		}
		if (geoFencesList != null){
			for (GeoFences geoFences: geoFencesList){
				addFromGeoFences(geoFences);
			}
		} // end if (geoFencesList != null)
		
		return true;
	} // end addFromGeoFencesList(String sn, Integer ianaId, List<GeoFences> geoFencesList)
	
	public boolean addFromGeoFences(GeoFences geoFences){
		return addFromGeoFences(null, null, geoFences);
	} // end addFromGeoFences(GeoFences geoFences)
	
	public boolean deleteThenAddFromGeoFences(GeoFences geoFences){
		boolean result = false;
		try {
			if (this.geoFencesObjectList == null){
				this.geoFencesObjectList = new CopyOnWriteArrayList<GeoFencesObject>();
			}
			GeoFencesObject geoFencesObject = convert2GeoFencesObject(geoFences);
			
			int index = findGeoFencesIndex(geoFencesObject.getId());
			if (index != -1){
				this.geoFencesObjectList.remove(index);
			} // end if (!isGeoFencesExistedAtList(geoFenceObject.getId()))
			
			this.geoFencesObjectList.add(geoFencesObject);		
			result = true;

			
		} catch (Exception e){
			logger.error("GEO20140204 - deleteThenAddFromGeoFences", e);
		}
		return result;
	} // end deleteThenAddFromGeoFences
	
	
	
	public boolean addFromGeoFences(String sn, Integer ianaId, GeoFences geoFences){
		boolean result = false;
		try{
			
			if (sn != null && ianaId != null){
				this.setSn(sn);
				this.setIana_id(ianaId);
			}
			
			if (this.geoFencesObjectList == null){
				this.geoFencesObjectList = new CopyOnWriteArrayList<GeoFencesObject>();
			} // end if (this.geoFencesObject == null)
	
			GeoFencesObject geoFencesObject = convert2GeoFencesObject(geoFences);
			result = addFromGeoFencesObject(geoFencesObject);

			
		} catch (Exception e){
			logger.error("GEO20140204 - addFromGeoFences", e);
		} // end try ... catch ...
		
		return result;
	} // end addFromGeoFences(String sn, Integer ianaId, GeoFences geoFences)
	
	
	public boolean addFromGeoFencesObject(GeoFencesObject geoFencesObject){
		boolean result = false;
		if (!isGeoFencesExistedAtList(geoFencesObject.getId())){
			this.geoFencesObjectList.add(geoFencesObject);		
			result = true;
		} // end if (!isGeoFencesExistedAtList(geoFenceObject.getId()))		
		return result;
	}
	
	private GeoFencesObject convert2GeoFencesObject(GeoFences geoFences){
		
		GeoFencesObject geoFenceObject = new GeoFencesObject();
		geoFenceObject.setDeviceId(geoFences.getDeviceId());
		geoFenceObject.setId(geoFences.getId());
		geoFenceObject.setNetworkId(geoFences.getNetworkId());

		geoFenceObject.setSpeedLimit(geoFences.getSpeedLimit());
		geoFenceObject.setType(geoFences.getType());
		geoFenceObject.setZoneName(geoFences.getZoneName());

		geoFenceObject.setDeviceTag(geoFences.getDeviceTag());
		geoFenceObject.setEmailNotify(geoFences.getEmailNotify());
		geoFenceObject.setCreatedAt(geoFences.getCreatedAt());
		geoFenceObject.setEnabled(geoFences.getEnabled());
		
		
		
		if (geoFences.getGeoFencePointsList() != null){
			List<GeoFencePointsObject> geoFencePointsObjectList = new ArrayList<GeoFencePointsObject>();
			for(GeoFencePoints geoFencePoints: geoFences.getGeoFencePointsList()){
				GeoFencePointsObject geoFencePointsObject = new GeoFencePointsObject();
				geoFencePointsObject.setGeoId(geoFencePoints.getGeoId());
				geoFencePointsObject.setPointGroupId(geoFencePoints.getPointGroupId());
				geoFencePointsObject.setId(geoFencePoints.getId());
				geoFencePointsObject.setLatitude(geoFencePoints.getLatitude());
				geoFencePointsObject.setLongitude(geoFencePoints.getLongitude());
				geoFencePointsObject.setRadius(geoFencePoints.getRadius());
				geoFencePointsObject.setPointSeq(geoFencePoints.getPointSeq());
				geoFencePointsObjectList.add(geoFencePointsObject);
			} // end for(GeoFencePoints geoFencePoints: geoFences.getGeoFencePointsList())
			geoFenceObject.setGeoFencePointsObjectList(geoFencePointsObjectList);
		} // end if (geoFences.getGeoFencePointsList() != null)		
		
		return geoFenceObject;
	} // end convert2GeoFencesObject(GeoFences geoFences)
	
	public boolean isGeoFencesExistedAtList(Integer geoFenceId){
		boolean isExisted = false;
		
		int index = findGeoFencesIndex(geoFenceId);
		if (index != -1){
			isExisted = true;
		} // end if (index != -1)

		
		return isExisted;
	} // end isGeoFencesExistedAtList(Integer geoFenceId)
	
	public int findGeoFencesIndex(Integer geoFenceId){
		int index = -1;
		if (this.geoFencesObjectList != null){
			for (int i = 0; i < this.geoFencesObjectList.size(); i++){
				GeoFencesObject geoFencesObject = this.geoFencesObjectList.get(i);
				if (geoFenceId.equals(geoFencesObject.getId())){
					index = i;
				} // end if (geoFenceId.equals(geoFencesObject.getId()))
			} // end for (GeoFencesObject geoFencesObject: this.geoFencesObjectList)
		} // end if (this.geoFencesObjectList != null)
		return index;
	} // end findGeoFencesIndex(Integer geoFenceId)
	
	public List<GeoFences> getGeoFencesList(){
		List<GeoFences> geoFencesList = null;
		try{
			if (this.geoFencesObjectList != null){
				geoFencesList = new ArrayList<GeoFences>();
				
				for (GeoFencesObject geoFencesObject: this.geoFencesObjectList){
					GeoFences geoFences = new GeoFences();
					geoFences.setId(geoFencesObject.getId());
					geoFences.setNetworkId(geoFencesObject.getNetworkId());
					geoFences.setDeviceId(geoFencesObject.getDeviceId());
					geoFences.setZoneName(geoFencesObject.getZoneName());
					geoFences.setType(geoFencesObject.getType());
					geoFences.setSpeedLimit(geoFencesObject.getSpeedLimit());
					geoFences.setDeviceTag(geoFencesObject.getDeviceTag());
					geoFences.setEmailNotify(geoFencesObject.getEmailNotify());
					geoFences.setCreatedAt(geoFencesObject.getCreatedAt());
					geoFences.setEnabled(geoFencesObject.getEnabled());
					
					if (geoFencesObject.getGeoFencePointsObjectList() != null){
						List<GeoFencePoints> geoFencePointsList = new ArrayList<GeoFencePoints>();
						
						for (GeoFencePointsObject geoFencePointsObject: geoFencesObject.getGeoFencePointsObjectList()){
							GeoFencePoints geoFencePoints = new GeoFencePoints();
							geoFencePoints.setGeoId(geoFencePointsObject.getGeoId());
							geoFencePoints.setPointGroupId(geoFencePointsObject.getPointGroupId());
							geoFencePoints.setId(geoFencePointsObject.getId());
							geoFencePoints.setLatitude(geoFencePointsObject.getLatitude());
							geoFencePoints.setLongitude(geoFencePointsObject.getLongitude());
							geoFencePoints.setRadius(geoFencePointsObject.getRadius());
							geoFencePoints.setPointSeq(geoFencePointsObject.getPointSeq());
							geoFencePointsList.add(geoFencePoints);
							
						} // end for (GeoFencePointsObject geoFencePointsObject: geoFencesObject.getGeoFencePointsObjectList())
						geoFences.setGeoFencePointsList(geoFencePointsList);
					} // end if (geoFencesObject.getGeoFencePointsObjectList() != null)
					geoFencesList.add(geoFences);
				} // end for (GeoFencesObject geoFencesObject: this.geoFencesObjectList)
			} // end if (this.geoFencesObjectList != null)
		} catch (Exception e){
			logger.error("GEO20140204 - getGeoFencesList", e);
		} // end try ... catch ...
		return geoFencesList;
	} // end getGeoFencesList()
} // end class
