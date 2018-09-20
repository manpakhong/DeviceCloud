package com.littlecloud.ac.json.model;

public class Json_OpMode {

	private Integer version = 1;
	private Integer rpt_enable = 1;
	private Integer rpt_offset;
	private Integer rpt_period;
	private Integer evt_period;
	private Integer gps_sample_intv;	/* report data points captured in every x seconds within 2 minutes, at most 256 points */
	private Integer gps_live_intv;		/* data update every x seconds */
	private Integer gps_live_pts;		/* data points every x seconds */
	private Integer heartbeat;
	private Integer peer_dead;

	public Json_OpMode(Integer rpt_enable, Integer rpt_offset, Integer rpt_period, Integer evt_period, 
			Integer gps_sample_intv,
			Integer gps_live_intv,
			Integer gps_live_pts,			
			Integer heartbeat, Integer peer_dead) {
		super();
		this.rpt_enable = rpt_enable;
		this.gps_sample_intv = gps_sample_intv;
		this.gps_live_intv = gps_live_intv;
		this.gps_live_pts = gps_live_pts;
		
		/* avoid globally change all reports settings */
		//		this.rpt_offset = rpt_offset;
		//		this.rpt_period = rpt_period;
		//		this.evt_period = evt_period;
		//		this.heartbeat = heartbeat;
		//		this.peer_dead = peer_dead;
	}
	
	@Override
	public String toString() {
		return "Json_OpMode [version=" + version + ", rpt_enable=" + rpt_enable + ", rpt_offset=" + rpt_offset + ", rpt_period=" + rpt_period + ", evt_period=" + evt_period + ", heartbeat=" + heartbeat + ", peer_dead=" + peer_dead + "]";
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getRpt_enable() {
		return rpt_enable;
	}

	public void setRpt_enable(Integer rpt_enable) {
		this.rpt_enable = rpt_enable;
	}

	public Integer getRpt_offset() {
		return rpt_offset;
	}

	public void setRpt_offset(Integer rpt_offset) {
		this.rpt_offset = rpt_offset;
	}

	public Integer getRpt_period() {
		return rpt_period;
	}

	public void setRpt_period(Integer rpt_period) {
		this.rpt_period = rpt_period;
	}

	public Integer getEvt_period() {
		return evt_period;
	}

	public void setEvt_period(Integer evt_period) {
		this.evt_period = evt_period;
	}
	
	public Integer getGps_sample_intv() {
		return gps_sample_intv;
	}

	public void setGps_sample_intv(Integer gps_sample_intv) {
		this.gps_sample_intv = gps_sample_intv;
	}

	public Integer getGps_live_intv() {
		return gps_live_intv;
	}

	public void setGps_live_intv(Integer gps_live_intv) {
		this.gps_live_intv = gps_live_intv;
	}

	public Integer getGps_live_pts() {
		return gps_live_pts;
	}

	public void setGps_live_pts(Integer gps_live_pts) {
		this.gps_live_pts = gps_live_pts;
	}

	public Integer getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(Integer heartbeat) {
		this.heartbeat = heartbeat;
	}

	public Integer getPeer_dead() {
		return peer_dead;
	}

	public void setPeer_dead(Integer peer_dead) {
		this.peer_dead = peer_dead;
	}
}
