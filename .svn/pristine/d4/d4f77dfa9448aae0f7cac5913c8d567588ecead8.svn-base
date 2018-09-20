package com.littlecloud.ac.messagehandler.threads.impl;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.Json_AcInfoDevice;
import com.littlecloud.ac.messagehandler.AcInfoUpdateMessageHandler;
import com.littlecloud.ac.messagehandler.threads.MessageHandlerThread;

public class AcInfoDeviceMessageHandlerThreadImpl implements MessageHandlerThread<Json_AcInfoDevice> {
	private static final Logger log = Logger.getLogger(AcInfoDeviceMessageHandlerThreadImpl.class);
	private Json_AcInfoDevice messageObject;
	
	public AcInfoDeviceMessageHandlerThreadImpl(Json_AcInfoDevice messageObject){
		setMessageObject(messageObject);
	}
	
	@Override
	public void run() {
		boolean isHandled = handleMessageObject();
		if (!isHandled){
			log.warnf("ACINFOUP20140630 - AcInfoDeviceMessageHandlerThreadImpl.run(), messageObject is not handled!: %s", messageObject);
		}
	}

	@Override
	public void setMessageObject(Json_AcInfoDevice messageObject) {
		this.messageObject = messageObject;
		
	}

	@Override
	public boolean handleMessageObject() {
		if (log.isDebugEnabled()) log.debugf("ACINFOUP20140630 - AcInfoDeviceMessageHandlerThreadImpl.handleMessageObject()");
		boolean isHandled = false;
		if (messageObject != null){
			isHandled = AcInfoUpdateMessageHandler.doAcInfoUpdateMessage(messageObject);
		} else {
			log.warnf("ACINFOUP20140630 - AcInfoDeviceMessageHandlerThreadImpl.handleMessageObject(), messageObject is null: %s", messageObject);
		}
		return isHandled;
	}

}
