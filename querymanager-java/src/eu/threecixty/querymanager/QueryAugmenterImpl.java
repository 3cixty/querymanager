package eu.threecixty.querymanager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.util.ExprUtils;

import eu.threecixty.Configuration;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserProfile;

public class QueryAugmenterImpl implements QueryAugmenter {
	
	public static String allPrefixes;
	
	private static final float MIN_SCORE = 3.0f;
	private static final int DESC = - 1;
	private static final String HIGHLIGHTED_VAR_NAME = "augmented";

	@Override
	public String createQueryAugmented(String original,
			QueryAugmenterFilter filter, String uid) throws InvalidSparqlQuery {
		if (filter == QueryAugmenterFilter.FriendsRating)
			return createAugmentedQueryBasedOnFriends(original, uid);
		else if (filter == QueryAugmenterFilter.MyRating)
			return createAugmentedQueryBasedOnMyRating(original, uid);
		return original;
	}

	/**
	 * Augments the given query based on my own ratings.
	 * <br>
	 * In principle, the method takes all IDs of places which have been rated by me to order results.
	 * through Google places.
	 * @param original
	 * @param uid
	 * @return
	 * @throws InvalidSparqlQuery
	 */
	private String createAugmentedQueryBasedOnMyRating(String original,
			String uid) throws InvalidSparqlQuery {
		if (original == null) return null;
		if (uid == null) return original;
		try {
			UserProfile profile = ProfileManagerImpl.getInstance().getProfile(uid, null);
			if (profile == null) return original;
			List <String> placeIds = ProfileManagerImpl.getInstance().getPlaceIdsFromRating(
					profile, MIN_SCORE);
			if (placeIds == null || placeIds.size() == 0) return original;
			return addOrderBysToQuery(original, placeIds);
		} catch (TooManyConnections e) {
			e.printStackTrace();
		}
		return original;
	}

	/**
	 * Augments the given query based on my friends' ratings.
	 * <br>
	 * This method takes all IDs of places which have been rated by my friends to order results.
	 * @param original
	 * @param uid
	 * @return
	 * @throws InvalidSparqlQuery
	 */
	private String createAugmentedQueryBasedOnFriends(String original,
			String uid) throws InvalidSparqlQuery {
		if (original == null) return null;
		if (uid == null) return original;
		try {
			UserProfile profile = ProfileManagerImpl.getInstance().getProfile(uid, null);
			if (profile == null) return original;
			List <String> placeIds = ProfileManagerImpl.getInstance().getPlaceIdsFromRatingOfFriends(
					profile, MIN_SCORE);
			if (placeIds == null || placeIds.size() == 0) return original;
			return addOrderBysToQuery(original, placeIds);
		} catch (TooManyConnections e) {
			e.printStackTrace();
		}
		return original;
	}
	
	/**
	 * Adds "ORDER BY" to a given query. If the query already has "ORDER BY", new "ORDER BY" should
	 * be placed on the top.
	 * @param original
	 * @param placeIds
	 * @throws InvalidSparqlQuery
	 * @return
	 */
	private String addOrderBysToQuery(String original, List<String> placeIds) throws InvalidSparqlQuery {
		String queryWithPrefixes = allPrefixes == null ? Configuration.PREFIXES + " "+ original
				: allPrefixes + " " + Configuration.PREFIXES + " " + original;
		try {
			Query query = QueryFactory.create(queryWithPrefixes);
			
			Expr expr = createExpr(placeIds);
			augmentQuery(query, expr);
			query.setPrefixMapping(null); // remove all prefixes
			return query.toString();
		} catch (Exception e) {
			throw new InvalidSparqlQuery(e.getMessage());
		}
	}
	
	private void augmentQuery(Query query, Expr expr) {
		// for queries having COUNT, GROUP BY, they should not be augmented
		if (!query.hasAggregators() && !query.hasGroupBy()) {
			List <SortCondition> scs = null;
			if (query.getOrderBy() == null) {
				scs = Collections.emptyList();
			} else {
				scs = new LinkedList <SortCondition>();
				scs.addAll(query.getOrderBy());
				query.getOrderBy().clear();
			}
			query.addResultVar(HIGHLIGHTED_VAR_NAME, expr);
			query.addOrderBy(HIGHLIGHTED_VAR_NAME, DESC);
			for (SortCondition sc: scs) {
				query.addOrderBy(sc);
			}
		}
		
		// check for sub query
		Element element = query.getQueryPattern();
		if (element == null) return;
		if (element instanceof ElementGroup) {
			ElementGroup eg = (ElementGroup) element;
			if (eg.getElements() == null) return;
			for (Element tmp: eg.getElements()) {
				if (tmp instanceof ElementSubQuery) {
					ElementSubQuery esq = (ElementSubQuery) tmp;
					augmentQuery(esq.getQuery(), expr);
				}
			}
		}
	}
	
	private Expr createExpr(List<String> placeIds) {
		StringBuilder sb = new StringBuilder();
		for (String placeId: placeIds) {
			if (sb.length() > 0) sb.append(" || ");
			sb.append("?venue = <" + placeId + ">");
		}
		return ExprUtils.parse(sb.toString());
	}
}
