package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Weather information
 * @author Mobidot 
 * 
 */
public class Weather {
	@Description(hasText="ID")
	private int hasWeatherID;
	@Description(hasText="Average temperature, in Celsius, over the validity of the measurement.")
	private Number hasTemperature;
	@Description(hasText="Total amount of rain, in mm, over the validity of the measurement.")
	private Number hasRain;
	@Description(hasText="Most prominent weather condition during the validity of the measurement.")
	private String hasCondition;
	public int getHasWeatherID() {
		return hasWeatherID;
	}
	public void setHasWeatherID(int hasWeatherID) {
		this.hasWeatherID = hasWeatherID;
	}
	public Number getHasTemperature() {
		return hasTemperature;
	}
	public void setHasTemperature(Number hasTemperature) {
		this.hasTemperature = hasTemperature;
	}
	public Number getHasRain() {
		return hasRain;
	}
	public void setHasRain(Number hasRain) {
		this.hasRain = hasRain;
	}
	public String getHasCondition() {
		return hasCondition;
	}
	public void setHasCondition(String hasCondition) {
		this.hasCondition = hasCondition;
	}
	
	

}
