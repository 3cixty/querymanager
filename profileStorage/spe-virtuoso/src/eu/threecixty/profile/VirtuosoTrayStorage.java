package eu.threecixty.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;
import eu.threecixty.Configuration;
import eu.threecixty.profile.Tray.OrderType;

/**
 * This class is to deal with Tray Elements in Virtuoso.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class VirtuosoTrayStorage implements TrayManager {
	
	private static final String PREFIXES = Configuration.PREFIXES;
	private static final String PROFILE_TRAY_ELEMENT_PREDICATE = "profile:trayElement";
	private static final String TRAY_ID_PREDICATE = "profile:trayId";
	private static final String TRAY_TYPE_PREDICATE = "profile:trayType";
	private static final String TRAY_TITLE_PREDICATE = "profile:trayTitle";
	private static final String TRAY_TIMESTAMP_PREDICATE = "profile:trayTimestamp";
	//private static final String TRAY_TOKEN_PREDICATE = "profile:trayToken"; // UID or junk token
	private static final String TRAY_SOURCE_PREDICATE = "profile:traySource";
	private static final String TRAY_ATTEND_PREDICATE = "profile:trayAttend";
	private static final String TRAY_ATTENDED_DATETIME_PREDICATE = "profile:trayAttendedDatetime";
	private static final String TRAY_IMAGE_URL_PREDICATE = "profile:trayImageUrl";
	private static final String TRAY_RATING_PREDICATE = "profile:trayHasRating";
	
	private static final String PREFIX_RATING = "http://data.linkedevents.org/def/location#rating";
	private static final String LONG_SCHEMA = "^^<http://www.w3.org/2001/XMLSchema#long>";
	
	private static final Pattern PATTERN = Pattern.compile("([a-z]*[A-Z]*[0-9]*[-]*[/]*[:]*[.]*)*");
	
	private static final String INVALID_TRAY_ELEMENT_EXCEPTION_MSG =
			"The UID and tray element ID must conform to the following pattern: ([a-z]*[A-Z]*[0-9]*[-]*[/]*[:]*[.]*)*";


	public static TrayManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public boolean addTray(Tray tray) throws InvalidTrayElement, TooManyConnections {
		if (tray == null) return false;
		if (!checkValidTray(tray)) {
			throw new InvalidTrayElement(INVALID_TRAY_ELEMENT_EXCEPTION_MSG);
		}
		try {
			if (checkTrayExisted(tray)) {
				return false;
			}
		} catch (InterruptedException e) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
		return save(tray);
	}
	
	/**
	 * Deletes a given tray from Virtuoso.
	 * @param tray
	 * @return
	 * @throws InvalidTrayElement
	 */
	public boolean deleteTray(Tray tray) throws InvalidTrayElement, TooManyConnections {
		if (tray == null) return false;
		if (!checkValidTray(tray)) {
			throw new InvalidTrayElement(INVALID_TRAY_ELEMENT_EXCEPTION_MSG);
		}
		String personUri = getPersonURI(tray);
		String trayUri = getTrayURI(tray);
		StringBuffer buf = new StringBuffer(PREFIXES);
		buf.append(" DELETE WHERE { GRAPH <").append(getGraphName(tray)).append("> \n");
		buf.append("{\n");
		buf.append(trayUri).append(" ?p ?o .\n ");
		buf.append(personUri).append(" ").append(PROFILE_TRAY_ELEMENT_PREDICATE).append(" ").append(trayUri).append(" .\n");
		buf.append("}}");
		
		try {
			VirtuosoManager.getInstance().executeUpdateQuery(buf.toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
		return false;
	}

	public boolean replaceUID(String junkID, String uid) throws InvalidTrayElement, TooManyConnections {
		if (junkID == null || uid == null) return false;
		if (!containValidCharacters(junkID) || !containValidCharacters(uid)) {
			throw new InvalidTrayElement(INVALID_TRAY_ELEMENT_EXCEPTION_MSG);
		}
		List <Tray> trays = getTrays(junkID);
		boolean ok = cleanTrays(junkID);
		if (!ok) return false;
		VirtGraph virtGraph;
		try {
			virtGraph = VirtuosoManager.getInstance().getVirtGraph();
			VirtuosoUpdateRequest vurToInsertData = null;
			for (Tray tray: trays) {
				tray.setUid(uid);
				String query = createQueryToSave(tray);
				
				if (vurToInsertData == null) vurToInsertData = VirtuosoUpdateFactory.create(query, virtGraph);
				else vurToInsertData.addUpdate(query);
			}

			if (vurToInsertData != null) {
				vurToInsertData.exec();
			}
			
			return true;
		} catch (InterruptedException e) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}

	public boolean updateTray(Tray tray) throws InvalidTrayElement, TooManyConnections {
		if (tray == null) return false;
		if (!checkValidTray(tray)) {
			throw new InvalidTrayElement(INVALID_TRAY_ELEMENT_EXCEPTION_MSG);
		}
		boolean ok = deleteTray(tray);
		if (!ok) return false;

		return save(tray);
	}
	
	public List <Tray> getTrays(String uid, int offset, int limit,
			OrderType orderType, boolean eventsPast) throws TooManyConnections {
		List <Tray> trays = getTrays(uid);
		int firstIndex = (offset < 0) ? 0: offset;
		if (firstIndex >= trays.size()) {
			trays.clear();
			return trays;
		}
		if (limit <= -1) return getTraysWithOrderAndEventPast(trays, orderType, eventsPast);
		List <Tray> limitedTrays = new ArrayList <Tray>();
		int lastIndex = Math.min(firstIndex + limit, trays.size());
		for (int i = firstIndex; i < lastIndex; i++) {
			limitedTrays.add(trays.get(i));
		}
		return getTraysWithOrderAndEventPast(limitedTrays, orderType, eventsPast);
	}
	
	public boolean cleanTrays(String token) throws InvalidTrayElement, TooManyConnections {
		if (!containValidCharacters(token)) {
			throw new InvalidTrayElement(INVALID_TRAY_ELEMENT_EXCEPTION_MSG);
		}
		String personUri = getPersonURI(token);
		StringBuffer buf = new StringBuffer(PREFIXES);
		buf.append(" DELETE WHERE { GRAPH <").append(getGraphName(token)).append("> \n");
		buf.append("{\n");
		buf.append(" ?tray ?p ?o .\n");
		buf.append(personUri).append(" ").append(PROFILE_TRAY_ELEMENT_PREDICATE).append(" ?tray .\n");
		buf.append("}}");
		
		try {
			VirtuosoManager.getInstance().executeUpdateQuery(buf.toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
		return false;
	}

	private static List<Tray> getTraysWithOrderAndEventPast(List<Tray> trays,
			OrderType orderType, boolean eventsPast) {
		// TODO: correct this method with eventsPast. Need to get event time to decide
		// which event was taken place
		if (orderType == OrderType.Desc) {
			Collections.sort(trays, new Comparator<Tray>() {

				@Override
				public int compare(Tray tray1, Tray tray2) {
					long distance = tray1.getTimestamp() - tray2.getTimestamp();
					if (distance > 0) return 1;
					else if (distance < 0) return -1;
					return 0;
				}
			});
		} else if (orderType == OrderType.Asc) {
			Collections.sort(trays, new Comparator<Tray>() {

				@Override
				public int compare(Tray tray1, Tray tray2) {
					long distance = tray1.getTimestamp() - tray2.getTimestamp();
					if (distance > 0) return -1;
					else if (distance < 0) return 1;
					return 0;
				}
			});
		}
		if (eventsPast) return trays;
		return trays;
	}

	public List <Tray> getTrays(String uid) throws TooManyConnections {
		List <Tray> trays = new LinkedList <Tray>();
		if (uid == null) return trays;
		if (!containValidCharacters(uid)) return trays;
		String personUri = getPersonURI(uid);
		StringBuffer buf = new StringBuffer(PREFIXES).append(
				" SELECT ?trayId ?title ?type ?source  ?timestamp ?attend ?attendedDateTime ?imageUrl ?ratingValue \n");
		buf.append(" FROM <").append(getGraphName(uid)).append(">\n");
		buf.append("WHERE {");
		buf.append(personUri).append(" ").append(PROFILE_TRAY_ELEMENT_PREDICATE).append(" ?tray .\n");
		buf.append("?tray ").append(TRAY_ID_PREDICATE).append(" ?trayId .\n");
		buf.append("?tray ").append(TRAY_TITLE_PREDICATE).append(" ?title .\n");
		buf.append("?tray ").append(TRAY_TYPE_PREDICATE).append(" ?type .\n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_SOURCE_PREDICATE).append(" ?source .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_TIMESTAMP_PREDICATE).append(" ?timestamp .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_ATTEND_PREDICATE).append(" ?attend .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_ATTENDED_DATETIME_PREDICATE).append(" ?attendedDateTime .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_IMAGE_URL_PREDICATE).append(" ?imageUrl .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_RATING_PREDICATE).append(" ?rating .\n ?rating schema:ratingValue ?ratingValue .} \n");
		buf.append("}");

		JSONObject jsonObject;
		try {
			jsonObject = VirtuosoManager.getInstance().executeQueryWithDBA(buf.toString());
			if (jsonObject == null) return trays;
			try {
				JSONArray jsonArr = jsonObject.getJSONObject("results").getJSONArray("bindings");
				if (jsonArr.length() == 0) return trays;
				for (int i = 0; i < jsonArr.length(); i++) {
					Tray tray = createTray(jsonArr.getJSONObject(i));
					tray.setUid(uid);
					trays.add(tray);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}	
			
			return trays;
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}

	// ?trayId ?title ?type ?source  ?timestamp ?attend ?attendedDateTime ?imageUrl ?ratingValue
	private static Tray createTray(JSONObject jsonObject) {
		Tray tray = new Tray();
		try {
			tray.setItemId(getValue(jsonObject, "trayId"));
			tray.setItemType(CodeBaseUtils.decode(getValue(jsonObject, "type")));
			tray.setTimestamp(Long.parseLong(getValue(jsonObject, "timestamp")));
			
			if (jsonObject.has("source"))  tray.setSource(CodeBaseUtils.decode(getValue(jsonObject, "source")));
			if (jsonObject.has("title")) tray.setElement_title(CodeBaseUtils.decode(getValue(jsonObject, "title")));

		    if (jsonObject.has("attend")) tray.setAttended(Boolean.parseBoolean(getValue(jsonObject, "attend")));
		    if (jsonObject.has("attendedDateTime")) tray.setDateTimeAttended(getValue(jsonObject, "attendedDateTime"));
		    if (jsonObject.has("ratingValue")) {
		    	String ratingStr = ((JSONObject) jsonObject.get("ratingValue")).getString("value");
		    	if (ratingStr != null && !ratingStr.trim().equals("")) tray.setRating(Integer.parseInt(ratingStr));
		    }
		    if (jsonObject.has("imageUrl")) tray.setImage_url(CodeBaseUtils.decode(getValue(jsonObject, "imageUrl")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tray;
	}

	public Tray getTray(String uid, String trayId) throws TooManyConnections {
		if (uid == null || trayId == null) return null;
		
		if (!containValidCharacters(uid) || !containValidCharacters(trayId)) return null;
		String personUri = getPersonURI(uid);
		String trayUri = getTrayUri(uid, trayId);
		StringBuffer buf = new StringBuffer(PREFIXES).append(
				" SELECT ?trayId ?title ?type ?source  ?timestamp ?attend ?attendedDateTime ?imageUrl ?ratingValue \n");
		buf.append(" FROM <").append(getGraphName(uid)).append(">\n");
		buf.append("WHERE {");
		buf.append(personUri).append(" ").append(PROFILE_TRAY_ELEMENT_PREDICATE).append(" ").append(trayUri).append(" .\n");
		buf.append(trayUri).append(" ").append(TRAY_ID_PREDICATE).append(" ?trayId .\n");
		buf.append(trayUri).append(" ").append(TRAY_TITLE_PREDICATE).append(" ?title .\n");
		buf.append(trayUri).append(" ").append(TRAY_TYPE_PREDICATE).append(" ?type .\n");
		buf.append("OPTIONAL { ").append(trayUri).append(" ").append(TRAY_SOURCE_PREDICATE).append(" ?source .} \n");
		buf.append("OPTIONAL { ").append(trayUri).append(" ").append(TRAY_TIMESTAMP_PREDICATE).append(" ?timestamp .} \n");
		buf.append("OPTIONAL { ").append(trayUri).append(" ").append(TRAY_ATTEND_PREDICATE).append(" ?attend .} \n");
		buf.append("OPTIONAL { ").append(trayUri).append(" ").append(TRAY_ATTENDED_DATETIME_PREDICATE).append(" ?attendedDateTime .} \n");
		buf.append("OPTIONAL { ").append(trayUri).append(" ").append(TRAY_IMAGE_URL_PREDICATE).append(" ?imageUrl .} \n");
		buf.append("OPTIONAL { ").append(trayUri).append(" ").append(TRAY_RATING_PREDICATE).append(" ?rating .\n ?rating schema:ratingValue ?ratingValue .} \n");
		buf.append("}");

		JSONObject jsonObject;
		try {
			jsonObject = VirtuosoManager.getInstance().executeQueryWithDBA(buf.toString());
			if (jsonObject == null) return null;
			try {
				JSONArray jsonArr = jsonObject.getJSONObject("results").getJSONArray("bindings");
				if (jsonArr.length() == 0) return null;
				Tray tray = createTray(jsonArr.getJSONObject(0));
				tray.setUid(uid);
				return tray;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}
	
	@Override
	public List<Tray> getAllTrays() throws TooManyConnections {
		List <Tray> trays = new LinkedList <Tray>();
		StringBuffer buf = new StringBuffer(PREFIXES).append(
				" SELECT ?person ?trayId ?title ?type ?source  ?timestamp ?attend ?attendedDateTime ?imageUrl ?ratingValue \n");
		buf.append(" FROM <").append(getGraphName("dba")).append(">\n");
		buf.append("WHERE {\n");
		buf.append("?person ").append(PROFILE_TRAY_ELEMENT_PREDICATE).append(" ?tray .\n");
		buf.append("?tray ").append(TRAY_ID_PREDICATE).append(" ?trayId .\n");
		buf.append("?tray ").append(TRAY_TITLE_PREDICATE).append(" ?title .\n");
		buf.append("?tray ").append(TRAY_TYPE_PREDICATE).append(" ?type .\n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_SOURCE_PREDICATE).append(" ?source .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_TIMESTAMP_PREDICATE).append(" ?timestamp .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_ATTEND_PREDICATE).append(" ?attend .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_ATTENDED_DATETIME_PREDICATE).append(" ?attendedDateTime .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_IMAGE_URL_PREDICATE).append(" ?imageUrl .} \n");
		buf.append("OPTIONAL { ?tray ").append(TRAY_RATING_PREDICATE).append(" ?rating .\n ?rating schema:ratingValue ?ratingValue .} \n");
		buf.append("}");

		JSONObject jsonObject;
		try {
			jsonObject = VirtuosoManager.getInstance().executeQueryWithDBA(buf.toString());
			if (jsonObject == null) return trays;
			try {
				JSONArray jsonArr = jsonObject.getJSONObject("results").getJSONArray("bindings");
				if (jsonArr.length() == 0) return trays;
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject tmp = jsonArr.getJSONObject(i);
					Tray tray = createTray(tmp);
					String uid = getValue(tmp, "person").substring(
							GetSetQueryStrings.PROFILE_URI.length());
					tray.setUid(uid);
					trays.add(tray);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}	
			
			return trays;
		} catch (InterruptedException e1) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		}
	}
	
	/**
	 * Saves a given tray into Virtuoso.
	 * @param tray
	 * @return
	 */
	private static boolean save(Tray tray) throws TooManyConnections {

		String query = createQueryToSave(tray);
		
		try {
			VirtuosoManager.getInstance().executeUpdateQuery(query);
			return true;
		} catch (InterruptedException e) {
			throw new TooManyConnections(VirtuosoManager.BUSY_EXCEPTION);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static String createQueryToSave(Tray tray) {
		// encode with Base64 to avoid SQL injection
		String personUri = getPersonURI(tray);
		String trayUri = getTrayURI(tray);
		tray.setTimestamp(System.currentTimeMillis());
		StringBuffer buf = new StringBuffer(PREFIXES).append(
				"   INSERT DATA { GRAPH <").append(getGraphName(tray)).append("> \n");
		buf.append(" {");
		buf.append(personUri).append(" ").append(PROFILE_TRAY_ELEMENT_PREDICATE).append(" ").append(trayUri).append(" .\n");
		buf.append(trayUri).append(" ").append(TRAY_ID_PREDICATE).append(" \"" + tray.getItemId() + "\"").append(" .\n");
		buf.append(trayUri).append(" ").append(TRAY_TITLE_PREDICATE).append(" \" " + CodeBaseUtils.encode(tray.getElement_title()) + " \"").append(" .\n");
		buf.append(trayUri).append(" ").append(TRAY_TYPE_PREDICATE).append(" \"" + CodeBaseUtils.encode(tray.getItemType()) + "\"").append(" .\n");
		buf.append(trayUri).append(" ").append(TRAY_TIMESTAMP_PREDICATE).append(" \"").append(tray.getTimestamp()).append("\"").append(LONG_SCHEMA).append(" .\n");
		if (!isNullOrEmpty(tray.getSource())) {
			buf.append(trayUri).append(" ").append(TRAY_SOURCE_PREDICATE).append(" \"" + CodeBaseUtils.encode(tray.getSource()) + "\"").append(" .\n");
		}
		
		buf.append(trayUri).append(" ").append(TRAY_ATTEND_PREDICATE).append(" ").append(tray.isAttended()).append(" .\n");
		
		if (!isNullOrEmpty(tray.getDateTimeAttended())) {
			buf.append(trayUri).append(" ").append(TRAY_ATTENDED_DATETIME_PREDICATE).append(" \"" + tray.getDateTimeAttended() + "\"").append(" .\n");
		}
		if (!isNullOrEmpty(tray.getImage_url())) {
			buf.append(trayUri).append(" ").append(TRAY_IMAGE_URL_PREDICATE).append(" \"" + CodeBaseUtils.encode(tray.getImage_url()) + "\"").append(" .\n");
		}
		
		if (tray.getRating() > 0 && tray.getRating() <= 5) {
			buf.append(trayUri).append(" ").append(TRAY_RATING_PREDICATE).append(" <" + PREFIX_RATING + tray.getRating() + ">").append(" .\n");
		}

		buf.append("}}");
		return buf.toString();
	}
	
	/**
	 * Checks whether or not a given tray exists in the KB.
	 * @param tray
	 * @return
	 * @throws InterruptedException 
	 */
	private static boolean checkTrayExisted(Tray tray) throws InterruptedException {
		String trayUri = getTrayURI(tray);
		StringBuffer buf = new StringBuffer(PREFIXES).append(" SELECT * \n");
		buf.append(" FROM <").append(getGraphName(tray)).append(">\n");
		buf.append("WHERE {");
		buf.append(trayUri).append(" ").append(TRAY_ID_PREDICATE).append(" ?trayId .\n");
		buf.append("}");
		JSONObject jsonObject = VirtuosoManager.getInstance().executeQueryWithDBA(buf.toString());
		if (jsonObject == null) return false;
		JSONArray jsonArr;
		try {
			jsonArr = jsonObject.getJSONObject("results").getJSONArray("bindings");
			if (jsonArr.length() == 0) return false;
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return false;
	}
	
	private static String getValue(JSONObject jsonObj, String key) {
		if (!jsonObj.has(key)) return null;
		try {
			return jsonObj.getJSONObject(key).getString("value");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getPersonURI(Tray tray) {
		return getPersonURI(tray.getUid());
	}
	
	private static String getPersonURI(String uid) {
		return "<" + GetSetQueryStrings.PROFILE_URI + uid + ">";
	}
	
	private static String getTrayURI(Tray tray) {
		return getTrayUri(tray.getUid(), tray.getItemId());
	}
	
	private static String getTrayUri(String uid, String trayId) {
		return "<" + GetSetQueryStrings.PROFILE_URI + uid + "/" + trayId + ">";
	}
	
	/**
	 * Checks whether or not a given tray is valid to avoid SQL injection.
	 * @param tray
	 * @return
	 */
	private static boolean checkValidTray(Tray tray) {
		// only check for uid and trayId to avoid SQL injection
		if (!containValidCharacters(tray.getUid())) return false;
		return containValidCharacters(tray.getItemId());
	}
	
	/**
	 * Checks whether or not a given string only contains valid characters.
	 * <br>
	 * A valid character can only be 'A'-'Z', 'a'-'z', -
	 * @param str
	 * @return
	 */
	private static boolean containValidCharacters(String str) {
		Matcher matcher = PATTERN.matcher(str);
		return matcher.matches();
	}	
	
	private static boolean isNullOrEmpty(String str) {
		if (str == null || str.equals("")) return true;
		return false;
	}
	
	/**
	 * Delegates method to get graph name.
	 * @param tray
	 * @return
	 */
	private static String getGraphName(Tray tray) {
		return VirtuosoManager.getInstance().getGraph(tray.getUid());
	}
	
	/**
	 * Delegates method to get graph name.
	 * @param uid
	 * @return
	 */
	private static String getGraphName(String uid) {
		return VirtuosoManager.getInstance().getGraph(uid);
	}
	
	/**
	 * Prohibits instantiations.
	 */
	private VirtuosoTrayStorage() {
	}

	/**Singleton holder*/
	private static class SingletonHolder {
		private static final TrayManager INSTANCE = new VirtuosoTrayStorage();
	}
}
