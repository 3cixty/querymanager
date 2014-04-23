package eu.threecixty.profile;


import java.util.HashSet;
import java.util.Set;

import eu.threecixty.profile.models.Place;
import eu.threecixty.profile.models.PlaceDetail;
import eu.threecixty.profile.models.Preference;
import eu.threecixty.profile.models.Rating;

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
		}
		// TODO remove the following lines when there will be possible to use ProfilingTechniques
		Preference fakePref = new Preference();
		Set<Place> fakePlaces = new HashSet<Place>();
		Place fakePlace = new Place();
		PlaceDetail fakePD = new PlaceDetail();
		fakePD.setHasName("France");
		fakePlace.setHasPlaceDetail(fakePD);
		
		fakePlaces.add(fakePlace);
		fakePref.setHasPlaces(fakePlaces);

//		Rating fakeRating = new Rating();
//		
//		fakeRating.setRating(9.0f);
//		fakePlace.setHasRating(fakeRating);
		
		return fakePref;
	}
}
