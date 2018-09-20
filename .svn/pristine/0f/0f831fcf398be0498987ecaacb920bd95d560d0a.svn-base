package com.littlecloud.ac.json.model;

import java.util.List;

import com.littlecloud.ac.json.model.command.MessageType;

public class ReportInterval {

	private List<IntervalObject> intervalObjectLst;
	
	public List<IntervalObject> getIntervalObjectLst() {
		return intervalObjectLst;
	}

	public void setIntervalObjectLst(List<IntervalObject> intervalObjectLst) {
		this.intervalObjectLst = intervalObjectLst;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportInterval [intervalObjectLst=");
		builder.append(intervalObjectLst);
		builder.append("]");
		return builder.toString();
	}


	public static class IntervalObject {
		
		private MessageType report_id;
		private Integer interval;
		
		public MessageType getReport_id() {
			return report_id;
		}
		public void setReport_id(MessageType report_id) {
			this.report_id = report_id;
		}
		public Integer getInterval() {
			return interval;
		}
		public void setInterval(Integer interval) {
			this.interval = interval;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SuppressObject [report_id=");
			builder.append(report_id);
			builder.append(", interval=");
			builder.append(interval);
			builder.append("]");
			return builder.toString();
		}
		
	}
}
