package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * Based on Thales Inputs
 * @author Thales
 *
 */
public class TravelPreferences {
	@Description(hasText = "")
	private boolean acceptCrowdedTravel;
	@Description(hasText = "")
	private boolean acceptDelayedTravel;
	@Description(hasText = "")
	private boolean acceptEmptyTravel;
	@Description(hasText = "")
	private boolean acceptHotTravel;
	@Description(hasText = "")
	private boolean acceptNoisyTravel;
	@Description(hasText = "")
	private boolean acceptStandingUpTravel;
	@Description(hasText = "")
	private boolean acceptOnDemandService;
	@Description(hasText = "")
	private boolean acceptPrivateService;
	@Description(hasText = "")
	private ModalityType firstPreferredTransportMode;
	@Description(hasText = "")
	private ModalityType secondPreferredTransportMode;
	public boolean isAcceptCrowdedTravel() {
		return acceptCrowdedTravel;
	}
	public void setAcceptCrowdedTravel(boolean acceptCrowdedTravel) {
		this.acceptCrowdedTravel = acceptCrowdedTravel;
	}
	public boolean isAcceptDelayedTravel() {
		return acceptDelayedTravel;
	}
	public void setAcceptDelayedTravel(boolean acceptDelayedTravel) {
		this.acceptDelayedTravel = acceptDelayedTravel;
	}
	public boolean isAcceptEmptyTravel() {
		return acceptEmptyTravel;
	}
	public void setAcceptEmptyTravel(boolean acceptEmptyTravel) {
		this.acceptEmptyTravel = acceptEmptyTravel;
	}
	public boolean isAcceptHotTravel() {
		return acceptHotTravel;
	}
	public void setAcceptHotTravel(boolean acceptHotTravel) {
		this.acceptHotTravel = acceptHotTravel;
	}
	public boolean isAcceptNoisyTravel() {
		return acceptNoisyTravel;
	}
	public void setAcceptNoisyTravel(boolean acceptNoisyTravel) {
		this.acceptNoisyTravel = acceptNoisyTravel;
	}
	public boolean isAcceptStandingUpTravel() {
		return acceptStandingUpTravel;
	}
	public void setAcceptStandingUpTravel(boolean acceptStandingUpTravel) {
		this.acceptStandingUpTravel = acceptStandingUpTravel;
	}
	public boolean isAcceptOnDemandService() {
		return acceptOnDemandService;
	}
	public void setAcceptOnDemandService(boolean acceptOnDemandService) {
		this.acceptOnDemandService = acceptOnDemandService;
	}
	public boolean isAcceptPrivateService() {
		return acceptPrivateService;
	}
	public void setAcceptPrivateService(boolean acceptPrivateService) {
		this.acceptPrivateService = acceptPrivateService;
	}
	public ModalityType getFirstPreferredTransportMode() {
		return firstPreferredTransportMode;
	}
	public void setFirstPreferredTransportMode(
			ModalityType firstPreferredTransportMode) {
		this.firstPreferredTransportMode = firstPreferredTransportMode;
	}
	public ModalityType getSecondPreferredTransportMode() {
		return secondPreferredTransportMode;
	}
	public void setSecondPreferredTransportMode(
			ModalityType secondPreferredTransportMode) {
		this.secondPreferredTransportMode = secondPreferredTransportMode;
	}
	
}
