package eu.threecixty.profile;

import java.util.Set;

import eu.threecixty.profile.annotations.*;
import eu.threecixty.profile.oldmodels.*;

/**
 * User Profile
 * @author Rachit.Agarwal@inria.fr
 *
 */
@Extend(hasText = "http://www.w3.org/2006/vcard/ns#Individual //extends individual in current ontology")
public class UserProfile{
    //following are the required fields
    @RequiredNotNullEntities
    @Description(hasText="User ID on the 3cxity Plateform")
    private String hasUID="";
    @Description(hasText="Last Crawl Time on the 3cxity Plateform")
    private String hasLastCrawlTime="0";
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Name")
    private Name hasName;    
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
    private Address hasAddress;    
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Gender")
    private String hasGender;      
    @Description(hasText = "Collection hold the various identities that the user has holds other than 3cixty ID. "
    		+ "These identities are the source from where user data is gathered. some sample identities are user�s"
    		+ " facebook identity, user�s twitter identity etc." )
    private Set<ProfileIdentities> hasProfileIdenties; 
    
    //each of the following can be empty 
//    @RequiredCanBeNullEntities
//    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Email")
//    private String hasEmail; 
    @Description(hasText = "Collection of preferences of the user.")
    private Preference preferences;
//    @Description(hasText = "Collection of Hobbies of the user.")
//    private Set <String> hasHobbies;
//    @Description(hasText = "Collection of languages the user speaks.")
//    private Set <Language> hasLanguage;
//    @Description(hasText = "Collection of friends the user has. This collection also holds the information "
//    		+ "about the type of friendship")
    //private Set <UserProfile> knows;
    private Set <String> knows;
    @Description(hasText = "History of user made query. Check this new architecture")
    private Set<QueryHistory> hasQueryHistory; 
    
//    @ComingSoonEntities
//    @Description(hasText = "Collection of skills of the user.")
//    private Set <Skills> hasSkills;
//    @Description(hasText = "Collection that holds the Education information about the user. "
//    				+"This collection holds the level of education user has completed or is currently enroled in")
//    private Set <Eduation> hasEducation;
//    @Description(hasText = "Collection that holds the Employment history of the user. "
//    				+ "This collection holds the user�s previous employers and the current employer if any")
//    private Set <EmployerInformation> hasEmployerInformation;
  
    /**Attribute to store profile picture*/
    private String profileImage;
    
    public String getHasLastCrawlTime() {
		return hasLastCrawlTime;
	}
	public void setHasLastCrawlTime(String hasLastCrawlTime) {
		this.hasLastCrawlTime = hasLastCrawlTime;
	}
	public Name getHasName() {
		return hasName;
	}
	public void setHasName(Name hasName) {
		this.hasName = hasName;
	}
	public Address getHasAddress() {
		return hasAddress;
	}
	public void setHasAddress(Address hasAddress) {
		this.hasAddress = hasAddress;
	}
//	public Date getBirthDate() {
//		return birthDate;
//	}
//	public void setBirthDate(Date birthDate) {
//		this.birthDate = birthDate;
//	}
	public String getHasGender() {
		return hasGender;
	}
	public void setHasGender(String hasGender) {
		this.hasGender = hasGender;
	}
	public Set<ProfileIdentities> getHasProfileIdenties() {
		return hasProfileIdenties;
	}
	public void setHasProfileIdenties(Set<ProfileIdentities> hasProfileIdenties) {
		this.hasProfileIdenties = hasProfileIdenties;
	}
//	public String getHasEmail() {
//		return hasEmail;
//	}
//	public void setHasEmail(String hasEmail) {
//		this.hasEmail = hasEmail;
//	}
	public Preference getPreferences() {
		return preferences;
	}
	public void setPreferences(Preference preferences) {
		this.preferences = preferences;
	}
//	public Set<String> getHasHobbies() {
//		return hasHobbies;
//	}
//	public void setHasHobbies(Set<String> hasHobbies) {
//		this.hasHobbies = hasHobbies;
//	}
//	public Set<Language> getHasLanguage() {
//		return hasLanguage;
//	}
//	public void setHasLanguage(Set<Language> hasLanguage) {
//		this.hasLanguage = hasLanguage;
//	}
	public Set<String> getKnows() {
		return knows;
	}
	public void setKnows(Set<String> knows) {
		this.knows = knows;
	}
//	public Set<Skills> getHasSkills() {
//		return hasSkills;
//	}
//	public void setHasSkills(Set<Skills> hasSkills) {
//		this.hasSkills = hasSkills;
//	}
//	public Set<Eduation> getHasEducation() {
//		return hasEducation;
//	}
//	public void setHasEducation(Set<Eduation> hasEducation) {
//		this.hasEducation = hasEducation;
//	}
//	public Set<EmployerInformation> getHasEmployerInformation() {
//		return hasEmployerInformation;
//	}
//	public void setHasEmployerInformation(Set<EmployerInformation> hasEmployerInformation) {
//		this.hasEmployerInformation = hasEmployerInformation;
//	}
	public String getHasUID() {
		return hasUID;
	}
	public void setHasUID(String hasUID) {
		this.hasUID = hasUID;
	}
	public Set<QueryHistory> getHasQueryHistory() {
		return hasQueryHistory;
	}
	public void setHasQueryHistory(Set<QueryHistory> hasQueryHistory) {
		this.hasQueryHistory = hasQueryHistory;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}	
}