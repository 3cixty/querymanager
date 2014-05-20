package eu.threecixty.querymanager;


import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.Expr;

import eu.threecixty.ThreeCixtyExpression;
import eu.threecixty.profile.models.Address;
import eu.threecixty.profile.models.Area;
import eu.threecixty.profile.models.Event;
import eu.threecixty.profile.models.EventDetail;
import eu.threecixty.profile.models.Place;
import eu.threecixty.profile.models.PlaceDetail;
import eu.threecixty.profile.models.Rating;

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

	public void addExpressionsAndTriples(Object object, List <Expr> exprs, List <Triple> triples) {
		if (query == null || object == null) return;
		
		if (object instanceof Event) {
			addExpressionsAndTriples((Event) object, exprs, triples);
		} else if (object instanceof Place) {
			addExpressionsAndTriples((Place) object, exprs, triples);
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
	protected void addExprsAndTriples(Object object, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr) {
		QueryUtils.addExprsAndTriples(query, object, exprs, triples, threeCixyExpr);
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
			String attrName, String propertyName, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr) {
		QueryUtils.addExprsAndTriplesFromAttributeNameAndPropertyName(query, object, attrName, propertyName, exprs, triples, threeCixyExpr);
	}

	/**
	 * Add expressions and triples for a given event into a list of expressions and triples.
	 * @param event
	 * @return
	 */
	private void addExpressionsAndTriples(Event event, List <Expr> exprs, List <Triple> triples) {
		
		Rating rating = event.getHasRating();
		if (rating != null) {
			addExprsAndTriples(rating, exprs, triples, ThreeCixtyExpression.Equal);
		}

		EventDetail eventDetail = event.getHasEventDetail();
		if (eventDetail != null) {
			List <Expr> edExprs = new ArrayList <Expr>();
			if (eventDetail.getHasEventName() != null && !eventDetail.getHasEventName().equals("")) {
			    addExprsAndTriplesFromAttributeNameAndPropertyName(eventDetail, "hasEventName",
					    "hasEventName", edExprs, triples, ThreeCixtyExpression.StringEqual);
			}
			Expr expr1 = QueryUtils.createExprWithOrOperandForExprs(edExprs);
			if (expr1 != null) exprs.add(expr1);
			edExprs.clear();
			if (eventDetail.getHasTemporalDetails() != null) {
			    addExprsAndTriplesFromAttributeNameAndPropertyName(eventDetail.getHasTemporalDetails(), "hasDateFrom",
					    "datetime", edExprs, triples, ThreeCixtyExpression.GreaterThanOrEqual);
			    addExprsAndTriplesFromAttributeNameAndPropertyName(eventDetail.getHasTemporalDetails(), "hasDateUntil",
					    "datetime", edExprs, triples, ThreeCixtyExpression.LessThanOrEqual);
			}
			Expr expr2 = QueryUtils.createExprWithAndOperandForExprs(edExprs);
			if (expr2 != null) exprs.add(expr2);
		}
	}

	/**
	 * Add expressions and triples for a given place into a list of expressions and triples.
	 *
	 * @param place
	 */
	private void addExpressionsAndTriples(Place place, List <Expr> exprs, List <Triple> triples) {

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
