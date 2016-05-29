package com.example.service23;

public class Object_Location {

	private long Id;
	private double latitude;
	private double longitude;
	private String timeStamp;
	private int isCallLocation = 0;
	
	public int getIsCallLocation() {
		return isCallLocation;
	}
	public void setIsCallLocation(int isCallLocation) {
		this.isCallLocation = isCallLocation;
	}
	public long getId() {
		return Id;
	}
	public void setId(long id) {
		Id = id;
	}
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
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}
