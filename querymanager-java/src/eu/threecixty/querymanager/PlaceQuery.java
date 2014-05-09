package eu.threecixty.querymanager;

import java.util.ArrayList;
import java.util.List;

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
	 * Creates expressions for filter to add to query.
	 *
	 * @param place
	 */
	public List <Expr> createExpressions(Place place) {
		List <Expr> exprs = new ArrayList <Expr>();
		if (query == null || place == null) return exprs;

		PlaceDetail placeDetail = place.getHasPlaceDetail();
		if (placeDetail != null) {
			
			exprs.addAll(createExprsFromAttributeNameAndPropertyName(placeDetail, "hasPlaceName",
					placeDetail.getHasNatureOfPlace().toString().toLowerCase()));

			Address address = placeDetail.getHasAddress();
			if (address != null) {
				exprs.addAll(createExprs(address));
			}
		}
		Rating rating = place.getHasRating();
		if (rating != null) {
			exprs.addAll(createExprs(rating));
		}
		return exprs;
	}
}
