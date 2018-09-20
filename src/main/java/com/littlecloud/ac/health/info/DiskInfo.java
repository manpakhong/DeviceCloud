package com.littlecloud.ac.health.info;

public class DiskInfo {
	private String path;
	private long totalSpace;
	private long freeSpace;
	private long usableSpace;
	private int timestamp;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public long getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}

	public long getUsableSpace() {
		return usableSpace;
	}

	public void setUsableSpace(long usableSpace) {
		this.usableSpace = usableSpace;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "DiskInfo [path=" + path + ", totalSpace=" + totalSpace + ", freeSpace=" + freeSpace + ", usableSpace=" + usableSpace + ", timestamp=" + timestamp + "]";
	}

}
