package com.zyw.nwpulib.model;

public class PositionInfo {
	public PositionInfo() {
	}

	public PositionInfo(String n, double lng, double lat) {
		this.positionName = n;
		this.lat = lat;
		this.lng = lng;
	}

	public String positionName;
	public double lat, lng;
}
