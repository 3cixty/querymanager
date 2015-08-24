package eu.threecixty.querymanager;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.util.ExprUtils;

import eu.threecixty.Configuration;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserProfile;

public class QueryAugmenterImpl implements QueryAugmenter {
	
	public static String allPrefixes;
	
	 private static final Logger LOGGER = Logger.getLogger(
			 QueryAugmenterImpl.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static final float MIN_SCORE = 3.0f;
	private static final int DESC = - 1;
	private static final String SOCIAL_SCORE_VAR_NAME = "socialScore";

	@Override
	public String createQueryAugmented(String original,
			QueryAugmenterFilter filter, String uid, double coef) throws InvalidSparqlQuery {
		if (filter == QueryAugmenterFilter.FriendsRating)
			return createAugmentedQueryBasedOnFriends(original, uid, coef);
		else if (filter == QueryAugmenterFilter.MyRating)
			return createAugmentedQueryBasedOnMyRating(original, uid, coef);
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
			String uid, double coef) throws InvalidSparqlQuery {
		if (original == null) return null;
		if (uid == null) return original;
		try {
			UserProfile profile = ProfileManagerImpl.getInstance().getProfile(uid, null);
			if (profile == null) return original;
			
			List <String> placeIds = new LinkedList <String>();
			List <Double> socialScores = new LinkedList <Double>();
			ProfileManagerImpl.getInstance().findPlaceIdsAndSocialScore(
					profile, MIN_SCORE, placeIds, socialScores);
			if (placeIds == null || placeIds.size() == 0) return original;
			return addOrderBysToQuery(original, placeIds, socialScores, coef);
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
			String uid, double coef) throws InvalidSparqlQuery {
		if (original == null) return null;
		if (uid == null) return original;
		try {
			UserProfile profile = ProfileManagerImpl.getInstance().getProfile(uid, null);
			if (profile == null) return original;
			List <String> placeIds = new LinkedList <String>();
			List <Double> socialScores = new LinkedList <Double>();
			ProfileManagerImpl.getInstance().findPlaceIdsAndSocialScoreForFriends(
					profile, MIN_SCORE, placeIds, socialScores);
			if (placeIds == null || placeIds.size() == 0) return original;
			return addOrderBysToQuery(original, placeIds, socialScores, coef);
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
	private String addOrderBysToQuery(String original, List<String> placeIds,
			List <Double> socialScores, double coef) throws InvalidSparqlQuery {
		String queryWithPrefixes = allPrefixes == null ? Configuration.PREFIXES + " "+ original
				: allPrefixes + " " + Configuration.PREFIXES + " " + original;
		try {
			Query query = QueryFactory.create(queryWithPrefixes);
			Expr expr = createExpr(placeIds);
			ElementBind elementBind = createElementBind(placeIds, socialScores);
			augmentQuery(query, expr, elementBind, coef);
			query.setPrefixMapping(null); // remove all prefixes
			query.setQueryPattern(elementBind);
			return query.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidSparqlQuery(e.getMessage());
		}
	}

	private void augmentQuery(Query query, Expr expr, ElementBind elementBind, double coef) {
		// for queries having COUNT, GROUP BY, they should not be augmented
		if (!query.hasAggregators() && !query.hasGroupBy()) {
			if (query.getOrderBy() == null || query.getOrderBy().size() == 0) {
				query.addOrderBy(expr, DESC);
			} else {
				if (query.getOrderBy().size() == 1) {
					SortCondition sc = query.getOrderBy().get(0);
					Expr editorialExpr = sc.getExpression();
					query.getOrderBy().clear();
					if (DEBUG_MOD) LOGGER.info("parsing the expression: " + editorialExpr.toString() + coef + " * ?" + SOCIAL_SCORE_VAR_NAME);
					Expr newExpr = ExprUtils.parse(editorialExpr.toString() + " + " + coef + " * ?" + SOCIAL_SCORE_VAR_NAME);
					query.addOrderBy(newExpr, DESC);
				}
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
					augmentQuery(esq.getQuery(), expr, elementBind, coef);
				}
			}
		}
		
	}
	
	private ElementBind createElementBind(List<String> placeIds,
			List<Double> socialScores) {
		StringBuilder sb = new StringBuilder();
		int lastIndex = placeIds.size() - 1;
		for (int i = 0; i <= lastIndex; i++) {
			String placeId = placeIds.get(i);
			double socialScore = socialScores.get(i);
			if (sb.length() > 0) sb.append(", ");
			sb.append("if (?venue = <" + placeId + ">, " + socialScore);
			if (i == lastIndex) {
				sb.append(", 0");
				for (int j = 0; j <= lastIndex; j++) {
					sb.append(")");
				}
			}
		}
		ElementBind eb = new ElementBind(Var.alloc(SOCIAL_SCORE_VAR_NAME), ExprUtils.parse(sb.toString()));
		return eb;
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
