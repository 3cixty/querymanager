package eu.threecixty.querymanager;

import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.Expr;

import eu.threecixty.profile.models.Event;
import eu.threecixty.profile.models.EventDetail;
import eu.threecixty.profile.models.Rating;

/**
 * This class is to deal with query for event.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class EventQuery extends ThreeCixtyQuery {

	public EventQuery(Query query) {
		this.query = query;
	}

	/**
	 * Add expressions and triples for a given event into a list of expressions and triples.
	 * @param event
	 * @return
	 */
	public void addExpressionsAndTriples(Event event, List <Expr> exprs, List <Triple> triples) {
		if (query == null || event == null) return;
		
		Rating rating = event.getHasRating();
		if (rating != null) {
			addExprsAndTriples(rating, exprs, triples);
		}

		EventDetail eventDetail = event.getHasEventDetail();
		if (eventDetail != null) {
			addExprsAndTriples(eventDetail, exprs, triples);
		}
	}

	@Override
	public EventQuery cloneQuery() {
		if (query == null) return null;
		Query tmpQuery = query.cloneQuery();
		return new EventQuery(tmpQuery);
	}
}
