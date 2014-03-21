package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.*;

public class Transport {
	@ThalesInputs
	@Description(hasText = "Travel preferences of the user.")
	private TravelPreferences hasTravelPreferences;
	@Description(hasText = "Collection of actual trips made by the user. "
			+ "This collection can be requested from the MoveSmarter API for a given period of time.")
	private Set <TripMeasurement> hasTrips;
	@Description(hasText = "Collection of regular trips, frequently made by the user. "
			+ "This collection can be requested from the MoveSmarter API for a given user, and contains usage statistics as well.")
	private Set <RegularTrip> hasRegularTrips;
	@Description(hasText = "Collection of personal places, frequently visited by the user. "
			+ "This collection can be requested from the MoveSmarter API for a given user, "
			+ "and contains visitation statistics as well.")
	private Set <PersonalPlace> hasPersonalPlaces;
	@Description(hasText = "Collection of  personal modal split of a user. "
			+ "This collection can be requested from the MoveSmarter API computed over a given period of time,"
			+ " and filtered or aggregated on request, e.g. make a difference between rush hour and non rush hour trips,"
			+ " or between trips while it is raining and trips while it is dry.")    
	private Set <ModalityStatistic> hasModalityStatistics;
	public TravelPreferences getHasTravelPreferences() {
		return hasTravelPreferences;
	}
	public void setHasTravelPreferences(TravelPreferences hasTravelPreferences) {
		this.hasTravelPreferences = hasTravelPreferences;
	}
	public Set<TripMeasurement> getHasTrips() {
		return hasTrips;
	}
	public void setHasTrips(Set<TripMeasurement> hasTrips) {
		this.hasTrips = hasTrips;
	}
	public Set<RegularTrip> getHasRegularTrips() {
		return hasRegularTrips;
	}
	public void setHasRegularTrips(Set<RegularTrip> hasRegularTrips) {
		this.hasRegularTrips = hasRegularTrips;
	}
	public Set<PersonalPlace> getHasPersonalPlaces() {
		return hasPersonalPlaces;
	}
	public void setHasPersonalPlaces(Set<PersonalPlace> hasPersonalPlaces) {
		this.hasPersonalPlaces = hasPersonalPlaces;
	}
	public Set<ModalityStatistic> getHasModalityStatistics() {
		return hasModalityStatistics;
	}
	public void setHasModalityStatistics(
			Set<ModalityStatistic> hasModalityStatistics) {
		this.hasModalityStatistics = hasModalityStatistics;
	}
	
	
}
