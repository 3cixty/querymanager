package eu.threecixty.querymanager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.hp.hpl.jena.graph.Triple;
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
		
		List <Triple> triples = new ArrayList <Triple>();
		List <Expr> exprs = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		Period period = createPeriod(); // two days
		profiler.requirePeriod(period);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requireAreaWithin(2); // within 2 km
		findTriplesAndExprs(profiler, qm, triples, exprs);

		profiler.initDefaultParametersForAugmentation();
		profiler.requireCurrentTown(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requireCurrentCountry(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);
		
		QueryUtils.removeDoubleExpressions(exprs);

		if (exprs.size() == 0) {
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
		} else if (exprs.size() == 1) {
			qm.performORAugmentation(triples, exprs);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
		} else {
			qm.performORAugmentation(triples, exprs);
			return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
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
		profiler.requireScoreRatedAtLeast(PreferencesUtils.getMinimumScoreRated(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs);
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requireNumberOfTimesVisitedAtLeast(PreferencesUtils.getMinimumNumberOfTimesVisited(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs);

		QueryUtils.removeDoubleExpressions(exprs);

		qm.performORAugmentation(triples, exprs);
		return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
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
		List <Triple> triples = new ArrayList <Triple>();
		List <Expr> exprs = new ArrayList<Expr>();
		profiler.initDefaultParametersForAugmentation();
		profiler.requireScoreRatedForFriendsAtLeast(
				PreferencesUtils.getMinimumScoreRatedForFriends(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs);

		profiler.initDefaultParametersForAugmentation();
		profiler.requireNumberOfTimesVisitedForFriendsAtLeast(
				PreferencesUtils.getMinimumNumberOfTimesVisitedForFriends(profiler));
		findTriplesAndExprs(profiler, qm, triples, exprs);

		profiler.initDefaultParametersForAugmentation();
		profiler.requireFriendsLikeVisit(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);

		QueryUtils.removeDoubleExpressions(exprs);

		qm.performORAugmentation(triples, exprs);
		return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
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
		
		profiler.initDefaultParametersForAugmentation();
		profiler.requirePreferredEventDates(true);
		findTriplesAndExprs(profiler, qm, triples, exprs);

		QueryUtils.removeDoubleExpressions(exprs);

		qm.performORAugmentation(triples, exprs);
		return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), format);
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
