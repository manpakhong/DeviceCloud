package com.littlecloud.ac.health;

import java.io.IOException;

import org.jboss.logging.Logger;

import com.littlecloud.ac.health.info.ACInfo;
import com.littlecloud.ac.health.util.Cli;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.utils.PropertyService;

public class ACHealthMonitor implements HealthMonitor<ACInfo> {

	private static final Logger log = Logger.getLogger(ACHealthMonitor.class);
	private static final String CMD_HEALTHCHECK = "capwap_cli queue_status";
	private static final String CMD_GETSTAT = "capwap_cli getstat";
	
	private static final PropertyService<ACHealthMonitor> ps = new PropertyService<ACHealthMonitor>(ACHealthMonitor.class);
	private static final Integer HEALTH_AC_QUEUE_SIZE = ps.getInteger("health_ac_queue_size");
	private static final Integer HEALTH_AC_UPTIME_SEC = ps.getInteger("health_ac_uptime_min") * 60;	
	private static final Integer MONITOR_AC_INTERVAL_MIN = ps.getInteger("monitor_ac_interval_min");
	private static final Integer HEALTH_AC_INFO_UPDATE_TIME = ps.getInteger("health_ac_info_update_time") * 60;
	
	private static ACInfo info = new ACInfo();
	
	public static Integer getMonitorAcIntervalMin() {
		return MONITOR_AC_INTERVAL_MIN;
	}

	@Override
	public ACInfo getInfo() {
		return info;
	}

	@Override
	public void collectInfo() {
		try {
			ACInfo parseInfo = JsonUtils.<ACInfo>fromJson(Cli.getOutput(CMD_HEALTHCHECK), ACInfo.class);
			if (parseInfo!=null)
			{
				String capwap_stat = Cli.getOutput(CMD_GETSTAT);
				if( capwap_stat != null )
				{
					String[] statInfos = capwap_stat.split("Active WTP:");
					if( statInfos != null && statInfos.length > 1)
					{
						String[] infos = statInfos[1].split(",");
						if( infos != null && infos.length > 0)
						{
							String active_wtp = infos[0];
							if( active_wtp != null )
							{
								parseInfo.setActive_wtp(Integer.parseInt(active_wtp.trim()));
							}
						}
					}
				}
				parseInfo.setTimestamp(DateUtils.getUnixtime());
				info = parseInfo;
			}
		} catch (IOException e) {
			log.error("IOException",e);
		} catch (Exception e) {
			log.error("Other exception",e);
		}
	}

	@Override
	public boolean isHealthy() {
		boolean isHealthy;		
		try {
			
			if (DateUtils.getUnixtime() - info.getTimestamp() > HEALTH_AC_INFO_UPDATE_TIME || info.getQueue_size() >=HEALTH_AC_QUEUE_SIZE || info.getUptime_sec() < HEALTH_AC_UPTIME_SEC)
			{
				log.warnf("Unhealthy! DateUtils.getUnixtime() %d - info.getTimestamp() %d > health_db_info_update_time %d "
						+ "|| info.getQueue_size() %d < HEALTH_AC_QUEUE_SIZE %d && info.getUptime_sec() %d == HEALTH_AC_UPTIME_SEC %d" , 
						DateUtils.getUnixtime(), info.getTimestamp(), HEALTH_AC_INFO_UPDATE_TIME, 
						info.getQueue_size(), HEALTH_AC_QUEUE_SIZE, info.getUptime_sec(), HEALTH_AC_UPTIME_SEC);
				
				isHealthy = false;
			}
			else
			{
				isHealthy = true;
			}
		} catch (Exception e) {
			log.errorf("Unhealthy! - Exception ", e);			
			isHealthy = false;
		}		
		return isHealthy;
	}

	public String getUnhealthReason()
	{
		String reason = "";
		
		if(DateUtils.getUnixtime() - info.getTimestamp() > HEALTH_AC_INFO_UPDATE_TIME)
			reason += "- Unhealthy ac info update time<br/>";
		
		if(info.getQueue_size() >= HEALTH_AC_QUEUE_SIZE)
			reason += "- Unhealth ac queue size<br/>";
		
		if(info.getUptime_sec() < HEALTH_AC_UPTIME_SEC)
			reason += "- Unhealth ac uptime sec<br/>";
		
		return reason;
	}
	
	public boolean isHealthy(ACInfo acInfo) {
		boolean isHealthy;		
		try {
			
			if (DateUtils.getUnixtime() - acInfo.getTimestamp() > HEALTH_AC_INFO_UPDATE_TIME || acInfo.getQueue_size() >=HEALTH_AC_QUEUE_SIZE || acInfo.getUptime_sec() < HEALTH_AC_UPTIME_SEC)
				isHealthy = false;
			else
			{
				isHealthy = true;
			}
		} catch (Exception e) {
			log.errorf("Exception isHealthy ", e);			
			isHealthy = false;
		}		
		return isHealthy;
	}
	
	@Override
	public String toJson() {
		return JsonUtils.toJson(getInfo());
	}
	
	public String toJson(ACInfo info) {
		return JsonUtils.toJson(info);
	}
	
	public String toHtml(ACInfo acInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------AC INFO-----------------");
		sb.append("<br/>");
		log.debug("acHealthMonitor.isHealthy");
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
			String reason = this.getUnhealthReason();
			if(reason != null)
			{
				sb.append("<br/>");
				sb.append("<br/>");
				sb.append("Unhealthy reason : ");
				sb.append(reason);
			}
		}
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("QueueSize : ");
		sb.append(acInfo.getQueue_size());
		sb.append("<br/>");
		sb.append("Uptime : ");
		sb.append(acInfo.getUptime_sec());
		sb.append("<br/>");
		sb.append("Active WTP : ");
		sb.append(acInfo.getActive_wtp());
		sb.append("<br/>");
		sb.append("Timestamp : ");
		sb.append(acInfo.getTimestamp());
		sb.append("<br/>");
		sb.append("<br/>");
		return sb.toString();
	}
}