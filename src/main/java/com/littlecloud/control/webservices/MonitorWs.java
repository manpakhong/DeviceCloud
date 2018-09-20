package com.littlecloud.control.webservices;

import java.util.Iterator;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.control.json.request.JsonClientRequest;
import com.littlecloud.pool.Cluster;
import com.littlecloud.pool.Cluster.CacheException;

@WebService()
public class MonitorWs extends BaseWs {
	private static final Logger log = Logger.getLogger(MonitorWs.class);
	
	@WebMethod()
	public String getRequest() {
		Gson gson = new Gson();

		JsonClientRequest request = new JsonClientRequest();
		request.setCaller_ref(genCallerRef());
		request.setVersion("0.1");
		request.setOrganization_id("oVPZkS");
		request.setMac("11:22:33:44:55:66");

		String json = gson.toJson(request);
		return json;
	}

	@WebMethod()
	public String getAll(String json) {

		StringBuilder sb = new StringBuilder();

		sb.append("***************** result=\n");
		
		try {
			Iterator<String> itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCache).iterator();
			while (itr.hasNext())
			{
				String key = (String) itr.next();
				try
				{
					Object value = Cluster.get(Cluster.CACHENAME.LittleCloudCache, key);
					sb.append(key);
					sb.append("|");
					sb.append(value);
					sb.append("\n");
				} catch (NullPointerException e)
				{
					log.info("getAll() - key entry " + key + " does not exist.");
				}
			}
		} catch (CacheException e) {
			log.error("getAll() - CacheExceptioin " + e);
		}
		
		return sb.toString();
	}
}
