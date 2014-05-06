package eu.threecixty.profile;


import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.threecixty.profile.models.Place;
import eu.threecixty.profile.models.PlaceDetail;
import eu.threecixty.profile.models.Preference;

public class Profiler implements IProfiler {

	private ProfilingTechniques profilingTechnique;
	
	private UserProfile kbUserProfile;
	
	private String uID;

	public Profiler(String uid) {
		this.uID = uid;
	}
	
	@Override
	public void PopulateProfile() {
		// TODO: set kbUserProfile here....
		if (kbUserProfile == null) {
		    kbUserProfile = new UserProfile();
		}
		
		Preference pref = new Preference();
		kbUserProfile.setPreferences(pref); // set preferences
		
		InputStream input = Profiler.class.getResourceAsStream("/UserProfileKBmodelWithIndividuals.rdf");
		if (input != null) {
		    Model rdfModel = ModelFactory.createDefaultModel().read(input, "UTF-8");
		    ProfilerPlaceUtils.addCountryName(pref, rdfModel, uID);
		    ProfilerPlaceUtils.addTownName(pref, rdfModel, uID);
		}
	}

	@Override
	public String getUID() {
		return uID;
	}
	@Override
	public UserProfile getKBUserProfile() {
		return kbUserProfile;
	}

	
	public ProfilingTechniques getProfilingTechnique() {
		return profilingTechnique;
	}
	public void setProfilingTechnique(ProfilingTechniques profilingTechnique) {
		this.profilingTechnique = profilingTechnique;
	}

	@Override
	public Preference getPreference() {
		if (kbUserProfile != null && kbUserProfile.getPreferences() != null) {
			return kbUserProfile.getPreferences();
		} else {
			if (kbUserProfile == null) {
			    kbUserProfile = new UserProfile();
			}
			Preference pref = new Preference();
			kbUserProfile.setPreferences(pref); 
			return pref;
		}
	}
	
}
