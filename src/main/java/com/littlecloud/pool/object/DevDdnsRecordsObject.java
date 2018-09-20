package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import com.littlecloud.control.entity.DdnsRecords;

public class DevDdnsRecordsObject extends PoolObject implements PoolObjectIf, Serializable{
	private static final Logger logger = Logger.getLogger(DevDdnsRecordsObject.class);
	/* key */
	private String sn;
	private Integer iana_id;
	private Date lastModified;
	
	/* fields */
	private CopyOnWriteArrayList<DdnsRecordsObject> ddnsRecordsObjectList;
	
	public DevDdnsRecordsObject(Integer ianaId, String sn){
		this.iana_id = ianaId;
		this.sn = sn;
	}
	
	public DevDdnsRecordsObject(){
		
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public class DdnsRecordsObject implements Serializable{
		private String sn;
		private Integer iana_id;
		private Integer id;
		private String organizationId;
		private String wanId;
		private String ddnsName;
		private String wanIp;
		private Date lastUpdated;
				
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
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getOrganizationId() {
			return organizationId;
		}
		public void setOrganizationId(String organizationId) {
			this.organizationId = organizationId;
		}
		public String getWanId() {
			return wanId;
		}
		public void setWanId(String wanId) {
			this.wanId = wanId;
		}
		public String getDdnsName() {
			return ddnsName;
		}
		public void setDdnsName(String ddnsName) {
			this.ddnsName = ddnsName;
		}
		public String getWanIp() {
			return wanIp;
		}
		public void setWanIp(String wanIp) {
			this.wanIp = wanIp;
		}
		public Date getLastUpdated() {
			return lastUpdated;
		}
		public void setLastUpdated(Date lastUpdated) {
			this.lastUpdated = lastUpdated;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DdnsRecordsObject [sn=");
			builder.append(sn);
			builder.append(", iana_id=");
			builder.append(iana_id);
			builder.append(", id=");
			builder.append(id);
			builder.append(", organizationId=");
			builder.append(organizationId);
			builder.append(", wanId=");
			builder.append(wanId);
			builder.append(", ddnsName=");
			builder.append(ddnsName);
			builder.append(", wanIp=");
			builder.append(wanIp);
			builder.append(", lastUpdated=");
			builder.append(lastUpdated);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}
	}
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
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

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevDdnsRecordsObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", lastModified=");
		builder.append(lastModified);
		builder.append(", ddnsRecordsObjectList=");
		builder.append(ddnsRecordsObjectList);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

	public boolean addFromDdnsRecordsList(List<DdnsRecords> ddnsRecordsList){
		return addFromDdnsRecordsList(null, null, ddnsRecordsList);
	} // end addFromGeoFencesList(List<GeoFences> geoFencesList)
	public boolean addFromDdnsRecordsList(Integer ianaId, String sn, List<DdnsRecords> ddnsRecordsList){
		if (sn != null && ianaId != null){
			this.setSn(sn);
			this.setIana_id(ianaId);
		}
		if (ddnsRecordsList != null){
			for (DdnsRecords ddnsRecords: ddnsRecordsList){
				addFromDdnsRecords(ddnsRecords);
			}
		} // end if (geoFencesList != null)
		
		return true;
	} // end addFromGeoFencesList(String sn, Integer ianaId, List<GeoFences> geoFencesList)
	
	public boolean addFromDdnsRecords(DdnsRecords ddnsRecords){
		return addFromDdnsRecords(null, null, ddnsRecords);
	} // end addFromDdnsRecords(DdnsRecords ddnsRecords)
	
	public boolean deleteThenAddFromDdnsRecords(DdnsRecords ddnsRecords){
		boolean result = false;
		try {
			if (this.ddnsRecordsObjectList == null){
				this.ddnsRecordsObjectList = new CopyOnWriteArrayList<DdnsRecordsObject>();
			}
			DdnsRecordsObject ddnsRecordsObject = convert2DdnsRecordsObject(ddnsRecords);
			
			int index = findDdnsRecordsIndex(ddnsRecordsObject.getId());
			if (index != -1){
				this.ddnsRecordsObjectList.remove(index);
			} // end if (!isGeoFencesExistedAtList(geoFenceObject.getId()))
			
			this.ddnsRecordsObjectList.add(ddnsRecordsObject);		
			result = true;

		} catch (Exception e){
			logger.error("DDNS20140402 - deleteThenAddFromDdnsRecords", e);
		}
		return result;
	} // end deleteThenAddFromDdnsRecords
	
	public boolean addFromDdnsRecords(String sn, Integer ianaId, DdnsRecords ddnsRecords){
		boolean result = false;
		try{
			
			if (sn != null && ianaId != null){
				this.setSn(sn);
				this.setIana_id(ianaId);
			}
			
			if (this.ddnsRecordsObjectList == null){
				this.ddnsRecordsObjectList = new CopyOnWriteArrayList<DdnsRecordsObject>();
			} // end if (this.ddnsRecordsObjectList == null)
	
			DdnsRecordsObject ddnsRecordsObject = convert2DdnsRecordsObject(ddnsRecords);
			result = addFromDdnsRecordsObject(ddnsRecordsObject);

			
		} catch (Exception e){
			logger.error("DDNS20140402 - addFromDdnsRecords", e);
		} // end try ... catch ...
		
		return result;
	} // end addFromDdnsRecords(String sn, Integer ianaId, DdnsRecords ddnsRecords)
	
	public boolean addFromDdnsRecordsObject(DdnsRecordsObject ddnsRecordsObject){
		boolean result = false;
		if (!isDdnsRecordsExistedAtList(ddnsRecordsObject.getId())){
			this.ddnsRecordsObjectList.add(ddnsRecordsObject);		
			result = true;
		}
		return result;
	}
	
	private DdnsRecordsObject convert2DdnsRecordsObject(DdnsRecords ddnsRecords){
		DdnsRecordsObject ddnsRecordsObject = null;
		if (ddnsRecords != null){
			ddnsRecordsObject = new DdnsRecordsObject();
			ddnsRecordsObject.setDdnsName(ddnsRecords.getDdnsName());
			ddnsRecordsObject.setId(ddnsRecords.getId());
			ddnsRecordsObject.setOrganizationId(ddnsRecords.getOrganizationId());
			ddnsRecordsObject.setSn(ddnsRecords.getSn());
			ddnsRecordsObject.setLastUpdated(ddnsRecords.getLastUpdated());
			ddnsRecordsObject.setIana_id(ddnsRecords.getIanaId());
			ddnsRecordsObject.setWanIp(ddnsRecords.getWanIp());
			ddnsRecordsObject.setWanId(ddnsRecords.getWanId());
		}
		return ddnsRecordsObject;
	}
	
	public boolean isDdnsRecordsExistedAtList(Integer ianaId_sn){
		boolean isExisted = false;
		
		int index = findDdnsRecordsIndex(ianaId_sn);
		if (index != -1){
			isExisted = true;
		} // end if (index != -1)		
		return isExisted;
	} // end isDdnsRecordsExistedAtList(Integer ianaId_sn)
	
	public int findDdnsRecordsIndex(Integer ianaId_sn){
		int index = -1;
		if (this.ddnsRecordsObjectList != null){
			for (int i = 0; i < this.ddnsRecordsObjectList.size(); i++){
				DdnsRecordsObject ddnsRecordsObject = this.ddnsRecordsObjectList.get(i);
				if (ianaId_sn.equals(ddnsRecordsObject.getId())){
					index = i;
				} // end if (ianaId_sn.equals(ddnsRecordsObject.getId()))
			} // end for (int i = 0; i < this.ddnsRecordsObjectList.size(); i++)
		} // end if (this.ddnsRecordsObjectList != null)
		return index;
	} // end findDdnsRecordsIndex(Integer ianaId_sn)
	
	public List<DdnsRecords> getDdnsRecordsList(){
		List<DdnsRecords> ddnsRecordsList = null;
		try{
			if (this.ddnsRecordsObjectList != null){
				ddnsRecordsList = new ArrayList<DdnsRecords>();
				for (DdnsRecordsObject ddnsRecordsObject: this.ddnsRecordsObjectList){
					DdnsRecords ddnsRecords = new DdnsRecords();
					ddnsRecords.setDdnsName(ddnsRecordsObject.getDdnsName());
					ddnsRecords.setIanaId(ddnsRecordsObject.getIana_id());
					ddnsRecords.setId(ddnsRecordsObject.getId());
					ddnsRecords.setOrganizationId(ddnsRecordsObject.getOrganizationId());
					ddnsRecords.setSn(ddnsRecordsObject.getSn());
					ddnsRecords.setLastUpdated(ddnsRecordsObject.getLastUpdated());
					ddnsRecords.setWanIp(ddnsRecordsObject.getWanIp());
					ddnsRecords.setWanId(ddnsRecordsObject.getWanId());
					ddnsRecordsList.add(ddnsRecords);
				}
			}
		} catch (Exception e){
			logger.error("DDNS20140402 - getDdnsRecordsList", e);
		}
		return ddnsRecordsList;
	} // end getDdnsRecordsList()
	
} // end class
