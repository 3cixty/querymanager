package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueFloat;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

import eu.threecixty.profile.models.Place;
import eu.threecixty.profile.models.PlaceDetail;
import eu.threecixty.profile.models.Rating;

/**
 * This class is to deal with query for place.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class PlaceQuery implements IQuery {

	private static final Object _SYNC = new Object();
	private static final String PLACE_PROPS_FILE = "/place_query.properties";
	private static final String RATING_RATING = "RATING_RATING";
	private static final String PLACEDETAIL_NAME = "PLACEDETAIL_NAME";
//	private static final String FOAF_PREFIX = "foaf:";

	private static Properties props;
	
	private Query query;
	

	public PlaceQuery(Query query) {
		this.query = query;
		
		// init properties
		if (props == null) {
			synchronized (_SYNC) {
				if (props == null) {
					props = new Properties();
					InputStream in = System.class.getResourceAsStream(PLACE_PROPS_FILE);
					if (in != null) {
						try {
							props.load(in);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public String convert2String() {
		return QueryUtils.convert2String(query);
	}

	@Override
	public IQuery cloneQuery() {
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
			addPlaceDetail(placeDetail);
		}
		Rating rating = place.getHasRating();
		if (rating != null) {
			addRating(rating);
		}
	}

	private void addRating(Rating rating) {
		// TODO: the question is how to pick all information when we want ?
		// for sake of simplicity, only add rating value 
		
		// rate name
		String rating_rate = props.getProperty(RATING_RATING);
		if (rating_rate != null && !rating_rate.equals("")) {
			Triple pattern = Triple.create(Var.alloc("x"),
					Var.alloc(":" +rating_rate), Var.alloc(rating_rate));

			ElementTriplesBlock block = new ElementTriplesBlock();
			block.addTriple(pattern);

			Expr expr = new E_Equals(new ExprVar(rating_rate), new NodeValueFloat(rating.getRating()));
			ElementFilter filter = new ElementFilter(expr);
			
			ElementGroup body = (ElementGroup) query.getQueryPattern();
			if (body == null)  body = new ElementGroup();
			
			body.addElement(block);
			body.addElement(filter);
			
			query.setQueryPattern(body);
		}
	}

	private void addPlaceDetail(PlaceDetail placeDetail) {

		// TODO: the same question with addRating
		// for sake of simplicity, only add placeName

		String placeName = props.getProperty(PLACEDETAIL_NAME);
		if (placeName != null && !placeName.equals("")) {
//			Triple pattern = Triple.create(Var.alloc("x"),
//					Var.alloc(FOAF_PREFIX + placeName), Var.alloc(placeName));
			Triple pattern = Triple.create(Var.alloc("x"),
			        Var.alloc(":" + placeName), Var.alloc(placeName));

			ElementTriplesBlock block = new ElementTriplesBlock();
			block.addTriple(pattern);

			Expr expr = new E_Equals(new ExprVar(placeName), new NodeValueString(placeDetail.getHasName()));
			ElementFilter filter = new ElementFilter(expr);

			ElementGroup body = (ElementGroup) query.getQueryPattern();
			if (body == null)  body = new ElementGroup();
			body.addElement(block);
			body.addElement(filter);
			
			query.setQueryPattern(body);
		}
	}
}
