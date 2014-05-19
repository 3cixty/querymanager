package eu.threecixty.querymanager;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.Expr;

import eu.threecixty.ThreeCixtyExpression;
import eu.threecixty.profile.models.Address;
import eu.threecixty.profile.models.Area;
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
	public void addExpressionsAndTriples(Object object, List <Expr> exprs, List <Triple> triples) {
		if (query == null || object == null) return;
		if (!(object instanceof Place)) return;
		Place place = (Place) object;

		PlaceDetail placeDetail = place.getHasPlaceDetail();
		if (placeDetail != null) {
			if (placeDetail.getHasNatureOfPlace() != null) {
				addExprsAndTriplesFromAttributeNameAndPropertyName(placeDetail, "hasPlaceName",
						placeDetail.getHasNatureOfPlace().toString().toLowerCase(), exprs, triples, ThreeCixtyExpression.Equal);
			}

			Address address = placeDetail.getHasAddress();
			if (address != null) {
//				addExprsAndTriples(address, exprs, triples, ThreeCixtyExpression.);
			}

			Area area = placeDetail.getArea();
			if (area != null) {
				List <Expr> areaExprs = new ArrayList <Expr>();
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "minLat",
						"latitude", areaExprs, triples, ThreeCixtyExpression.GreaterThanOrEqual);
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "maxLat",
						"latitude", areaExprs, triples, ThreeCixtyExpression.LessThanOrEqual);
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "minLon",
						"longitute", areaExprs, triples, ThreeCixtyExpression.GreaterThanOrEqual);
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "maxLon",
						"longitute", areaExprs, triples, ThreeCixtyExpression.LessThanOrEqual);
				Expr expr = QueryUtils.createExprWithAndOperandForExprs(areaExprs);
				exprs.add(expr);
			}
		}
		Rating rating = place.getHasRating();
		if (rating != null) {
			addExprsAndTriples(rating, exprs, triples, ThreeCixtyExpression.Equal);
		}
	}
}
