package eu.threecixty.CrawlSocialProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.profile.IDMapping;
import eu.threecixty.profile.UserProfile;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.PlaceDetail;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.Rating;
import eu.threecixty.profile.oldmodels.UserEnteredRating;
import eu.threecixty.profile.oldmodels.UserInteractionMode;
import eu.threecixty.profile.oldmodels.UserPlaceRating;

/**
 * GPlus Profile Parser
 * 
 * @author Rachit@inria, Rosca@inria
 * 
 */
public class CallGPlusProfileParser {

	private String OS = System.getProperty("os.name").toLowerCase();

	private String PhantomUnix = "./phantomjs";
	private String PhantomWin = "phantomjs.exe";

	/**
	 * Extracts user public information and reviews made by the user about a
	 * specific place. Extracts information like gender, current address, 
	 * work places, school attended, and UserEnteredRatings. And Sets them to 
	 * respective objects
	 * 
	 * @param Long currentTime,
	 * @param IDMapping map,
	 * @param UserProfile user,
	 * @param String lastCrawlTime,
	 * @param Preference preference
	 */
	public void getInfoAndReviews(Long currentTime, IDMapping map,
			UserProfile user, String lastCrawlTime, Preference preference) {
		JSONObject jsonObject = new JSONObject(callParser(
				map.getThreeCixtyID(), currentTime,
				Long.parseLong(lastCrawlTime)));
		user.setHasGender(jsonObject.getString("gender"));

		JSONArray arr = null;

		if (jsonObject.has("organizations")) {
			arr = jsonObject.getJSONArray("organizations");
			// get current organization it may be of type
			if (arr.length() == 1) {
				JSONObject jsonarrobj = arr.getJSONObject(0);
				String type = jsonarrobj.getString("type");
				if (type == "School") {
					// TODO: add code
				} else if (type == "work") {
					// TODO: add code
				}
			}
			for (int length = 0; length < arr.length(); length++) {
				JSONObject jsonarrobj = arr.getJSONObject(length);
				if (jsonarrobj.has("primary")) {
					if (jsonarrobj.getBoolean("primary")) {
						String type = jsonarrobj.getString("type");
						if (type == "School") {
							// TODO: add code
						} else if (type == "work") {
							// TODO: add code
						}
					}
				}
			}
		}

		if (jsonObject.has("placesLived")) {
			arr = jsonObject.getJSONArray("placesLived");
			int length = arr.length();
			Address address = new Address();
			System.out.println("error is here?");
			JSONObject jsonarrobj = arr.getJSONObject(0);
			String addresstoParse = null;

			if (arr.length() == 1) {
				addresstoParse = jsonarrobj.getString("value");
			} else {
				for (int i = 0; i < length; i++) {
					if (arr.getJSONObject(i).has("primary")) {
						addresstoParse = jsonarrobj.getString("value");
					}
				}
			}
			parseAddress(address, addresstoParse);
			user.setHasAddress(address);
		}

		JSONArray arrRating = null;
		if (jsonObject.has("ratings")) {
			arrRating = jsonObject.getJSONArray("ratings");

			if (arrRating.length() > 0) {
				Set<UserEnteredRating> userEnteredRatings = new HashSet<UserEnteredRating>();
				UserEnteredRating userEnteredRating = new UserEnteredRating();

				Set<UserPlaceRating> userPlaceRatings = new HashSet<UserPlaceRating>();
				for (int index = 0; index < arrRating.length(); index++) {
					JSONObject jsonarrobj = arrRating.getJSONObject(index);
					UserPlaceRating userPlaceRating = new UserPlaceRating();
					Rating rating = new Rating();
					rating.setHasUseDefinedRating(jsonarrobj
							.getDouble("rating"));
					rating.setHasUserInteractionMode(UserInteractionMode.Visited);

					// get place form eventmedia once the new model is set
					// String PlaceId = jsonarrobj.getString("placeid");

					PlaceDetail placeDetail = new PlaceDetail();
					placeDetail.setHasPlaceName(jsonarrobj.getString("name"));

					Address address = new Address();
					parseAddress(address, jsonarrobj.getString("address"));
					placeDetail.setHasAddress(address);

					userPlaceRating.setHasRating(rating);
					userPlaceRating.setHasPlaceDetail(placeDetail);
					userPlaceRating.setHasNumberOfTimesVisited(1);
					userPlaceRating.setHasPlaceDetail(placeDetail);
					userPlaceRatings.add(userPlaceRating);
				}
				userEnteredRating.setHasUserPlaceRating(userPlaceRatings);
				userEnteredRatings.add(userEnteredRating);

				Preference pref = user.getPreferences();
				if (pref == null) {
					pref = new Preference();
					user.setPreferences(pref);
				}
				preference.setHasUserEnteredRating(userEnteredRatings);
				user.setPreferences(preference);
			}
		}
	}
	
	/**
	 * Sets address. it first extracts city, street, country using google maps api.
	 * 
	 * @param Address address
	 * @param String addresstoParse
	 */
	private void parseAddress(Address address, String addresstoParse) {
		HtmlParser htmlParser = new HtmlParser();
		if (addresstoParse != null || addresstoParse != "") {
			try {
				//ToDo: add api key version as this is limited to 2500 calls per day   
				String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
						+ URLEncoder.encode(addresstoParse, "UTF-8");
				JSONObject jsonObjectAddress = new JSONObject(
						htmlParser.printAddress(url));
				//System.out.println(url);
				if (jsonObjectAddress.getJSONArray("results") != null) {
					JSONObject jsonob = jsonObjectAddress.getJSONArray(
							"results").getJSONObject(0);
					JSONArray addarr = jsonob
							.getJSONArray("address_components");
					String streetAddress = "";
					for (int i = 0; i < addarr.length(); i++) {
						String types = addarr.getJSONObject(i)
								.getJSONArray("types").toString();
						if (types.contains("country")) {
							address.setCountryName(addarr.getJSONObject(i)
									.getString("long_name"));
						}
						if (types.contains("locality")) {
							address.setTownName(addarr.getJSONObject(i)
									.getString("long_name"));

						}
						/*if (types.contains("Street_number")
								|| types.contains("route")
								|| types.contains("establishment")) {
							if (streetAddress != "") {
								streetAddress.concat(", ");
								streetAddress.concat(addarr.getJSONObject(i)
										.getString("long_name"));
							}
						}*/
					}
					//address.setStreetAddress(streetAddress);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * extract user info from google+.
	 * 
	 * @param String UID
	 * @param Long timeStamp
	 * @param Long lastCrawlTime
	 * @return String
	 */
	public String callParser(String UID, Long timeStamp, Long lastCrawlTime) {
		try {
			Process proc = Runtime.getRuntime().exec(
					new String[] {
							OS.indexOf("win") >= 0 ? PhantomWin : PhantomUnix,
							"scrap.js", UID, timeStamp.toString() });
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			StringBuilder result = new StringBuilder(); // http://stackoverflow.com/questions/65668/why-to-use-stringbuffer-in-java-instead-of-the-string-concatenation-operator
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
			HtmlParser htmlParser = new HtmlParser();
			return htmlParser.printProfile(UID, result.toString(), timeStamp,
					lastCrawlTime);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
