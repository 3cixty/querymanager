package eu.threecixty.profile;

import java.util.Set;
import java.util.Date;

import eu.threecixty.profile.annotations.*;
import eu.threecixty.profile.models.*;

/**
 * User Profile
 * @author Rachit.Agarwal@inria.fr
 *
 */
@Extend(hasText = "http://www.w3.org/2006/vcard/ns#Individual //extends individual in current ontology")
public class Profile {    
    //following are the required fields
    @RequiredNotNullEntities 
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Name")
    private Name hasName;    
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Address")
    private Address hasAddress;    
    @Uses(hasText = "http://www.w3.org/2006/vcard/ns#latitude")
    private Double hasLatitude;  
    @Uses(hasText = "http://www.w3.org/2006/vcard/ns#longitude")
    private Double hasLongitude;
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#bday")
    private Date birthDate;  
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Gender")
    private Gender hasGender;      
    @Description(hasText = "Collection hold the various identities that the user has holds other than 3cixty ID. "
    		+ "These identities are the source from where user data is gathered. some sample identities are user’s"
    		+ " facebook identity, user’s twitter identity etc." )
    private Set<ProfileIdentities> hasProfileIdenties; 
    //each of the following can be empty 
    
    @RequiredCanBeNullEntities
    @Exists(hasText = "http://www.w3.org/2006/vcard/ns#Email")
    private String hasEmail; 
    @Description(hasText = "Collection of preferences of the user.")
    private Set <Preference> preferences;
    @Description(hasText = "Collection of Hobbies of the user.")
    private Set <String> hasHobbies;
    @Description(hasText = "Collection of languages the user speaks.")
    private Set <Language> languagesSpoken;
    @Description(hasText = "Collection of friends the user has. This collection also holds the information "
    		+ "about the type of friendship")
    private Set <Friend> hasContacts;                                                                                                               
    /**
     * @comingSoonEntities
     * @Description(hasText = "Collection of skills of the user.")
     * private Set <Skills> hasSkills;
     * @Description(hasText = "Collection that holds the Education information about the user. "
     * 				+"This collection holds the level of education user has completed or is currently enroled in")
     * private Set <Eduation> hasEducation;
     * @Description(hasText = "Collection that holds the Employment history of the user. "
    				+ "This collection holds the user’s previous employers and the current employer if any")
     * private Set <EmployerInformation> hasEmployer;
    */
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
	public Double getHasLatitude() {
		return hasLatitude;
	}
	public void setHasLatitude(Double hasLatitude) {
		this.hasLatitude = hasLatitude;
	}
	public Double getHasLongitude() {
		return hasLongitude;
	}
	public void setHasLongitude(Double hasLongitude) {
		this.hasLongitude = hasLongitude;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public Gender getHasGender() {
		return hasGender;
	}
	public void setHasGender(Gender hasGender) {
		this.hasGender = hasGender;
	}
	public Set<ProfileIdentities> getHasProfileIdenties() {
		return hasProfileIdenties;
	}
	public void setHasProfileIdenties(Set<ProfileIdentities> hasProfileIdenties) {
		this.hasProfileIdenties = hasProfileIdenties;
	}
	public String getHasEmail() {
		return hasEmail;
	}
	public void setHasEmail(String hasEmail) {
		this.hasEmail = hasEmail;
	}
	public Set<Preference> getPreferences() {
		return preferences;
	}
	public void setPreferences(Set<Preference> preferences) {
		this.preferences = preferences;
	}
	public Set<String> getHasHobbies() {
		return hasHobbies;
	}
	public void setHasHobbies(Set<String> hasHobbies) {
		this.hasHobbies = hasHobbies;
	}
	public Set<Language> getLanguagesSpoken() {
		return languagesSpoken;
	}
	public void setLanguagesSpoken(Set<Language> languagesSpoken) {
		this.languagesSpoken = languagesSpoken;
	}
	public Set<Friend> getHasContacts() {
		return hasContacts;
	}
	public void setHasContacts(Set<Friend> hasContacts) {
		this.hasContacts = hasContacts;
	} 
    
}