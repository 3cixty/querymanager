package eu.threecixty.CrawlSocialProfiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Parse google+ HTML code.
 * 
 * @author Rachit@Inria, Rosca@Inria
 * 
 */
public class HtmlParser {

	private String SchemaPerson = "http://schema.org/Person";
	private String SchemaReview = "http://schema.org/Review";
	private String SchemaVideo = "http://schema.org/VideoObject";
	private String SchemaType = "itemType";

	private String ProfileFormat = "https://www.googleapis.com/plus/v1/people/%s?&key="
			+ "AIzaSyC5EJ6otc4O5T6XcF0o04HWNmYGQqNyQng";

	private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy",
			Locale.US);

	/**
	 * Extract user specific information from HTML code.
	 * 
	 * @param String profileId,
	 * @param String ratingsHtml,
	 * @param long timestamp,
	 * @param long lastcrawltime
	 * @return String
	 */
	public String printProfile(String profileId, String ratingsHtml,
			long timestamp, long lastcrawltime) {
		String content = readURL(String.format(ProfileFormat, profileId));

		JSONObject object = null;

		try {
			JSONParser parser = new JSONParser();
			object = (JSONObject) parser.parse(content);

			object.put("ratings",
					getReviews(timestamp, lastcrawltime, ratingsHtml));
		} catch (ParseException ex) {
		}
		return object.toJSONString();
	}

	/**
	 * Extract address specific information from HTML code.
	 *  
	 * @param String URL
	 * @return String
	 */
	public String printAddress(String URL) {
		String content = readURL(URL);

		JSONObject object = null;

		try {
			JSONParser parser = new JSONParser();
			object = (JSONObject) parser.parse(content);
		} catch (ParseException ex) {
		}
		return object.toJSONString();
	}

	/**
	 * Read URL 
	 * @param String url
	 * @return String
	 */
	public String readURL(String url) {
		String result = "";
		try {
			URL oracle = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				result += inputLine;
			}
			in.close();
		} catch (Exception ex) {

		}

		return result;
	}
	
	/**
	 *Get connections of the user
	 *TODO: implement the method 
	 *
	 * @return
	 */
	private JSONArray getConnections() {
		return null;
	}
	
	/**
	 * get reviews made by the user between [lowerTime, upperTime]
	 * 
	 * @param long upperTime
	 * @param long lowerTime
	 * @param String html
	 * @return JSONArray
	 */
	private JSONArray getReviews(long upperTime, long lowerTime, String html) {
		JSONArray allPlaces = new JSONArray();
		JSONObject lastPlace = null;

		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("[itemscope]");
		for (Element elem : elements) {
			String itemType = elem.attr(SchemaType);

			if (itemType.equals(SchemaPerson) || itemType.equals(SchemaVideo)) {
				// ignore persons;
			} else if (itemType.equals(SchemaReview)) {
				Elements els = elem.select("[role=button]");
				int max = 0;
				int count = 0;
				boolean review = false;
				for (Element e : els) {
					String elementClass = e.attr("class");

					if (elementClass.length() >= max) {
						max = elementClass.length();
						count++;
					}

					review = true;
				}

				if (review) {
					if (lastPlace != null) {
						lastPlace.put("rating", count);

						els = elem.select("[itemprop=dateModified]");
						if (els.size() > 0) {
							try {
								String dateStr = els.get(0).attr("content");
								Date date = sdf.parse(dateStr);

								long timestamp = date.getTime() / 1000;
								lastPlace.put("dateModified", timestamp);
								if (lowerTime < timestamp
										&& timestamp < upperTime) {
									allPlaces.add(lastPlace);
									lastPlace = null;
								} else {
									lastPlace = null;
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			} else {
				lastPlace = new JSONObject();

				Elements els = elem.select("[itemprop=name]");
				if (els.size() >= 2) {
					lastPlace.put("name", els.get(0).text());
					lastPlace.put("address", els.get(1).text());
				}
				els = elem.select("[itemprop=url]");
				if (els.size() > 0) {
					lastPlace.put("placeid", els.get(0).attr("content")
							.replace("./", ""));
				}
			}
		}

		return allPlaces;
	}
}
