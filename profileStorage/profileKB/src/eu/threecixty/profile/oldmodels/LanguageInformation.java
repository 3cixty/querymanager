package eu.threecixty.profile.oldmodels;

import eu.threecixty.profile.annotations.Description;

/**
 * Language details
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class LanguageInformation {

	@Description(hasText="Name of the language")
	private String hasName="";

	public String getHasName() {
		return hasName;
	}

	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	
}
