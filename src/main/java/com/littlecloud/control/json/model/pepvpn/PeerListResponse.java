package com.littlecloud.control.json.model.pepvpn;

import java.io.Serializable;
import java.util.ArrayList;

public class PeerListResponse implements Serializable {
	private ArrayList<SerialStatus> peer;

	public ArrayList<SerialStatus> getPeer() {
		return peer;
	}
	public void setPeer(ArrayList<SerialStatus> peer) {
		this.peer = peer;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[peer=");
		builder.append(peer);
		builder.append("]");
		return builder.toString();
	}		
}