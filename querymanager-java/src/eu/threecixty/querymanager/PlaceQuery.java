package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;

import eu.threecixty.profile.models.Address;
import eu.threecixty.profile.models.Place;
import eu.threecixty.profile.models.PlaceDetail;
import eu.threecixty.profile.models.Rating;

/**
 * This class is to deal with query for place.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class PlaceQuery extends ThreeCixtyQuery {

	public PlaceQuery(Query query) {
		this.query = query;
	}

	@Override
	public PlaceQuery cloneQuery() {
		if (query == null) return null;
		Query tmpQuery = query.cloneQuery();
		return new PlaceQuery(tmpQuery);
	}

	/**
	 * Adds place as preference to query.
	 *
	 * @param place
	 */
	public void addPlace(Place place) {
		if (query == null || place == null) return;

		PlaceDetail placeDetail = place.getHasPlaceDetail();
		if (placeDetail != null) {
			
			addPreferenceFromAttributeNameAndPropertyName(placeDetail, "hasName",
					placeDetail.getIsTheNatureOfPlace().toString().toLowerCase());

			Address address = placeDetail.getHasAddress();
			if (address != null) {
				addPreference(address);
			}
		}
		Rating rating = place.getHasRating();
		if (rating != null) {
			addPreference(rating);
		}
	}
}
