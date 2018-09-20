package com.littlecloud.control.json.model.config.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.json.model.Json_PepvpnStatus;
import com.littlecloud.control.json.model.pepvpn.Link;
import com.littlecloud.control.json.model.pepvpn.PeerDetail;
import com.littlecloud.control.json.model.pepvpn.PeerDetailResponse;
import com.littlecloud.control.json.model.pepvpn.PeerListResponse;
import com.littlecloud.control.json.model.pepvpn.PepvpnConnection;
import com.littlecloud.control.json.model.pepvpn.PepvpnFetchObject;
import com.littlecloud.control.json.model.pepvpn.VpnGroupInfo;
import com.littlecloud.control.json.model.pepvpn.PepvpnConnection.Stat;
import com.littlecloud.control.json.model.pepvpn.PepvpnLinkInfo;
import com.littlecloud.control.json.model.pepvpn.SerialStatus;
import com.littlecloud.control.json.model.pepvpn.TunnelDetail;
import com.littlecloud.control.json.model.pepvpn.TunnelStatResponse;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.PepvpnEndpointObject;
import com.littlecloud.pool.object.PepvpnPeerDetailObject;
import com.littlecloud.pool.object.PepvpnPeerListObject;
import com.littlecloud.pool.object.PepvpnTunnelStatObject;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils.ATTRIBUTE;

public class PepvpnStatusUtils {
	
	
	private static final Logger log = Logger.getLogger(PepvpnStatusUtils.class);
	private static final  int MAX_FETCH_TIME = 5;
	
	public static String convertPepvpnEndpointV2(PepvpnEndpointObject endpointobj)
	{
		/*
		 * 1. input: query data string
		 * 2. convert corresponding pepvpn status version to Json_PepvpnStatus
		 *  
		 */
	
		log.debugf("case PIPE_INFO_TYPE_PEPVPN_ENDPOINT: pepvpnEndpointObject convert to new pepvpn begin, endpointobj= %s", endpointobj);
		String status = "FAIL";
		
		if (endpointobj == null)
			return status;
		
		PepvpnPeerDetailObject peerdetailobj = new PepvpnPeerDetailObject();
		peerdetailobj.setSid(endpointobj.getSid());
		peerdetailobj.setSn(endpointobj.getSn());
		peerdetailobj.setIana_id(endpointobj.getIana_id());
		peerdetailobj.setStatus(endpointobj.getStatus());
		peerdetailobj.setTimestamp(endpointobj.getTimestamp());
		peerdetailobj.setFetchStartTime(endpointobj.getFetchStartTime());
		
		PepvpnTunnelStatObject tunnelstatobj = new PepvpnTunnelStatObject();
		tunnelstatobj.setSid(endpointobj.getSid());
		tunnelstatobj.setSn(endpointobj.getSn());
		tunnelstatobj.setIana_id(endpointobj.getIana_id());
		tunnelstatobj.setStatus(endpointobj.getStatus());
		tunnelstatobj.setTimestamp(endpointobj.getTimestamp());
		tunnelstatobj.setFetchStartTime(endpointobj.getFetchStartTime());
		
		ArrayList<PeerDetail> peerdetail_lst = new ArrayList<PeerDetail>();
		Map<String, Map<String, TunnelDetail>> tunnel_map = new HashMap<String,Map<String, TunnelDetail>>();
		ArrayList<String> tunnel_order = new ArrayList<String>();
		CopyOnWriteArrayList<PepvpnConnection> pepconnlst = new CopyOnWriteArrayList<PepvpnConnection>();
		ArrayList<VpnGroupInfo> vpn_grp = new ArrayList<VpnGroupInfo>();	
		ArrayList<Integer> endpoints = new ArrayList<Integer>();
		List<String> ip_host_list = new ArrayList<String>();
		VpnGroupInfo vpnInfo = new VpnGroupInfo();
		PepvpnLinkInfo linkinfolist = new PepvpnLinkInfo();
		Map<Integer, String> name_map = new HashMap<Integer, String>();
		Map<Integer, String> ip_map = new HashMap<Integer, String>();
		
		linkinfolist = endpointobj.getLinkinfo();
		if (linkinfolist != null && linkinfolist.getLink_list()!=null)
		{	
			for (Link link : linkinfolist.getLink_list())
			{
				name_map.put((int) link.getId(), link.getName());
				ip_map.put((int) link.getId(), link.getStatic_ip());
			}
		}
					
		pepconnlst = endpointobj.getMvpn_conn_list();
		if (pepconnlst != null && pepconnlst.size()!=0)
		{
			for (PepvpnConnection pepconn : pepconnlst)
			{
				PeerDetail peerdetail = new PeerDetail();
				peerdetail.setSerial(endpointobj.getSn());
//				peerdetail.setDevice_id(endpoint_dev.getId());				
				
				
				List<Stat> stat_list = new ArrayList<Stat>();
				stat_list = pepconn.getStat();
				int stat_size = 0;
				if (stat_list != null) 
					stat_size = stat_list.size();
				
//					peerdetail.setId(pepconn.getId());
					String peerid = pepconn.getId() + "_" + stat_size;
					log.debug("peerid "+peerid);//tunnel_order.add(peerid);/* add all peerid, including 0 */
					peerdetail.setPeer_id(peerid);
					peerdetail.setName(pepconn.getDevice_name());	
				if (pepconn.getName() != null);
					peerdetail.setUsername(pepconn.getName());//TODO
//				if (pepconn.getDevice_network_id() != null)
//					{peerdetail.setDevice_network_id(pepconn.getDevice_network_id());
//					vpnInfo.setHub_net_id(pepconn.getDevice_network_id());}
//				if (pepconn.getSn() != null)
//					peerdetail.setRemote_serial(pepconn.getSn());
//				if (pepconn.getDevice_id() != null)
//					{
//					peerdetail.setRemote_device_id(pepconn.getDevice_id());
//					endpoints.add(pepconn.getDevice_id());}
				if (pepconn.getMain_state() != null) 
				{
					peerdetail.setStatus(pepconn.getMain_state().toUpperCase());
					if (pepconn.getMain_state().equalsIgnoreCase("Established"))
						peerdetail.setStatus("CONNECTED");					
				}
				if (pepconn.getNetwork_list() != null)
				{
					peerdetail.setRoute(pepconn.getNetwork_list());
					peerdetail.setType("l3");
					ip_host_list = pepconn.getNetwork_list();
				}
				if (pepconn.isEnable() != null)
					peerdetail.setSecure(pepconn.isEnable());
				//peerdetail.setPid();
				
				log.debug("peerid "+peerdetail);			
				peerdetail_lst.add(peerdetail);		
			
				
				if (stat_size!=0)
				{
					tunnel_order.add(peerid);//only add !0
					log.debug("peerid 3 "+stat_list);
					Map<String, TunnelDetail> inner_map = new HashMap<String, TunnelDetail>();
					for (Stat st : stat_list) 
					{
						log.debug("peerid stat "+st);
						TunnelDetail tunneldetail = new TunnelDetail();
						ArrayList<Integer> arr1 = new ArrayList<Integer>();																	
						arr1.add((int) st.getLost());
						tunneldetail.setAck_miss(arr1);
						tunneldetail.setRtt((int) st.getRtt());//long
						ArrayList<Integer> arr2 = new ArrayList<Integer>();
						arr2.add((int) st.getRx());
						tunneldetail.setRx(arr2);
						ArrayList<Integer> arr3 = new ArrayList<Integer>();
						arr3.add((int) st.getTx());
						tunneldetail.setTx(arr3);
						tunneldetail.setName(name_map.get((int)st.getId()));
//						tunneldetail.setTimestamp(st.getTimestamp());
//						tunneldetail.setDatetime(st.getDatetime());
						tunneldetail.setNanostime(0);
						tunneldetail.setState("HC_OK");
						tunneldetail.setStime((int) st.getTimestamp());
						//String key_id = String.valueOf(st.getId());
						String key_id = st.getId()+"";
						log.debug("key_id "+key_id);
						inner_map.put(key_id, tunneldetail);
					}
				tunnel_map.put(peerid, inner_map);				
				}
			}
		}
		
		vpnInfo.setEndpoints(endpoints);
		vpnInfo.setIp_host_list((ArrayList<String>) ip_host_list);
		vpn_grp.add(vpnInfo);
		peerdetailobj.setVpn_grp(vpn_grp);
		PeerDetailResponse peerdetailres = new PeerDetailResponse();
		peerdetailres.setPeer(peerdetail_lst);
		peerdetailobj.setResponse(peerdetailres);
		
		TunnelStatResponse tunnelstatres = new TunnelStatResponse();
		tunnelstatres.setTunnel(tunnel_map);
		tunnelstatobj.setResponse(tunnelstatres);
		tunnelstatobj.setTunnel_order(tunnel_order);
		
		try {
			ACUtil.cachePoolObjectBySn(peerdetailobj, PepvpnPeerDetailObject.class);
			ACUtil.cachePoolObjectBySn(tunnelstatobj, PepvpnTunnelStatObject.class);
			status = "SUCCESS";
		} catch (InstantiationException | IllegalAccessException e) {
			status = "ERROR";
			log.errorf("convert PIPE_INFO_TYPE_PEPVPN_ENDPOINT: pepvpnPeerDetailObject = %s", peerdetailobj);
			log.errorf("convert PIPE_INFO_TYPE_PEPVPN_ENDPOINT: pepvpnTunnelStatObject = %s", tunnelstatobj);
			e.printStackTrace();
		}
		
		return status;
	}
	
	 public static boolean fetchPepvpnCommand (PepvpnFetchObject fetchobj)
	    {//TODO
	    	
	    	String sid = fetchobj.getSid();
	    	String org_id = fetchobj.getOrg_id();
	    	Integer iana_id = fetchobj.getIana_id();
	    	String snSearch = fetchobj.getSn();
	    	MessageType type = fetchobj.getType();
	    	
	    	if (org_id == null || iana_id == null || snSearch == null || type == null) 
	    		return false;
	    	log.debugf("fetchPepvpnCommand-- org_id =%s || iana_id =%s || snSearch =%s || type =%s", org_id, iana_id, snSearch, type);
	    	
	    	Devices devOnlineObject = NetUtils.getDevicesBySn(org_id, iana_id, snSearch, true);
	    	if (devOnlineObject==null) 
	    		return false;

	    	if (ONLINE_STATUS.statusOf(devOnlineObject.getOnline_status())==ONLINE_STATUS.ONLINE)
			{					
				FeatureGetObject fgo = FeatureGetUtils.getFeatureGetObject(23695, snSearch);	
				String mvpnStatus = "0";
				
				if (fgo != null && fgo.getFeatures()!=null)
				{
					mvpnStatus = FeatureGetUtils.getFeatureAttributeAsStr(fgo, ATTRIBUTE.mvpn_status);					
				}
				log.debug("fetchPepvpnCommand-- mvpnStatus = "+mvpnStatus + "fgo = "+fgo);
						
				if (mvpnStatus != null && !mvpnStatus.isEmpty() && mvpnStatus.equals("1")) //Firmware version "status": 1  <----- NEW!!
					{
						if (type == MessageType.PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL)
						{
							if (ACService.fetchCommand(type, sid, 23695, snSearch, fetchobj.getQuerycommand()) == false)						
							{
									log.error("fetchPepvpnCommand-- Fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL, NewPepvpn snSearch " + snSearch);//JsonUtils.genServerRef()
									
									return false;
							}
							else
						    	return true;
						}
						else if (type == MessageType.PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT)
						{
							if (ACService.fetchCommand(type, sid, 23695, snSearch, fetchobj.getQuerycommand()) == false)						
							{
									log.error("fetchPepvpnCommand-- Fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT, NewPepvpn snSearch " + snSearch);//JsonUtils.genServerRef()
									
									return false;
							}
							else
								return true;							
						}
						else 
						{
							log.debugf("fetchPepvpnCommand-- Wrong type type = %s, sn = %s", snSearch, type);
							return false;
						}
					}
				else
					{
						if (type == MessageType.PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL || type == MessageType.PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT )
						{
							type = MessageType.PIPE_INFO_TYPE_PEPVPN_ENDPOINT;
							if (ACService.fetchCommand(type, sid, 23695, snSearch) == false)
							{
								log.error("fetchPepvpnCommand-- fail to fetchCommand: PIPE_INFO_TYPE_PEPVPN_ENDPOINT, snSearch " + snSearch);
								
								return false;
							}
							else 
								return true;							
						}
						else
						{
							log.debugf("fetchPepvpnCommand-- Wrong type type = %s, sn = %s", snSearch, type);
							return false;
						}
					}
			}
	    	else
	    	{
	    		log.debugf("fetchPepvpnCommand-- Device offline sn = %s", snSearch);
				return false;
			}
			
	    }
		
		public static  boolean isNeedFetch (PepvpnEndpointObject pepvpnStatus)
		{
			if ( pepvpnStatus == null )
				return true;
			int fetchTime = DateUtils.getUnixtime() - pepvpnStatus.getFetchStartTime();
			if ( fetchTime > MAX_FETCH_TIME ) 
				return true;
			else
				return false;
		}
		public static  boolean isNeedFetch (PepvpnPeerDetailObject pepvpnStatus)
		{
			if ( pepvpnStatus == null )
				return true;
			int fetchTime = DateUtils.getUnixtime() - pepvpnStatus.getFetchStartTime();
			if ( fetchTime > MAX_FETCH_TIME ) 
				return true;
			else
				return false;
		}
		public static  boolean isNeedFetch (PepvpnTunnelStatObject pepvpnStatus)
		{
			if ( pepvpnStatus == null )
				return true;
			int fetchTime = DateUtils.getUnixtime() - pepvpnStatus.getFetchStartTime();
			log.info("PepvpnTunnelStatObject fetchTime= "+ fetchTime);
			if ( fetchTime > MAX_FETCH_TIME ) 
				return true;
			else
				return false;
		}

	
	public static PepvpnPeerDetailObject getPepvpnPeerDetailFromInfo(PepvpnEndpointObject endpointobj)
	{
		log.infof("case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL: pepvpnEndpointObject convert begin1 endpointobj= %s", endpointobj);
		if (endpointobj == null) {
			return null;
		}
		PepvpnPeerDetailObject peerdetailobj = new PepvpnPeerDetailObject();
		peerdetailobj.setSid(endpointobj.getSid());
		peerdetailobj.setSn(endpointobj.getSn());
		peerdetailobj.setIana_id(endpointobj.getIana_id());
		peerdetailobj.setStatus(endpointobj.getStatus());
		
		
		ArrayList<PeerDetail> peerdetail_lst = new ArrayList<PeerDetail>();
		CopyOnWriteArrayList<PepvpnConnection> pepconnlst = endpointobj.getMvpn_conn_list();
		if (pepconnlst != null && pepconnlst.size()!=0) {
			for (PepvpnConnection pepconn : pepconnlst) {
				PeerDetail peerdetail = new PeerDetail();
//				peerdetail.setId(pepconn.getId());
				String peerid = pepconn.getId() + "_" + pepconnlst.size();
				peerdetail.setPeer_id(peerid);/* generate peer_id */
				peerdetail.setName(pepconn.getDevice_name());
				peerdetail.setUsername(pepconn.getName());
				//peerdetail.setPid(pepconn.getDevice_network_id());
				peerdetail.setSerial(pepconn.getSn());
				peerdetail.setDevice_id(pepconn.getDevice_id());
				peerdetail.setStatus(pepconn.getMain_state());
				peerdetail.setRoute(pepconn.getNetwork_list());
				peerdetail.setSecure(pepconn.isEnable());				
				peerdetail_lst.add(peerdetail);
				
				
			}
		}
		
		PeerDetailResponse peerdetailres = new PeerDetailResponse();
		peerdetailres.setPeer(peerdetail_lst);
		peerdetailobj.setResponse(peerdetailres);
		return peerdetailobj;
	}
	
	
	
	public static PepvpnEndpointObject getPepvpnStatusFromInfo(PepvpnPeerDetailObject peerdetailobj)
	{
		log.infof("case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL: pepvpnEndpointObject convert begin2 peerdetailobj= %s", peerdetailobj);
		if (peerdetailobj == null) {
			return null;
		}
		PepvpnEndpointObject pepvpn_endpoint = new PepvpnEndpointObject();
		pepvpn_endpoint.setSid(peerdetailobj.getSid());
		pepvpn_endpoint.setSn(peerdetailobj.getSn());
		pepvpn_endpoint.setIana_id(peerdetailobj.getIana_id());//23695
		pepvpn_endpoint.setStatus(peerdetailobj.getStatus());
		
		CopyOnWriteArrayList<Integer> mvpn_order_list = new CopyOnWriteArrayList<Integer>();
		CopyOnWriteArrayList<PepvpnConnection> mvpn_conn_list = new CopyOnWriteArrayList<PepvpnConnection>();		
		
		List<PeerDetail> peer_detail_list= new ArrayList<PeerDetail>();
		List<String> peer_ids = new ArrayList<String>();
		if (peerdetailobj.getResponse() != null)
			peer_detail_list = peerdetailobj.getResponse().getPeer();
		System.out.println(peer_detail_list);
		if (peer_detail_list != null)
		{
			for (PeerDetail pd : peer_detail_list)
			{
			 /*convert peerdetail to mvpn_conn*/		
				peer_ids.add(pd.getPeer_id());
								
				PepvpnConnection  pepconn = new PepvpnConnection();
				pepconn.setName(pd.getName());
				pepconn.setId(pd.getPid());
				pepconn.setMain_state(pd.getStatus());
				List<String> network_list = new ArrayList<String>();
				if (pd.getServer() != null) {
					network_list.add(pd.getServer());
					network_list.add(pd.getClient());
				}
				pepconn.setNetwork_list(network_list);
				
				mvpn_conn_list.add(pepconn);
				//mvpn_order_list.add(pd.getPid());
			}
			pepvpn_endpoint.setCreateTime(DateUtils.getUtcDate().getTime());	
			pepvpn_endpoint.setMvpn_order_list(mvpn_order_list);
			pepvpn_endpoint.setMvpn_conn_list(mvpn_conn_list);
			log.infof("case PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL: pepvpnEndpointObject after: ", pepvpn_endpoint);
		}		
		
		return pepvpn_endpoint;
	}
	
	/* Update PepvpnPeerList Info for each sn(both connected and disconnected) on peerlist */
	public static PepvpnPeerListObject updatePepvpnPeerListInfo(PepvpnPeerListObject peerlistobj)
	{
		if (peerlistobj == null) 
			return null;		

		if (peerlistobj.getResponse()==null || peerlistobj.getResponse().getPeer()==null)
			return null;
		
		PepvpnPeerListObject pepvpn_peerlist = new PepvpnPeerListObject();
		pepvpn_peerlist.setSid(peerlistobj.getSid());
		pepvpn_peerlist.setSn(peerlistobj.getSn());
		pepvpn_peerlist.setIana_id(23695);
		pepvpn_peerlist.setStatus(peerlistobj.getStatus());
		
		ArrayList<SerialStatus> serial_status_updatelst= new ArrayList<SerialStatus>();
		serial_status_updatelst = peerlistobj.getResponse().getPeer();
		 log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE: serial_status_updatelst= %s", serial_status_updatelst);					
		try {
			
			pepvpn_peerlist = ACUtil.<PepvpnPeerListObject> getPoolObjectBySn(pepvpn_peerlist, PepvpnPeerListObject.class);
			if (pepvpn_peerlist == null) {// || ACUtil.isNeedRefresh(pepvpn_peerlist)
				/* If not found peerlist in cache, then fetch from device */
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_PEER_LIST, peerlistobj.getSid(), 23695, peerlistobj.getSn());
			   log.debug("case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE: fetch command sent "+peerlistobj.getSid()+ peerlistobj.getSn());
				return peerlistobj;
			}
			
			PeerListResponse peerlist_res = new PeerListResponse();
			if (pepvpn_peerlist.getResponse()==null || pepvpn_peerlist.getResponse().getPeer()== null) 
			{				
				if (pepvpn_peerlist.getResponse()!=null) 
					peerlist_res = pepvpn_peerlist.getResponse();					
				
				peerlist_res.setPeer(serial_status_updatelst);
				pepvpn_peerlist.setResponse(peerlist_res);
				return pepvpn_peerlist;
			}
			
			peerlist_res = pepvpn_peerlist.getResponse();
			ArrayList<SerialStatus> serial_status_lst= new ArrayList<SerialStatus>();
			serial_status_lst = peerlist_res.getPeer();
			 log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE: serial_status_lst= %s", serial_status_lst);
			ArrayList<SerialStatus> serial_status_newlst= new ArrayList<SerialStatus>();
			for (SerialStatus ss : serial_status_lst)/* Update old SerialStatus if exist*/
				{
					boolean updated = false; int up=-1;
					if (serial_status_updatelst!=null && serial_status_updatelst.size()!=0)
					{
						for (int i=0; i< serial_status_updatelst.size();i++)
						{
							SerialStatus ss_update=serial_status_updatelst.get(i);
							if (ss_update.getSerial().equals(ss.getSerial()))
							{																
								serial_status_newlst.add(ss_update);
								updated = true;
								up= i;
								break;
							}		
						}
					}
				
					if (updated)
					{						
						serial_status_updatelst.remove(up);
					} 
					else
					{
						serial_status_newlst.add(ss);
					}											
				}
				
			if (serial_status_updatelst!=null)/* Add all new connected SerialStatus*/
			{ 
				for (SerialStatus ss_update : serial_status_updatelst){
						serial_status_newlst.add(ss_update);
					}
			}
			 log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE: serial_status_newlst= %s", serial_status_newlst);
				peerlist_res.setPeer(serial_status_newlst);
				pepvpn_peerlist.setResponse(peerlist_res);				
							
		} 
		catch (Exception e) 
		{
			log.error("fetchCommand - fail to getPoolObjectBySn - fail to update peerlist" + e);
			return null;									
		}
		
		log.debugf("case PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE: return pepvpn_peerlist= %s", pepvpn_peerlist);
		return pepvpn_peerlist;
	}
	
	
//	public PepvpnEndpointObject getPepvpnStatusFromInfo(PepvpnTunnelStatObject tunnel_stat)
//	{
//		if (tunnel_stat == null) {
//			return null;
//		}
//				
//		PepvpnEndpointObject pepvpn_endpoint = new PepvpnEndpointObject();
//		pepvpn_endpoint.setSid(tunnel_stat.getSid());
//		pepvpn_endpoint.setSn(tunnel_stat.getSn());
//		pepvpn_endpoint.setIana_id(23695);
//		pepvpn_endpoint.setStatus(tunnel_stat.getStatus());
//
//		PepvpnLinkInfo linkinfo = new PepvpnLinkInfo();
//		List<Integer> mvpn_order_list = new ArrayList<Integer>();
//		List<PepvpnConnection> mvpn_conn_list = new ArrayList<PepvpnConnection>();		
//		
//		tunnel_stat.getStat();
//
//		try {
//			PepvpnPeerDetailObject pepvpnPeerDetail = new PepvpnPeerDetailObject();
//			pepvpnPeerDetail.setSn(tunnel_stat.getSn());
//		
//				pepvpnPeerDetail = ACUtil.<PepvpnPeerDetailObject> getPoolObjectBySn(pepvpnPeerDetail, PepvpnPeerDetailObject.class);
//			
//			PepvpnConnection pepconn = null;
//			Integer order = null;
//			if (pepvpnPeerDetail!=null) { /* convert */
//				//pepconn.setStat(pepvpnPeerDetail.getStat());
//				
//			
//			}
//			mvpn_conn_list.add(pepconn);
//			mvpn_order_list.add(order);
//			if (pepvpnPeerDetail == null || ACUtil.isNeedRefresh(pepvpnPeerDetail)) {
//				
//				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL, pepvpn_endpoint.getSid(), 23695, tunnel_stat.getSn());
//			}
//			} catch (InstantiationException | IllegalAccessException e) 
//			{
////				log.error("fetchCommand - fail to getPoolObjectBySn");
//				return null;									
//			}
//		
//		pepvpn_endpoint.setCreateTime(DateUtils.getUtcDate().getTime());
//		pepvpn_endpoint.setLinkinfo(linkinfo);		
//		pepvpn_endpoint.setMvpn_order_list((CopyOnWriteArrayList<Integer>) mvpn_order_list);
//		pepvpn_endpoint.setMvpn_conn_list((CopyOnWriteArrayList<PepvpnConnection>) mvpn_conn_list);
//		return pepvpn_endpoint;
//	}
	


//	public static PepvpnPeerDetailObject loadPepvpnEndpointsObj(String orgId, Integer netId, Integer maxHubLicense, Devices selectedHub)
//		{		 
//			log.debugf("orgId %s, netId %d, maxHubLicense %d",orgId, netId, maxHubLicense);
//			
//			int id = 0;
//			Devices hub_device = null; 
//			List<Devices> devLst =null;
//			PepvpnPeerDetailObject pepvpnStatus = new PepvpnPeerDetailObject();	
//			PeerDetailResponse peerdetailresponse = new PeerDetailResponse();
//			ArrayList<PeerDetail> peerdetailLst = new ArrayList<PeerDetail>();		
//			ArrayList<VpnGroupInfo> vpn_grp = new ArrayList<VpnGroupInfo>();	
//			ArrayList<Integer> endpoints = new ArrayList<Integer>();		
//			
//			try
//			{
//				hub_device = selectedHub;
//				devLst = NetUtils.getDeviceLstByNetId(orgId, netId);
//			if (hub_device == null || devLst == null)
//			{
//				log.warnf("hub_device %s or devLst %s is null", hub_device, devLst);
//				return null;
//			}
//	
//			for (Devices dev : devLst)
//			{
//				if (dev.getSn().equalsIgnoreCase(hub_device.getSn()) && (dev.getIanaId().intValue()==hub_device.getIanaId().intValue())) continue;
//				//if (ONLINE_STATUS.statusOf(dev.getOnline_status())==ONLINE_STATUS.ONLINE)
//				
//				id++;
//				PeerDetail peerd = new PeerDetail();
//	//			peerd.setId(id);
//				peerd.setPeer_id(id+"_0");//TODO, currently empty 0	connection	
//				
//				peerd.setRemote_device_id(hub_device.getId());
//				peerd.setRemote_serial(hub_device.getSn());
//				
//				peerd.setDevice_id(dev.getId());
//				peerd.setSerial(dev.getSn());
//				endpoints.add(dev.getId());			
//				peerd.setName(dev.getName());			
//				peerd.setDevice_network_id(dev.getNetworkId());
//				DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
//				if (devO != null && devO.isOnline())
//					peerd.setStatus("Starting...");
//				else
//					peerd.setStatus("offline");			
//				peerd.setUsername(RadioConfigUtils.formatProfileNameFromDevnameSn(dev.getName(), dev.getSn()));//TODO profileName			
//				peerdetailLst.add(peerd);
//			}
//			VpnGroupInfo vpnInfo = new VpnGroupInfo();
//			if (hub_device!=null)
//			{			
//				vpnInfo.setHub_id(hub_device.getId());	
//				vpnInfo.setHub_net_id(hub_device.getNetworkId());					
//			}		
//			vpnInfo.setEndpoints(endpoints);
//			vpn_grp.add(vpnInfo);
//			pepvpnStatus.setVpn_grp(vpn_grp);
//			peerdetailresponse.setPeer(peerdetailLst);		
//			pepvpnStatus.setResponse(peerdetailresponse);
//		
//			} catch (Exception e) {
//				log.info("loadDatabasePepvpnEndpointsObject, getPepvpnPeerDetailObj= " + e);
//				e.printStackTrace();
//			}
//			return pepvpnStatus;
//		}


		/* ***Add for PepvpnV2*** */
		public static PepvpnPeerDetailObject loadPepvpnPeerDetailObj(String orgId, Integer netId, Integer maxHubLicense, Devices selectedHub)
		{		 
			log.debugf("orgId %s, netId %d, maxHubLicense %d",orgId, netId, maxHubLicense);
			
			int id = 0;
			Devices hub_device = null; 
			List<Devices> devLst =null;
			PepvpnPeerDetailObject pepvpnStatus = new PepvpnPeerDetailObject();	
			PeerDetailResponse peerdetailresponse = new PeerDetailResponse();
			ArrayList<PeerDetail> peerdetailLst = new ArrayList<PeerDetail>();		
			ArrayList<VpnGroupInfo> vpn_grp = new ArrayList<VpnGroupInfo>();	
			ArrayList<Integer> endpoints = new ArrayList<Integer>();		
			//PeerDetail hub_detail = new PeerDetail();
			try
			{
				
			//hub_device= RadioConfigUtils.getPepvpnHubDevice(orgId, netId, maxHubLicense);		
			//devLst = RadioConfigUtils.getAssignedEndptLst(orgId, netId, maxHubLicense);
				hub_device = selectedHub;
				devLst = NetUtils.getDeviceLstByNetId(orgId, netId);
			if (hub_device == null || devLst == null)
			{
				log.warnf("hub_device %s or devLst %s is null", hub_device, devLst);
				return null;
			}
	
			for (Devices dev : devLst)
			{/* ****** Add hub => endpoints ******/
				if (dev.getSn().equalsIgnoreCase(hub_device.getSn()) && (dev.getIanaId().intValue()==hub_device.getIanaId().intValue())) continue;
				id++;
				PeerDetail peerd = new PeerDetail();
	//			peerd.setId(id);
				peerd.setPeer_id(id+"_0");//TODO, currently empty 0	connection	
				
				peerd.setDevice_id(hub_device.getId());
				peerd.setSerial(hub_device.getSn());
				peerd.setName(hub_device.getName());//TODO	
				
				peerd.setRemote_device_id(dev.getId());
				peerd.setRemote_serial(dev.getSn());
				endpoints.add(dev.getId());			
				peerd.setDevice_network_id(dev.getNetworkId());
				//DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
				//if (devO != null && devO.isOnline())
				if (ONLINE_STATUS.statusOf(dev.getOnline_status())==ONLINE_STATUS.ONLINE)
					peerd.setStatus("STARTING...");
				else
					peerd.setStatus("OFFLINE");
				
				peerd.setUsername(PepvpnConfigUtils.formatProfileNameFromDevnameSn(dev.getName(), dev.getSn()));//TODO profileName				
				peerdetailLst.add(peerd);
			
			/* ****** Add endpoints => hub ******/
				if (dev.getSn().equalsIgnoreCase(hub_device.getSn()) && (dev.getIanaId().intValue()==hub_device.getIanaId().intValue())) continue;
				//if (ONLINE_STATUS.statusOf(dev.getOnline_status())==ONLINE_STATUS.ONLINE)			
				id++;
				PeerDetail peerd_end = new PeerDetail();
	//			peerd_end.setId(id);
				peerd_end.setPeer_id(id+"_0");//TODO, currently empty 0	connection	
				
				peerd_end.setRemote_device_id(hub_device.getId());
				peerd_end.setRemote_serial(hub_device.getSn());
				
				peerd_end.setDevice_id(dev.getId());
				peerd_end.setSerial(dev.getSn());
				//endpoints.add(dev.getId());			
				peerd_end.setName(dev.getName());			
				peerd_end.setDevice_network_id(dev.getNetworkId());
				//DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
				//if (devO != null && devO.isOnline())
				if (ONLINE_STATUS.statusOf(dev.getOnline_status())==ONLINE_STATUS.ONLINE)
					peerd_end.setStatus("STARTING...");
				else
					peerd_end.setStatus("OFFLINE");			
				peerd_end.setUsername(PepvpnConfigUtils.formatProfileNameFromDevnameSn(hub_device.getName(), hub_device.getSn()));//TODO profileName			
				peerdetailLst.add(peerd_end);
			
			}
			VpnGroupInfo vpnInfo = new VpnGroupInfo();
			if (hub_device!=null)
			{			
				vpnInfo.setHub_id(hub_device.getId());	
				vpnInfo.setHub_net_id(hub_device.getNetworkId());
				//ip_host_list.add(hub_device.getAddress());//dev.getWebadmin_cfg()
				//vpnInfo.setIp_host_list(ip_host_list);					
			}
			//peerdetailLst.add(hub_detail);
			vpnInfo.setEndpoints(endpoints);
			vpn_grp.add(vpnInfo);
			pepvpnStatus.setVpn_grp(vpn_grp);
			peerdetailresponse.setPeer(peerdetailLst);		
			pepvpnStatus.setResponse(peerdetailresponse);
		
			} catch (Exception e) {
				log.info("loadDatabasePepvpnPeerDetailObject, getPepvpnPeerDetailObj= " + e);
				e.printStackTrace();
			}
			return pepvpnStatus;
		}


	public static void main(String[] args) {
		Gson gson = new Gson();

		PepvpnPeerDetailObject pepvpnPeerDetailObj = new PepvpnPeerDetailObject();
		String peerDetailJson = "{\"sid\": \"20140904042634609962014090404265490731800\",\"qid\": 876689,\"timestamp\": 1409617908,\"iana_id\": 23695,\"status\": 200,\"sn\": \"1824-D00C-0721\",\"type\": \"PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL\",\"stat\": \"ok\",\"response\": {\"peer\":"
				+"[{\"peer_id\": \"191_1\",\"name\": \"FSH_0183\",\"pid\": 191,\"type\": \"l3\",\"serial\": \"feaeaf26\",\"status\": \"CONNECTED\",\"username\": \"0191\",\"route\": [\"172.16.183.0/24\"]},{\"peer_id\": \"198_1\",\"name\": \"FSH_0181\",\"pid\": 198,\"serial\": \"feae9f23\",\"type\": \"l3\",\"status\": \"CONNECTED\",\"username\": \"0198\",\"route\": [\"172.16.181.0/24\"]},{\"peer_id\": \"8_1\",\"name\": \"Balance_2663\",\"pid\": 8,\"serial\": \"feae9f26\",\"type\": \"l2\",\"status\": \"CONNECTED\",\"username\": \"0008\",\"bridge\": \"78.78.1.2\"},{\"peer_id\": \"52_4820\",\"name\": \"SoftClient\",\"pid\": 52,\"serial\": \"feae9f25\",\"type\": \"nats\",\"status\": \"CONNECTED\",\"username\": \"0052\",\"server\": \"79.3.1.1\",\"client\": \"79.3.1.137/16\"},{\"peer_id\": \"52_4821\",\"name\": \"SoftClient\",\"pid\": 52,\"serial\": \"7bfcbcfb\",\"type\": \"natc\",\"status\": \"CONNECTED\",\"username\": \"0052\",\"server\": \"79.3.1.1\",\"client\": \"79.3.1.174/16\"}]}}";
		pepvpnPeerDetailObj = gson.fromJson(peerDetailJson, PepvpnPeerDetailObject.class);
		System.out.println(pepvpnPeerDetailObj);
		
		PepvpnEndpointObject pepvpn_endpoint = new PepvpnEndpointObject();
		PepvpnStatusUtils pepvpnStatusUtils = new PepvpnStatusUtils();
//		pepvpn_endpoint = pepvpnStatusUtils.getPepvpnStatusFromInfo(pepvpnPeerDetailObj);
		System.out.println(pepvpn_endpoint);
		
		
		String newTunnelStatJson = "{\"stat\": \"ok\", \"response\": { \"tunnel_order\": [ \"23_43442\" ], \"tunnel\": { \"23_43442\": { \"1\": { \"rtt\": 1, \"stime\": 145193, \"tx\": [ 6913524, null, null, null, null, null, null, 6917288 ], \"nanostime\": 938839666, \"ack_miss\": [ 0, null, null, null, null, null, null, 0 ], \"rx\": [ 3717360, null, null, null, null, null, null, 3718320 ], \"state\": \"HC_OK\", \"name\": \"WAN 1\" }, \"2\": { \"rtt\": 4, \"stime\": 145193, \"tx\": [ 6913156, null, null, null, null, null, null, 6917632 ], \"nanostime\": 938832877, \"ack_miss\": [ 0, null, null, null, null, null, null, 0 ], \"rx\": [ 3717396, null, null, null, null, null, null, 3718748 ], \"state\": \"HC_OK\", \"name\": \"WAN 2\" }}, \"order\": [ 1, 2 ] }}}";//
		Map<String, String> out;
		String upperLayer;
//		JsonUtils.toPropertyFormat(newTunnelStatJson, out, upperLayer);
	}
	
}
