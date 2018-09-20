package com.littlecloud.ac;

import java.util.Calendar;
import java.util.Date;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.util.DateUtils;

public class WtpCounterService {
	
//	private static Calendar cal;
	private static int start, end;	
	private static int average_msg_process_counter;
	private static int interval;
	private static int wtp_records;
	private static boolean count_task_start = false;


	private static final Logger log = Logger.getLogger(WtpCounterService.class);

	
	public static int getAverage_msg_process_counter() {
		return average_msg_process_counter;
	}

	public static void setAverage_msg_process_counter(
			int average_msg_process_counter) {
		WtpCounterService.average_msg_process_counter = average_msg_process_counter;
	}

	public static int getWtp_records() {
		return wtp_records;
	}

	public static void setWtp_records(int wtp_records) {
		WtpCounterService.wtp_records = wtp_records;
	}

	public static void setStart(int start) {
		WtpCounterService.start = start;
	}
	
	public static void setEnd(int end) {
		WtpCounterService.end = end;
	}

	public static boolean isCount_task_start() {
		return count_task_start;
	}

	public static void setCount_task_start(boolean count_task_start) {
		WtpCounterService.count_task_start = count_task_start;
	}

	public static int getInterval() {
		return interval;
	}

	public static void setInterval(int interval) {
		WtpCounterService.interval = interval;
	}

	public static void snapshot() {
		
		WtpCounterService.setAverage_msg_process_counter(WtpMsgHandler.getMsg_process_counter());
		WtpCounterService.setStart(0);
		WtpCounterService.setEnd(DateUtils.getUnixtime());
		WtpCounterService.setCount_task_start(true);
	}

	public static void CountTask(int records) {
		
		WtpCounterService.setWtp_records(records);	
		start = end;
		end = DateUtils.getUnixtime();
		interval = end -start;
		
		if (interval>0) {
			
			WtpCounterService.setAverage_msg_process_counter((int)(WtpCounterService.getWtp_records()*60/interval));

			
		}else
		{
			log.infof("After: WtpCounterService.CountTask(), interval=%d error.", interval); 
		}
		
		
		
			
	}
	
}