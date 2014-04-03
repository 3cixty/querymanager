package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;

import eu.threecixty.profile.models.Event;

/**
 * This class is to deal with query for event.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class EventQuery implements IQuery {

	private Query query;

	public EventQuery(Query query) {
		this.query = query;
	}

	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public String convert2String() {
		return QueryUtils.convert2String(query);
	}

	public void addEvent(Event event) {
		if (query == null || event == null) return;
		// TODO
		
		
	}

	@Override
	public IQuery cloneQuery() {
		if (query == null) return null;
		Query tmpQuery = query.cloneQuery();
		return new EventQuery(tmpQuery);
	}
}
