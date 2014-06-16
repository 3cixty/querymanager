package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

public class Accompanying {
	@Description(hasText = "Unique accompany ID")
	private Long hasAccompanyId; //Unique accompany ID
	@Description(hasText = "Start time of the accompany")
	private Long hasAccompanyTime; //Start time of the accompany
	@Description(hasText = "Duration of the accompany in seconds")
	private Long hasAccompanyValidity; //Duration of the accompany in seconds
	@Description(hasText = "User 1")
	private Long hasAccompanyUserid1; //User 1
	@Description(hasText = "User 2")
	private Long hasAccompanyUserid2; //User 2
	@Description(hasText = "Matching score between trip 1 and trip 2")
	private Double hasAccompanyScore; //Matching score between trip 1 and trip 2
	@Description(hasText = "Decision level, automatic for any accompany automatically detected by the system, and manual for those accompanies indicated by the user.")
	private DecisionLevel hasAccompanyLevel; //Decision level, automatic for any accompany automatically detected by the system, and manual for those accompanies indicated by the user.
	@Description(hasText = "Accompany domain or group")
	private String hasAccompanyDomainName; //Accompany domain or group
	@Description(hasText = "Existing trip of user 1, with trip id of user 1 smaller than trip id of user 2, to avoid double accompanies")
	private TripMeasurement hasAccompanyTrip1; //Existing trip of user 1, with trip id of user 1 smaller than trip id of user 2, to avoid double accompanies
	@Description(hasText = "Existing trip of user 2")
	private TripMeasurement hasAccompanyTrip2; //Existing trip of user 2
	public Long getHasAccompanyId() {
		return hasAccompanyId;
	}
	public void setHasAccompanyId(Long hasAccompanyId) {
		this.hasAccompanyId = hasAccompanyId;
	}
	public Long getHasAccompanyTime() {
		return hasAccompanyTime;
	}
	public void setHasAccompanyTime(Long hasAccompanyTime) {
		this.hasAccompanyTime = hasAccompanyTime;
	}
	public Long getHasAccompanyValidity() {
		return hasAccompanyValidity;
	}
	public void setHasAccompanyValidity(Long hasAccompanyValidity) {
		this.hasAccompanyValidity = hasAccompanyValidity;
	}
	public Long getHasAccompanyUserid1() {
		return hasAccompanyUserid1;
	}
	public void setHasAccompanyUserid1(Long hasAccompanyUserid1) {
		this.hasAccompanyUserid1 = hasAccompanyUserid1;
	}
	public Long getHasAccompanyUserid2() {
		return hasAccompanyUserid2;
	}
	public void setHasAccompanyUserid2(Long hasAccompanyUserid2) {
		this.hasAccompanyUserid2 = hasAccompanyUserid2;
	}
	public Double getHasAccompanyScore() {
		return hasAccompanyScore;
	}
	public void setHasAccompanyScore(Double hasAccompanyScore) {
		this.hasAccompanyScore = hasAccompanyScore;
	}
	public DecisionLevel getHasAccompanyLevel() {
		return hasAccompanyLevel;
	}
	public void setHasAccompanyLevel(DecisionLevel hasAccompanyLevel) {
		this.hasAccompanyLevel = hasAccompanyLevel;
	}
	public String getHasAccompanyDomainName() {
		return hasAccompanyDomainName;
	}
	public void setHasAccompanyDomainName(String hasAccompanyDomainName) {
		this.hasAccompanyDomainName = hasAccompanyDomainName;
	}
	public TripMeasurement getHasAccompanyTrip1() {
		return hasAccompanyTrip1;
	}
	public void setHasAccompanyTrip1(TripMeasurement hasAccompanyTrip1) {
		this.hasAccompanyTrip1 = hasAccompanyTrip1;
	}
	public TripMeasurement getHasAccompanyTrip2() {
		return hasAccompanyTrip2;
	}
	public void setHasAccompanyTrip2(TripMeasurement hasAccompanyTrip2) {
		this.hasAccompanyTrip2 = hasAccompanyTrip2;
	}
	
}
