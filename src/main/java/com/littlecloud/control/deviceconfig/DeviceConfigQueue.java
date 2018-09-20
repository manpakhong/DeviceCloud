package com.littlecloud.control.deviceconfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.json.util.JsonUtils;

public class DeviceConfigQueue implements Runnable {
	public static Logger log = Logger.getLogger(DeviceConfigQueue.class);

	private static BlockingQueue<DeviceConfigObj> queue;
	// /* move to grid */private static Set<Integer> scheduleIdSet = new
	// HashSet<Integer>();

	public DeviceConfigQueue(int maxQueueSize) {
		DeviceConfigQueue.queue = new ArrayBlockingQueue<DeviceConfigObj>(maxQueueSize);
	}

	
	@Override
	public void run() {
		try{
			
			DeviceConfigObj devConfigObj = DeviceConfigQueue.queue.take();
			if (log.isDebugEnabled()){
				log.debugf("DEVCONF20140424 - DeviceConfigQueue.take(): iana: %s, sn:%s, counter: %s",devConfigObj.getIana_id(), devConfigObj.getSn(),  DeviceConfigQueue.queue.size());
			}
			if (devConfigObj != null && devConfigObj.getIana_id() != null
					&& devConfigObj.getSn() != null) {
				if (log.isDebugEnabled()){
					log.debugf("DEVCONF20140424 - DeviceConfigQueue.take() - fetchCommand - PIPE_INFO_TYPE_CONFIG_GET: iana: %s, sn:%s, counter: %s",devConfigObj.getIana_id(), devConfigObj.getSn(),  DeviceConfigQueue.queue.size());
				}
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_CONFIG_GET,
						JsonUtils.genServerRef(), devConfigObj.getIana_id(),
						devConfigObj.getSn());
			}
		} catch (Exception e){
			log.errorf(e, "DEVCONF20140424 - DeviceConfigQueue.run)");
		}
	}
	
	public static int getQueueSize(){
		if (DeviceConfigQueue.queue != null){
			return DeviceConfigQueue.queue.size();
		} else {
			return 0;
		}
	}

	public int put(DeviceConfigObj deviceConfigObj){
		try{
			DeviceConfigQueue.queue.put(deviceConfigObj);
			if (log.isDebugEnabled()){
				log.debugf("DEVCONF20140424 - DeviceConfigQueue.put(): iana: %s, sn:%s, counter: %s",deviceConfigObj.getIana_id(), deviceConfigObj.getSn(),  queue.size());
			}
		}catch (Exception e){
			log.errorf(e, "DEVCONF20140424 - DeviceConfigQueue.put()");
		}
		return DeviceConfigQueue.queue.size();
	}

} // end class
