package eu.threecixty.querymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hp.hpl.jena.query.Query;

import eu.threecixty.profile.IProfiler;
import eu.threecixty.profile.Profiler;

/**
 * This class provides API to connect to QueryManager.
 * 
 * @author Cong-Kinh Nguyen
 *
 */
@Path("/")
public class QueryManagerService {

	/**
	 * 
	 * @param userkey
	 * 			User key to execute a remote query. To be decided
	 * @param isUsingPreferences
	 * 			Option which indicates whether or not QueryManager augments a given query before executing at EventMedia
	 * @param format
	 * 			result format received. There are two types of format supported: rdf or json.
	 * @param query
	 * 			a sparql query.
	 * @return a string which is in the given format.
	 */
    @GET
    @Path("/executeQuery/{userkey}/{isUsingPreferences}/{format}/{query}")
    @Produces("text/plain")
    public String executeQuery(@PathParam("userkey")String userkey,
    		@PathParam("isUsingPreferences")boolean isUsingPreferences,
    		@PathParam("format")String format, @PathParam("query")String query) {
    	if (userkey == null || format == null || query == null) return null;

    	EventMediaFormat eventMediaFormat = EventMediaFormat.parse(format);
    	if (eventMediaFormat == null) return null;
    	
		IProfiler profiler = new Profiler(userkey);
		
		IQueryManager qm = new QueryManager(userkey);
		
		// TODO: set to RDF model
		//qm.setModelFromFileOrUri(filenameOrURI);
		String rootPath = InitServlet.getRealRootPath();
		try {
			InputStream inStream = new FileInputStream(rootPath + File.separatorChar 
					+ "WEB-INF" + File.separatorChar + "data.rdf");
			qm.setModel(inStream);
			inStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Query jenaQuery = qm.createJenaQuery(query);
		
		// take preferences into account to augment queries (only fade place preferences are available)
		qm.requestPreferences(profiler);
		
		// TODO: correct the following line by exactly recognizing query's type
		// suppose that we recognize that the query is for places
		ThreeCixtyQuery placeQuery = new PlaceQuery(jenaQuery);
		
		qm.setQuery(placeQuery);
		
		// perform query augmentation when necessary
		// TODO: remove the following line to augment a query (now Events database seems to only contain event's links. Question: how to do with event's name, ... )
		isUsingPreferences = false;
		if (isUsingPreferences) {
		    qm.performAugmentingTask();
		}
    	
        return qm.askForExecutingAugmentedQueryAtEventMedia(qm.getAugmentedQuery(), eventMediaFormat);   
    }
}
