package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Exists;
import eu.threecixty.profile.annotations.Extend;
/**
 * information about a Event
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class EventDetail {
	@Extend(hasText="http://www.w3.org/2006/vcard/ns#Kind")
	private String hasName="";
	@Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
    private Address hasAddress;
	@Description(hasText="Start and end information about the event")
    private Information hasInformation;
	@Description(hasText="Nature of the event")
    private NatureOfEvent isThenatureOfEvent;
	public String getHasName() {
		return hasName;
	}
	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	public Address getHasAddress() {
		return hasAddress;
	}
	public void setHasAddress(Address hasAddress) {
		this.hasAddress = hasAddress;
	}
	public Information getHasInformation() {
		return hasInformation;
	}
	public void setHasInformation(Information hasInformation) {
		this.hasInformation = hasInformation;
	}
	public NatureOfEvent getIsThenatureOfEvent() {
		return isThenatureOfEvent;
	}
	public void setIsThenatureOfEvent(NatureOfEvent isThenatureOfEvent) {
		this.isThenatureOfEvent = isThenatureOfEvent;
	}
	
}
