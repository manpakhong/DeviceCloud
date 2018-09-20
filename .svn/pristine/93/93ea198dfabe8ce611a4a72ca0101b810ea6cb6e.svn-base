package com.littlecloud.factories;

import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.services.AlertClientMgr;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoClientConnectionFactory {
	private static final Logger log = Logger.getLogger(MongoClientConnectionFactory.class);
	private static PropertyService<MongoClientConnectionFactory> ps = new PropertyService<MongoClientConnectionFactory>(MongoClientConnectionFactory.class);
	private static DB fifoDb;
	private static String FIFO_MONGO_SERVER = "localhost";
	private static Integer FIFO_MONGO_SERVER_PORT = 27017;
	private static String FIFO_MONGO_DB = "fifo";
	
	private static void initBundles(){
		try{
			FIFO_MONGO_SERVER = ps.getString("FIFO_MONGO_SERVER");
			FIFO_MONGO_SERVER_PORT = ps.getInteger("FIFO_MONGO_SERVER_PORT");
			FIFO_MONGO_DB = ps.getString("FIFO_MONGO_DB");
		} catch (Exception e){
			log.error("FIFOREPLAY20140918 - MongoClientConnectionFactory.initBundles( )", e);
		}
	}
	
	public static DB getFifoMongoDb(){
		initBundles();
		try{
			if (fifoDb == null){
				MongoClient mongoClient = new MongoClient(FIFO_MONGO_SERVER , FIFO_MONGO_SERVER_PORT);
				fifoDb = mongoClient.getDB(FIFO_MONGO_DB);
			}
		} catch (Exception e){
			log.error("FIFOREPLAY20140918 - MongoClientConnectionFactory.getFifoMongoDb( )", e);
		}
		return fifoDb;
	}
}
