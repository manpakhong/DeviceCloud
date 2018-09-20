package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HourlyNdpiUsageObject extends PoolObject implements PoolObjectIf, Serializable
{
	/* key */
	private Integer iana_id;
	private String sn;
	/* value */
	private Integer begin_sec;
	private Integer packets_all;
	private Integer ip_packets_all;
	private Integer ipv4_fragments;
	private Integer non_ipv4;
	private CopyOnWriteArrayList<Protocols> protocols;
	
	public class Protocols
	{
		private String protocol;
		private Integer packets;
		private Long bytes;
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public Integer getPackets() {
			return packets;
		}
		public void setPackets(Integer packets) {
			this.packets = packets;
		}
		public Long getBytes() {
			return bytes;
		}
		public void setBytes(Long bytes) {
			this.bytes = bytes;
		}
		
	}
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		// TODO Auto-generated method stub
		this.iana_id = iana_id;
		this.sn = sn;
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

	public Integer getBegin_sec() {
		return begin_sec;
	}

	public void setBegin_sec(Integer begin_sec) {
		this.begin_sec = begin_sec;
	}

	public Integer getPackets_all() {
		return packets_all;
	}

	public void setPackets_all(Integer packets_all) {
		this.packets_all = packets_all;
	}

	public Integer getIp_packets_all() {
		return ip_packets_all;
	}

	public void setIp_packets_all(Integer ip_packets_all) {
		this.ip_packets_all = ip_packets_all;
	}

	public Integer getIpv4_fragments() {
		return ipv4_fragments;
	}

	public void setIpv4_fragments(Integer ipv4_fragments) {
		this.ipv4_fragments = ipv4_fragments;
	}

	public Integer getNon_ipv4() {
		return non_ipv4;
	}

	public void setNon_ipv4(Integer non_ipv4) {
		this.non_ipv4 = non_ipv4;
	}

	public CopyOnWriteArrayList<Protocols> getProtocols() {
		return protocols;
	}

	public void setProtocols(CopyOnWriteArrayList<Protocols> protocols) {
		this.protocols = protocols;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HourlyNdpiUsageObject [iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", begin_sec=");
		builder.append(begin_sec);
		builder.append(", packets_all=");
		builder.append(packets_all);
		builder.append(", ip_packets_all=");
		builder.append(ip_packets_all);
		builder.append(", ipv4_fragments=");
		builder.append(ipv4_fragments);
		builder.append(", non_ipv4=");
		builder.append(non_ipv4);
		builder.append(", protocols=");
		builder.append(protocols);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
