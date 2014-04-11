package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.*;
 
/**
 * Modality statistic of a trip, a number of trips, or a time period, 
 * extended with a number of context variables such as travel objective or weather condition. 
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.ModalityStatistic
 * @author Mobidot
 *
 */
public class ModalityStatistic {
    @Description(hasText = "Chosen modality")
    ModalityType modalityType;
    @Description(hasText = "Travel objective")
    TravelObjectiveType objectiveType;
    @Description(hasText = "Origin destination on name of type level, e.g. 'home-office','office-home'")
    String originDestinationPair=""; 
    @Description(hasText = "Standardized weather condition string, based on the trip weather")
    String weatherCondition=""; 
    @Description(hasText = "Indicates whether this trip was during rush hour")
    Boolean inRushHour; 
    @Description(hasText = "Total count of trips in this category")
    Long totalcount; 
    @Description(hasText = "Total time, in seconds")
    Long totalTime; 
    @Description(hasText = "Total distance, in meter")
    Double totalDistance=0.0; 
    @Description(hasText = "Average speed, in meter per second")
    Double averageSpeed=0.0; 
    @Description(hasText = "Total time lost relative to fastest time for a trip, in seconds")
    Long totalLostTime; 
    @Description(hasText = "Total cost of the travels, in Euros")
    Double totalCost=0.0; 
    @Description(hasText = "Total emissions of the travels, per emission type")
    Emission[] totalEmission; 
    @Description(hasText = "Total calories spent travelling")
    Double totalCalories;
	public ModalityType getModalityType() {
		return modalityType;
	}
	public void setModalityType(ModalityType modalityType) {
		this.modalityType = modalityType;
	}
	public TravelObjectiveType getObjectiveType() {
		return objectiveType;
	}
	public void setObjectiveType(TravelObjectiveType objectiveType) {
		this.objectiveType = objectiveType;
	}
	public String getOriginDestinationPair() {
		return originDestinationPair;
	}
	public void setOriginDestinationPair(String originDestinationPair) {
		this.originDestinationPair = originDestinationPair;
	}
	public String getWeatherCondition() {
		return weatherCondition;
	}
	public void setWeatherCondition(String weatherCondition) {
		this.weatherCondition = weatherCondition;
	}
	public Boolean getInRushHour() {
		return inRushHour;
	}
	public void setInRushHour(Boolean inRushHour) {
		this.inRushHour = inRushHour;
	}
	public Long getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(Long totalcount) {
		this.totalcount = totalcount;
	}
	public Long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}
	public Double getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}
	public Double getAverageSpeed() {
		return averageSpeed;
	}
	public void setAverageSpeed(Double averageSpeed) {
		this.averageSpeed = averageSpeed;
	}
	public Long getTotalLostTime() {
		return totalLostTime;
	}
	public void setTotalLostTime(Long totalLostTime) {
		this.totalLostTime = totalLostTime;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public Emission[] getTotalEmission() {
		return totalEmission;
	}
	public void setTotalEmission(Emission[] totalEmission) {
		this.totalEmission = totalEmission;
	}
	public Double getTotalCalories() {
		return totalCalories;
	}
	public void setTotalCalories(Double totalCalories) {
		this.totalCalories = totalCalories;
	} 
    
}

