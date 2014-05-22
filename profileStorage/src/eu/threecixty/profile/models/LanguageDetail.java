package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Language details
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class LanguageDetail {

	@Description(hasText="Name of the language")
	private String hasLanguageName="";

	public String getHasLanguageName() {
		return hasLanguageName;
	}

	public void setHasLanguageName(String hasLanguageName) {
		this.hasLanguageName = hasLanguageName;
	}
	
}
