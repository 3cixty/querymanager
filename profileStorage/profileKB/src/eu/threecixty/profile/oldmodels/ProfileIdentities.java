package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * collection of different profiles a user is associate to 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class ProfileIdentities {
	@Description(hasText="User Social profile carrier other than 3cixty. @uses http://www.w3.org/2006/vcard/ns#url")
	private String hasSource="";
	@Description(hasText="User ID on the Social profile carrier other than 3cixty")
    private String hasUserAccountID="";
	@Description(hasText="User status on the profile identity being used. It tells whether the user is using the social media or not")
    private UserInteractionMode hasUserInteractionMode;
	
	public String getHasSource() {
		return hasSource;
	}
	public void setHasSource(String hasSource) {
		this.hasSource = hasSource;
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
	
}
