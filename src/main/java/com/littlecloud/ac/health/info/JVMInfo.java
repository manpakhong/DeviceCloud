package com.littlecloud.ac.health.info;

public class JVMInfo {
	private int availableProcessors;
	private long freeMem;
	private long maxMem;
	private long usedMem;
	private int timestamp;

	public int getAvailableProcessors() {
		return availableProcessors;
	}

	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public long getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(long freeMem) {
		this.freeMem = freeMem;
	}

	public long getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(long maxMem) {
		this.maxMem = maxMem;
	}

	public long getUsedMem() {
		return usedMem;
	}

	public void setUsedMem(long usedMem) {
		this.usedMem = usedMem;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "JVMInfo [availableProcessors=" + availableProcessors + ", freeMem=" + freeMem + ", maxMem=" + maxMem + ", usedMem=" + usedMem + ", timestamp=" + timestamp + "]";
	}
	
	
}
