package eu.threecixty.querymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.query.Query;

import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;

@WebServlet("InitServlet")
public class QueryManagerServlet extends HttpServlet {

	private static final String ACCESS_TOKEN_PARAM = "accessToken";
	private static final String IS_USING_PREFS_PARAM = "isUsingPreferences";
	private static final String FORMAT_PARAM = "format";
	private static final String QUERY_PARAM = "query";

	private static final String FILTER_PARAM = "filter";
	private static final String FRIENDS_PARAM = "friends";
	
	// FILTER Options
	private static final String LOCATION = "location";
	private static final String ENTERED_RATING = "enteredrating";
	private static final String GPS_LOCATION = "gpslocation";
	
	
	
	private static final String PARAM_EXCEPTION = "There is an error for parameters";

	private static String allPrefixes = null;
	
	public static String realPath;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    realPath = this.getServletContext().getRealPath("/");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    PrintWriter out = resp.getWriter();
	    IProfiler profiler = null;
		String userkey = req.getParameter(ACCESS_TOKEN_PARAM);
		boolean isUsingPreferences = "true".equalsIgnoreCase(req.getParameter(IS_USING_PREFS_PARAM));
		String format = req.getParameter(FORMAT_PARAM);
		String query = req.getParameter(QUERY_PARAM);
		boolean isAccessTokenFalse = "false".equals(userkey);
		String user_id =  null;
		if (!isAccessTokenFalse) {
			user_id = TokenVerifier.getInstance().getUserId(userkey); // which corresponds with Google user_id (from Google account)
		}
		if ((user_id == null || user_id.equals("")) && (!isAccessTokenFalse)) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.write("Access token is incorrect or expired");
		} else {
			resp.setContentType("text/plain");
			
			profiler = isAccessTokenFalse ? null : new Profiler(user_id);
			QueryManager qm = isAccessTokenFalse ? new QueryManager("false") : new QueryManager(user_id);
			EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);

			if (eventMediaFormat != null && query != null) {
				String result = executeQuery(profiler, qm, isAccessTokenFalse ? false : isUsingPreferences,
						eventMediaFormat, query, req);
				out.write(result);
			} else {
				out.write(PARAM_EXCEPTION);
			}
		}
		
		out.close();
	}

	/**
	 * Executes the query.
	 *
	 * @param profiler
	 * 			user profile
	 * @param qm
	 * 			query manager
	 * @param isUsingPreferences
	 * 			which indicates that preferences are whether or not used
	 * @param eventMediaFormat
	 * 			which indicates a desired result format
	 * @param query
	 * 			Which is a query to be executed
	 * @return
	 */
	private String executeQuery(IProfiler profiler, IQueryManager qm,
			boolean isUsingPreferences, EventMediaFormat eventMediaFormat, String query, HttpServletRequest req) {

		if (allPrefixes == null) {
			allPrefixes = getAllPrefixes() + " ";
		}

		Query jenaQuery = qm.createJenaQuery(allPrefixes + query);

		// populate user preferences from user profile
		if ((profiler != null) && isUsingPreferences) {
			setInfoRequirementsFromProfiler(req, profiler);
		    profiler.PopulateProfile();
		}
		
		// take preferences into account to augment queries (only fade place preferences are available)
		qm.requestPreferences(profiler);

		// TODO: correct the following line by exactly recognizing query's type
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new PlaceQuery(jenaQuery);

		qm.setQuery(placeQuery);

		// perform query augmentation when necessary
		if (isUsingPreferences) {
			qm.performAugmentingTask();
		}

		String result = qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), eventMediaFormat);
		
		return result;
	}

	/**
	 * Requires information to populate from UserProfile.
	 * @param req
	 * @param profiler
	 */
    private void setInfoRequirementsFromProfiler(HttpServletRequest req,  IProfiler profiler) {
    	try {
    		String friends = req.getParameter(FRIENDS_PARAM);
    		if (friends == null || "false".equalsIgnoreCase(friends)) { // prefs based on "I"
        		String filter = req.getParameter(FILTER_PARAM);
        		if (filter == null) return;
    			if (filter.equalsIgnoreCase(LOCATION)) {
    				profiler.requireCurrentCountry(true);
    				profiler.requireCurrentTown(true);
    			} else if (filter.equalsIgnoreCase(ENTERED_RATING)) {
    				// TODO: fixed value
    				// should find minimum value from PreferredProfile
    				profiler.requireScoreRatedAtLeast(5);
    				profiler.requireNumberOfTimesVisitedAtLeast(3);
    			} else if (filter.equalsIgnoreCase(GPS_LOCATION)) {
    				// TODO: fixed value
    				profiler.requireAreaWithin(10);
    			}
    		} else { // based on "my friends"
    			profiler.requireNumberOfTimesVisitedForFriendsAtLeast(2); // value rated in UserProfile
    			profiler.requireScoreRatedForFriendsAtLeast(4);
    		}
    	} catch (Exception e) {
    	}
	}

	/**
     * To validate the sparql query, we need prefixes. These prefixes are same as those used by EventMedia.
     * @return string
     */
    private String getAllPrefixes() {
		String rootPath = getRealRootPath();
		try {
			InputStream inStream = new FileInputStream(rootPath + File.separatorChar 
					+ "WEB-INF" + File.separatorChar + "prefix.properties");
			StringBuilder sb = new StringBuilder();
			Properties props = new Properties();
			props.load(inStream);
			inStream.close();
			for (java.util.Map.Entry<Object, Object> entry: props.entrySet()) {
				sb.append("PREFIX " + entry.getKey() + ":\t");
				sb.append('<');
				sb.append(entry.getValue());
				sb.append(">\n");
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return "";
    }

    private String getRealRootPath() {
    	return realPath;
    }
}
