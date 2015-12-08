/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.MobilityCrawlerCron;

import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.RegularTrip;
import eu.threecixty.profile.oldmodels.TripPreference;

/**
 * Sets TripPreferences based on the inferences from the mobility data.
 * 
 * @author Rachit@inria
 * 
 */

public class MobilityInferences {

	/**
	 * Sets TripPreferences based on the inferences from the mobility data.
	 * Properties set are setHasModalityType,setHasPreferredMaxTotalDistance,
	 * setHasPreferredTripTime,setHasPreferredTripDuration,setHasPreferredCity,
	 * setHasPreferredCountry, setHasPreferredWeatherCondition.
	 * 
	 * @param RegularTrip maxRegularTrip,
	 * @param TripPreference tripPreference,
	 * @param Double distance,
	 * @param Address address
	 */
	public void setTripPreferences(RegularTrip maxRegularTrip,
			TripPreference tripPreference, Double distance, Address address) {
		tripPreference.setHasModalityType(maxRegularTrip.getHasModalityType());
		tripPreference.setHasPreferredMaxTotalDistance(distance);
		tripPreference.setHasPreferredTripDuration(maxRegularTrip
				.getHasRegularTripTravelTime());
		String[] dhpattern = maxRegularTrip.getHasRegularTripDayhourPattern()
				.split("\\+");
		Long max = 0L;
		int index = 0;
		for (int i = 0; i < 24; i++) {
			if (max < Long.parseLong(dhpattern[i])) {
				max = Long.parseLong(dhpattern[i]);
				index = i;
			}
		}
		if (maxRegularTrip.getHasRegularTripWeatherPattern()!=null && !maxRegularTrip.getHasRegularTripWeatherPattern().isEmpty())
		{
			String[] weatherPattern=maxRegularTrip.getHasRegularTripWeatherPattern().split("\\+");
		
			float maxWeatherpercentage=0;
			for (int i=0;i<weatherPattern.length;i++){
				String[] weatherPatternValues=	weatherPattern[i].split(":");
				if (maxWeatherpercentage<Float.parseFloat(weatherPatternValues[1])){
					maxWeatherpercentage=Float.parseFloat(weatherPatternValues[1]);
					tripPreference.setHasPreferredWeatherCondition(weatherMapping(weatherPatternValues[0]));
				}
			}
		}
		//preferred start time of the trip
		tripPreference.setHasPreferredTripTime((Long) (long) index);
		if (address != null) {
			if (address.getTownName() != null)
				tripPreference.setHasPreferredCity(address.getTownName());
			if (address.getCountryName() != null)
				tripPreference.setHasPreferredCountry(address.getCountryName());
		}
	}
	/**
	 * Map from dutch to english
	 * @param dutchWeatherName
	 * @return englishWeatherName
	 */
	private String weatherMapping(String dutchWeatherName){
		if (dutchWeatherName=="zonnig") return "sunny";
		else if (dutchWeatherName=="licht bewolkt") return "partly cloudy";
		else if (dutchWeatherName=="bewolkt") return "cloudy";
		else if (dutchWeatherName=="zware regen") return "heavy rain";
		else if (dutchWeatherName=="lichte regen") return "drizzle";
		else if (dutchWeatherName=="regenbuien") return "patchy rain";
		else if (dutchWeatherName=="regen") return "rain";
		else if (dutchWeatherName=="mistig") return "fog";
		else if (dutchWeatherName=="heldere lucht") return "clear";
		else if (dutchWeatherName=="onweer") return "thunder";
		else if (dutchWeatherName=="sneeuw") return "snow";
		else return "sunny";
		//else if (dutchWeatherName=="lichte regen") return "light rain";
	    //else if (dutchWeatherName=="zonnig") return "overcast";
		//else if (dutchWeatherName=="zonnig") return "mist";
				
	}
}
