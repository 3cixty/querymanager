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
	
	private static final String PARAM_EXCEPTION = "There is an error for parameters";
	private static final String ACCESS_TOKEN_EXCEPTION = "Access token is incorrect or expired";

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
	    resp.setContentType("text/plain");
	    IProfiler profiler = null;
		String userkey = req.getParameter(ACCESS_TOKEN_PARAM);
		boolean isUsingPreferences = "true".equalsIgnoreCase(req.getParameter(IS_USING_PREFS_PARAM));
		String format = req.getParameter(FORMAT_PARAM);
		String query = req.getParameter(QUERY_PARAM);
    	
		String user_id =  TokenVerifier.getInstance().getUserId(userkey); // which corresponds with Google user_id (from Google account)
		if ((user_id == null || user_id.equals("")) && (!"false".equals(userkey))) {
			out.write(ACCESS_TOKEN_EXCEPTION);
		} else {
			boolean isAccessTokenFalse = "false".equals(userkey);
			profiler = isAccessTokenFalse ? null : new Profiler(user_id);
			QueryManager qm = isAccessTokenFalse ? new QueryManager("false") : new QueryManager(user_id);
			EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);

			if (eventMediaFormat != null && query != null) {
				String result = executeQuery(profiler, qm, isAccessTokenFalse ? false : isUsingPreferences,
						eventMediaFormat, query);
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
			boolean isUsingPreferences, EventMediaFormat eventMediaFormat, String query) {

		// TODO: set to RDF model
		//qm.setModelFromFileOrUri(filenameOrURI);
		if (allPrefixes == null) {
			allPrefixes = getAllPrefixes() + " ";
		}

		Query jenaQuery = qm.createJenaQuery(allPrefixes + query);

		// take preferences into account to augment queries (only fade place preferences are available)
		qm.requestPreferences(profiler);

		// TODO: correct the following line by exactly recognizing query's type
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new PlaceQuery(jenaQuery);

		qm.setQuery(placeQuery);

		// perform query augmentation when necessary
		// TODO: remove the following line to augment a query (now Events database seems to only contain event's links. Question: how to do with event's name, ... )
		//isUsingPreferences = false;
		if (isUsingPreferences) {
			qm.performAugmentingTask();
		}

		String result = qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), eventMediaFormat);
		
		return result;
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
