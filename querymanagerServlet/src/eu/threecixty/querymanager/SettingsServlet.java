package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.SettingsStorage;
import eu.threecixty.profile.ThreeCixtySettings;
import eu.threecixty.profile.oldmodels.EventDetailPreference;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

/**
 * This class is to store settings information into UserProfile.
 * @author Cong-Kinh NGUYEN
 *
 */
public class SettingsServlet extends HttpServlet {

	private static final long serialVersionUID = -3598054909867424454L;
	
	private static final String ACCESS_TOKEN_PARAM = "accessToken";
	private static final String TOWN_NAME_PARAM = "townName";
	private static final String COUNTRY_NAME_PARAM = "countryName";
	private static final String LAT_PARAM = "lat";
	private static final String LON_PARAM = "lon";
	
	private static final String START_DATE_PARAM = "startDate";
	private static final String END_DATE_PARAM = "endDate";
	
	private static final String PROFILE_IDENTITIES_SOURCE_PARAM = "pi_source";
	private static final String PROFILE_IDENTITIES_ACCOUNT_ID_PARAM = "pi_id";
	private static final String PROFILE_IDENTITIES_ACCESS_TOKEN_PARAM = "pi_at";
//	private static final String PROFILE_IDENTITIES_SERVICE_PROVIDER_PARAM = "pi_provider";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		
		String userkey = req.getParameter(ACCESS_TOKEN_PARAM);
		String uid = GoogleAccountUtils.updateInfo(userkey);
		if (!uid.equals("")) { // for a valid access token
			
			ThreeCixtySettings settings = SettingsStorage.load(uid);
			if (settings == null) {
				settings = new ThreeCixtySettings();
				settings.setUid(uid);
			}
			
			String townName = req.getParameter(TOWN_NAME_PARAM);
			if (isNotNullOrEmpty(townName)) settings.setTownName(townName);

			String countryName = req.getParameter(COUNTRY_NAME_PARAM);
			if (isNotNullOrEmpty(countryName)) settings.setCountryName(countryName);
			
			addGPSInfoIntoSettings(req, settings);
			addEventDetailPreferenceIntoSettings(req, settings);

			addProfileIdentities(req, settings);

			SettingsStorage.save(settings);
		}
		
		out.close();
	}

	/**
	 * Adds profile identities into a given settings instance.
	 * @param req
	 * @param settings
	 */
	private void addProfileIdentities(HttpServletRequest req,
			ThreeCixtySettings settings) {
		// TODO Auto-generated method stub
		if (req.getParameter(PROFILE_IDENTITIES_SOURCE_PARAM) != null
				&& !"".equals(req.getParameter(PROFILE_IDENTITIES_SOURCE_PARAM))) {
			addProfileIdentities(req.getParameter(PROFILE_IDENTITIES_SOURCE_PARAM),
					req.getParameter(PROFILE_IDENTITIES_ACCOUNT_ID_PARAM),
					req.getParameter(PROFILE_IDENTITIES_ACCESS_TOKEN_PARAM),
					settings);
		} else {
			String[] sources = req.getParameterValues(PROFILE_IDENTITIES_SOURCE_PARAM);
			String [] accountIds = req.getParameterValues(PROFILE_IDENTITIES_ACCOUNT_ID_PARAM);
			String [] accessTokens = req.getParameterValues(PROFILE_IDENTITIES_ACCESS_TOKEN_PARAM);
			if (sources == null || sources.length == 0 || accountIds == null
					|| accountIds.length == 0 || accessTokens == null || accessTokens.length == 0
					) return;
			if (sources.length != accountIds.length || sources.length != accessTokens.length
					|| accountIds.length != accessTokens.length) return;
			
			for (int i = 0; i < sources.length; i++) {
				addProfileIdentities(sources[i], accountIds[i], accessTokens[i], settings);
			}
		}
	}

	/**
	 * Adds profile identities composed by a given source, a given accountId, and a given access token
	 * to a given settings instance.
	 * @param source
	 * @param accountId
	 * @param accessToken
	 * @param settings
	 */
	private void addProfileIdentities(String source, String accountId,
			String accessToken, ThreeCixtySettings settings) {
		if (!isNotNullOrEmpty(source) || !isNotNullOrEmpty(accountId)
				|| !isNotNullOrEmpty(accessToken)) return;
		List <ProfileIdentities> profileIdentities = settings.getIdentities();
		if (profileIdentities == null) profileIdentities = new ArrayList <ProfileIdentities>();
		ProfileIdentities tmpProfile = new ProfileIdentities();
		tmpProfile.setHasSource(source);
		tmpProfile.setHasUserAccountID(accountId);
		// TODO: update private data from accessToken ?
		profileIdentities.add(tmpProfile);
		settings.setIdentities(profileIdentities);
	}

	/**
	 * Adds GPS information into a given settings instance.
	 * @param req
	 * @param settings
	 */
	private void addGPSInfoIntoSettings(HttpServletRequest req,
			ThreeCixtySettings settings) {
		try {
			String latStr = req.getParameter(LAT_PARAM);
			if (isNotNullOrEmpty(latStr)) {
				settings.setCurrentLatitude(Double.parseDouble(latStr));
			}
			String lonStr = req.getParameter(LON_PARAM);
			if (isNotNullOrEmpty(lonStr)) {
				settings.setCurrentLongitude(Double.parseDouble(lonStr));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds preferred event detail into a given settings instance.
	 * @param req
	 * @param settings
	 */
	private void addEventDetailPreferenceIntoSettings(HttpServletRequest req,
			ThreeCixtySettings settings) {
		EventDetailPreference edp = settings.getEventDetailPreference();
		if (edp == null) edp = new EventDetailPreference();
		
		try {
			String sdStr = req.getParameter(START_DATE_PARAM);
			if (isNotNullOrEmpty(sdStr)) {
				Date startDate = convert(sdStr);
				if (startDate != null) edp.setHasPreferredStartDate(startDate);
			}
			String edStr = req.getParameter(END_DATE_PARAM);
			if (isNotNullOrEmpty(edStr)) {
				Date endDate = convert(edStr);
				if (endDate != null) edp.setHasPreferredEndDate(endDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		settings.setEventDetailPreference(edp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doPost(req, resp);
	}

	/**
	 * Checks whether or not a given string is not null or empty.
	 * @param str
	 * @return
	 */
	private boolean isNotNullOrEmpty(String str) {
		if (str == null || str.equals("")) return false;
		return true;
	}

	private Date convert(String dStr) {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return df.parse(dStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}