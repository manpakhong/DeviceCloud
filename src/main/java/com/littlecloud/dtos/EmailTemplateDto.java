package com.littlecloud.dtos;

import java.util.List;

import com.littlecloud.dtos.json.AlertEmailContactDto;

public class EmailTemplateDto {
	List<AlertEmailContactDto> contactList;

	public List<AlertEmailContactDto> getContactList() {
		return contactList;
	}

	public void setContactList(List<AlertEmailContactDto> contactList) {
		this.contactList = contactList;
	}
	
	
}
