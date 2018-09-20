package com.littlecloud.ac.health;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.info.JVMInfo;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.utils.PropertyService;

public class JVMHealthMonitor implements HealthMonitor<JVMInfo> {

	private static final Logger log = Logger.getLogger(JVMHealthMonitor.class);

	private static final PropertyService<JVMHealthMonitor> ps = new PropertyService<JVMHealthMonitor>(JVMHealthMonitor.class);
	private static final Integer health_jvm_info_update_time = ps.getInteger("health_jvm_info_update_time") * 60;
	private static final Integer monitor_jvm_interval_min = ps.getInteger("monitor_jvm_interval_min");

	private static JVMInfo info = new JVMInfo();

	public static Integer getMonitorJvmIntervalMin() {
		return monitor_jvm_interval_min;
	}

	@Override
	public JVMInfo getInfo() {
		return info;
	}

	@Override
	public void collectInfo() {
		info.setAvailableProcessors(Runtime.getRuntime().availableProcessors());
		info.setFreeMem(Runtime.getRuntime().freeMemory() / (1024 * 1024));
		info.setMaxMem(Runtime.getRuntime().maxMemory() / (1024 * 1024));
		info.setUsedMem(Runtime.getRuntime().totalMemory() / (1024 * 1024));
		info.setTimestamp(DateUtils.getUnixtime());
	}

	@Override
	public boolean isHealthy() {
		boolean result = false;
		if( DateUtils.getUnixtime() - info.getTimestamp() > health_jvm_info_update_time )
		{
			log.warnf("Unhealthy! DateUtils.getUnixtime() %d - info.getTimestamp() %d > health_jvm_info_update_time %d",
					DateUtils.getUnixtime(), info.getTimestamp(), health_jvm_info_update_time);
			result = false;
		}
		else
		{
			result = true;
		}
		return result;
	}
	
	@Override
	public String toJson() {
		return JsonUtils.toJson(getInfo());
	}
	
	public String toHtml(JVMInfo jvmInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------JVM INFO-----------------");
		sb.append("<br/>");
		log.debug("jvmHealthMonitor.isHealthy");
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
			sb.append("Unhealthy");
			sb.append("</font>");
		}
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("available processor : ");
		sb.append(jvmInfo.getAvailableProcessors());
		sb.append("<br/>");
		sb.append("MaxMem : ");
		sb.append(jvmInfo.getMaxMem());
		sb.append(" MB");
		sb.append("<br/>");
		sb.append("FreeMem : ");
		sb.append(jvmInfo.getFreeMem());
		sb.append(" MB");
		sb.append("<br/>");
		sb.append("UsedMem : ");
		sb.append(jvmInfo.getUsedMem());
		sb.append(" MB");
		sb.append("<br/>");
		sb.append("timestamp : ");
		sb.append(jvmInfo.getTimestamp());
		sb.append("<br/>");
		sb.append("<br/>");
		return sb.toString();
	}
}
