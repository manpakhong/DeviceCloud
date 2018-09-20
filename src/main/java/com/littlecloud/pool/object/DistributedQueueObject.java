package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.transaction.TransactionManager;

import org.infinispan.AdvancedCache;
import org.infinispan.util.concurrent.TimeoutException;
import org.infinispan.context.Flag;
import org.jboss.logging.Logger;

import com.littlecloud.pool.Cluster;

public class DistributedQueueObject implements Serializable  {
	public static final Logger log  = Logger.getLogger(DistributedQueueObject.class);
	private static DistributedQueueObject dqueue = null;
	private static final int MAX_LOCK_RETRY = 3;
	private static final int TIME_LOCK_RETRY_WAIT = 200;
	private static final int TIME_OP_GAP = 10;
	private static AdvancedCache<String, Object> acache = null;
	private static final int DEFAULT_CACHE_EXPIRE_HOUR = 5;

	
	public synchronized static DistributedQueueObject getInstance() {
		try {
			if (dqueue == null) {
				acache = Cluster.getCache(Cluster.CACHENAME.LittleCloudCacheRepl).getAdvancedCache();
				dqueue = new DistributedQueueObject();
				log.warn("getInstance: new dqueue");
			} else {
				log.warn("getInstance: returns existing dqueue");
			}
			return dqueue;
		} catch (java.lang.IllegalStateException ie) {
			log.error("getInstance: failed to init DistributedQueueObject, ie=" + ie);
			return null;
		} catch (Exception e) {
			log.error("getInstance: failed to init DistributedQueueObject, e=" + e);
			return null;
		} 
	}

	private DistributedQueueObject() throws Exception {
		TransactionManager tm = null;
		
		try {
			tm = acache.getTransactionManager();
			tm.begin();
			if(log.isInfoEnabled())
				log.info("Constructor: tm.begin()");
			int i = 0;
			for(i = 0; i < MAX_LOCK_RETRY; i++) { 
				boolean rtn = acache.withFlags(Flag.FAIL_SILENTLY).lock("QUEUE_LOCK");
				if(rtn) {
					break;
				} else {
					if(log.isInfoEnabled())
						log.infof("Constructor: retry %d !", i);
				}
				try {
					java.lang.Thread.sleep(TIME_LOCK_RETRY_WAIT);
				} catch(Exception e) {}
			}
			
			if(i<MAX_LOCK_RETRY) { 			
				String head_uuid = (String) acache.get("QUEUE_HEAD");
				if(head_uuid == null || head_uuid.equals("NULL")) {
					if(log.isInfoEnabled())
						log.info("Constructor: head = " + head_uuid);		
					Set<String> keyset = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCacheRepl);
					if(keyset != null) {
						for(String key: keyset) {
							if(key.startsWith("QUEUE_")) {
								String value = (String)acache.get(key);
								acache.remove(key);
								log.warnf("Constructor: Cleanup inconsistent queue data in cache (%s.%s)", key, value);
							}
						}
					}
					putQueue("QUEUE_HEAD", "NULL");
					if(log.isInfoEnabled())
						log.info("Constructor: put QUEUE_HEAD = NULL");
					putQueue("QUEUE_TAIL", "NULL");
					if(log.isInfoEnabled())
						log.info("Constructor: put QUEUE_TAIL = NULL");				
				} else {
					log.warn("Constructor: There is dqueue data in cache already");
				}
				tm.commit();
				if(log.isInfoEnabled())
					log.info("Constructor: tm.commit() done");
			} else {
				log.warn("Constructor: >MAX_LOCK_RETRY and rollback");
				throw (new TimeoutException(">MAX_LOCK_RETRY"));
			}
		} catch (Exception e) {
			log.error("Constructor: e="+e, e);
			if (tm != null) {
		        try {
		            tm.rollback();
		            if(log.isInfoEnabled())
		            	log.info("Constructor: tm.rollback() ex");
		        } catch (Exception e1) {
		        	log.error("Constructor: rollback e="+e1, e1);
		        }
		    }
			throw e;
		}
		
		try {
			java.lang.Thread.sleep(TIME_OP_GAP);
		} catch(Exception e) {}
	}
	
	public boolean isEmpty() throws Exception {
		boolean flag = true;
		TransactionManager tm = null;
		
		try {
			tm = Cluster.getTransactionManager(Cluster.CACHENAME.LittleCloudCacheRepl);
			tm.begin();
			if(log.isInfoEnabled())
				log.info("isEmpty: tm.begin()");
			int i = 0;
			for(i = 0; i < MAX_LOCK_RETRY; i++) { 
				boolean rtn = acache.withFlags(Flag.FAIL_SILENTLY).lock("QUEUE_LOCK");
				if(rtn) {
					break;
				} else {
					if(log.isInfoEnabled())
						log.infof("isEmpty: retry %d !", i);
				}
				try {
					java.lang.Thread.sleep(TIME_LOCK_RETRY_WAIT);
				} catch(Exception e) {}
			}
			if(i<MAX_LOCK_RETRY) { 			
				String head_uuid = (String) acache.get("QUEUE_HEAD");
				if(head_uuid != null && !head_uuid.equals("NULL")) 
					flag = false;
				tm.commit();
				if(log.isInfoEnabled())
					log.info("isEmpty: tm.commit() done");
			} else {
				log.warn("isEmpty: >MAX_LOCK_RETRY and rollback");
				throw (new TimeoutException(">MAX_LOCK_RETRY"));
			}
		} catch (Exception e) {
			log.error("isEmpty: e="+e, e);
			if(tm!=null) {
				try {
					tm.rollback();
					if(log.isInfoEnabled())
						log.info("isEmpty: tm.rollback() ex");
				} catch(Exception tme) {
					log.error("isEmpty: ex="+tme);
				}			
			}
			throw e;
		}
		if(log.isInfoEnabled())
			log.info("isEmpty: flag = " + flag);				

		try {
			java.lang.Thread.sleep(TIME_OP_GAP);
		} catch(Exception e) {}

		return flag;
	}
	
	public Object Pop() throws Exception {
		Object obj = null;
		TransactionManager tm = null;
		
		try {
			tm = Cluster.getTransactionManager(Cluster.CACHENAME.LittleCloudCacheRepl);
			tm.begin();
			if(log.isInfoEnabled())
				log.info("Pop: tm.begin()");
			int i = 0;
			for(i = 0; i < MAX_LOCK_RETRY; i++) { 
				boolean rtn = acache.withFlags(Flag.FAIL_SILENTLY).lock("QUEUE_LOCK");
				if(rtn) {
					break;
				} else {
					if(log.isInfoEnabled())
						log.infof("Pop: retry %d !", i);
				}
				try {
					java.lang.Thread.sleep(TIME_LOCK_RETRY_WAIT);
				} catch(Exception e) {}
			}
			if(i<MAX_LOCK_RETRY) { 
				String head_uuid = (String) acache.get("QUEUE_HEAD");
				if(head_uuid != null && !head_uuid.equals("NULL")) {
					DistributedQueueElementObject element = (DistributedQueueElementObject) acache.get(head_uuid);
					if(log.isInfoEnabled())
						log.info("Pop: got HEAD = " + head_uuid);					
  					obj = element.getObject();
  					acache.remove(head_uuid);
					if(log.isInfoEnabled())
						log.info("Pop: removed HEAD = " + head_uuid);
					putQueue("QUEUE_HEAD", element.getNext());
					if(log.isInfoEnabled())
						log.info("Pop: put QUEUE_HEAD = " + element.getNext());				
					if(element.getNext() == null) {
						log.error("Pop: rollback, error in next = " + element.getNext());
						tm.rollback();
						if(log.isInfoEnabled())
							log.info("Pop: tm.rollback() null");
					} else {
						if(element.getNext().equals("NULL")) {
							putQueue("QUEUE_TAIL", "NULL");
							if(log.isInfoEnabled())
								log.info("Pop: put QUEUE_TAIL = NULL");
						}
						tm.commit();
						if(log.isInfoEnabled())
							log.info("Pop: tm.commit() done");
					}
				} else {
					tm.commit();
					if(log.isInfoEnabled())
						log.info("Pop: tm.commit() empty");
				}
			} else {
				log.warn("Pop: >MAX_LOCK_RETRY and rollback");
				throw (new TimeoutException(">MAX_LOCK_RETRY"));
			}
		} catch (Exception e) {
			log.error("Pop: e="+e, e);
			obj = null;
			if (tm != null) {
		        try {
		            tm.rollback();
		            if(log.isInfoEnabled())
		            	log.info("Pop: tm.rollback() ex");
		        } catch (Exception e1) {
		        	log.error("Pop: rollback e="+e1, e1);
		        }
		    }
			throw e;
		}
		
		try {
			java.lang.Thread.sleep(TIME_OP_GAP);
		} catch(Exception e) {}

		return obj;
	}

	public boolean Push(Object obj) throws Exception {
		boolean ret = false;
		
		if(obj == null)
			return ret;
		
		TransactionManager tm = null;
		try {
			tm = Cluster.getTransactionManager(Cluster.CACHENAME.LittleCloudCacheRepl);
			tm.begin();
			if(log.isInfoEnabled())
				log.info("Push: tm.begin()");
			int i = 0;
			for(i = 0; i < MAX_LOCK_RETRY; i++) { 
				boolean rtn = acache.withFlags(Flag.FAIL_SILENTLY).lock("QUEUE_LOCK");
				if(rtn) {
					break;
				} else {
					if(log.isInfoEnabled())
						log.infof("Push: retry %d !", i);
				}
				try {
					java.lang.Thread.sleep(TIME_LOCK_RETRY_WAIT);
				} catch(Exception e) {}
			}
			if(i<MAX_LOCK_RETRY) {
				DistributedQueueElementObject new_element = new DistributedQueueElementObject(obj, "NULL");
				String uuid = "QUEUE_" + Cluster.getUniqueLocalId(); 
				putQueue(uuid, new_element);
				if(log.isInfoEnabled())
					log.info("Push: appended new " + uuid + " = " + new_element);				
				
				String head_uuid = (String) acache.get("QUEUE_HEAD");
				if(head_uuid == null || head_uuid.equals("NULL")) {
					putQueue("QUEUE_HEAD", uuid);
					if(log.isInfoEnabled())
						log.info("Push: put QUEUE_HEAD = " + uuid);
				}
				String tail_uuid = (String) acache.get("QUEUE_TAIL");
				if(tail_uuid==null) {
					log.error("Push: tail == null");
					throw (new Exception("Push: tail == null"));
				}
				if(tail_uuid != null && !tail_uuid.equals("NULL")) {
					log.info("Push: tail_uuid="+tail_uuid);
					DistributedQueueElementObject tail_element = (DistributedQueueElementObject) acache.get(tail_uuid);
					tail_element.setNext(uuid);
					putQueue(tail_uuid, tail_element);
					if(log.isInfoEnabled())
						log.info("Push: put " + tail_uuid + " = " + tail_element);	
				}	
				putQueue("QUEUE_TAIL", uuid);
				if(log.isInfoEnabled())
					log.info("Push: put QUEUE_TAIL = " + uuid);
				ret = true;
				tm.commit();
				if(log.isInfoEnabled())
					log.info("Push: tm.commit() done");
			} else {
				log.warn("Push: >MAX_LOCK_RETRY and rollback");
				throw (new TimeoutException("Push: >MAX_LOCK_RETRY and rollback"));
			}
		} catch (Exception e) {
			log.error("Push: e="+e, e);
			if (tm != null) {
		        try {
		            tm.rollback();
		            if(log.isInfoEnabled())
		            	log.info("Push: tm.rollback() ex");
		            ret = false;
		        } catch (Exception e1) {
		        	log.error("Push: rollback e="+e1, e1);
		        }
		    }
			throw e;
		}
		try {
			java.lang.Thread.sleep(TIME_OP_GAP);
		} catch(Exception e) {}
		
		return ret;
	}

	private void putQueue(String key, Object elem) throws Exception
	{
		acache.put(key, elem, DEFAULT_CACHE_EXPIRE_HOUR, TimeUnit.HOURS);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(1024);
		sb.append("DistributedQueueObject<br>=====================<br>");
		TransactionManager tm = null;
		try {
			tm = Cluster.getTransactionManager(Cluster.CACHENAME.LittleCloudCacheRepl);
			tm.begin();
			if(log.isInfoEnabled())
				log.info("toString: tm.begin()");
			int i = 0;
			for(i = 0; i < MAX_LOCK_RETRY; i++) { 
				boolean rtn = acache.withFlags(Flag.FAIL_SILENTLY).lock("QUEUE_LOCK");
				if(rtn) {
					break;
				} else {
					if(log.isInfoEnabled())
						log.infof("toString: retry %d !", i);
				}
				try {
					java.lang.Thread.sleep(TIME_LOCK_RETRY_WAIT);
				} catch(Exception e) {}
			}
			if(i<MAX_LOCK_RETRY) {
				String head_uuid = (String) acache.get("QUEUE_HEAD");
				sb.append("QUEUE_HEAD => "+head_uuid+"<br>");
				String tail_uuid = (String) acache.get("QUEUE_TAIL");
				sb.append("QUEUE_TAIL => "+tail_uuid+"<br>");

				String current_uuid = head_uuid;
				while(current_uuid !=null && !current_uuid.equals("NULL")) {
					DistributedQueueElementObject element = (DistributedQueueElementObject) acache.get(current_uuid);
					sb.append(current_uuid + "(" + element.getObject() + ").next => " + element.getNext()+"<br>");
					current_uuid = element.getNext();
				}
				tm.commit();
				if(log.isInfoEnabled())
					log.info("toString: tm.commit() done");
			} else {
				log.warn("toString: >MAX_LOCK_RETRY and rollback");
				throw (new TimeoutException(">MAX_LOCK_RETRY"));
			}
		} catch (Exception e) {
			log.error("toString: e="+e, e);
			if(tm!=null) {
				try {
					tm.rollback();
					if(log.isInfoEnabled())
						log.info("toString: tm.rollback() ex");
				} catch(Exception tme) {
					log.error("toString: ex="+tme);
				}
			}
//			throw e;
		}
		try {
			java.lang.Thread.sleep(TIME_OP_GAP);
		} catch(Exception e) {}

		return sb.toString();
	}
}
