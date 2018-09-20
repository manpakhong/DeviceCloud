package com.littlecloud.control.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.peplink.api.db.DBObject;

@Entity
@Table(name = "device_configurations"
		, uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
public class DeviceConfigurations extends DBObject implements java.io.Serializable{
	private Integer id;
	private Integer deviceId;
	private Integer backupTime;
	private Integer fileSize;
	private byte[] fileContent;
	private Integer createdAt;
	private String md5;
	


	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "device_id")
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	@Column(name = "backup_time")
	public Integer getBackupTime() {
		return backupTime;
	}
	public void setBackupTime(Integer backupTime) {
		this.backupTime = backupTime;
	}
	@Column(name = "file_size")
	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}
	@Column(name = "file_content")
	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	@Column(name = "created_at")
	public Integer getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Integer createdAt) {
		this.createdAt = createdAt;
	}
	@Column(name = "md5")
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceConfigurations [id=");
		builder.append(id);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", backupTime=");
		builder.append(backupTime);
		builder.append(", fileSize=");
		builder.append(fileSize);
		builder.append(", fileContent=");
		builder.append(Arrays.toString(fileContent));
		builder.append(", createdAt=");
		builder.append(createdAt);
		builder.append(", md5=");
		builder.append(md5);
		builder.append("]");
		return builder.toString();
	}
}
