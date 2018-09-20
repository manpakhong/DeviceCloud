package com.littlecloud.control.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.request.JsonTunnelRequest;
import com.littlecloud.control.webservices.handler.TunnelWsHandler;

@WebService()
public class TunnelWs extends BaseWs {

	private static final Logger log = Logger.getLogger(TunnelWs.class);

	private enum HANDLER_NAME {
		startWebAdminTunnel, stopWebAdminTunnel, pollWebAdminTunnel, executeCommand
	};

	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName)
	{
		return BaseWs.<JsonTunnelRequest, RESPONSE_ENTITY>
			fetch(json, JsonTunnelRequest.class, JsonResponse.class, TunnelWsHandler.class, handlerName.toString());
	}
	
	@WebMethod()
	public String startWebAdminTunnel(String json)
	{
		return TunnelWs.fetchWrapper(json, HANDLER_NAME.startWebAdminTunnel);
	}
	
	@WebMethod()
	public String stopWebAdminTunnel(String json)
	{
		return TunnelWs.fetchWrapper(json, HANDLER_NAME.stopWebAdminTunnel);
	}
	
	@WebMethod()
	public String pollWebAdminTunnel(String json)
	{
		return TunnelWs.fetchWrapper(json, HANDLER_NAME.pollWebAdminTunnel);
	}
	
	@WebMethod()
	public String executeCommand(String json)
	{
		return TunnelWs.fetchWrapper(json, HANDLER_NAME.executeCommand);
	}
}
