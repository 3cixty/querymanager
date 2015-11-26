package eu.threecixty.profile.oldmodels;

import java.io.Serializable;

import eu.threecixty.profile.annotations.Description;

/**
 * Trip route, mapped and interpolated on the OSM network.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.MappedLocation
 * @author Mobidot
 *
 */
public class MappedLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3437314189907918195L;
	@Description(hasText = "Local ID")
    Long hasMappedLocationId; 
    @Description(hasText = "OSM node ID")
    Long hasMappedLocationNodeId; 
    @Description(hasText = "OSM way ID")
    Long hasMappedLocationWayId; 
    @Description(hasText = "Time stamp, interpolated to this node ID, in UTC. "
    		+ "Time zone information can be copied from the TripMeasurement. In case of a RegularTrip, "
    		+ "time is relative to trip start time.")
    Long hasMappedLocationTime; 
    @Description(hasText = "Latitude of the node")
    Double latitude=0.0; 
    @Description(hasText = "Longitude of the node")
    Double longitude=0.0; 
    @Description(hasText = "Distance between this node and the previous one")
    Double hasMappedLocationDistance=0.0; 
    @Description(hasText = "Name of the way")
    String hasMappedLocationName=""; 
    @Description(hasText = "Highway classification")
    String hasMappedLocationHighway="";
    @Description(hasText = "Railway classification")
    String hasMappedLocationRailway=""; 
    @Description(hasText = "Waterway classification")
    String hasMappedLocationWaterway=""; 
    @Description(hasText = "Aerialway classification")
    String hasMappedLocationAerialway=""; 
    @Description(hasText = "Service road classification")
    String hasMappedLocationServiceRoadway=""; 
    @Description(hasText = "Measure for the accuracy of the mapping")
    Double hasMappedLocationDeviation=0.0;
	public Long getHasMappedLocationId() {
		return hasMappedLocationId;
	}
	public void setHasMappedLocationId(Long hasMappedLocationId) {
		this.hasMappedLocationId = hasMappedLocationId;
	}
	public Long getHasMappedLocationNodeId() {
		return hasMappedLocationNodeId;
	}
	public void setHasMappedLocationNodeId(Long hasMappedLocationNodeId) {
		this.hasMappedLocationNodeId = hasMappedLocationNodeId;
	}
	public Long getHasMappedLocationWayId() {
		return hasMappedLocationWayId;
	}
	public void setHasMappedLocationWayId(Long hasMappedLocationWayId) {
		this.hasMappedLocationWayId = hasMappedLocationWayId;
	}
	public Long getHasMappedLocationTime() {
		return hasMappedLocationTime;
	}
	public void setHasMappedLocationTime(Long hasMappedLocationTime) {
		this.hasMappedLocationTime = hasMappedLocationTime;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getHasMappedLocationDistance() {
		return hasMappedLocationDistance;
	}
	public void setHasMappedLocationDistance(Double hasMappedLocationDistance) {
		this.hasMappedLocationDistance = hasMappedLocationDistance;
	}
	public String getHasMappedLocationName() {
		return hasMappedLocationName;
	}
	public void setHasMappedLocationName(String hasMappedLocationName) {
		this.hasMappedLocationName = hasMappedLocationName;
	}
	public String getHasMappedLocationHighway() {
		return hasMappedLocationHighway;
	}
	public void setHasMappedLocationHighway(String hasMappedLocationHighway) {
		this.hasMappedLocationHighway = hasMappedLocationHighway;
	}
	public String getHasMappedLocationRailway() {
		return hasMappedLocationRailway;
	}
	public void setHasMappedLocationRailway(String hasMappedLocationRailway) {
		this.hasMappedLocationRailway = hasMappedLocationRailway;
	}
	public String getHasMappedLocationWaterway() {
		return hasMappedLocationWaterway;
	}
	public void setHasMappedLocationWaterway(String hasMappedLocationWaterway) {
		this.hasMappedLocationWaterway = hasMappedLocationWaterway;
	}
	public String getHasMappedLocationAerialway() {
		return hasMappedLocationAerialway;
	}
	public void setHasMappedLocationAerialway(String hasMappedLocationAerialway) {
		this.hasMappedLocationAerialway = hasMappedLocationAerialway;
	}
	public String getHasMappedLocationServiceRoadway() {
		return hasMappedLocationServiceRoadway;
	}
	public void setHasMappedLocationServiceRoadway(String hasMappedLocationServiceRoadway) {
		this.hasMappedLocationServiceRoadway = hasMappedLocationServiceRoadway;
	}
	public Double getHasMappedLocationDeviation() {
		return hasMappedLocationDeviation;
	}
	public void setHasMappedLocationDeviation(Double hasMappedLocationDeviation) {
		this.hasMappedLocationDeviation = hasMappedLocationDeviation;
	}
    
    
 }