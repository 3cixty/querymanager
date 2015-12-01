package eu.threecixty.profile.elements;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.threecixty.profile.SparqlEndPointUtils;

/**
 * 
 * Utility class to get top categories from KB.
 *
 */
public class CategoryUtils {
	
	public static List <String> getTopCategories(String endPointUrl) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT DISTINCT ?category \n");
		builder.append("WHERE {\n");
		builder.append("GRAPH <http://3cixty.com/places> {?poi a dul:Place .} \n");
		builder.append("?poi (locationOnt:businessTypeTop) ?cat . \n");
		builder.append("?cat skos:prefLabel ?category .\n");
		builder.append("}");
		
		StringBuilder result = new StringBuilder();
		
		SparqlEndPointUtils.executeQueryViaSPARQL(builder.toString(), "application/sparql-results+json", 
				SparqlEndPointUtils.HTTP_GET, endPointUrl, result);
		
		JSONObject json = new JSONObject(
				SparqlEndPointUtils.cleanResultReceivedFromVirtuoso(result.toString()));
		JSONArray jsonArrs = json.getJSONObject("results").getJSONArray("bindings");
		int len = jsonArrs.length();
		if (len < 1) return Collections.emptyList();
		
		List <String> rets = new LinkedList <String>();
		for (int i = 0; i < len; i++) {
			JSONObject jsonElement = jsonArrs.getJSONObject(i);
			rets.add(jsonElement.getJSONObject("category").getString("value"));
		}
		return rets;
	}
	
	private CategoryUtils() {
	}

}
