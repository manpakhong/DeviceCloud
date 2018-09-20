package com.littlecloud.ac.health;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.info.DiskInfo;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.utils.PropertyService;

public class DiskHealthMonitor implements HealthMonitor<CopyOnWriteArrayList<DiskInfo>> {

	private static final Logger log = Logger.getLogger(DiskHealthMonitor.class);

	private static final PropertyService<DiskHealthMonitor> ps = new PropertyService<DiskHealthMonitor>(DiskHealthMonitor.class);
	private static final Integer health_disk_info_update_time=ps.getInteger("health_disk_info_update_time")* 60;
	private static final Integer monitor_disk_interval_min = ps.getInteger("monitor_disk_interval_min");

	public static Integer getMonitorDiskIntervalMin() {
		return monitor_disk_interval_min;
	}

	private static CopyOnWriteArrayList<DiskInfo> infoLst = null;

	@Override
	public CopyOnWriteArrayList<DiskInfo> getInfo() {
		return infoLst;
	}

	@Override
	public void collectInfo() {
		List<DiskInfo> result = new ArrayList<DiskInfo>();		
		File[] roots = File.listRoots();
		for (File root : roots)
		{
			DiskInfo di = new DiskInfo();
			di.setPath(root.getAbsolutePath());
			di.setTotalSpace(root.getTotalSpace() / (1024 * 1024));
			di.setFreeSpace(root.getFreeSpace() / (1024 * 1024));
			di.setUsableSpace(root.getUsableSpace() / (1024 * 1024));
			di.setTimestamp(DateUtils.getUnixtime());
			result.add(di);
		}		
		infoLst = new CopyOnWriteArrayList<DiskInfo>(result);
	}

	@Override
	public boolean isHealthy() {
		// TODO not yet defined
		boolean result = false;
		int i = 0;
		for( DiskInfo info : infoLst )
		{
			if( DateUtils.getUnixtime() - info.getTimestamp() > health_disk_info_update_time || info.getFreeSpace() == 0 )
			{
				log.warnf("Unhealthy! DateUtils.getUnixtime() %d - info.getTimestamp() %d > health_disk_info_update_time %d || info.getFreeSpace() %d == 0",
						DateUtils.getUnixtime(), info.getTimestamp(),  health_disk_info_update_time, info.getFreeSpace());
				result = false;
				break;
			}
			i++;
		}
		if( i == infoLst.size() )
			result = true;
		
		if (!result)
			log.warnf("Unhealthy!");
		
		return result;
	}
	
	@Override
	public String toJson() {
		return JsonUtils.toJson(getInfo());
	}
	
	public String toHtml(List<DiskInfo> diskInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------DISK INFO-----------------");
		sb.append("<br/>");
		log.debug("diskHealthMonitor.isHealthy");
		if(this.isHealthy())
		{
			sb.append("Status : ");
			sb.append("<font color=\"green\">");
			sb.append("Healthy");
			sb.append("</font>");
		}
		else
		{
			sb.append("Status : ");
			sb.append("<font color=\"red\">");
			sb.append("Unhealthy\n");
			sb.append("</font>");
		}
		sb.append("<br/>");
		sb.append("<br/>");
		if( diskInfo != null )
		{
			for( DiskInfo info : diskInfo )
			{
				sb.append("disk path : ");
				sb.append(info.getPath());
				sb.append("<br/>");
				sb.append("total space : ");
				sb.append(info.getTotalSpace());
				sb.append(" MB");
				sb.append("<br/>");
				sb.append("usable space : ");
				sb.append(info.getUsableSpace());
				sb.append(" MB");
				sb.append("<br/>");
				sb.append("free space : ");
				sb.append(info.getFreeSpace());
				sb.append(" MB");
				sb.append("<br/>");
				sb.append("Timestamp : ");
				sb.append(info.getTimestamp());
				sb.append("<br/>");
				sb.append("<br/>");
			}
		}
		return sb.toString();
	}
}
