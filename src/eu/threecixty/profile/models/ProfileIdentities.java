package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * collection of different profiles a user is associate to 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class ProfileIdentities {
	@Description(hasText="User Social profile carrier other than 3cixty")
	private String hasSource="";
	@Description(hasText="User ID on the Social profile carrier other than 3cixty")
    private String hasUID="";
	
	public String getHasSource() {
		return hasSource;
	}
	public void setHasSource(String hasSource) {
		this.hasSource = hasSource;
	}
	public String getHasUID() {
		return hasUID;
	}
	public void setHasUID(String hasUID) {
		this.hasUID = hasUID;
	}
    
}
