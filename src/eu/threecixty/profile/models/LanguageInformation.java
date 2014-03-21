package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Extend;

/**
 * Language details
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class LanguageInformation {

	@Extend(hasText="http://www.w3.org/2006/vcard/ns#Kind")
	private String hasName="";

	public String getHasName() {
		return hasName;
	}

	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	
}
