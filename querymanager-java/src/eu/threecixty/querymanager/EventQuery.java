package eu.threecixty.querymanager;

import java.util.ArrayList;
import java.util.List;

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
	 * Creates expressions for filter to add to query.
	 * @param event
	 * @return
	 */
	public List <Expr> createExpressions(Event event) {
		List <Expr> exprs = new ArrayList<Expr>();
		if (query == null || event == null) return exprs;
		
		Rating rating = event.getHasRating();
		if (rating != null) {
			exprs.addAll(createExprs(rating));
		}

		EventDetail eventDetail = event.getHasEventDetail();
		if (eventDetail != null) {
			exprs.addAll(createExprs(eventDetail));
		}
		return exprs;
	}

	@Override
	public EventQuery cloneQuery() {
		if (query == null) return null;
		Query tmpQuery = query.cloneQuery();
		return new EventQuery(tmpQuery);
	}
}
