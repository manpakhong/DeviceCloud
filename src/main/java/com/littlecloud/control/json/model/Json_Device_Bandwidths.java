package com.littlecloud.control.json.model;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.pool.object.DevBandwidthObject;

public class Json_Device_Bandwidths {

	protected static final Logger log = Logger.getLogger(Json_Device_Bandwidths.class);

	private Integer id;
	private Integer device_id;
	private Integer tx;
	private Integer rx;
	private Integer lt_tx;
	private Integer lt_rx;
	private String lt_unit;
	private Date lt_timestamp;
	private Date timestamp;

	public void parseDevBandwidthObject(DevBandwidthObject devBandObject)
	{
		/* sum all bandwidth of list */
		int txSum = 0;
		int rxSum = 0;
		try
		{
			List<DevBandwidthObject.BandwidthList> bandLst = devBandObject.getBandwidth_list();

			for (DevBandwidthObject.BandwidthList band : bandLst)
			{
				txSum += band.getDtx();
				rxSum += band.getDrx();
			}

			tx = txSum;
			rx = rxSum;

		} catch (NullPointerException e) {
			log.warn("No bandwidth data from cache object ("+devBandObject+")");
		}
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Integer getTx() {
		return tx;
	}

	public void setTx(Integer tx) {
		this.tx = tx;
	}

	public Integer getRx() {
		return rx;
	}

	public void setRx(Integer rx) {
		this.rx = rx;
	}

	public Integer getLt_tx() {
		return lt_tx;
	}

	public void setLt_tx(Integer lt_tx) {
		this.lt_tx = lt_tx;
	}

	public Integer getLt_rx() {
		return lt_rx;
	}

	public void setLt_rx(Integer lt_rx) {
		this.lt_rx = lt_rx;
	}

	public String getLt_unit() {
		return lt_unit;
	}

	public void setLt_unit(String lt_unit) {
		this.lt_unit = lt_unit;
	}

	public Date getLt_timestamp() {
		return lt_timestamp;
	}

	public void setLt_timestamp(Date lt_timestamp) {
		this.lt_timestamp = lt_timestamp;
	}

	@Override
	public String toString() {
		return "Json_Device_Bandwidths [id=" + id + ", device_id=" + device_id
				+ ", tx=" + tx + ", rx=" + rx + ", lt_tx=" + lt_tx + ", lt_rx="
				+ lt_rx + ", lt_unit=" + lt_unit + ", lt_timestamp="
				+ lt_timestamp + ", timestamp=" + timestamp + "]";
	}
}
