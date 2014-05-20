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
import eu.threecixty.profile.models.Period;

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
	 * Executes the query found in a given query manager.
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
		List <Triple> triples = new ArrayList <Triple>();
		List <Expr> exprs = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 1);
		Date startDate = calendar.getTime();
		Date endDate = new Date(startDate.getTime() + 3 * 24 * 60 * 60 * 1000L - 2 * 1000);
		Period period = new Period(startDate, endDate); // for 2 days
		profiler.requirePeriod(period);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr daysExpr = createExpr(exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requireAreaWithin(2); // within 2 km
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr areaExpr = createExpr(exprs);

		profiler.initDefaultParametersForAugmentation();
		profiler.requireCurrentTown(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr townExpr = createExpr(exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requireCurrentCountry(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr countryExpr = createExpr(exprs);
		
		if (daysExpr != null) exprs.add(daysExpr);
		if (areaExpr != null) exprs.add(areaExpr);
		if (townExpr != null) exprs.add(townExpr);
		if (countryExpr != null) exprs.add(countryExpr);
		
		QueryUtils.removeDoubleExpressions(exprs);

		if (exprs.size() == 0) {
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
		} else if (exprs.size() == 1) {
			qm.performANDAugmentation(triples, exprs);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
		} else {
			List <String> results = new ArrayList <String>();
			for (int i = 0; i < exprs.size() - 1; i++) {
				qm.performANDAugmentation(triples, exprs);
				results.add(qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format));
				Expr notExpr = new E_LogicalNot(exprs.get(i));
				exprs.set(i, notExpr);
			}
			return getResultWithLimitNumber(results, qm.getQuery().getQuery().getLimit());
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
		List <Expr> exprs = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireScoreRatedAtLeast(5);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr scoreExpr = createExpr(exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requireNumberOfTimesVisitedAtLeast(3);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr visitedExpr = createExpr(exprs);
		
		return filterBasedOnTwoExprs(scoreExpr, visitedExpr, qm, triples, format);
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
		List <Expr> exprs = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireScoreRatedForFriendsAtLeast(4);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr scoreExpr = createExpr(exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requireNumberOfTimesVisitedForFriendsAtLeast(2);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		Expr visitedExpr = createExpr(exprs);
		
		return filterBasedOnTwoExprs(scoreExpr, visitedExpr, qm, triples, format);
	}

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

		return filterBasedOnTwoExprs(eventNameExpr, preferredDates, qm, triples, format);
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
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
		} else if (exprs.size() == 1) {
			qm.performANDAugmentation(triples, exprs);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
		} else {
			List <String> results = new ArrayList <String>();
			
			qm.performANDAugmentation(triples, exprs);
			results.add(qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format));
			
			exprs.set(0, new E_LogicalNot(expr1)); // (not first one) and second one 
			qm.performANDAugmentation(triples, exprs);
			results.add(qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format));

			exprs.set(0, expr1);
			exprs.set(1, new E_LogicalNot(expr2)); // first one and (not second one)
			qm.performANDAugmentation(triples, exprs);
			results.add(qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format));
			
			return getResultWithLimitNumber(results, qm.getQuery().getQuery().getLimit());
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
	public static String getResultWithLimitNumber(List <String> resultsOfQueries, long limit) {	
		JSONArray rets = new JSONArray();
		for (String result: resultsOfQueries) {
			JSONObject obj = new JSONObject(result);
			JSONObject objResults = obj.getJSONObject("results");
			JSONArray arrBindings = objResults.getJSONArray("bindings");
			for (int i = 0; i < arrBindings.length(); i++) {
				if (rets.length() >= limit && limit != -1) {
					break;
				}
				rets.put(arrBindings.getJSONObject(i));
			}
			if (rets.length() >= limit && limit != -1) {
				break;
			}
		}
		if (rets.length() == 0) return resultsOfQueries.get(0);
		StringBuffer buffer = new StringBuffer();
		String bindingStr = "\"bindings\": ";
		String firstResult = resultsOfQueries.get(0);
		int bindingsIndex = firstResult.indexOf(bindingStr);
		if (bindingsIndex < 0) return "";
		buffer.append(firstResult.substring(0, bindingsIndex));
		buffer.append(bindingStr);
		buffer.append(rets.toString());
		buffer.append('}').append('}');
		return buffer.toString();
	}

	/**
	 * Prohibits instantiations.
	 */
	private QueryManagerDecision() {
	}
}
