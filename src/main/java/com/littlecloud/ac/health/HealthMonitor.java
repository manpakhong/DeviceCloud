package com.littlecloud.ac.health;


public interface HealthMonitor<T> {
	public T getInfo();
	public void collectInfo();
	public boolean isHealthy();
	public String toJson();
}
