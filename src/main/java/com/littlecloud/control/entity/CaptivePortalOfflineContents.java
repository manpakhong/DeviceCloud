package com.littlecloud.control.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "captive_portals")
public class CaptivePortalOfflineContents extends DBObject {
	int id;
	long contentSize;
	long compressedSize;
	String md5sum;
	byte [] offlineContent;
	
	public CaptivePortalOfflineContents(){}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	@Column(name = "offline_content_size")
	public long getContentSize() {
		return contentSize;
	}

	@Column(name = "offline_compressed_size")
	public long getCompressedSize() {
		return compressedSize;
	}

	@Column(name = "offline_content_md5sum")
	public String getMd5sum() {
		return md5sum;
	}

	@Column(name = "offline_content")
	public byte[] getOfflineContent() {
		return offlineContent;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setContentSize(long contentSize) {
		this.contentSize = contentSize;
	}

	public void setCompressedSize(long compressedSize) {
		this.compressedSize = compressedSize;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public void setOfflineContent(byte[] offlineContent) {
		this.offlineContent = offlineContent;
	}
	
	
}
