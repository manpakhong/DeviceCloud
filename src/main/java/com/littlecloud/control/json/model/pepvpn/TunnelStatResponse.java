package com.littlecloud.control.json.model.pepvpn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.littlecloud.control.json.model.pepvpn.TunnelDetail;

public class TunnelStatResponse implements Serializable {
	
		private ArrayList<String> tunnel_order;
		private Map<String,Map<String, TunnelDetail>> tunnel;
               
		public Map<String, Map<String, TunnelDetail>> getTunnel() {
			return tunnel;
		}
		public void setTunnel(Map<String, Map<String, TunnelDetail>> tunnel) {
			this.tunnel = tunnel;
		}
//		public Map<String, Object> getTunnel() {
//			return tunnel;
//		}
//		public void setTunnel(Map<String, Object> tunnel) {
//			this.tunnel = tunnel;
//		}
		
					
		public ArrayList<String> getTunnel_order() {
			return tunnel_order;
		}
		public void setTunnel_order(ArrayList<String> tunnel_order) {
			this.tunnel_order = tunnel_order;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{tunnel=");			
			builder.append(tunnel);
			builder.append("}");
			return builder.toString();
		}			
	}	