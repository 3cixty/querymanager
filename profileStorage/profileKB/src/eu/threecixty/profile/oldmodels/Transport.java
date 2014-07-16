package eu.threecixty.profile.oldmodels;

import java.util.Set;

import eu.threecixty.profile.annotations.*;

public class Transport {
	@Description(hasText = "Collection of actual trips made by the user. "
			+ "This collection can be requested from the MoveSmarter API for a given period of time.")
	private Set <TripMeasurement> hasTripMeasurement;
	@Description(hasText = "Collection of regular trips, frequently made by the user. "
			+ "This collection can be requested from the MoveSmarter API for a given user, and contains usage statistics as well.")
	private Set <RegularTrip> hasRegularTrip;
	@Description(hasText = "Collection of personal places, frequently visited by the user. "
			+ "This collection can be requested from the MoveSmarter API for a given user, "
			+ "and contains visitation statistics as well.")
	private Set <PersonalPlace> hasPersonalPlace;
	@Description(hasText = "Collection of  personal modal split of a user. "
			+ "This collection can be requested from the MoveSmarter API computed over a given period of time,"
			+ " and filtered or aggregated on request, e.g. make a difference between rush hour and non rush hour trips,"
			+ " or between trips while it is raining and trips while it is dry.")    
	private Set <ModalityStatistic> hasModalityStatistics;
	
	private Set <Accompanying> hasAccompanyings;
	
	// data is new 
	private Boolean newForKB = null;
	
	public Set<TripMeasurement> getHasTripMeasurement() {
		return hasTripMeasurement;
	}
	public void setHasTripMeasurement(Set<TripMeasurement> hasTripMeasurement) {
		this.hasTripMeasurement = hasTripMeasurement;
	}
	public Set<RegularTrip> getHasRegularTrip() {
		return hasRegularTrip;
	}
	public void setHasRegularTrip(Set<RegularTrip> hasRegularTrip) {
		this.hasRegularTrip = hasRegularTrip;
	}
	public Set<PersonalPlace> getHasPersonalPlace() {
		return hasPersonalPlace;
	}
	public void setHasPersonalPlace(Set<PersonalPlace> hasPersonalPlace) {
		this.hasPersonalPlace = hasPersonalPlace;
	}
	public Set<ModalityStatistic> getHasModalityStatistics() {
		return hasModalityStatistics;
	}
	public void setHasModalityStatistics(
			Set<ModalityStatistic> hasModalityStatistics) {
		this.hasModalityStatistics = hasModalityStatistics;
	}
	public Set<Accompanying> getHasAccompanyings() {
		return hasAccompanyings;
	}
	public void setHasAccompanyings(Set<Accompanying> hasAccompanyings) {
		this.hasAccompanyings = hasAccompanyings;
	}
	public Boolean getNewForKB() {
		return newForKB;
	}
	public void setNewForKB(Boolean newForKB) {
		this.newForKB = newForKB;
	}
	
	
}
