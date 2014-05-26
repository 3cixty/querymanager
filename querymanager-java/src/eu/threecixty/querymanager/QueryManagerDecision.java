package eu.threecixty.querymanager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.expr.E_LogicalNot;
import com.hp.hpl.jena.sparql.expr.Expr;

import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.PreferencesUtils;
import eu.threecixty.profile.oldmodels.Period;

/**
 * The class is used to make decision about query augmentation.
 * @author Cong-Kinh NGUYEN
 *
 */
public class QueryManagerDecision {

	private static final String LOCATION = "location";
	private static final String ENTERED_RATING = "enteredrating";
	private static final String PREFERRED = "preferred";

	/**
	 * Augments and executes the query found in a given query manager.
	 * @param profiler
	 * @param qm
	 * @param filter
	 * @param format
	 * @return
	 */
	public static String run(IProfiler profiler, IQueryManager qm, String filter,
			boolean basedOnFriends, EventMediaFormat format) {
		if (filter == null) return "";
		
		if (!basedOnFriends) {
			if (filter.equalsIgnoreCase(LOCATION)) {
				return filterBasedOnLocation(profiler, qm, format);
			} else if (filter.equalsIgnoreCase(ENTERED_RATING)) {
				return filterBasedOnEnteredRating(profiler, qm, format);
			} else if (filter.equalsIgnoreCase(PREFERRED)) {
				return filterBasedOnPreferredEvent(profiler, qm, format);
			}
		} else {
			return filterBasedOnFriends(profiler, qm, format);
		}
		
		return "";
	}

	/**
	 * Executes the query found in a given query manager based on location.
	 * <br><br>
	 * The query is contained inside of the given query manager. The method takes UserProfile
	 * from a given profiler to populate preferences based on location, then augment the query
	 * based on the populated preferences. After that, the method sends the augmented queries
	 * to EventMedia to get what users want.
	 * <br><br>
	 * The preferences based on location contain information about four elements: events which
	 * will take place within two days, events which are found within two kilometers from the current GPS
	 * coordinates, events which take place in the current town and current country.
	 * <br><br>
	 * The principle of the query augmentation based on location follows by the suppose: each 
	 * element mentioned creates a set of result. So such four elements create four sets:
	 * A, B, C, D (A <= B <= C <= D). The ranking of the output should take into account of all
	 * the elements. So, the very first output elements should be resulted by an expression of AND
	 * for all subsets. The second output elements should be resulted by an expression representing
	 * (!A & B & C & D) and so on.
	 * @param profiler
	 * @param qm
	 * @param format
	 * @return
	 */
	private static String filterBasedOnLocation(IProfiler profiler, IQueryManager qm,
			EventMediaFormat format) {
		List <Triple> triples1 = new ArrayList <Triple>();
		List <Expr> exprs = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		Period period = createPeriod(); // two days
		profiler.requirePeriod(period);
		findTriplesAndExprs(profiler, qm, triples1, exprs);
		Expr daysExpr = createExpr(exprs);
		
		List <Triple> triples2 = new ArrayList <Triple>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireAreaWithin(2); // within 2 km
		findTriplesAndExprs(profiler, qm, triples2, exprs);
		Expr areaExpr = createExpr(exprs);

		List <Triple> triples3 = new ArrayList <Triple>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireCurrentTown(true);
		findTriplesAndExprs(profiler, qm, triples3, exprs);
		Expr townExpr = createExpr(exprs);
		
		List <Triple> triples4 = new ArrayList <Triple>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireCurrentCountry(true);
		findTriplesAndExprs(profiler, qm, triples4, exprs);
		Expr countryExpr = createExpr(exprs);
		
		List <Triple> allTriples = new ArrayList <Triple>();
		List <List<Triple>> triplesCollection = new ArrayList <List<Triple>>();
		
		if (daysExpr != null) {
			triplesCollection.add(triples1);
			allTriples.addAll(triples1);
			exprs.add(daysExpr);
		}
		if (areaExpr != null) {
			triplesCollection.add(triples2);
			allTriples.addAll(triples2);
			exprs.add(areaExpr);
		}
		if (townExpr != null) {
			triplesCollection.add(triples3);
			allTriples.addAll(triples3);
			exprs.add(townExpr);
		}
		if (countryExpr != null) {
			triplesCollection.add(triples4);
			allTriples.addAll(triples4);
			exprs.add(countryExpr);
		}
		
		QueryUtils.removeDoubleExpressions(exprs);

		if (exprs.size() == 0) {
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
		} else if (exprs.size() == 1) {
			qm.performANDAugmentation(allTriples, exprs);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
		} else {
			if (qm.getAugmentedQuery().getQuery().getQuery().hasAggregators()) {
				return executeQueryAugmentationForCounting(qm, format, triplesCollection, exprs);
			} else {
				long limit = qm.getQuery().getQuery().getLimit();
				JSONArray triplesResults = new JSONArray();
				String aRespMsg = null;
				List <String> augQueriesResponse = new ArrayList <String>();
				for (int i = 0; i < exprs.size() - 1; i++) {
					qm.performANDAugmentation(allTriples, exprs);
					aRespMsg = qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, false);
					findTriplesResult(aRespMsg, limit, triplesResults);
					String response = qm.getAugmentedQueryWithoutPrefixes();
					augQueriesResponse.add(response);
					if (triplesResults.length() >= limit && limit != -1) return createRespFromJSONArray(triplesResults, aRespMsg, augQueriesResponse);
					Expr notExpr = new E_LogicalNot(exprs.get(i));
					exprs.set(i, notExpr);
				}
				if (aRespMsg == null) return "";
				return createRespFromJSONArray(triplesResults, aRespMsg, augQueriesResponse);
			}
		}
	}

	/**
	 * Executes the query (at EventMedia) based on entered rating information.
	 * @param profiler
	 * @param qm
	 * @param format
	 * @return
	 */
	private static String filterBasedOnEnteredRating(IProfiler profiler, IQueryManager qm,
			EventMediaFormat format) {
		// TODO: find minimum values from preferences
		List <Triple> triples = new ArrayList <Triple>();
		List <Expr> exprs1 = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireScoreRatedAtLeast(PreferencesUtils.getMinimumScoreRated(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs1);

		
		List <Expr> exprs2 = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireNumberOfTimesVisitedAtLeast(PreferencesUtils.getMinimumNumberOfTimesVisited(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs2);

		if (qm.getAugmentedQuery().getQuery().getQuery().hasAggregators()) {
			exprs1.addAll(exprs2);
			qm.performORAugmentation(triples, exprs1);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
		} else {
		
			for (Expr tmpExpr: exprs1) {
				if (exprs2.contains(tmpExpr)) exprs2.remove(tmpExpr);
			}
			QueryUtils.removeDoubleExpressions(exprs1);
			QueryUtils.removeDoubleExpressions(exprs2);

			Expr scoreExpr = createExpr(exprs1);
			Expr visitedExpr = createExpr(exprs2);

			return filterBasedOnTwoExprs(scoreExpr, visitedExpr, qm, triples, format);
		}
	}

	/**
	 * Executes the query (at EventMedia) based on Friends' entered ratings.
	 * @param profiler
	 * @param qm
	 * @param format
	 * @return
	 */
	private static String filterBasedOnFriends(IProfiler profiler, IQueryManager qm,
			EventMediaFormat format) {
		// TODO: find minimum values from preferences
		List <Triple> triples = new ArrayList <Triple>();
		List <Expr> exprs1 = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireScoreRatedForFriendsAtLeast(
				PreferencesUtils.getMinimumScoreRatedForFriends(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs1);

		
		List <Expr> exprs2 = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireNumberOfTimesVisitedForFriendsAtLeast(
				PreferencesUtils.getMinimumNumberOfTimesVisitedForFriends(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs2);

		if (qm.getAugmentedQuery().getQuery().getQuery().hasAggregators()) {
			exprs1.addAll(exprs2);
			qm.performORAugmentation(triples, exprs1);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
		} else {
		
			for (Expr tmpExpr: exprs1) {
				if (exprs2.contains(tmpExpr)) exprs2.remove(tmpExpr);
			}
			QueryUtils.removeDoubleExpressions(exprs1);
			QueryUtils.removeDoubleExpressions(exprs2);

			Expr scoreExpr = createExpr(exprs1);
			Expr visitedExpr = createExpr(exprs2);

			return filterBasedOnTwoExprs(scoreExpr, visitedExpr, qm, triples, format);
		}
	}

	/**
	 * Executes the query (at EventMedia) based on preferred events info.
	 * @param profiler
	 * @param qm
	 * @param format
	 * @return
	 */
	private static String filterBasedOnPreferredEvent(IProfiler profiler, IQueryManager qm,
			EventMediaFormat format) {
		// TODO
		List <Triple> triples = new ArrayList <Triple>();
		List <Expr> exprs = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireEventName(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr eventNameExpr = createExpr(exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requirePreferredEventDates(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr preferredDates = createExpr(exprs);

		if (qm.getAugmentedQuery().getQuery().getQuery().hasAggregators()) {
			List <Expr> exprs1 = new ArrayList<Expr>();
			if (eventNameExpr != null) exprs1.add(eventNameExpr);
			if (preferredDates != null) exprs1.add(preferredDates);
			qm.performORAugmentation(triples, exprs1);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
		} else {
		
		    return filterBasedOnTwoExprs(eventNameExpr, preferredDates, qm, triples, format);
		}
	}

	/**
	 * Executes the query (at EventMedia) based on two expressions.
	 * @param expr1
	 * @param expr2
	 * @param qm
	 * @param triples
	 * @param format
	 * @return
	 */
	private static String filterBasedOnTwoExprs(Expr expr1, Expr expr2, IQueryManager qm, List <Triple> triples,
			EventMediaFormat format) {
		List <Expr> exprs = new ArrayList <Expr>();
		if (expr1 != null) exprs.add(expr1);
		if (expr2 != null) exprs.add(expr2);

		QueryUtils.removeDoubleExpressions(exprs);

		if (exprs.size() == 0) {
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
		} else if (exprs.size() == 1) {
			qm.performANDAugmentation(triples, exprs);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
		} else {
			List <String> augmentedQueries = new ArrayList <String>();
			long limit = qm.getQuery().getQuery().getLimit();
			JSONArray triplesResults = new JSONArray();
			
			qm.performANDAugmentation(triples, exprs);
			String result1 = qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, false);
			findTriplesResult(result1, limit, triplesResults);
			augmentedQueries.add(qm.getAugmentedQueryWithoutPrefixes());
			if (triplesResults.length() >= limit && limit != -1) return createRespFromJSONArray(triplesResults, result1, augmentedQueries);
			
			exprs.set(0, new E_LogicalNot(expr1)); // (not first one) and second one 
			qm.performANDAugmentation(triples, exprs);
			String result2 = qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, false);
			findTriplesResult(result2, limit, triplesResults);
			augmentedQueries.add(qm.getAugmentedQueryWithoutPrefixes());
			if (triplesResults.length() >= limit && limit != -1) return createRespFromJSONArray(triplesResults, result2, augmentedQueries);

			exprs.set(0, expr1);
			exprs.set(1, new E_LogicalNot(expr2)); // first one and (not second one)
			qm.performANDAugmentation(triples, exprs);
			String result3 = qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, false);
			findTriplesResult(result3, limit, triplesResults);
			augmentedQueries.add(qm.getAugmentedQueryWithoutPrefixes());
			if (triplesResults.length() >= limit && limit != -1) return createRespFromJSONArray(triplesResults, result3, augmentedQueries);
			
			return createRespFromJSONArray(triplesResults, result1, augmentedQueries);
		}
	}

	/**
	 * Finds triples and expressions from preferences in the UserProfile.
	 * @param profiler
	 * @param qm
	 * @param triples
	 * @param exprs
	 */
	private static void findTriplesAndExprs(IProfiler profiler, IQueryManager qm,
			List <Triple> triples, List <Expr> exprs) {
		profiler.PopulateProfile();
		qm.requestPreferences(profiler);
		qm.addTriplesAndExprsToLists(triples, exprs);
	}

	/**
	 * Creates OR expression for a given list of expressions.
	 * @param exprs
	 * @return
	 */
	private static Expr createExpr(List <Expr> exprs) {
		Expr tmpExpr = QueryUtils.createExprWithOrOperandForExprs(exprs);
		exprs.clear();
		return tmpExpr;
	}

	/**
	 * Creates result from a list of subsets' result.
	 * @param resultsOfQueries
	 * @param limit
	 * @return
	 */
	private static String createRespFromJSONArray(JSONArray triplesResults, String aRespMsg,
			List <String> augmentedQueriesResponse) {	
		if (triplesResults.length() == 0) return aRespMsg;
		StringBuffer buffer = new StringBuffer();
		String bindingStr = "\"bindings\": ";
		int bindingsIndex = aRespMsg.indexOf(bindingStr);
		if (bindingsIndex < 0) return "";
		buffer.append(aRespMsg.substring(0, bindingsIndex));
		buffer.append(bindingStr);
		buffer.append(triplesResults.toString());
		buffer.append('}');
		buffer.append(',');
		JSONArray augmentedQueriesArr = new JSONArray();
		for (String augQuery: augmentedQueriesResponse) {
		JSONObject jsonObj = new JSONObject();
		    jsonObj.put("AugmentedQuery", augQuery);
		    augmentedQueriesArr.put(jsonObj);
		}
		buffer.append("\"AugmentedQueries\": ").append(augmentedQueriesArr.toString());
		buffer.append('}');
		return buffer.toString();
	}

	private static void findTriplesResult(String aRespMsg, long limit, JSONArray triplesResults) {
		JSONObject obj = new JSONObject(aRespMsg);
		JSONObject objResults = obj.getJSONObject("results");
		JSONArray arrBindings = objResults.getJSONArray("bindings");
		for (int i = 0; i < arrBindings.length(); i++) {
			if (triplesResults.length() >= limit && limit != -1) {
				return;
			}
			triplesResults.put(arrBindings.getJSONObject(i));
		}
		if (triplesResults.length() >= limit && limit != -1) {
			return;
		}
	}

	/**
	 * Augments and executes a query (at EventMedia).
	 * @param qm
	 * @param format
	 * @param triples
	 * @param exprs
	 * @return
	 */
	private static String executeQueryAugmentationForCounting(IQueryManager qm,
			EventMediaFormat format, List <List<Triple>> triples, List <Expr> exprs) {
		// only keep the last element which is the largest subset
		Expr lastEl = exprs.get(exprs.size() - 1);
		List <Triple> lastTriples = triples.get(exprs.size() - 1);
		exprs.clear();
		exprs.add(lastEl);
		qm.performANDAugmentation(lastTriples, exprs);
		return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format, true);
	}

	/**
	 * Creates a period of two days from now
	 * @return
	 */
	private static Period createPeriod() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);
		Date startDate = calendar.getTime();
		Date endDate = new Date(startDate.getTime() + 3 * 24 * 60 * 60 * 1000L - 2 * 1000);
		return new Period(startDate, endDate); // for 2 days
	}

	/**
	 * Prohibits instantiations.
	 */
	private QueryManagerDecision() {
	}
}
