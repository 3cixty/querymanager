package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;

/**
 * information about a Event
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class EventDetail{
	@Description(hasText="name of the event")
	private String hasEventName="";
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
    private Address hasAddress;
	@Description(hasText = "Details")
	private String hasDetails="";
	@Description(hasText="Start and end information about the event")
    private TemporalDetails hasTemporalDetails;
	@Description(hasText="Nature of the event")
    private NatureOfEvent hasNatureOfEvent;
	public String getHasEventName() {
		return hasEventName;
	}
	public void setHasEventName(String hasEventName) {
		this.hasEventName = hasEventName;
	}
	public Address getHasAddress() {
		return hasAddress;
	}
	public void setHasAddress(Address hasAddress) {
		this.hasAddress = hasAddress;
	}
	public String getHasDetails() {
		return hasDetails;
	}
	public void setHasDetails(String hasDetails) {
		this.hasDetails = hasDetails;
	}
	public TemporalDetails getHasTemporalDetails() {
		return hasTemporalDetails;
	}
	public void setHasTemporalDetails(TemporalDetails hasTemporalDetails) {
		this.hasTemporalDetails = hasTemporalDetails;
	}
	public NatureOfEvent getHasNatureOfEvent() {
		return hasNatureOfEvent;
	}
	public void setHasNatureOfEvent(NatureOfEvent hasNatureOfEvent) {
		this.hasNatureOfEvent = hasNatureOfEvent;
	}
	
	
}
