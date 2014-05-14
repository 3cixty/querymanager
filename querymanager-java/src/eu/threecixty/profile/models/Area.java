package eu.threecixty.profile.models;

public class Area {

	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;

	public Area(double minLat, double minLon, double maxLat, double maxLon) {
		this.maxLat = maxLat;
		this.maxLon = maxLon;
		this.minLat = minLat;
		this.minLon = minLon;
	}
	
	public double getMinLat() {
		return minLat;
	}
	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}
	public double getMaxLat() {
		return maxLat;
	}
	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}
	public double getMinLon() {
		return minLon;
	}
	public void setMinLon(double minLon) {
		this.minLon = minLon;
	}
	public double getMaxLon() {
		return maxLon;
	}
	public void setMaxLon(double maxLon) {
		this.maxLon = maxLon;
	}
}
