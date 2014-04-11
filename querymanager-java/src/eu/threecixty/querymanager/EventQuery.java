package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;

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

	public void addEvent(Event event) {
		if (query == null || event == null) return;
		
		Rating rating = event.getHasRating();
		if (rating != null) {
			addPreference(rating);
		}

		EventDetail eventDetail = event.getHasEventDetail();
		if (eventDetail != null) {
			addPreference(eventDetail);
		}
	}

	@Override
	public EventQuery cloneQuery() {
		if (query == null) return null;
		Query tmpQuery = query.cloneQuery();
		return new EventQuery(tmpQuery);
	}
}
