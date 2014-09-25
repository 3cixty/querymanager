package eu.threecixty.querymanager;

import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.Expr;

import eu.threecixty.ThreeCixtyExpression;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Area;
import eu.threecixty.profile.oldmodels.Event;
import eu.threecixty.profile.oldmodels.EventDetail;
import eu.threecixty.profile.oldmodels.Place;
import eu.threecixty.profile.oldmodels.PlaceDetail;
import eu.threecixty.profile.oldmodels.Rating;

/**
 * Abstract class to represent a query.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class ThreeCixtyQuery {
	
	protected Query query;

	public ThreeCixtyQuery(Query query) {
		this.query = query;
	}
	
	/**
	 * Clones query.
	 * @return
	 */
	public ThreeCixtyQuery cloneQuery() {
		if (query == null) return null;
		Query tmpQuery = query.cloneQuery();
		return new ThreeCixtyQuery(tmpQuery);
	}

	public void addExpressionsAndTriples(Object object, List <Expr> exprs, List <Triple> triples, boolean isForEvents) {
		if (query == null || object == null) return;
		
		if (object instanceof Event) {
			addExpressionsAndTriples((Event) object, exprs, triples, isForEvents);
		} else if (object instanceof Place) {
			addExpressionsAndTriples((Place) object, exprs, triples, isForEvents);
		} else if (object instanceof Rating) {
			addExpressionsAndTriples((Rating) object, exprs, triples, isForEvents);
		}
	}

	/**
	 * Gets Jena's query.
	 *
	 * @return
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Converts the query to string.
	 * @return
	 */
	public String convert2String() {
		return QueryUtils.convert2String(query);
	}

	/**
	 * Adds expressions and triples found from a given object to a list of expressions and triples.
	 * 
	 * @param object
	 */
	protected void addExprsAndTriples(Object object, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr, boolean isForEvents) {
		QueryUtils.addExprsAndTriples(query, object, exprs, triples, threeCixyExpr, isForEvents);
	}

	/**
	 * Adds expressions and triples found from a given attribute name of a given object and a given
	 * property value to a list of expressions and triples.
	 * @param object
	 * 			The preference instance which contains preference information such as place, country, city, etc.
	 * @param attrName
	 * 			The attribute name in the preference instance.
	 * @param propertyValue
	 * 			The property which defines triple links and filter.
	 */
	protected void addExprsAndTriples(Object object, String attrName, String propertyValue, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr) {
		QueryUtils.addExprsAndTriples(query, object, attrName, propertyValue, exprs, triples, threeCixyExpr);
	}

	/**
	 * Adds expressions and triples found from a given attribute name of a given object and a given
	 * property name in the property file to a list of expressions and triples.
	 * @param object
	 * 				The preference object.
	 * @param attrName
	 * 				The preference object's attribute name. The attribute name containing a value in the instance object which will be used to add a filter.
	 * @param propertyName
	 * 				The property name in the property file. The property name will be used to find triple links in the property file.
	 * @param exprs
	 * 				The list of expressions which is used to contain ones created by this method
	 * @param triples
	 * 				The list of triples which is used to contain ones created by this method
	 * @param threeCixyExpr
	 * 				The expression which is used for filtering results.
	 */
	protected void addExprsAndTriplesFromAttributeNameAndPropertyName(Object object,
			String attrName, String propertyName, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr,
			boolean isForEvents) {
		QueryUtils.addExprsAndTriplesFromAttributeNameAndPropertyName(query, object, attrName, propertyName, exprs, triples,
				threeCixyExpr, isForEvents);
	}

	/**
	 * Add expressions and triples for a given event into a list of expressions and triples.
	 * @param event
	 * @return
	 */
	private void addExpressionsAndTriples(Event event, List <Expr> exprs, List <Triple> triples, boolean isForEvents) {
		
		Rating rating = event.getHasRating();
		addExpressionsAndTriples(rating, exprs, triples, isForEvents);
		
		EventDetail eventDetail = event.getHasEventDetail();
		if (eventDetail != null) {
			if (eventDetail.getHasEventName() != null && !eventDetail.getHasEventName().equals("")) {
			    addExprsAndTriplesFromAttributeNameAndPropertyName(eventDetail, "hasEventName",
					    "hasEventName", exprs, triples, ThreeCixtyExpression.StringEqual, isForEvents);
			}
			if (eventDetail.getHasTemporalDetails() != null) {
			    addExprsAndTriplesFromAttributeNameAndPropertyName(eventDetail.getHasTemporalDetails(), "hasDateFrom",
					    "datetime", exprs, triples, ThreeCixtyExpression.GreaterThanOrEqual, isForEvents);
			    addExprsAndTriplesFromAttributeNameAndPropertyName(eventDetail.getHasTemporalDetails(), "hasDateUntil",
					    "datetime", exprs, triples, ThreeCixtyExpression.LessThanOrEqual, isForEvents);
			}
		}
	}

	/**
	 * Add expressions and triples for a given place into a list of expressions and triples.
	 *
	 * @param place
	 */
	private void addExpressionsAndTriples(Place place, List <Expr> exprs, List <Triple> triples, boolean isForEvents) {

		PlaceDetail placeDetail = place.getHasPlaceDetail();
		if (placeDetail != null) {
			if (placeDetail.getHasNatureOfPlace() != null) {
				addExprsAndTriplesFromAttributeNameAndPropertyName(placeDetail, "hasPlaceName",
						placeDetail.getHasNatureOfPlace().toString().toLowerCase(), exprs, triples,
						ThreeCixtyExpression.StringEqual, isForEvents);
			}

			Address address = placeDetail.getHasAddress();
			if (address != null) {
//				addExprsAndTriples(address, exprs, triples, ThreeCixtyExpression.);
			}

			Area area = placeDetail.getArea();
			if (area != null) {
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "minLat",
						"latitude", exprs, triples, ThreeCixtyExpression.GreaterThanOrEqual, isForEvents);
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "maxLat",
						"latitude", exprs, triples, ThreeCixtyExpression.LessThanOrEqual, isForEvents);
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "minLon",
						"longitute", exprs, triples, ThreeCixtyExpression.GreaterThanOrEqual, isForEvents);
				addExprsAndTriplesFromAttributeNameAndPropertyName(area, "maxLon",
						"longitute", exprs, triples, ThreeCixtyExpression.LessThanOrEqual, isForEvents);
			}
		}
		Rating rating = place.getHasRating();

		addExpressionsAndTriples(rating, exprs, triples, isForEvents);
	}

	private void addExpressionsAndTriples(Rating rating, List <Expr> exprs, List <Triple> triples, boolean isForEvents) {

		if (rating != null) {
			//addExprsAndTriples(rating, exprs, triples, ThreeCixtyExpression.GreaterThanOrEqual, isForEvents);
			
			addExprsAndTriplesFromAttributeNameAndPropertyName(rating, "hasUseDefinedRating",
					"rating", exprs, triples, ThreeCixtyExpression.GreaterThanOrEqual, isForEvents);
		}
	}
}
