package eu.threecixty.profile.models;

import eu.threecixty.profile.annotations.Description;

/**
 * Name Class
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class Name {
	@Description(hasText="first name")
	private String givenName="";
	@Description(hasText="middle name")
	private String middleName="";
	@Description(hasText="last name")
	private String familyName="";
	@Description(hasText="Honor prefix")
	private String honorificPrefix="";
	@Description(hasText="Honor suffix")
	private String honorificSuffix="";
	@Description(hasText="additional names")
	private String additionalName="";
	@Description(hasText="nick name")
	private String nickName="";
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getHonorificPrefix() {
		return honorificPrefix;
	}
	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}
	public String getHonorificSuffix() {
		return honorificSuffix;
	}
	public void setHonorificSuffix(String honorificSuffix) {
		this.honorificSuffix = honorificSuffix;
	}
	public String getAdditionalName() {
		return additionalName;
	}
	public void setAdditionalName(String additionalName) {
		this.additionalName = additionalName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
}
