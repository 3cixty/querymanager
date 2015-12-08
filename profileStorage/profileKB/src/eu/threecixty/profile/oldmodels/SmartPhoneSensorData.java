/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.oldmodels;

import java.util.Date;

import eu.threecixty.profile.annotations.Description;

/**
 * Based on Thales Inputs. The class contains the sensor information gathered from smartphone 
 *
 */
public class SmartPhoneSensorData {
	@Description(hasText="Date of measurement")
	private Date providesDateTime;
	@Description(hasText="longitute information where the measurement was made")
	private Long hasLongitude;
	@Description(hasText="latitute information where the measurement was made")
	private Long hasLatitude;
	@Description(hasText="Temperature when the measurement was made")
	private Double hasTemperature;
	public Date getProvidesDateTime() {
		return providesDateTime;
	}
	public void setProvidesDateTime(Date providesDateTime) {
		this.providesDateTime = providesDateTime;
	}
	public Long getHasLongitude() {
		return hasLongitude;
	}
	public void setHasLongitude(Long hasLongitude) {
		this.hasLongitude = hasLongitude;
	}
	public Long getHasLatitude() {
		return hasLatitude;
	}
	public void setHasLatitude(Long hasLatitude) {
		this.hasLatitude = hasLatitude;
	}
	public Double getHasTemperature() {
		return hasTemperature;
	}
	public void setHasTemperature(Double hasTemperature) {
		this.hasTemperature = hasTemperature;
	}
	
}
