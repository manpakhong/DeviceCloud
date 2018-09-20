package com.littlecloud.ac.health.util;

import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;

public class MessageUtils <T>{
	public static <T> QueryInfo<T> createQueryInfo(MessageType mt, T data, Integer ianaId, String sn){
		QueryInfo<T> qInfo = new QueryInfo<T>();
		if (data!=null)
			qInfo.setData(data);
		qInfo.setType(mt);
		
		qInfo.setStatus(200);
		qInfo.setSid(JsonUtils.genServerRef());
		qInfo.setSn(sn);
		qInfo.setIana_id(ianaId);
		qInfo.setTimestamp(DateUtils.getUnixtime());
		
		return qInfo;
	}
}
