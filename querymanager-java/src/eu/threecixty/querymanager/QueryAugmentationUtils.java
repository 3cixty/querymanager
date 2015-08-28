package eu.threecixty.querymanager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.util.ExprUtils;

import eu.threecixty.Configuration;
import eu.threecixty.profile.ProfileManagerImpl;
import eu.threecixty.profile.SparqlEndPointUtils;
import eu.threecixty.profile.TooManyConnections;
import eu.threecixty.profile.UserProfile;

public class QueryAugmentationUtils {
	
	private static final String FORMAT = "application/sparql-results+json";
	private static final String SOCIAL_SCORE_VAR_NAME = "socialScore";
	
	private static final Logger LOGGER = Logger.getLogger(
			QueryAugmentationUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	 
	 private static final int DESC = - 1;
	 private static final float MIN_SCORE = 3.0f;
	
	public static String allPrefixes;

	public static String augmentAndExecuteQuery(String original, QueryAugmenterFilter filter,
			String uid, double coef, String httpMethod) throws IOException {
		try {
			List <Query> queries = new LinkedList <Query>();
			UserProfile profile = ProfileManagerImpl.getInstance().getProfile(uid, null);
			if (profile != null) {
				if (filter == QueryAugmenterFilter.FriendsRating) {
					createAugmentedQueriesBasedOnFriends(original, profile, coef, queries);
				} else if (filter == QueryAugmenterFilter.MyRating) {
					createAugmentedQueryBasedOnMyRating(original, profile, coef, queries);
				}
				if (queries.size() == 2) { // one query augmented
					Query augmentedQuery = queries.get(0);
					Query originalQuery = queries.get(1);
					if (originalQuery.hasLimit()) {
						return executeQueries(augmentedQuery, originalQuery, httpMethod, coef);
					}
				}
			}
		} catch (TooManyConnections e) {
			e.printStackTrace();
		} catch (InvalidSparqlQuery e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(original, FORMAT, httpMethod, sb);
		return sb.toString();
	}
	
	
	private static String executeQueries(Query augmentedQuery,
			Query originalQuery, String httpMethod, double coef) throws IOException {
		if (DEBUG_MOD) LOGGER.info("query 1: " + augmentedQuery.toString());
		if (DEBUG_MOD) LOGGER.info("query 2: " + originalQuery.toString());
		int limit = (int) originalQuery.getLimit();
		StringBuilder sbForAug = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(augmentedQuery.toString(), FORMAT, httpMethod, sbForAug);
		JSONObject rootJsonAug = new JSONObject(sbForAug.toString());
		JSONArray jsonArrsAug = rootJsonAug.getJSONObject("results").getJSONArray("bindings");
		if (jsonArrsAug.length() == limit) {
			return sbForAug.toString();
		}
		StringBuilder sbForOri = new StringBuilder();
		SparqlEndPointUtils.executeQueryViaSPARQL(originalQuery.toString(), FORMAT, httpMethod, sbForOri);
		JSONObject rootJsonOri = new JSONObject(sbForOri.toString());
		JSONArray jsonArrsOri = rootJsonOri.getJSONObject("results").getJSONArray("bindings");
		
		// new JSON array contains final list of bindings which are in order of editorial + coef * socialScore
		JSONArray newJsonArr = new JSONArray();
		
		findFirstNItems(limit, jsonArrsAug, jsonArrsOri, coef, newJsonArr);
		
		rootJsonAug.getJSONObject("results").remove("bindings");
		rootJsonAug.getJSONObject("results").put("bindings", newJsonArr);
		return rootJsonAug.toString();
	}


	private static void findFirstNItems(int n, JSONArray jsonArrsAug,
			JSONArray jsonArrsOri, double coef, JSONArray jsonArrResult) {
		int index1 = 0;
		int index2 = 0;
		int len1 = jsonArrsAug.length();
		int len2 = jsonArrsOri.length();
		for (int i = 0; i < n; i++) {
			JSONObject json1 = index1 < len1 ? jsonArrsAug.getJSONObject(index1) : null;
			JSONObject json2 = index2 < len2 ? jsonArrsOri.getJSONObject(index2) : null;
			if (json1 == null && json2 == null) break;
			if (json1 == null) {
				jsonArrResult.put(i, json2);
				index2++;
			} else if (json2 == null) {
				jsonArrResult.put(i, json1);
				index1++;
			} else { // both are not null
				double d1 = getTotalScore(json1, coef);
				double d2 = getTotalScore(json2, coef);
				if (d1 >= d2) {
					jsonArrResult.put(i, json1);
					index1++;
				} else {
					jsonArrResult.put(i, json2);
					index2++;
				}
			}
		}
	}


	private static double getTotalScore(JSONObject json, double coef) {
		JSONObject scoreObj = json.getJSONObject("score");
		double val1 = scoreObj.getDouble("value");
		JSONObject socialScoreObj = json.getJSONObject("socialScore");
		double val2 = socialScoreObj.getDouble("value");
		return val1 + coef * val2;
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
	private static void createAugmentedQueryBasedOnMyRating(String original,
			UserProfile profile, double coef, List <Query> queries) throws InvalidSparqlQuery {
		if (original == null) return;
		List <String> placeIds = new LinkedList <String>();
		List <Double> socialScores = new LinkedList <Double>();
		ProfileManagerImpl.getInstance().findPlaceIdsAndSocialScore(
				profile, MIN_SCORE, placeIds, socialScores);

		createAugmentedQueries(original, placeIds, socialScores, coef, queries);
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
	private static void createAugmentedQueriesBasedOnFriends(String original,
			UserProfile profile, double coef, List <Query> queries) throws InvalidSparqlQuery {
		if (original == null) return;
		List <String> placeIds = new LinkedList <String>();
		List <Double> socialScores = new LinkedList <Double>();
		ProfileManagerImpl.getInstance().findPlaceIdsAndSocialScoreForFriends(
				profile, MIN_SCORE, placeIds, socialScores);
		createAugmentedQueries(original, placeIds, socialScores, coef, queries);
	}
	
	private static void createAugmentedQueries(String original, List<String> placeIds,
			List <Double> socialScores, double coef, List <Query> queries) throws InvalidSparqlQuery {
		String queryWithPrefixes = allPrefixes == null ? Configuration.PREFIXES + " "+ original
				: allPrefixes + " " + Configuration.PREFIXES + " " + original;
		try {
			Query query = QueryFactory.create(queryWithPrefixes);
			
			Query originalQuery = query.cloneQuery();
			originalQuery.setPrefixMapping(null);
			
			if (query.hasAggregators() || query.hasGroupBy()) {
				queries.add(originalQuery);
				return;
			}
			if (placeIds != null && placeIds.size() > 0) {
				Expr expr = createExpr(placeIds);
				ElementBind elementBind = createElementBind(placeIds, socialScores);
				augmentQuery(query, expr, elementBind, coef);
				query.setPrefixMapping(null); // remove all prefixes
				queries.add(query);
			}
			
			ElementBind elementBind2 = createElementBindForConstant(0);
			augmentQuery(originalQuery, null, elementBind2, coef);
			queries.add(originalQuery);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidSparqlQuery(e.getMessage());
		}
	}
	
	private static void augmentQuery(Query query, Expr expr, ElementBind elementBind, double coef) {
		if (query.getOrderBy() != null && query.getOrderBy().size() == 1) {
			SortCondition sc = query.getOrderBy().get(0);
			Expr editorialExpr = sc.getExpression();
			query.getOrderBy().clear();
			if (DEBUG_MOD) LOGGER.info("parsing the expression: " + editorialExpr.toString() + coef + " * ?" + SOCIAL_SCORE_VAR_NAME);
			Expr newExpr = ExprUtils.parse(editorialExpr.toString() + " + " + coef + " * ?" + SOCIAL_SCORE_VAR_NAME);
			query.addOrderBy(newExpr, DESC);
		} else if (query.getOrderBy() == null || query.getOrderBy().size() == 0) {
			if (expr != null) {
			    //query.addOrderBy(expr, 0);
				ElementFilter ef = new ElementFilter(expr);
				((ElementGroup) query.getQueryPattern()).addElementFilter(ef);
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
			eg.addElement(elementBind);
			query.addResultVar(SOCIAL_SCORE_VAR_NAME);
		}
	}
	
	private static ElementBind createElementBind(List<String> placeIds,
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
		ElementBind eb = new ElementBind(Var.alloc(SOCIAL_SCORE_VAR_NAME),
				ExprUtils.parse(sb.toString()));
		return eb;
	}
	
	private static ElementBind createElementBindForConstant(int constant) {
		ElementBind eb = new ElementBind(Var.alloc(SOCIAL_SCORE_VAR_NAME),
				NodeValue.makeInteger(0));
		return eb;
	}
	
	private static Expr createExpr(List<String> placeIds) {
		StringBuilder sb = new StringBuilder();
		for (String placeId: placeIds) {
			if (sb.length() > 0) sb.append(" || ");
			sb.append("?venue = <" + placeId + ">");
		}
		return ExprUtils.parse(sb.toString());
	}
	
	private QueryAugmentationUtils() {
	}
}
