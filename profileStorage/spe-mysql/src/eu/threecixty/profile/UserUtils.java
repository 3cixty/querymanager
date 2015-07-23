package eu.threecixty.profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import eu.threecixty.cache.ProfileCacheManager;
import eu.threecixty.db.HibernateUtil;
import eu.threecixty.profile.Utils.UidSource;
import eu.threecixty.profile.oldmodels.Accompanying;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;
import eu.threecixty.userprofile.AccompanyingModel;
import eu.threecixty.userprofile.AccountModel;
import eu.threecixty.userprofile.AddressModel;
import eu.threecixty.userprofile.UserModel;


public class UserUtils {
	
	public static final String MOBIDOT = "Mobidot";

	private static final Logger LOGGER = Logger.getLogger(
			 UserUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();

	public static boolean remove(UserProfile profile) {
		if (profile == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			
			session.getTransaction().commit();
			
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
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
	 * @return
	 */
	public static String find3cixtyUID(String uid, String source) {
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
			}

		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return _3cixtyUID;
	}
	
	/**
	 * Gets the corresponding user profile from a given 3cixty UID.
	 * @param _3cixtyUID
	 * @return
	 */
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
				userProfile.setModelIdInPersistentDB(userModel.getId());
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return userProfile;
	}
	
	/**
	 * Adds or updates a given user profile.
	 * @param userProfile
	 * @return
	 */
	public static boolean saveUserProfile(UserProfile userProfile) {
		if (userProfile == null) return false;
		Integer userModelId = userProfile.getModelIdInPersistentDB();
		if (userModelId == null) return addUserProfile(userProfile);
		return updateUserProfile(userProfile);
	}
	
	
	/**
	 * Creates user profiles.
	 * @param profiles
	 * @return
	 */
	public static boolean createProfiles(List<UserProfile> profiles) {
		Session session = null;
		boolean added = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			
			Map <String , UserModel> localUserModels = new HashMap<String, UserModel>();
			
			for (UserProfile profile: profiles) {

				UserModel userModel = new UserModel();
				userModel.setUid(profile.getHasUID());

				session.save(userModel);
				
				localUserModels.put(userModel.getUid(), userModel);
			}
			
			session.getTransaction().commit();

			for (UserProfile profile: profiles) {
				UserModel tmpModel = localUserModels.get(profile.getHasUID());
				if (tmpModel != null) {
					if (tmpModel.getId() != null) {
					    profile.setModelIdInPersistentDB(tmpModel.getId());
				        ProfileCacheManager.getInstance().put(profile);
					}
				}
			}
			added = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.getTransaction().rollback();
			for (UserProfile profile: profiles) {
				ProfileCacheManager.getInstance().remove(profile);
			}
		} finally {
			if (session != null) session.close();
		}
		return added;
	}
	
	/**
	 * Gets all the corresponding Google UIDs from a given set of 3cixty UIDs.
	 * @param _3cixtyUids
	 * @return
	 */
	public static List<String> getGoogleUidsFrom3cixtyUIDs(Set<String> _3cixtyUids) {
		if (_3cixtyUids == null || _3cixtyUids.size() == 0) return null;
		List <String> googleUids = new LinkedList <String>();
		Session session = null;
		try {
			String hql = "From AccountModel A WHERE A.userModel.uid in (:uids) AND A.source = :source";
			session = HibernateUtil.getSessionFactory().openSession();
			List <?> results = session.createQuery(hql).setParameterList("uids",
					_3cixtyUids).setParameter("source",
							SPEConstants.GOOGLE_SOURCE).list();
			for (Object obj: results) {
				AccountModel accountModel = (AccountModel) obj;
				googleUids.add(accountModel.getAccountId());
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return googleUids;
	}
	
	/**
	 * Finds the corresponding 3cixty UIDs from a given list of accountIDs and source.
	 * @param accountIds
	 * @param source
	 * @param unfoundAccountIds
	 * @return
	 */
	public static Set<String> find3cixtyUIDs(List<String> accountIds,
			String source, List <String> unfoundAccountIds) {
		if (accountIds == null || accountIds.size() == 0) return Collections.emptySet();
		Set <String> _3cixtyUids = new HashSet <String>();
		Session session = null;
		try {
			String sql = "SELECT accountId, uid FROM 3cixty_user_profile, 3cixty_account WHERE (3cixty_user_profile.id = 3cixty_account.3cixty_user_id) AND (accountId in (:accountIds)) AND source = :source";
			session = HibernateUtil.getSessionFactory().openSession();
			@SuppressWarnings("unchecked")
			List <Object[]> results = session.createSQLQuery(sql).setParameterList("accountIds",
					accountIds).setParameter("source",
							source).list();
			List <String> accountIdsExisted = new LinkedList<String>();
			for (Object [] obj: results) {
				_3cixtyUids.add(obj[1].toString());
				accountIdsExisted.add(obj[0].toString());
			}
			
			// XXX: for cases where we cannot find the accountModel which corresponds with user profile
			if (!"Mobidot".equalsIgnoreCase(source)) {
				List <String> tmpUids = new LinkedList <String>();
				for (String accountId: accountIds) {
					if (!accountIdsExisted.contains(accountId)) {
						String generatedID = Utils.gen3cixtyUID(accountId,
								SPEConstants.GOOGLE_SOURCE.equals(source) ? UidSource.GOOGLE : UidSource.FACEBOOK);
						tmpUids.add(generatedID);
					}
				}
				if (tmpUids.size() > 0) {
					String userModelSql = "SELECT uid FROM 3cixty_user_profile  WHERE uid IN (:uids)";
					List <?> userModelList = session.createSQLQuery(userModelSql).setParameterList("uids",
							tmpUids).list();
					for (Object obj: userModelList) {
						String tmpUid = obj.toString();
						_3cixtyUids.add(tmpUid);
						accountIdsExisted.add(tmpUid.substring(2));
					}
				}
			}
			
			if (unfoundAccountIds != null) {
				for (String accountId: accountIds) {
					if (!accountIdsExisted.contains(accountId))
						unfoundAccountIds.add(accountId);
				}
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return _3cixtyUids;
	}
	
	/**
	 * Finds user profile.
	 * @param uid
	 * @param source
	 * @param profileImage
	 * @return
	 */
	public static UserProfile findUserProfile(String uid, String source) {
		Session session = null;
		UserProfile userProfile = null;
		try {
			String tmpUid = Utils.gen3cixtyUID(uid, source.equals(SPEConstants.GOOGLE_SOURCE)
					? UidSource.GOOGLE : UidSource.FACEBOOK);
			session = HibernateUtil.getSessionFactory().openSession();

			String hql = "FROM AccountModel A WHERE A.accountId = ? AND A.source = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).setString(1, source).list();
			UserModel userModel = null;
			if (results.size() > 0) {
				userModel = ((AccountModel) results.get(0)).getUserModel();
			} else {
				hql = "FROM UserModel U WHERE U.uid = ?";
				query = session.createQuery(hql);
				results = query.setString(0, tmpUid).list();
				if (results.size() > 0) {
					userModel = ((UserModel) results.get(0));
				}
			}
			if (userModel != null) userProfile = convertToUserProfile(userModel);

		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return userProfile;
	}
	
	/**
	 * Find all friends which have a list of knows containing the given 3cixty UID.
	 * @param my3cixtyUID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Friend> findAll3cixtyFriendsHavingMyUIDInKnows(String my3cixtyUID) {
		Session session = null;
		List <Friend> friends = new LinkedList<Friend>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String sql = "SELECT DISTINCT accountId, uid, source, element, firstName, lastName FROM 3cixty.3cixty_user_profile, 3cixty_user_profile_knows where 3cixty_user_profile.id=3cixty_user_profile_knows.3cixty_user_profile_id AND (source like 'Google' OR source like 'Facebook') AND element = :myUID";
			List <Object[]> results = session.createSQLQuery(sql).setParameter("myUID", my3cixtyUID).list();
			
			for (Object [] obj: results) {
				Friend friend = new Friend();
				friend.setUid(obj[0].toString());
				friend.setSource(obj[2].toString());
				friend.setFirstName(obj[4].toString());
				friend.setLastName(obj[5].toString());
				friends.add(friend);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return friends;
	}
	
	/**
	 * Find all friends in the list of knows of the given 3cixty UID.
	 * @param my3cixtyUID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Friend> findAllFriendsInMyListOfKnows(String my3cixtyUID) {
		Session session = null;
		List <Friend> friends = new LinkedList<Friend>();
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String sql = "SELECT accountId, uid, source, firstName, lastName FROM 3cixty.3cixty_user_profile, 3cixty_account where 3cixty_user_profile.id = 3cixty_account.3cixty_user_id AND (source like 'Google' OR source like 'Facebook') AND uid in (SELECT element FROM 3cixty.3cixty_user_profile, 3cixty_user_profile_knows, 3cixty_account where 3cixty_user_profile.id= 3cixty_user_profile_id AND 3cixty_user_profile.id = 3cixty_account.3cixty_user_id AND (source like 'Google' OR source like 'Facebook') AND uid = :myUID)";
			List <Object[]> results = session.createSQLQuery(sql).setParameter("myUID", my3cixtyUID).list();
			
			for (Object [] obj: results) {
				Friend friend = new Friend();
				friend.setUid(obj[0].toString());
				friend.setSource(obj[2].toString());
				friend.setFirstName(obj[3].toString());
				friend.setLastName(obj[4].toString());
				friends.add(friend);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return friends;
	}
	
	
	private static boolean updateUserProfile(UserProfile userProfile) {
		Session session = null;
		boolean added = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();

			UserModel userModel = (UserModel) session.get(UserModel.class,
					userProfile.getModelIdInPersistentDB());
			
			session.beginTransaction();
		
			convertToUserModel(userProfile, userModel, session);
			
			session.update(userModel);

			session.getTransaction().commit();

			added = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return added;
	}

	private static boolean addUserProfile(UserProfile userProfile) {
		Session session = null;
		boolean added = false;
		try {
			UserModel userModel = new UserModel();
			userModel.setUid(userProfile.getHasUID());
			
			session = HibernateUtil.getSessionFactory().openSession();

			session.beginTransaction();
			
			session.save(userModel);
			
			convertToUserModel(userProfile, userModel, session);

			session.update(userModel);
			
			session.getTransaction().commit();
			
			userProfile.setModelIdInPersistentDB(userModel.getId());

			added = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return added;
	}

	private static void convertToUserModel(UserProfile userProfile,
			UserModel userModel, Session session) throws HibernateException {
		convertNameForPersistence(userProfile, userModel);
		convertAddressForPersistence(userProfile, userModel, session);
		userModel.setGender(userProfile.getHasGender());
		userModel.setProfileImage(userProfile.getProfileImage());
		if (!isNullOrEmpty(userProfile.getHasLastCrawlTime())) {
			userModel.setLastCrawlTimeToKB(Long.parseLong(userProfile.getHasLastCrawlTime()));
		}
		convertKnowsForPersistence(userProfile.getKnows(), userModel);
		convertAccountsForPersistence(userProfile, userModel, session);
		convertAccompanyingsForPersistence(userProfile, userModel, session);
	}

	private static void convertAccompanyingsForPersistence(
			UserProfile profile, UserModel userModel, Session session) {
		Set <Accompanying> accompanyings = profile.getAccompanyings();
		Set <AccompanyingModel> accompanyingModels = userModel.getAccompanyings();
		if (accompanyings == null || accompanyings.size() == 0) {
			if (DEBUG_MOD) LOGGER.info("list of accompanyings before saving: empty");
			if (accompanyingModels != null && accompanyingModels.size() > 0) {
				accompanyingModels.clear();
			}
			return;
		}
		if (DEBUG_MOD) LOGGER.info("number of accompanyings before saving: " + accompanyings.size());
		if (accompanyingModels == null) {
			accompanyingModels = new HashSet<AccompanyingModel>();
			userModel.setAccompanyings(accompanyingModels);
		}
		for (Iterator<AccompanyingModel> it = accompanyingModels.iterator(); it.hasNext();) {
			AccompanyingModel am = it.next();
			boolean found = AccompanyingUtils.findAccompanying(am, accompanyings);
			if (!found) {
				it.remove();
				session.delete(am);
			}
		}
		for (Accompanying accompanying: accompanyings) {
			boolean found = AccompanyingUtils.findAccompanying(accompanying, accompanyingModels);
			if (!found) {
				AccompanyingModel am = AccompanyingUtils.save(accompanying, userModel, session);
				if (am != null) accompanyingModels.add(am);
			}
		}
	}

	private static void convertAccountsForPersistence(UserProfile userProfile,
			UserModel userModel, Session session) throws HibernateException {
		Set <ProfileIdentities> pis = userProfile.getHasProfileIdenties();
		Set <AccountModel> accountModels = userModel.getAccounts();
		if (pis == null || pis.size() == 0) {
			if (accountModels != null && accountModels.size() > 0) {
				accountModels.clear();
			}
			return;
		}
		if (accountModels == null) {
			accountModels = new HashSet<AccountModel>();
			userModel.setAccounts(accountModels);
		}
		for (Iterator <AccountModel> it = accountModels.iterator(); it.hasNext(); ) {
			AccountModel accountModel = it.next();
			boolean found = findAccountModel(accountModel, pis);
			if (!found) {
				it.remove();
				session.delete(accountModel);
			}
		}
		for (ProfileIdentities pi: pis) {
			boolean found = findProfileIdentities(pi, accountModels);
			if (!found) {
				AccountModel accountModel = new AccountModel();
				accountModel.setUserModel(userModel);
				accountModel.setSource(pi.getHasSourceCarrier());
				accountModel.setAccountId(pi.getHasUserAccountID());
				accountModels.add(accountModel);
				session.save(accountModel);
			}
		}
	}

	private static boolean findProfileIdentities(ProfileIdentities pi,
			Set<AccountModel> accountModels) {
		for (AccountModel accountModel: accountModels) {
			if (accountModel.getAccountId().equals(pi.getHasUserAccountID()) &&
					accountModel.getSource().equals(pi.getHasSourceCarrier())) return true;
		}
		return false;
	}

	private static boolean findAccountModel(AccountModel accountModel,
			Set<ProfileIdentities> pis) {
		for (ProfileIdentities pi: pis) {
			if (accountModel.getAccountId().equals(pi.getHasUserAccountID()) &&
					accountModel.getSource().equals(pi.getHasSourceCarrier())) return true;
		}
		return false;
	}

	private static void convertKnowsForPersistence(Set <String> knowsStrs,
			UserModel userModel) {
		if (DEBUG_MOD) LOGGER.info("Entering in the method convertKnowsForPersistence");
		if (knowsStrs == null || knowsStrs.size() == 0) {
			if (DEBUG_MOD) LOGGER.info("Empty knows");
			userModel.setKnows(null);
		}
		else {
			if (DEBUG_MOD) LOGGER.info("Knows size: " + knowsStrs.size()+ ", " + knowsStrs);
			Set <String> knowsModel = userModel.getKnows();
			if (knowsModel == null) {
				knowsModel = new HashSet <String>();
				userModel.setKnows(knowsModel);
			}
			knowsModel.clear();
			knowsModel.addAll(knowsStrs);
		}
	}

	private static void convertAddressForPersistence(UserProfile userProfile,
			UserModel userModel, Session session) throws HibernateException {
		Address address = userProfile.getHasAddress();
		if (address == null) {
			AddressModel addrModel = userModel.getAddress();
			if (addrModel != null) session.delete(addrModel);
			userModel.setAddress(null);
			return;
		}
		AddressModel addressModel = userModel.getAddress();
		if (addressModel == null) {
			addressModel = new AddressModel();
			addressModel.setUserModel(userModel);
		}
		addressModel.setCountryName(address.getCountryName());
		addressModel.setTownName(address.getTownName());
		addressModel.setStreetAddress(address.getStreetAddress());
		addressModel.setPostalCode(address.getPostalCode());
		addressModel.setLatitude(address.getLatitude());
		addressModel.setLongitude(address.getLongitute());
		
		session.saveOrUpdate(addressModel);
		
		userModel.setAddress(addressModel);
	}

	private static void convertNameForPersistence(UserProfile userProfile,
			UserModel userModel) {
		Name name = userProfile.getHasName();
		userModel.setFirstName(name == null ? null : name.getGivenName());
		userModel.setLastName(name == null ? null : name.getFamilyName());
	}

	private static UserProfile convertToUserProfile(UserModel userModel) {
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(userModel.getUid());
		userProfile.setModelIdInPersistentDB(userModel.getId());
		
		convertName(userModel, userProfile);
		convertAddress(userModel, userProfile);
		
		if (!isNullOrEmpty(userModel.getGender())) userProfile.setHasGender(userModel.getGender());
		
		if (!isNullOrEmpty(userModel.getProfileImage()))
			userProfile.setProfileImage(userModel.getProfileImage());
		
		userProfile.setHasLastCrawlTime(userModel.getLastCrawlTimeToKB() + "");
		
		convertKnows(userModel, userProfile);
		convertAccounts(userModel, userProfile);
		convertAccompanyings(userModel, userProfile);
		return userProfile;
	}

	private static void convertAccompanyings(UserModel userModel,
			UserProfile userProfile) {
		Set <AccompanyingModel> ams = userModel.getAccompanyings();
		if (ams == null || ams.size() == 0) return;
		Set <Accompanying> accompanyings = new HashSet<Accompanying>();
		userProfile.setAccompanyings(accompanyings);
		for (AccompanyingModel am: ams) {
			Accompanying accompanying = AccompanyingUtils.createAccompanying(am);
			accompanyings.add(accompanying);
		}
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

	private static void convertKnows(UserModel userModel, UserProfile userProfile) {
		Set <String> knows = userModel.getKnows();
		if (knows == null || knows.size() == 0) return;
		Set <String> toKnows = new HashSet <String>();
		userProfile.setKnows(toKnows);
		toKnows.addAll(knows);
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

	public static Set<IDMapping> getIDMappings() {
		Session session = null;
		Set <IDMapping> mappings = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String sql = "SELECT distinct uid, accountId FROM 3cixty_user_profile, 3cixty_account  WHERE source LIKE \"" + MOBIDOT + "\" AND 3cixty_user_profile.id = 3cixty_user_id";
			SQLQuery query = session.createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			List <Object[]> results = query.list();
			if (results == null || results.size() == 0) return Collections.emptySet();
			mappings = new HashSet <IDMapping>();
			for (Object[] row: results) {
				IDMapping idMapping = new IDMapping();
				idMapping.setThreeCixtyID(((String) row[0]).trim());
				idMapping.setMobidotID(((String) row[1]).trim());
				mappings.add(idMapping);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return mappings;
	}

	public static Set<IDCrawlTimeMapping> getIDCrawlTimeMappings() {
		Session session = null;
		Set <IDCrawlTimeMapping> mappings = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			String sql = "SELECT uid, lastCrawlTimeToKB FROM 3cixty_user_profile";
			SQLQuery query = session.createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			List <Object[]> results = query.list();
			if (results == null || results.size() == 0) return Collections.emptySet();
			mappings = new HashSet <IDCrawlTimeMapping>();
			for (Object[] row: results) {
				IDCrawlTimeMapping mapping = new IDCrawlTimeMapping();
				mapping.setThreeCixtyID((String) row[0]);
				mapping.setLastCrawlTime((String) row[1]);
				mappings.add(mapping);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return mappings;
	}

	public static boolean updateKnows(UserProfile profile, Set<String> knows) {
		Session session = null;
		boolean successful = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();

			UserModel userModel = (UserModel) session.get(UserModel.class,
					profile.getModelIdInPersistentDB());
			
			session.beginTransaction();
		
			convertKnowsForPersistence(knows, userModel);
			
			session.update(userModel);

			session.getTransaction().commit();

			profile.setKnows(knows);
			successful = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			if (session != null) session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return successful;
	}
}
