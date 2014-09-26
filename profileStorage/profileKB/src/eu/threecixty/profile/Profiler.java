package eu.threecixty.profile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import eu.threecixty.profile.GpsCoordinateUtils.GpsCoordinate;
import eu.threecixty.profile.ProfileManager.StartAndEndDate;
import eu.threecixty.profile.oldmodels.Area;
import eu.threecixty.profile.oldmodels.Event;
import eu.threecixty.profile.oldmodels.EventDetail;
import eu.threecixty.profile.oldmodels.NatureOfPlace;
import eu.threecixty.profile.oldmodels.Period;
import eu.threecixty.profile.oldmodels.Place;
import eu.threecixty.profile.oldmodels.PlaceDetail;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.TemporalDetails;

public class Profiler implements IProfiler {

	
	private ProfilingTechniques profilingTechnique;
	
	private String uID;

	private boolean currentCountryRequired = false;
	private boolean currentTownRequired = false;
	private int numberOfTimeVisitedAtLeast = -1;
	private float scoreRatedAtLeast = -1;
	
	private int numberOfTimeVisitedForFriendsAtLeast = -1;
	private float scoreRatedForFriendsAtLeast = -1;

	private double distanceFromCurrentPosition = -1;
	private Period period = null;

	private boolean eventNamesFromEventPreferenceRequired = false;
	private boolean preferredEventDatesRequired = false;
	
	private boolean friendsLikeVisitRequired = false;
	
	private Preference pref;
	
	public Profiler(String uid) {
		this.uID = uid;
		
		initDefaultParametersForAugmentation();
	}
	
	@Override
	public void PopulateProfile() {
		
		pref = new Preference();
		
		addPlaces();
		
		if (period != null) {
			addPeriod(period, pref);
		}

		addEvents();
	}

	@Override
	public String getUID() {
		return uID;
	}
//	@Override
//	public UserProfile getKBUserProfile() {
//		return kbUserProfile;
//	}

	
	public ProfilingTechniques getProfilingTechnique() {
		return profilingTechnique;
	}
	public void setProfilingTechnique(ProfilingTechniques profilingTechnique) {
		this.profilingTechnique = profilingTechnique;
	}

	@Override
	public Preference getPreference() {
		return pref;
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
		
		numberOfTimeVisitedForFriendsAtLeast = -1;
		scoreRatedForFriendsAtLeast = -1;
		currentCountryRequired = false;
		currentTownRequired = false;
		distanceFromCurrentPosition = -1;
		period = null;
		eventNamesFromEventPreferenceRequired = false;
		preferredEventDatesRequired = false;
		friendsLikeVisitRequired = false;
	}

	@Override
	public void requireCurrentCountry(boolean currentCountryRequired) {
		this.currentCountryRequired = currentCountryRequired;
	}

	@Override
	public void requireCurrentTown(boolean currentTownRequired) {
		this.currentTownRequired = currentTownRequired;
	}

	@Override
	public void requireNumberOfTimesVisitedForFriendsAtLeast(int number) {
		this.numberOfTimeVisitedForFriendsAtLeast = number;
		
	}

	@Override
	public void requireScoreRatedForFriendsAtLeast(float f) {
		this.scoreRatedForFriendsAtLeast = f;
	}

	@Override
	public void requireAreaWithin(double d) {
		this.distanceFromCurrentPosition = d;
	}

	@Override
	public void requirePeriod(Period period) {
		this.period = period;
	}

	@Override
	public void requireEventName(boolean eventNameRequired) {
		this.eventNamesFromEventPreferenceRequired = eventNameRequired;
	}

	@Override
	public void requirePreferredEventDates(boolean preferredEventDates) {
		this.preferredEventDatesRequired = preferredEventDates;
	}

	public void requireFriendsLikeVisit(boolean friendsLikeVisitRequired) {
		this.friendsLikeVisitRequired = friendsLikeVisitRequired;
	}

	private void addEvents() {
		Set <Event> events = new HashSet <Event>();
		if (scoreRatedAtLeast != -1) {
			List <String> eventNames = ProfileManagerImpl.getInstance().getEventNamesFromRating(uID, scoreRatedAtLeast);
			addEvents(eventNames, events);
			addScoreRequired(scoreRatedAtLeast);
		}
		if (numberOfTimeVisitedAtLeast != -1) {
			List <String> eventNames = ProfileManagerImpl.getInstance().getEventNamesFromNumberOfTimesVisited(uID, numberOfTimeVisitedAtLeast);
			addEvents(eventNames, events);
		}
		if (scoreRatedForFriendsAtLeast != -1) {
			List <String> eventNames = ProfileManagerImpl.getInstance().getEventNamesFromRatingOfFriends(uID, scoreRatedForFriendsAtLeast);
			addEvents(eventNames, events);
			addScoreRequired(scoreRatedForFriendsAtLeast);
		}
		if (numberOfTimeVisitedForFriendsAtLeast != -1) {
			List <String> eventNames = ProfileManagerImpl.getInstance().getEventNamesFromNumberOfTimesVisitedOfFriends(uID, numberOfTimeVisitedForFriendsAtLeast);
			addEvents(eventNames, events);
		}
		
		if (eventNamesFromEventPreferenceRequired) {
			List <String> eventNames = ProfileManagerImpl.getInstance().getEventNamesFromEventPreferences(uID);
			addEvents(eventNames, events);
		}
		if (preferredEventDatesRequired) {
			List <StartAndEndDate> startAndEndDates = ProfileManagerImpl.getInstance().getPreferredStartAndEndDates(uID);
			if (startAndEndDates != null) {
				for (StartAndEndDate startAndEndDate: startAndEndDates) {

					Event event = new Event();
					EventDetail ed = new EventDetail();

					TemporalDetails td = new TemporalDetails();
					td.setHasDateFrom(startAndEndDate.getStartDate());
					td.setHasDateUntil(startAndEndDate.getEndDate());

					ed.setHasTemporalDetails(td);

					event.setHasEventDetail(ed);
					events.add(event);
				}
			}
		}
		if (friendsLikeVisitRequired) {
			List <String> eventNames = ProfileManagerImpl.getInstance().getEventNamesWhichFriendsLikeToVisit(uID);
			addEvents(eventNames, events);
		}
		
		if (events.size() > 0) {
			pref.setHasEvents(events);
		}
	}
	
	private void addEvents(List <String> eventNames, Set <Event> events) {
		if (eventNames != null) {
			for (String eventName: eventNames) {
				Event event = createEvent(eventName);
				if (event != null) events.add(event);
			}
		}
	}
	
	private Event createEvent(String eventName) {
		if (eventName != null && !eventName.equals("")) {
			Event event = new Event();
			EventDetail ed = new EventDetail();
			ed.setHasEventName(eventName);
			
			event.setHasEventDetail(ed);

		    return event;
		}
		return null;
	}

	private void addPlaces() {
		Set <Place> places = new HashSet <Place>();
		if (currentCountryRequired) {
			Place place = createPlace(ProfileManagerImpl.getInstance().getCountryName(uID), NatureOfPlace.Country);
			if (place != null) places.add(place);
		}
		if (currentTownRequired) {
			Place place = createPlace(ProfileManagerImpl.getInstance().getTownName(uID), NatureOfPlace.City);
			if (place != null) places.add(place);
		}

		if (scoreRatedAtLeast != -1) {
			List <String> placeNames = ProfileManagerImpl.getInstance().getPlaceNamesFromRating(uID, scoreRatedAtLeast);
			addPlaces(placeNames, places);
			addScoreRequired(scoreRatedAtLeast);
		}
		if (numberOfTimeVisitedAtLeast != -1) {
			List <String> placeNames = ProfileManagerImpl.getInstance().getPlaceNamesFromNumberOfTimesVisited(uID, numberOfTimeVisitedAtLeast);
			addPlaces(placeNames, places);
		}
		if (scoreRatedForFriendsAtLeast != -1) {
			List <String> placeNames = ProfileManagerImpl.getInstance().getPlaceNamesFromRatingOfFriends(uID, scoreRatedForFriendsAtLeast);
			addPlaces(placeNames, places);
			addScoreRequired(scoreRatedForFriendsAtLeast);
		}
		if (numberOfTimeVisitedForFriendsAtLeast != -1) {
			List <String> placeNames = ProfileManagerImpl.getInstance().getPlaceNamesFromNumberOfTimesVisitedOfFriends(uID, numberOfTimeVisitedForFriendsAtLeast);
			addPlaces(placeNames, places);
		}

		if (distanceFromCurrentPosition != -1) {
			GpsCoordinate coordinate = ProfileManagerImpl.getInstance().getCoordinate(uID);
			if (coordinate != null) {
			    addPlace(coordinate.getLatitude(), coordinate.getLongitude(), distanceFromCurrentPosition, places);
			}
		}
		
		if (places.size() > 0) {
			pref.setHasPlaces(places);
		}
	}

	/**
	 * Adds a period to preference.
	 * @param period
	 * @param pref
	 */
	private void addPeriod(Period period, Preference pref) {
		Set <Period> periods = pref.getHasPeriods();
		if (periods == null) periods = new HashSet <Period>();
		periods.add(period);
		pref.setHasPeriods(periods);
	}

	/**
	 * Adds place from GPS information.
	 * @param lat
	 * @param lon
	 * @param distanceFromCurrentPosition
	 * @param places
	 */
	private void addPlace(double lat, double lon,
			double distanceFromCurrentPosition, Set<Place> places) {
		GpsCoordinate originalPoint = GpsCoordinateUtils.convert(lat, lon);
		GpsCoordinate leftPoint = GpsCoordinateUtils.calc(originalPoint, distanceFromCurrentPosition, 270);
		GpsCoordinate rightPoint = GpsCoordinateUtils.calc(originalPoint, distanceFromCurrentPosition, 90);
		GpsCoordinate topPoint = GpsCoordinateUtils.calc(originalPoint, distanceFromCurrentPosition, 0);
		GpsCoordinate bottomPoint = GpsCoordinateUtils.calc(originalPoint, distanceFromCurrentPosition, 180);

		double maxLat = GpsCoordinateUtils.getLatitudeInDegree(topPoint);
		double minLat = GpsCoordinateUtils.getLatitudeInDegree(bottomPoint);
		double minLon = GpsCoordinateUtils.getLogitudeInDegree(leftPoint);
		double maxLon = GpsCoordinateUtils.getLogitudeInDegree(rightPoint);

		Area area = new Area(minLat, minLon, maxLat, maxLon);

		Place place = new Place();
		PlaceDetail pd = new PlaceDetail();
		pd.setArea(area);
		place.setHasPlaceDetail(pd);
		
		places.add(place);
	}

	/**
	 * Add places with 'Others' for NatureOfPlace.
	 * @param placeNames
	 * @param places
	 */
	private void addPlaces(List<String> placeNames, Set<Place> places) {
		if (placeNames != null) {
			for (String placeName: placeNames) {
				Place place = createPlace(placeName, NatureOfPlace.Others);
				if (place != null) places.add(place);
			}
		}
	}

	private Place createPlace(String placeName, NatureOfPlace nop) {
		if (placeName != null) {
		    Place place = new Place();
		    PlaceDetail pd = new PlaceDetail();
		    pd.setHasPlaceName(placeName);
		    pd.setHasNatureOfPlace(nop);
		    place.setHasPlaceDetail(pd);
		    return place;
		}
		return null;
	}

	private void addScoreRequired(double score) {
		if (pref == null) return;
		Set <Double> scores = pref.getScoresRequired();
		if (scores == null) {
			scores = new HashSet <Double>();
			pref.setScoresRequired(scores);
		}
		if (!scores.contains(score)) scores.add(score);
	}
}
