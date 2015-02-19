package com.example.service;

public class Object_CallDetails {

	private long id;
	private String phoneNo;
	private String name;
	private String timeStamp;
	private long durationInSec;
	private long locationId;
	private int callTypeId;
	private double latitude;
	private double longitude;
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getDurationInSec() {
		return durationInSec;
	}
	public void setDurationInSec(long durationInSec) {
		this.durationInSec = durationInSec;
	}
	public long getLocationId() {
		return locationId;
	}
	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	public int getCallTypeId() {
		return callTypeId;
	}
	public void setCallTypeId(int callTypeId) {
		this.callTypeId = callTypeId;
	}
	
	
}
