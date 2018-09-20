package com.littlecloud.control.devicechange;

import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.queue.BlockingQueueExecutorTask;
import com.littlecloud.control.dao.branch.DevicePendingChangesDAO;
import com.littlecloud.control.entity.branch.DevicePendingChanges;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.DeviceUtils;

public class DeviceChangeTask extends BlockingQueueExecutorTask<DevicePendingChanges> {

	private static final Logger log = Logger.getLogger(DeviceChangeTask.class);

	public DeviceChangeTask(DevicePendingChanges item) {
		super(item);
	}

	@Override
	public void run() {
		if (log.isInfoEnabled())
			log.infof("running item %s", item);
		try {
			DevicePendingChangesDAO devChangeDAO = new DevicePendingChangesDAO();
			
			JSONObject licenseObj = JSONObject.fromObject(item.getContent());
			
			/* only one type at the moment */
			DevOnlineObject devO = DeviceUtils.getDevOnlineObject(item.getIana_id(), item.getSn());
			if (devO!=null && devO.isOnline())
			{
				if (log.isDebugEnabled())
					log.debugf("Fetching item %d %s ", item.getIana_id(), item.getSn());
				ACService.<JSONObject>fetchCommand(MessageType.PIPE_INFO_TYPE_EVENT_ACT_FH_LIC, JsonUtils.genServerRef(), item.getIana_id(), item.getSn(), licenseObj);
			}
			else
			{
				if (log.isDebugEnabled())
					log.debugf("Fetching item %d %s is offline! ", item.getIana_id(), item.getSn());
			}
			
			item.setLast_attemp_time(DateUtils.getUtcDate());
			
			devChangeDAO.update(item);
		} catch (Exception e) {
			log.error("item "+item+" is interrupted", e);
		}
		
		if (log.isInfoEnabled())
			log.infof("done item %s", item);
	}
}
