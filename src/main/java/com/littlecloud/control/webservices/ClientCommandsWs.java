package com.littlecloud.control.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.JsonResponse;
import com.littlecloud.control.json.request.JsonCommandAcRedirectRequest;
import com.littlecloud.control.webservices.handler.ClientCommandsWsHandler;

@WebService()
public class ClientCommandsWs extends BaseWs {

	private static final Logger log = Logger.getLogger(ClientCommandsWs.class);
	
	private enum HANDLER_NAME {
		commandACRedirect
	};
	
	private static <RESPONSE_ENTITY> String fetchWrapper(String json, HANDLER_NAME handlerName){		
		return BaseWs.<JsonCommandAcRedirectRequest, RESPONSE_ENTITY>
			fetch(json, JsonCommandAcRedirectRequest.class, JsonResponse.class, ClientCommandsWsHandler.class, handlerName.toString());
	}
		
	@WebMethod()
	public String commandACRedirect(String json){
		return ClientCommandsWs.fetchWrapper(json, HANDLER_NAME.commandACRedirect);
	}
	
}
