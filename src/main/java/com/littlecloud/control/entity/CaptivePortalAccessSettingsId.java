package com.littlecloud.control.entity;

import javax.persistence.Embeddable;

@Embeddable
public class CaptivePortalAccessSettingsId {
	private int cpId;
	private String mode;
	
	public CaptivePortalAccessSettingsId(){}

	public CaptivePortalAccessSettingsId(int cpId, String mode) {
		super();
		this.cpId = cpId;
		this.mode = mode;
	}

	public int getCpId() {
		return cpId;
	}

	public void setCpId(int cpId) {
		this.cpId = cpId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof CaptivePortalAccessSettingsId))
			return false;
		CaptivePortalAccessSettingsId castOther = (CaptivePortalAccessSettingsId) other;

		return (this.getCpId() == castOther.getCpId())
				&& (this.getMode().equals(castOther.getMode()));
	}

	public String toString() {
		return "CaptivePortalAccessSettingsId [cpId=" + cpId + ", "
				+ (mode != null ? "mode=" + mode : "") + "]";
	}
}
