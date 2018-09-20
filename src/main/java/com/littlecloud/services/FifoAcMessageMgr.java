package com.littlecloud.services;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.mongo.daos.FifoCollectionDAO;
import com.littlecloud.mongo.daos.criteria.FifoAcMessageCriteria;
import com.littlecloud.mongo.eos.FifoAcMessage;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;

public class FifoAcMessageMgr {
	private static final Logger log = Logger.getLogger(FifoAcMessageMgr.class);
	private FifoCollectionDAO fifoCollectionDao;
	public FifoAcMessageMgr(){
		init();
	}
	private void init(){
		try{
			fifoCollectionDao = new FifoCollectionDAO();
		} catch (Exception e){
			log.error("FifoAcMessageMgr.init()", e);
		}
	}
	public List<FifoAcMessage> getFifoAcMessageList(FifoAcMessageCriteria fifoAcMessageCriteria){
		List<FifoAcMessage> fifoAcMessageList = null;
		try{
			fifoAcMessageList = getFifoAcMessageList(fifoAcMessageCriteria, null, null);
		} catch (Exception e){
			log.error("FifoAcMessageMgr.getFifoAcMessageList()", e);
		}
		return fifoAcMessageList;
	}

	public List<FifoAcMessage> getFifoAcMessageList(FifoAcMessageCriteria fifoAcMessageCriteria, String orgId){
		List<FifoAcMessage> fifoAcMessageList = null;
		try{
			fifoAcMessageList = getFifoAcMessageList(fifoAcMessageCriteria, orgId, null);
		} catch (Exception e){
			log.error("FifoAcMessageMgr.getFifoAcMessageList()", e);
		}
		return fifoAcMessageList;
	}
	public List<FifoAcMessage> getFifoAcMessageList(FifoAcMessageCriteria fifoAcMessageCriteria, String orgId, Integer networkId){
		List<FifoAcMessage> fifoAcMessageList = null;
		try{
			List<String> snList = new ArrayList<String>();
			if (fifoAcMessageCriteria.getSnList() == null){
				if (orgId != null && !orgId.isEmpty()){
					if (networkId != null){ // network level
						List<Integer> networkIdList = new ArrayList<Integer>();
						networkIdList.add(networkId);
						snList = getSnList(orgId, networkIdList);
					} else { // given org id 
						List<Networks> networkList = OrgInfoUtils.getNetworkLst(orgId);
						List<Integer> networkIdList = null;
						if (networkList != null && networkList.size() > 0){
							networkIdList = new ArrayList<Integer>();
							for (Networks network: networkList){
								if (network != null && network.getId() != null){
									networkIdList.add(network.getId());
								}
							}
						}
						if (networkIdList != null){
							List<String> snListTemp = getSnList(orgId, networkIdList);
							if (snListTemp != null && snListTemp.size() > 0){
								snList.addAll(snListTemp);
							}
						}
					}
				} 
			} // end if (fifoAcMessageCriteria.getSnList() == null)
			
			if (snList != null && snList.size() > 0){
				fifoAcMessageCriteria.setSnList(snList);
			}
			
			fifoAcMessageList = fifoCollectionDao.getFifoAcMessage(fifoAcMessageCriteria);
		} catch (Exception e){
			log.error("FifoAcMessageMgr.getFifoAcMessageList()", e);
		}
		return fifoAcMessageList;
	}
	
	private List<String> getSnList(String orgId, List<Integer> networkIdList){
		List<String> snList = null;
		try{
			for (Integer networkId: networkIdList){
				List<Devices> deviceList = NetUtils.getDeviceLstByNetId(orgId, networkId);
				if (deviceList != null && deviceList.size() > 0){
					if (snList == null){
						snList = new ArrayList<String>();
					}
					
					List<String> snListTemp = new ArrayList<String>();
					for (Devices device: deviceList){
						if (device != null && device.getSn() != null){
							snListTemp.add(device.getSn());
						}
					}
					if (snListTemp.size() > 0){
						snList.addAll(snListTemp);
					}
				}
			}
		} catch (Exception e){
			log.error("FifoAcMessageMgr.getSnList()", e);
		}
		return snList;
	}
	public List<String> getAllMessageTypes(){
		List<String> messageTypeList = new ArrayList<String> ();
		for (MessageType mt: MessageType.values()){
			messageTypeList.add(mt.name());
		}
		return messageTypeList;
	}
	
	
}
