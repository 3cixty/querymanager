package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

/**
 * particular language a user speaks
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Language {
	@Description(hasText="language information")
	private LanguageInformation hasLanguageInformation;
	@Description(hasText="user knowledge about the language")
    private UserLanguageState isKnown;          
	@Description(hasText="user would like to gather what knowledge about the language")
    private UserLanguageState isWanted;         
	@Description(hasText="comments")
    private Set <String> hasKeyTags;
	
	public LanguageInformation getHasLanguageInformation() {
		return hasLanguageInformation;
	}
	public void setHasLanguageInformation(LanguageInformation hasLanguageInformation) {
		this.hasLanguageInformation = hasLanguageInformation;
	}
	public UserLanguageState getIsKnown() {
		return isKnown;
	}
	public void setIsKnown(UserLanguageState isKnown) {
		this.isKnown = isKnown;
	}
	public UserLanguageState getIsWanted() {
		return isWanted;
	}
	public void setIsWanted(UserLanguageState isWanted) {
		this.isWanted = isWanted;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
    
}
