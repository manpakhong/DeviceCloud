package com.littlecloud.pool.object.utils;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.branch.DdnsRecordsDAO;
import com.littlecloud.control.dao.branch.criteria.DdnsRecordsCriteria;
import com.littlecloud.control.entity.DdnsRecords;
import com.littlecloud.pool.object.DevDdnsRecordsObject;

public class DdnsUtils {
	private static Logger log = Logger.getLogger(DdnsUtils.class);
	
	
	public static DevDdnsRecordsObject getDevDdnsRecordsObjectFromCache(DevDdnsRecordsObject devDdnsRecordsObject){
		try{
			if (devDdnsRecordsObject != null){
				if (devDdnsRecordsObject.getIana_id() != null && devDdnsRecordsObject.getSn() != null){
					devDdnsRecordsObject = ACUtil.getPoolObjectBySn(devDdnsRecordsObject, DevDdnsRecordsObject.class);			
				} // end if (devDdnsRecordsObject.getKey() != null)
			} // end if (devDdnsRecordsObject != null)
		
		} catch (Exception e){
			log.error("DDNS20140402 - getDevDdnsRecordsObjectFromCache", e);
		} // end try ... catch ...

		
		return devDdnsRecordsObject;
	} // end getDevDdnsRecordsObjectFromCache
	
	public static boolean setDevDdnsRecordsObject2Cache(DevDdnsRecordsObject devDdnsRecordsObject){
		boolean isSet = false;
		try{
			if (devDdnsRecordsObject != null){
				if (devDdnsRecordsObject.getIana_id() != null && devDdnsRecordsObject.getSn() != null){
					ACUtil.<DevDdnsRecordsObject> cachePoolObjectBySn(devDdnsRecordsObject, DevDdnsRecordsObject.class);
				}
			} // end if (devDdnsRecordsObject != null)
			isSet = true;
		} catch (Exception e){
			log.error("DDNS20140402 - setDevDdnsRecordsObject2Cache", e);
		} // end try ... catch ...
		return isSet;
	} // end setDevDdnsRecordsObject2Cache
	

	
	public static boolean removeDdnsRecordsObjectFromCache(DevDdnsRecordsObject devDdnsRecordsObject){
		boolean isRemoved = false;
		try{
			if (devDdnsRecordsObject != null){
				if (devDdnsRecordsObject.getIana_id() != null && devDdnsRecordsObject.getSn() != null){
					ACUtil.<DevDdnsRecordsObject> removePoolObjectBySn(devDdnsRecordsObject, DevDdnsRecordsObject.class);	
					isRemoved = true;
				}
			}
		}catch (Exception e){
			log.error("DDNS20140402 - removeDdnsRecordsObjectFromCache", e);
		}
		return isRemoved;
	}
	
	public static boolean insertOrUpdateDdnsRecords(DdnsRecords ddnsRecords){
		boolean isUpdated = false;
		boolean isUpdatedCache = false;
		boolean isUpdatedDb = false;
		try{
			if (ddnsRecords != null){
				if (ddnsRecords.getIanaId() != null && ddnsRecords.getSn() != null){
					DdnsRecordsDAO ddnsRecordsDao = new DdnsRecordsDAO();
					ddnsRecordsDao.saveOrUpdate(ddnsRecords);
					isUpdatedDb = true;
					DevDdnsRecordsObject devDdnsRecordsObject = new DevDdnsRecordsObject(ddnsRecords.getIanaId(), ddnsRecords.getSn());
					devDdnsRecordsObject = getDevDdnsRecordsObjectFromCache(devDdnsRecordsObject);
					if (devDdnsRecordsObject != null){
						devDdnsRecordsObject.deleteThenAddFromDdnsRecords(ddnsRecords);
						isUpdatedCache = true;
					} else {
						devDdnsRecordsObject = new DevDdnsRecordsObject(ddnsRecords.getIanaId(), ddnsRecords.getSn());
						devDdnsRecordsObject.addFromDdnsRecords(ddnsRecords);
						setDevDdnsRecordsObject2Cache(devDdnsRecordsObject);
						isUpdatedCache = true;
					}
					isUpdated = true;
					if (log.isDebugEnabled()){
						log.debugf("DDNS20140402 - updateDdnsRecords - ianaId: %s, sn: %s, isUpdatedCache: %s, isUpdatedDb: %s", ddnsRecords.getIanaId(), ddnsRecords.getSn(), isUpdatedCache, isUpdatedDb);
					}
				}
			}
		}catch (Exception e){
			log.error("DDNS20140402 - updateDdnsRecords", e);
		}
		return isUpdated;
	}
	
	public static List<DdnsRecords> getDdnsRecordsList(Integer ianaId, String sn){
		List<DdnsRecords> ddnsRecordsList = null;
		try{
			DevDdnsRecordsObject devDdnsRecordsObject = new DevDdnsRecordsObject(ianaId, sn);
			devDdnsRecordsObject = getDevDdnsRecordsObjectFromCache(devDdnsRecordsObject);
			if (devDdnsRecordsObject != null){
				List<DdnsRecords> ddnsRecordsTempList = devDdnsRecordsObject.getDdnsRecordsList();
				if (ddnsRecordsTempList != null && ddnsRecordsTempList.size() > 0){
					ddnsRecordsList = ddnsRecordsTempList;
				}
			} else {
				DdnsRecordsDAO ddnsRecordsDao = new DdnsRecordsDAO();
				DdnsRecordsCriteria criteria = new DdnsRecordsCriteria();
				criteria.setSn(sn);
				criteria.setIanaId(ianaId);
				List<DdnsRecords> ddnsRecordsTempList = ddnsRecordsDao.getDdnsRecordsList(criteria);
				if (ddnsRecordsTempList != null && ddnsRecordsTempList.size() > 0){
					devDdnsRecordsObject = new DevDdnsRecordsObject();
					devDdnsRecordsObject.setLastModified(new Date());
					devDdnsRecordsObject.addFromDdnsRecordsList(ianaId, sn, ddnsRecordsTempList);
					setDevDdnsRecordsObject2Cache(devDdnsRecordsObject);
					ddnsRecordsList = ddnsRecordsTempList;
				}
			}
		} catch (Exception e){
			log.error("DDNS20140402 - getDdnsRecordsList", e);
		}
		return ddnsRecordsList;
	}
	
	public static boolean deleteDdnsRecords(Integer ianaId, String sn){
		boolean result = false;
		try{
			DdnsRecordsDAO ddnsRecordsDao = new DdnsRecordsDAO();
			DevDdnsRecordsObject devDdnsRecordsObject = new DevDdnsRecordsObject();
			devDdnsRecordsObject.setIana_id(ianaId);
			devDdnsRecordsObject.setSn(sn);
			devDdnsRecordsObject = getDevDdnsRecordsObjectFromCache(devDdnsRecordsObject);
			if (devDdnsRecordsObject != null){
				List<DdnsRecords> ddnsRecordsList = devDdnsRecordsObject.getDdnsRecordsList();
				if (ddnsRecordsList != null && ddnsRecordsList.size() > 0){
					for (DdnsRecords ddnsRecords: ddnsRecordsList){
						if (ddnsRecords != null){
							ddnsRecordsDao.delete(ddnsRecords);
						}
					}
				}
				removeDdnsRecordsObjectFromCache(devDdnsRecordsObject);
				result = true;
			} else {
				DdnsRecordsCriteria criteria = new DdnsRecordsCriteria();
				criteria.setIanaId(ianaId);
				criteria.setSn(sn);
				List<DdnsRecords> ddnsRecordsList = ddnsRecordsDao.getDdnsRecordsList(criteria);
				if (ddnsRecordsList != null && ddnsRecordsList.size() > 0){
					for (DdnsRecords ddnsRecords: ddnsRecordsList){
						if (ddnsRecords != null){
							ddnsRecordsDao.delete(ddnsRecords);
						}
					}
					result = true;
				}
			}
		} catch (Exception e){
			log.error("DDNS20140402 - deleteDdnsRecords Exception: ianaId:" + ianaId + ",sn:" + sn , e);			
			return result;
		}
		return result;
	} // end deleteDdnsRecords
} // end class
