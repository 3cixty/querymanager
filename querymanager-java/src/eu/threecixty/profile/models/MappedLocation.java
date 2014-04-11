package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Trip route, mapped and interpolated on the OSM network.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.MappedLocation
 * @author Mobidot
 *
 */
public class MappedLocation  {
	@Description(hasText = "Local ID")
    Long id; 
    @Description(hasText = "OSM node ID")
    Long nodeId; 
    @Description(hasText = "OSM way ID")
    Long wayId; 
    @Description(hasText = "Time stamp, interpolated to this node ID, in UTC. "
    		+ "Time zone information can be copied from the TripMeasurement. In case of a RegularTrip, "
    		+ "time is relative to trip start time.")
    Long time; 
    @Description(hasText = "Latitude of the node")
    Double latitude=0.0; 
    @Description(hasText = "Longitude of the node")
    Double longitude=0.0; 
    @Description(hasText = "Distance between this node and the previous one")
    Double distance=0.0; 
    @Description(hasText = "Name of the way")
    String name=""; 
    @Description(hasText = "Highway classification")
    String highway="";
    @Description(hasText = "Railway classification")
    String railway=""; 
    @Description(hasText = "Waterway classification")
    String waterway=""; 
    @Description(hasText = "Aerialway classification")
    String aerialway=""; 
    @Description(hasText = "Service road classification")
    String service=""; 
    @Description(hasText = "Measure for the accuracy of the mapping")
    Double deviation=0.0;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public Long getWayId() {
		return wayId;
	}
	public void setWayId(Long wayId) {
		this.wayId = wayId;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
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
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHighway() {
		return highway;
	}
	public void setHighway(String highway) {
		this.highway = highway;
	}
	public String getRailway() {
		return railway;
	}
	public void setRailway(String railway) {
		this.railway = railway;
	}
	public String getWaterway() {
		return waterway;
	}
	public void setWaterway(String waterway) {
		this.waterway = waterway;
	}
	public String getAerialway() {
		return aerialway;
	}
	public void setAerialway(String aerialway) {
		this.aerialway = aerialway;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public Double getDeviation() {
		return deviation;
	}
	public void setDeviation(Double deviation) {
		this.deviation = deviation;
	} 
    
    
 }