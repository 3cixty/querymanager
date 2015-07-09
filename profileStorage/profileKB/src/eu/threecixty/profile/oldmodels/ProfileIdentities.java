package eu.threecixty.profile.oldmodels;

import java.io.Serializable;

import eu.threecixty.profile.annotations.Description;

/**
 * collection of different profiles a user is associate to 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class ProfileIdentities implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5540919218099267428L;
	@Description(hasText="ProfileIdentities URI")
	private String hasProfileIdentitiesURI="";
	@Description(hasText="User Social profile carrier other than 3cixty")
	private String hasSourceCarrier="";
	@Description(hasText="User ID on the Social profile carrier other than 3cixty")
    private String hasUserAccountID="";
	@Description(hasText="User status on the profile identity being used. It tells whether the user is using the social media or not")
    private UserInteractionMode hasUserInteractionMode;
	
	
	public String getHasProfileIdentitiesURI() {
		return hasProfileIdentitiesURI;
	}
	public void setHasProfileIdentitiesURI(String hasProfileIdentitiesURI) {
		this.hasProfileIdentitiesURI = hasProfileIdentitiesURI;
	}
	public String getHasUserAccountID() {
		return hasUserAccountID;
	}
	public void setHasUserAccountID(String hasUserAccountID) {
		this.hasUserAccountID = hasUserAccountID;
	}
	public UserInteractionMode getHasUserInteractionMode() {
		return hasUserInteractionMode;
	}
	public void setHasUserInteractionMode(UserInteractionMode hasUserInteractionMode) {
		this.hasUserInteractionMode = hasUserInteractionMode;
	}
	public String getHasSourceCarrier() {
		return hasSourceCarrier;
	}
	public void setHasSourceCarrier(String hasSourceCarrier) {
		this.hasSourceCarrier = hasSourceCarrier;
	}
	
}
