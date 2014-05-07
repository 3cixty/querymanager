package eu.threecixty.profile;


import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.threecixty.profile.models.Preference;

public class Profiler implements IProfiler {

	private ProfilingTechniques profilingTechnique;
	
	private UserProfile kbUserProfile;
	
	private String uID;

	private boolean currentCountryRequired = false;
	private boolean currentTownRequired = false;
	private int numberOfTimeVisitedAtLeast = -1;
	private float scoreRatedAtLeast = -1;

	public Profiler(String uid) {
		// TODO: uncomment and remove fixed UID
		this.uID = "100900047095598983805";
//		this.uID = uid;
		initDefaultParametersForAugmentation();
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
		    if (currentCountryRequired) {
		        ProfilerPlaceUtils.addCountryName(pref, rdfModel, uID);
		    }
		    if (currentTownRequired) {
		        ProfilerPlaceUtils.addTownName(pref, rdfModel, uID);
		    }
		    
		    if (scoreRatedAtLeast != -1) {
		        ProfilerPlaceUtils.addPlaceNameFromRating(pref, rdfModel, uID, scoreRatedAtLeast);
		    }
		    if (numberOfTimeVisitedAtLeast != -1) {
		        ProfilerPlaceUtils.addPlaceNameFromNumberOfTimesVisited(pref, rdfModel, uID, numberOfTimeVisitedAtLeast);
		    }
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

	@Override
	public void requireNumberOfTimesVisitedAtLeast(int number) {
		this.numberOfTimeVisitedAtLeast = number;
	}

	@Override
	public void requireScoreRatedAtLeast(float f) {
		this.scoreRatedAtLeast = f;
	}

	@Override
	public void initDefaultParametersForAugmentation() {
		numberOfTimeVisitedAtLeast = -1;
		scoreRatedAtLeast = -1;
		currentCountryRequired = false;
		currentTownRequired = false;
	}

	@Override
	public void requireCurrentCountry(boolean currentCountryRequired) {
		this.currentCountryRequired = currentCountryRequired;
	}

	@Override
	public void requireCurrentTown(boolean currentTownRequired) {
		this.currentTownRequired = currentTownRequired;
	}
	
}
