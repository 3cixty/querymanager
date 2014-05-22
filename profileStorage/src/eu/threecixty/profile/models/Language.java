package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;

/**
 * particular language a user speaks
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Language extends LanguageDetail{
	@Description(hasText="user knowledge about the language")
    private UserLanguageState hasLanguageState;          
	@Description(hasText="user would like to gather what knowledge about the language")
    private UserLanguageState wantedLanguageState;         
	@Description(hasText="comments")
    private Set <String> hasKeyTags;
	public UserLanguageState getHasLanguageState() {
		return hasLanguageState;
	}
	public void setHasLanguageState(UserLanguageState hasLanguageState) {
		this.hasLanguageState = hasLanguageState;
	}
	public UserLanguageState getWantedLanguageState() {
		return wantedLanguageState;
	}
	public void setWantedLanguageState(UserLanguageState wantedLanguageState) {
		this.wantedLanguageState = wantedLanguageState;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}

    
}
