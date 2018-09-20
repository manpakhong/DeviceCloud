package com.littlecloud.mongo.daos;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.factories.MongoClientConnectionFactory;
import com.littlecloud.helpers.MongoDbCriteriaHelper;
import com.littlecloud.mongo.daos.criteria.FifoAcMessageCriteria;
import com.littlecloud.mongo.eos.FifoAcMessage;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class FifoCollectionDAO {
	private static final Logger log = Logger.getLogger(FifoCollectionDAO.class);

	public List<FifoAcMessage> getFifoAcMessage(FifoAcMessageCriteria criteria){
		List<FifoAcMessage> fifoAcMessageList = null;
		try{
			DB db = MongoClientConnectionFactory.getFifoMongoDb();		
			DBCollection collection = db.getCollection("fifo");
			fifoAcMessageList = new ArrayList<FifoAcMessage>();
			
			MongoDbCriteriaHelper<FifoAcMessageCriteria> criteriaHelper = new MongoDbCriteriaHelper<FifoAcMessageCriteria>();
			
			BasicDBObject searchQuery = criteriaHelper.generateSearchJsonSearchBasicDBObject(criteria);
			
			//searchQuery.put("device_id", new BasicDBObject("$in", deviceIdList));
			
			DBCursor cursor = collection.find(searchQuery)
					.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
			if (criteria.getLimit() != null){
				cursor.limit(criteria.getLimit());
			}
			Integer recordCount = cursor.count();
			while (cursor.hasNext()){
				FifoAcMessage fifoAcMessage = new FifoAcMessage();
				DBObject dbObject = cursor.next();
				
				BasicDBObject  basicDBObject = (BasicDBObject) dbObject;
				if (basicDBObject.get("_id") != null){
					fifoAcMessage.setId(basicDBObject.getString("_id"));
				}
				if (basicDBObject.get("dir") != null){
					fifoAcMessage.setDir(basicDBObject.getString("dir"));
				}
				if (basicDBObject != null){
					fifoAcMessage.setFullMessage(basicDBObject.toString());
				}
				if (basicDBObject.get("timestamp") != null){
					fifoAcMessage.setTimestamp(basicDBObject.getInt("timestamp"));
				}
				if (basicDBObject.get("payload") != null){
					BasicDBObject payloadBasicDbObject = (BasicDBObject) basicDBObject.get("payload");
					
					fifoAcMessage.setPayload(payloadBasicDbObject.toString());
					if (payloadBasicDbObject.get("iana_id") != null){
						fifoAcMessage.setIanaId(payloadBasicDbObject.getInt("iana_id")); 
					}
					if (payloadBasicDbObject.get("sn") != null){
						fifoAcMessage.setSn(payloadBasicDbObject.getString("sn"));
					}
					if (payloadBasicDbObject.get("status") != null){
						fifoAcMessage.setStatus(payloadBasicDbObject.getInt("status"));
					}
					if (payloadBasicDbObject.get("type") != null){
						fifoAcMessage.setType(payloadBasicDbObject.getString("type"));
					}
				}
				fifoAcMessage.setTotalRecordCount(recordCount);
				fifoAcMessageList.add(fifoAcMessage);
			}

			cursor.close();
			
		} catch (Exception e){
			log.error("FIFOREPLAY20140918 - FifoCollectionDAO.getFifoAcMessage() - ", e);
		}
		return fifoAcMessageList;
	}
}
