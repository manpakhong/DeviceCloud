package com.littlecloud.pool.object;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.entity.Devices;

public class PoolObjectDAO {

	private static Logger log = Logger.getLogger(PoolObjectDAO.class);	
	
	public static DevDetailJsonObject getDevDetailJsonObject(Devices dev)
	{
		/* seek cache, else seek device */
		DevDetailJsonObject devDetailJsonObjectExample = new DevDetailJsonObject();
		devDetailJsonObjectExample.setIana_id(dev.getIanaId());
		devDetailJsonObjectExample.setSn(dev.getSn());

		try {
			return ACUtil.<DevDetailJsonObject> getPoolObjectBySn(devDetailJsonObjectExample, DevDetailJsonObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static DevDetailObject getDevDetailObject(Devices dev)
	{
		/* seek cache, else seek device */
		DevDetailObject devDetailObjectExample = new DevDetailObject();
		devDetailObjectExample.setIana_id(dev.getIanaId());
		devDetailObjectExample.setSn(dev.getSn());

		try {
			return ACUtil.<DevDetailObject> getPoolObjectBySn(devDetailObjectExample, DevDetailObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static DevOnlineObject getDevOnlineObject(Integer iana_id, String sn)
	{
		if(log.isDebugEnabled())
			log.debugf("getDevOnlineObject iana_id=%d, sn=%s", iana_id, sn);
		
		/* seek cache, else seek device */
		DevOnlineObject devOnlineObjectExample = new DevOnlineObject();
		devOnlineObjectExample.setIana_id(iana_id);
		devOnlineObjectExample.setSn(sn);

		try {
			return ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineObjectExample, DevOnlineObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("getPoolObjectBySn exception "+e, e);
			return null;
		}
	} 
	
	public static DevOnlineObject getDevOnlineObject(Devices dev)
	{
		if(log.isDebugEnabled())
			log.debugf("getDevOnlineObject dev=%s", dev);
		
		/* seek cache, else seek device */
		DevOnlineObject devOnlineObjectExample = new DevOnlineObject();
		devOnlineObjectExample.setIana_id(dev.getIanaId());
		devOnlineObjectExample.setSn(dev.getSn());

		try {
			return ACUtil.<DevOnlineObject> getPoolObjectBySn(devOnlineObjectExample, DevOnlineObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static StationListObject getStationListObject(Devices dev)
	{
		/* seek cache, else seek device */
		StationListObject stationListObjectExample = new StationListObject();
		stationListObjectExample.setIana_id(dev.getIanaId());
		stationListObjectExample.setSn(dev.getSn());

		try {
			return ACUtil.<StationListObject> getPoolObjectBySn(stationListObjectExample, StationListObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("getPoolObjectBySn exception "+e, e);
			return null;
		}
	}
	
	public static String convertToClientId(String mac, String ip){
		String ret = "";
		if (mac != null && !mac.isEmpty()){
			mac = mac.toUpperCase();
			mac = mac.replaceAll(":", "");
			mac = mac.replaceAll("-", "");
			ret = "01"+mac;
			return ret;
		}
		
		if (ip != null && !ip.isEmpty()){
			String[] addrArray = ip.split("\\."); 
			long num = 0; 
			for (int i = 0; i < addrArray.length; i++) { 
				int power = 3 - i; 
				num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power))); 
			}
			String hexIp = Long.toHexString(num);
			ret = "00"+hexIp;
			return ret;
		}
		return ret;
	}
}
