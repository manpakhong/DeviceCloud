package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;

import com.littlecloud.control.json.model.pepvpn.PeerListResponse;
import com.littlecloud.control.json.model.pepvpn.SerialStatus;

public class PepvpnPeerListObject extends PoolObject implements PoolObjectIf, Serializable{	

		private String sid;
		private long timestamp;
		private Integer iana_id;
		protected String sn;
		private Integer status;

		private String stat;
		private PeerListResponse response;

		 @Override
		 public void setKey(Integer iana_id, String sn) {
		 this.iana_id = iana_id;
		 this.sn = sn;
		 }
		 @Override
		 public String getKey() {
		 return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
		 }

		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public String getSid() {
			return sid;
		}
		public void setSid(String sid) {
			this.sid = sid;
		}
		public Integer getIana_id() {
			return iana_id;
		}
		public void setIana_id(Integer iana_id) {
			this.iana_id = iana_id;
		}
		public String getSn() {
			return sn;
		}
		public void setSn(String sn) {
			this.sn = sn;
		}
		public Integer getStatus() {
			return status;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
		public String getStat() {
			return stat;
		}
		public void setStat(String stat) {
			this.stat = stat;
		}
		public PeerListResponse getResponse() {
			return response;
		}
		public void setResponse(PeerListResponse response) {
			this.response = response;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("PepvpnPeerListObject [sid=");
			builder.append(sid);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append(", iana_id=");
			builder.append(iana_id);
			builder.append(", sn=");
			builder.append(sn);
			builder.append(", status=");
			builder.append(status);
			builder.append(", stat=");
			builder.append(stat);
			builder.append(", response=");
			builder.append(response);
			builder.append("]");
			return builder.toString();
		}

//		public class ss {
//			private String serial;
//			private String status;
//
//			public String getSerial() {
//				return serial;
//			}
//			public void setSerial(String serial) {
//				this.serial = serial;
//			}
//			public String getStatus() {
//				return status;
//			}
//			public void setStatus(String status) {
//				this.status = status;
//			}
//
//			@Override
//			public String toString() {
//				StringBuilder builder = new StringBuilder();
//				builder.append("[serial=");
//				builder.append(serial);
//				builder.append(", status=");
//				builder.append(status);
//				builder.append("]");
//				return builder.toString();
//			}
//		}

		
	}
