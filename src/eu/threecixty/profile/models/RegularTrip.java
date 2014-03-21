package eu.threecixty.profile.models;


import eu.threecixty.profile.annotations.Description;
 
/**
 * Description of a regular, weekly trip of a user between the point of departure and
 * the point of arrival, derived on the server.
 * https://www.movesmarter.nl/portal/appdev/jsonschema/IzonePlatformService.Data.RegularTrip
 * @author Mobidot
*/
public class RegularTrip {
	@Description(hasText = "Trip name, formatted as [start street - end street, city] or "
		+ "[start street, start city - end street, end city] or [place name 1 - place name 2], "
		+ "to distinguish between trips")
    String tripName=""; 
    @Description(hasText = "Average deparature time, in seconds since 0:00")
    Long departureTime; 
    @Description(hasText = "Standard deviation in the deparature time, in seconds")
    Long departureTimeSD; 
    @Description(hasText = "Average travel time, in seconds")
    Long travelTime; 
    @Description(hasText = "Standard deviation in the travel time, in seconds")
    Long travelTimeSD; 
    @Description(hasText = "Fastest travel time, in seconds")
    Long fastestTravelTime; 
    @Description(hasText = "Distance of the regular trip")
    Double totalDistance=0.0; 
    @Description(hasText = "Number of times the user took this route for this particular trip "
		+ "(e.g. Staringlaan-Brouwerijstraat), within the totalTravelTime of the mobility profile")
    Long totalCount; 
    @Description(hasText = "Important personal places along the trip route. In the simplest "
		+ "form this could be start and end place only, but later also a school drop-off.")
    PersonalPlace[] tripPlaces; 
    @Description(hasText = "Modality used during trip: none, foot, bike, car, bus, ...")
    ModalityType tripModality; 
    @Description(hasText = "Unique trip ID")
    Long id; 
    @Description(hasText = "User ID")
    Long userid; 
    @Description(hasText = "Usage pattern per week day")
    String weekdayPattern=""; 
    @Description(hasText = "Departure time per day hour")
    String dayhourPattern=""; 
    @Description(hasText = "Concatenated string of street names")
    String routeName="";
    @Description(hasText = "Names of the most important via roads of this regular trip")
    String viaName=""; 
    @Description(hasText = "Locations mapped on the OSM network")
    MappedLocation[] mappedLocations; 
    @Description(hasText = "Timestamp of the last modification of this regular trip")
    Long lastChanged; 
    @Description(hasText = "Total travel time as function of the departure time")
    String travelTimePattern=""; 
}

