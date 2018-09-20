package com.littlecloud.ac.json.model;


public class Json_DeviceNeighborList_Response extends Json_GenicApiResponse{


	private Json_DeviceNeighborList response;

	public Json_DeviceNeighborList getResponse() {
		return response;
	}

	public void setResponse(Json_DeviceNeighborList response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "Json_DeviceNeighborList_Response [response=" + response + "]";
	}

	
}
