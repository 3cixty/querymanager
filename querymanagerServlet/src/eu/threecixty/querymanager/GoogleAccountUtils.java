package eu.threecixty.querymanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


import eu.threecixty.models.MyFactory;
import eu.threecixty.models.Name;
import eu.threecixty.models.UserProfile;
import eu.threecixty.profile.RdfFileManager;

/**
 * Utility class to update account info.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class GoogleAccountUtils {

	/**
	 * Extract Google info from a given accessToken to update a given RDF model.
	 * @param accessToken
	 * @param model
	 */
	public static void updateInfo(String accessToken) {
		if (accessToken == null) return;
		try {
			String reqMsg = readUrl(
					"https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken);
			JSONObject json = new JSONObject(reqMsg);
			String user_id = json.getString("id");
			String givenName = json.getString("given_name");
			String familyName = json.getString("family_name");
			
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(RdfFileManager.getInstance().getPathToRdfFile());
            IRI iri= IRI.create(file);
           
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);
           
            MyFactory mf = new MyFactory(ontology);

            UserProfile userProfile = mf.createUserProfile(user_id);
            userProfile.addHasUID(user_id);
            Name name = mf.createName(user_id + familyName);
            name.addFamily_name(familyName);
            name.addGiven_name(givenName);
            userProfile.addHas_name(name);
            mf.saveOwlOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets content from a given URL string.
	 *
	 * @param urlString
	 * @return
	 * @throws Exception
	 */
	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	private GoogleAccountUtils() {
	}
}
