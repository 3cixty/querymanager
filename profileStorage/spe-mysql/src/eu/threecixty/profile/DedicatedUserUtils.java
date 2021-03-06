/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.db.HibernateUtil;
import eu.threecixty.userprofile.DedicatedUser;
import eu.threecixty.userprofile.UserActivation;
import eu.threecixty.userprofile.UserActivation.ActivationType;
import eu.threecixty.userprofile.UserModel;

/**
 * 
 * Utility class to create, update and activate 3cixty dedicated accounts in database. 
 *
 */
public class DedicatedUserUtils {
	
	private static final int ITERATION_NUMBER = 1000;
	private static final String UTF8 = "UTF-8";
	final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private static final Logger LOGGER = Logger.getLogger(
			DedicatedUserUtils.class.getName());

	/**
	 * Creates a dedicated 3cixty user.
	 * @param email
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param key
	 * @return The activation code.
	 */
	public static String createDedicatedUser(String email,
			String password, String firstName, String lastName, String key) {
		if (isNullOrEmpty(email) || isNullOrEmpty(password)
				|| isNullOrEmpty(firstName) || isNullOrEmpty(lastName)) return null;
		String hashedPassword = hashPassword(password, email); // email is salt
		if (hashedPassword == null) throw new RuntimeException("The algorithm for hashing password (SHA256) doesn't exist");
		String uid = UUID.randomUUID().toString();
		Session session = null;
		String code = null;
		try {
			DedicatedUser dedicatedUser = new DedicatedUser();
			dedicatedUser.setEmail(email);
			dedicatedUser.setEmailConfirmed(false);
			dedicatedUser.setPassword(hashedPassword);
			dedicatedUser.setUid(uid);
			dedicatedUser.setAppkey(key);
			
			UserModel userModel = createUserModel(uid, firstName, lastName);
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.save(dedicatedUser);
			
			session.save(userModel);
			
			UserActivation userActivation = createUserActivation(dedicatedUser.getId(),
					TokenCacheManager.getInstance().getAppCache(key).getId(),
					ActivationType.CREATION);
			session.save(userActivation);
			session.getTransaction().commit();
			code = userActivation.getCode();
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return code;
	}
	
	/**
	 * This method is to activate a creation code. If successful, it will return appId and null otherwise.
	 * @param code
	 * @return
	 * @throws ActivationException
	 */
	@SuppressWarnings("unchecked")
	public static Integer activateForCreation(String code) throws ActivationException {
		Session session = null;
		boolean ok = false;
		Integer appId = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "FROM UserActivation WHERE code = ?";
			List <Object> results = session.createQuery(hql).setString(0, code).list();
			if (results != null && results.size() > 0) {
				UserActivation userActivation = (UserActivation) results.get(0);
				if (userActivation.isUsed()) throw new ActivationException("Code was aready used");
				if (userActivation.getType() != ActivationType.CREATION)
					throw new ActivationException("The email was already confirmed");
				String hql1 = "FROM DedicatedUser WHERE id = ?";
				List <Object> list = session.createQuery(hql1).setInteger(0,
						userActivation.getDedicatedUserId()).list();
				
				if (list != null && list.size() > 0) {
					DedicatedUser dedicatedUser = (DedicatedUser) list.get(0);
					
					session.beginTransaction();
					dedicatedUser.setEmailConfirmed(true);
					session.save(dedicatedUser);
					
					userActivation.setUsed(true);
					session.save(userActivation);

					session.getTransaction().commit();
					ok = true;
					appId = userActivation.getAppId();
				}
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		if (ok) return appId;
		return null;
	}
	
	/**
	 * This method is to activate a reset code. If successful, it will return appId and null otherwise.
	 * @param code
	 * @return
	 * @throws ActivationException
	 */
	@SuppressWarnings("unchecked")
	public static Integer activateForResettingPassword(String code) throws ActivationException {
		Session session = null;
		boolean ok = false;
		Integer appId = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "FROM UserActivation WHERE code = ?";
			List <Object> results = session.createQuery(hql).setString(0, code).list();
			if (results != null && results.size() > 0) {
				UserActivation userActivation = (UserActivation) results.get(0);
				if (userActivation.isUsed()) throw new ActivationException("Code was aready used");
				if (userActivation.getType() != ActivationType.FORGOTTEN_PASSWORD)
					throw new ActivationException("The email needs to be confirmed first");
					
				session.beginTransaction();

				userActivation.setUsed(true);
				session.save(userActivation);

				session.getTransaction().commit();
				ok = true;
				appId = userActivation.getAppId();
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		if (ok) return appId;
		return null;
	}
	
	/**
	 * Reset password
	 * @param email
	 * @return Code to reset password.
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static String resetPassword(String email, String key) throws Exception {
		if (isNullOrEmpty(email)) return null;
		Session session = null;
		String code = null;
		boolean ex = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql1 = "FROM DedicatedUser WHERE email = ?";
			List <Object> list = session.createQuery(hql1).setString(0, email).list();
			
			if (list != null && list.size() > 0) {
				DedicatedUser dedicatedUser = (DedicatedUser) list.get(0);
				Integer appId = TokenCacheManager.getInstance().getAppCache(key).getId();
				session.beginTransaction();
				
				UserActivation userActivation = createUserActivation(dedicatedUser.getId(), appId,
						ActivationType.FORGOTTEN_PASSWORD);
				session.save(userActivation);
				session.getTransaction().commit();
				code = userActivation.getCode();
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
			ex = true;
		} finally {
			if (session != null) session.close();
		}
		if (ex) throw new Exception("Error to update database");
		return code;
	}
	
	/**
	 * Change password.
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean changePassword(String email, String oldPassword,
			String newPassword) {
		if (isNullOrEmpty(email) || isNullOrEmpty(oldPassword)
				|| isNullOrEmpty(newPassword)) return false;
		Session session = null;
		boolean ok = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "FROM DedicatedUser WHERE email = ?";
			List <Object> list = session.createQuery(hql).setString(0,
					email).list();
			
			if (list != null && list.size() > 0) {
				DedicatedUser dedicatedUser = (DedicatedUser) list.get(0);
				String oldHashedPassword = hashPassword(oldPassword, email);
				if (oldHashedPassword.equals(dedicatedUser.getPassword())) {
					String newHashedPassword = hashPassword(newPassword, email);
					session.beginTransaction();

					dedicatedUser.setPassword(newHashedPassword);
					session.save(dedicatedUser);
					session.getTransaction().commit();
					ok = true;
				}
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}

	/**
	 * Set password.
	 * @param email
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean setPassword(String email, String password) {
		if (isNullOrEmpty(email) || isNullOrEmpty(password)) return false;
		Session session = null;
		boolean ok = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "FROM DedicatedUser WHERE email = ?";
			List <Object> list = session.createQuery(hql).setString(0,
					email).list();
			
			if (list != null && list.size() > 0) {
				DedicatedUser dedicatedUser = (DedicatedUser) list.get(0);
				String hashedPassword = hashPassword(password, email);
				session.beginTransaction();

				dedicatedUser.setPassword(hashedPassword);
				session.save(dedicatedUser);
				session.getTransaction().commit();
				ok = true;
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Checks a given email and password if they match in the database.
	 * @param email
	 * @param password
	 * @return
	 * @throws AccountNotActivatedException 
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkPassword(String email, String password) throws AccountNotActivatedException {
		if (isNullOrEmpty(email) || isNullOrEmpty(password)) return false;
		Session session = null;
		boolean ok = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "FROM DedicatedUser WHERE email = ?";
			List <Object> list = session.createQuery(hql).setString(0,
					email).list();
			
			if (list != null && list.size() > 0) {
				DedicatedUser dedicatedUser = (DedicatedUser) list.get(0);
				if (!dedicatedUser.isEmailConfirmed()) throw new AccountNotActivatedException();
				String hashedPassword = hashPassword(password, email);
				if (hashedPassword.equals(dedicatedUser.getPassword())) {
					ok = true;
				}
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Update first name and last name.
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean update(String email, String firstName, String lastName) {
		if (isNullOrEmpty(email) || isNullOrEmpty(firstName) || isNullOrEmpty(lastName)) return false;
		Session session = null;
		boolean ok = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			String dedicatedUserHql = "FROM DedicatedUser WHERE email = ? ";
			List <Object> list = session.createQuery(dedicatedUserHql).setString(0,
					email).list();
			
			if (list != null && list.size() > 0) {
				DedicatedUser dedicatedUser = (DedicatedUser) list.get(0);
				
				String userModelHql = "FROM UserModel WHERE uid = ?";
				
				List <Object> results = session.createQuery(userModelHql).setString(0,
						dedicatedUser.getUid()).list();
				if (results != null && results.size() > 0) {
					UserModel userModel = (UserModel) results.get(0);
					userModel.setFirstName(firstName);
					userModel.setLastName(lastName);
					session.beginTransaction();
					session.save(userModel);
					session.getTransaction().commit();
					ok = true;
				}
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Gets email from a given reset code.
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getEmail(String code) {
		Session session = null;
		String email = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "FROM UserActivation WHERE code = ?";
			List <Object> results = session.createQuery(hql).setString(0, code).list();
			if (results != null && results.size() > 0) {
				UserActivation userActivation = (UserActivation) results.get(0);
				String hql1 = "FROM DedicatedUser WHERE id = ?";
				List <Object> list = session.createQuery(hql1).setInteger(0,
						userActivation.getDedicatedUserId()).list();
				if (list != null && list.size() > 0) {
					email = ((DedicatedUser) list.get(0)).getEmail();
				}
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return email;
	}
	
	/**
	 * Gets 3cixty uid from a given email.
	 * @param code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getUid(String email) {
		Session session = null;
		String uid = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

				String hql1 = "FROM DedicatedUser WHERE email = ?";
				List <Object> list = session.createQuery(hql1).setString(0, email).list();
				if (list != null && list.size() > 0) {
					uid = ((DedicatedUser) list.get(0)).getUid();
				}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return uid;
	}
	
	/**
	 * Checks if a given email already existed in the database.
	 * @param email
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean exists(String email) {
		if (isNullOrEmpty(email)) return false;
		Session session = null;
		boolean ok = false;
		try {
			
			session = HibernateUtil.getSessionFactory().openSession();
			
			String dedicatedUserHql = "FROM DedicatedUser WHERE email = ? ";
			List list = session.createQuery(dedicatedUserHql).setString(0,
					email).list();
			
			if (list != null && list.size() > 0) {
				ok = true;
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	private static UserModel createUserModel(String uid, String firstName,
			String lastName) {
		UserModel userModel = new UserModel();
		userModel.setUid(uid);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		return userModel;
	}

	private static UserActivation createUserActivation(Integer dedicatedUserId, Integer appId,
			ActivationType type) {
		if (dedicatedUserId == null) return null;
		String code = UUID.randomUUID().toString();
		UserActivation userActivation = new UserActivation();
		userActivation.setCode(code);
		userActivation.setUsed(false);
		userActivation.setSent(false);
		userActivation.setDedicatedUserId(dedicatedUserId);
		userActivation.setType(type);
		userActivation.setCreation(System.currentTimeMillis());
		userActivation.setAppId(appId);
		return userActivation;
	}
	
	/**
	 * Hashes a given password with a given salt. Repeat for 1000 times.
	 * @param password
	 * @param salt
	 * @return
	 */
	private static String hashPassword(String password, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			md.update((salt.toLowerCase() ).getBytes(UTF8));
			
			byte[] input = md.digest(password.getBytes(UTF8));
			for (int i = 0; i < ITERATION_NUMBER; i++) {
				input = md.digest(input);
			}
			return bytesToHex(input);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
	
	private DedicatedUserUtils() {
	}
}
