package eu.threecixty.profile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.Preference;
import eu.threecixty.profile.oldmodels.ProfileIdentities;
import eu.threecixty.profile.oldmodels.Transport;
import eu.threecixty.userprofile.AccountModel;
import eu.threecixty.userprofile.AddressModel;
import eu.threecixty.userprofile.Know;
import eu.threecixty.userprofile.PreferenceModel;
import eu.threecixty.userprofile.TransportModel;
import eu.threecixty.userprofile.UserModel;


public class UserUtils {
	
	 private static final Logger LOGGER = Logger.getLogger(
			 UserUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	 /**
	  * Checks whether or not a given 3cixty UID exists in the DB.
	  * @param _3cixtyUid
	  * @return
	  */
	public static boolean exists(String _3cixtyUid) {
		if (isNullOrEmpty(_3cixtyUid)) return false;
		Session session = null;
		boolean existed = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserModel U WHERE U.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, _3cixtyUid).list();
			
			existed = results.size() > 0;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return existed;
	}
	
	/**
	 * Finds the corresponding 3cixtyUID with a given 
	 * @param uid
	 * @param source
	 * @param profileImage
	 * @return
	 */
	public static String find3cixtyUID(String uid, String source, String profileImage) {
		if (isNullOrEmpty(uid)) return null;
		String _3cixtyUID = null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM AccountModel A WHERE A.accountId = ? AND A.source = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).setString(1, source).list();
			if (results.size() > 0) {
				_3cixtyUID = ((AccountModel) results.get(0)).getUserModel().getUid();
			} else if (!isNullOrEmpty(profileImage)) {
				hql = "FROM UserModel U WHERE U.profileImage = ?";
				query = session.createQuery(hql);
				results = query.setString(0, profileImage).list();
				if (results.size() > 0) {
					_3cixtyUID = ((UserModel) results.get(0)).getUid();
				}
			}

		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return _3cixtyUID;
	}
	
	public static UserProfile getUserProfile(String _3cixtyUID) {
		Session session = null;
		UserProfile userProfile = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM UserModel U WHERE U.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, _3cixtyUID).list();
			
			if (results.size() > 0) {
				UserModel userModel = (UserModel) results.get(0);
				userProfile = convertToUserProfile(userModel);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return userProfile;
	}
	
	private static UserProfile convertToUserProfile(UserModel userModel) {
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(userModel.getUid());
		
		convertName(userModel, userProfile);
		convertAddress(userModel, userProfile);
		
		if (!isNullOrEmpty(userModel.getGender())) userProfile.setHasGender(userModel.getGender());
		
		if (!isNullOrEmpty(userModel.getProfileImage()))
			userProfile.setProfileImage(userModel.getProfileImage());
		
		userProfile.setHasLastCrawlTime(userModel.getLastCrawlTimeToKB() + "");
		
		convertKnows(userModel, userProfile);
		convertPreference(userModel, userProfile);
		convertAccounts(userModel, userProfile);
		
		return userProfile;
	}

	private static void convertAccounts(UserModel userModel,
			UserProfile userProfile) {
		Set <AccountModel> accountModels = userModel.getAccounts();
		if (accountModels == null  || accountModels.size() == 0) return;
		Set <ProfileIdentities> pis = new HashSet <ProfileIdentities>();
		userProfile.setHasProfileIdenties(pis);
		for (AccountModel accountModel: accountModels) {
			ProfileIdentities pi = new ProfileIdentities();
			pi.setHasSourceCarrier(accountModel.getSource());
			pi.setHasUserAccountID(accountModel.getAccountId());
			pis.add(pi);
		}
	}

	private static void convertPreference(UserModel userModel,
			UserProfile userProfile) {
		PreferenceModel preferenceModel = userModel.getPreferenceModel();
		if (preferenceModel == null) return;
		Set <TransportModel> transportModels = preferenceModel.getTransportModels();
		if (transportModels == null || transportModels.size() == 0) return;
		Preference preference = new Preference();
		userProfile.setPreferences(preference);
		Set <Transport> transports = new HashSet <Transport>();
		preference.setHasTransport(transports);
		for (TransportModel transportModel: transportModels) {
			Transport transport = new Transport();
			TransportUtils.convertTransport(transportModel, transport);
			transports.add(transport);
		}
	}

	private static void convertKnows(UserModel userModel, UserProfile userProfile) {
		Set <Know> knows = userModel.getKnows();
		if (knows == null || knows.size() == 0) return;
		Set <String> toKnows = new HashSet <String>();
		userProfile.setKnows(toKnows);
		for (Know know: knows) {
			toKnows.add(know.getUser().getUid());
		}
	}

	private static void convertAddress(UserModel userModel,
			UserProfile userProfile) {
		AddressModel addressModel = userModel.getAddress();
		if (addressModel == null) return;
		if (isNullOrEmpty(addressModel.getCountryName()) && isNullOrEmpty(addressModel.getTownName())
				&& isNullOrEmpty(addressModel.getStreetAddress())
				&& isNullOrEmpty(addressModel.getPostalCode())
				&& (addressModel.getLatitude() == null) && (addressModel.getLongitude() == null)) return;
		Address address = new Address();
		userProfile.setHasAddress(address);
		if (!isNullOrEmpty(addressModel.getCountryName()))
			address.setCountryName(addressModel.getCountryName());
		if (!isNullOrEmpty(addressModel.getTownName()))
			address.setTownName(addressModel.getTownName());
		if (!isNullOrEmpty(addressModel.getStreetAddress()))
			address.setStreetAddress(addressModel.getStreetAddress());
		if (!isNullOrEmpty(addressModel.getPostalCode()))
			address.setPostalCode(addressModel.getPostalCode());
		if (addressModel.getLatitude() != null) address.setLatitude(addressModel.getLatitude());
		if (addressModel.getLongitude() != null) address.setLongitute(addressModel.getLongitude());
	}

	private static void convertName(UserModel userModel, UserProfile userProfile) {
		if (!isNullOrEmpty(userModel.getFirstName()) || !isNullOrEmpty(userModel.getLastName())) {
			Name name = new Name();
			name.setFamilyName(userModel.getLastName());
			name.setGivenName(userModel.getFirstName());
			userProfile.setHasName(name);
		}
	}

	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private UserUtils() {
	}
}
