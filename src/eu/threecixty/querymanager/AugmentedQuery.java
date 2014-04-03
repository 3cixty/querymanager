package eu.threecixty.querymanager;

/**
 * Wrapper for augmented query.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class AugmentedQuery implements IAugmentedQuery {

	private IQuery query;
	
	public AugmentedQuery(IQuery query) {
		this.query = query;
	}
	
	@Override
	public IQuery getQuery() {
		return query;
	}

	@Override
	public String convert2String() {
		if (query == null) return null;
		return QueryUtils.convert2String(query.getQuery());
	}
}
