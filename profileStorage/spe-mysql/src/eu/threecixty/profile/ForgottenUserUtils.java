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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;
import eu.threecixty.userprofile.ForgottenUser;

/**
 * This utility class is used to insert, update, and delete information about users
 * who want to be forgotten.
 *
 */
public class ForgottenUserUtils {

	private static final Logger LOGGER = Logger.getLogger(
			ForgottenUserUtils.class.getName());

	/**Attribute which is used to improve performance for logging out information*/
	//private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	/**
	 * Adds forgotten user into database.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean add(ForgottenUser forgottenUser) {
		if (forgottenUser == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.save(forgottenUser);
			if (forgottenUser.isNeedToAvoidBeingCrawled()) removeAllUserProfile(session, forgottenUser.getUid());
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Update a given forgotten user in database.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean update(ForgottenUser forgottenUser) {
		if (forgottenUser == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.update(forgottenUser);
			if (forgottenUser.isNeedToAvoidBeingCrawled()) removeAllUserProfile(session, forgottenUser.getUid());
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Delete a given forgotten user in database.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean detele(ForgottenUser forgottenUser) {
		if (forgottenUser == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			session.delete(forgottenUser);
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Delete the corresponding forgotten user of a given UID.
	 * @param forgottenUser
	 * @return
	 */
	public static boolean delete(String uid) {
		if (uid == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			
			String hql = "DETELE FROM ForgottenUser WHERE uid = ?";
			session.createQuery(hql).setString(0, uid).executeUpdate();
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	/**
	 * Get the corresponding forgotten user of a given UID.
	 * @param forgottenUser
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ForgottenUser get(String uid) {
		if (uid == null) return null;
		Session session = null;
		ForgottenUser forgottenUser = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			String hql = "FROM ForgottenUser WHERE uid = ?";
			List <Object> list = session.createQuery(hql).setString(0, uid).list();
			if (list != null && list.size() > 0) {
				forgottenUser = (ForgottenUser) list.get(0);
			}
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (session != null) session.close();
		}
		return forgottenUser;
	}
	
	public static boolean deleteProfile(String uid) {
		if (uid == null) return false;
		Session session = null;
		boolean ok = false;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			session.beginTransaction();
			
			removeAllUserProfile(session, uid);
			session.getTransaction().commit();
			ok = true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
			session.getTransaction().rollback();
		} finally {
			if (session != null) session.close();
		}
		return ok;
	}
	
	private static void removeAllUserProfile(Session session, String uid)
			throws HibernateException {
		List <String> sqls = getSqlsToRemoveAllUSerProfile(uid);
		for (String sql: sqls) {
			session.createSQLQuery(sql).executeUpdate();
		}
	}
	
	private static List<String> getSqlsToRemoveAllUSerProfile(String uid) throws HibernateException {
		if (!uid.matches("[0-9]+")) throw new HibernateException("UID must only contain digits");
		List <String> list = new LinkedList <String>();
		list.add("DELETE FROM 3cixty_user_profile_knows WHERE 3cixty_user_profile_id IN (SELECT id FROM 3cixty_user_profile WHERE uid LIKE '" + uid + "');");
		list.add("DELETE FROM 3cixty_user_accessToken WHERE uid like '" + uid + "';");
		list.add("DELETE FROM 3cixty_tray WHERE uid like '" + uid + "';");
		list.add("DELETE FROM 3cixty_partner_account WHERE partner_user_id in (SELECT id FROM 3cixty_partner_user WHERE uid LIKE '" + uid + "');");
		list.add("DELETE FROM 3cixty_address WHERE 3cixty_user_id IN (SELECT id FROM 3cixty_user_profile WHERE uid LIKE '" + uid + "');");
		list.add("DELETE FROM 3cixty_account WHERE 3cixty_user_id IN (SELECT id FROM 3cixty_user_profile WHERE uid LIKE '" + uid + "');");
		list.add("DELETE FROM 3cixty_accompanying WHERE 3cixty_user_id IN (SELECT id FROM 3cixty_user_profile WHERE uid LIKE '" + uid + "');");
		list.add("DELETE FROM 3cixty_partner_user WHERE uid LIKE '" + uid + "';");
		list.add("DELETE FROM 3cixty_user WHERE uid LIKE '" + uid + "';");
		list.add("DELETE FROM 3cixty_user_profile WHERE uid LIKE '" + uid + "';");
		return list;
	}

	private ForgottenUserUtils() {
	}
}
