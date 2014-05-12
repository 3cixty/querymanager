package eu.threecixty.querymanager;

import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.Expr;

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
	 * Add expressions and triples for a given place into a list of expressions and triples.
	 *
	 * @param place
	 */
	public void addExpressionsAndTriples(Place place, List <Expr> exprs, List <Triple> triples) {
		if (query == null || place == null) return;

		PlaceDetail placeDetail = place.getHasPlaceDetail();
		if (placeDetail != null) {
			
			addExprsAndTriplesFromAttributeNameAndPropertyName(placeDetail, "hasPlaceName",
					placeDetail.getHasNatureOfPlace().toString().toLowerCase(), exprs, triples);

			Address address = placeDetail.getHasAddress();
			if (address != null) {
				addExprsAndTriples(address, exprs, triples);
			}
		}
		Rating rating = place.getHasRating();
		if (rating != null) {
			addExprsAndTriples(rating, exprs, triples);
		}
	}
}
