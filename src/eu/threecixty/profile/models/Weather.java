package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Weather information
 * @author Mobidot 
 * 
 */
public class Weather {
	@Description(hasText="ID")
	private int ID;
	@Description(hasText="Average temperature, in Celsius, over the validity of the measurement.")
	private Number temperature;
	@Description(hasText="Total amount of rain, in mm, over the validity of the measurement.")
	private Number rain;
	@Description(hasText="Most prominent weather condition during the validity of the measurement.")
	private String condition;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public Number getTemperature() {
		return temperature;
	}
	public void setTemperature(Number temperature) {
		this.temperature = temperature;
	}
	public Number getRain() {
		return rain;
	}
	public void setRain(Number rain) {
		this.rain = rain;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	

}
