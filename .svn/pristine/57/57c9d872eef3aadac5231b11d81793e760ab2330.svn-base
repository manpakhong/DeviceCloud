package com.littlecloud.ac.messagehandler.queue.threads;

import java.util.concurrent.BlockingQueue;

import org.jboss.logging.Logger;

import com.littlecloud.ac.messagehandler.CaptivePortalMessageHandler;
import com.littlecloud.ac.messagehandler.queue.messages.Message;
import com.littlecloud.ac.messagehandler.queue.messages.MessageContent;
import com.littlecloud.control.json.request.JsonCaptivePortalRequest;
import com.littlecloud.control.webservices.handler.CaptivePortalWsHandler;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.DeviceUtils;

public class CaptivePortalMessageConsumerThread implements Runnable {
	private static final Logger log = Logger.getLogger(CaptivePortalMessageConsumerThread.class);
	private static BlockingQueue<Message> queue;

	public CaptivePortalMessageConsumerThread(BlockingQueue<Message> q) {
		CaptivePortalMessageConsumerThread.queue = q;
	}
	public void run() {
		try {
			if(log.isDebugEnabled()){
				log.debugf("CaptivePortalMessageConsumerThread.run(): before while, queue.size(): %s", CaptivePortalMessageConsumerThread.queue.size());
			}
			while (CaptivePortalMessageConsumerThread.queue.size() > 0) {
				if(log.isDebugEnabled()){
					log.debugf("CaptivePortalMessageConsumerThread.run(): within while, queue.size(): %s", CaptivePortalMessageConsumerThread.queue.size());
				}
				consume(CaptivePortalMessageConsumerThread.queue.poll());
				if(log.isDebugEnabled())
					log.debug("CaptivePortalMessageConsumerThread: processing msg");
				try {
					java.lang.Thread.sleep(1);
				} catch(Exception e) {
					log.error("MSGHANDLER20140617 - CaptivePortalMessageConsumerThread.run()", e);
				}
			}
			if(log.isDebugEnabled()){
				log.debugf("CaptivePortalMessageConsumerThread.run(): exit while, queue.size(): %s", CaptivePortalMessageConsumerThread.queue.size());
			}
		} catch (Exception e) {
			log.error("MSGHANDLER20140617 - MessageConsumerThread.run() - ", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void consume(Message<?> msg) {
		if (msg != null && msg.getMessageType() != null){
			if (log.isDebugEnabled()){
				log.debugf("MSGHANDLER20140617 - MessageConsumerThread.consume() - ", msg);
			}
			switch (msg.getMessageType()){
				case Message.MESSAGE_TYPE_AC:
					consumeAC((Message<String>) msg);
					break;
				case Message.MESSAGE_TYPE_WEBSERVICE:
					consumeWebService((Message<JsonCaptivePortalRequest>) msg);
					break;
			}

		}
	}
	
	private boolean consumeWebService(Message<JsonCaptivePortalRequest> msg){
		boolean isConsume = false;
		try{
			if (msg.getMessageContent() != null){
				if (log.isDebugEnabled()){
					log.debugf("MSGHANDLER20140617 - MessageConsumerThread.consumeWebService() - ", msg);
				}
				MessageContent<JsonCaptivePortalRequest> msgContent = msg.getMessageContent();
				CaptivePortalWsHandler.putCpUserSessionInAction(msgContent.getData());
			}
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageConsumerThread.run() - ", e);
		}
		return isConsume;
	}
	
	private boolean consumeAC(Message<String> msg){
		boolean isConsume = false;
		
		try{
			if (msg.getMessageContent() != null){
				DevOnlineObject devOnlineO = DeviceUtils.getDevOnlineObject(msg.getMessageContent().getIanaId(), msg.getMessageContent().getSn());
				if (devOnlineO != null && msg.getMessageContent().getData() != null && !msg.getMessageContent().getData().isEmpty()){
					CaptivePortalMessageHandler.doCaptivePortalMessageHandle(devOnlineO, msg.getMessageContent().getData());
				}
			}
		} catch (Exception e){
			log.error("MSGHANDLER20140617 - MessageConsumerThread.run() - ", e);
		}
		return isConsume;
	}
	

}