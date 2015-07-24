package eu.threecixty.partners;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import eu.threecixty.db.HibernateUtil;



public class PartnerImpl implements Partner {
	
	private static final Logger LOGGER = Logger.getLogger(
			 PartnerImpl.class.getName());
	
	private static final Partner instance = new PartnerImpl();
	
	public static Partner getInstance() {
		return instance;
	}
	 
	@Override
	public boolean addUser(PartnerUser user) {
		if (user == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "FROM PartnerUser P WHERE P.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, user.getUid()).list();
			if (results.size() > 0) {
				session.close();
				return false;
			}
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		}
		if (session != null) session.close();
		return false;
	}

	@Override
	public boolean updateUser(PartnerUser user) {
		if (user == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		}
		if (session != null) session.close();
		return false;
	}

	@Override
	public boolean deleteUser(PartnerUser user) {
		if (user == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.delete(user);
			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		}
		if (session != null) session.close();
		return false;
	}

	@Override
	public boolean exist(String uid) {
		PartnerUser mobidotUser = getUser(uid);
		return mobidotUser != null;
	}

	@Override
	public PartnerUser getUser(String uid) {
		if (uid == null) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "FROM PartnerUser P WHERE P.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			if (results.size() == 0) {
				session.close();
				return null;
			}
			PartnerUser user = (PartnerUser) results.get(0);
			session.close();
			return user;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		}
		if (session != null) session.close();
		return null;
	}

	@Override
	public PartnerAccount findAccount(PartnerUser user, String appid, String role) {
		if (user == null || appid == null) return null;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			PartnerUser freshUser = (PartnerUser) session.get(PartnerUser.class, user.getId());
			PartnerAccount result = null;
			for (PartnerAccount account: freshUser.getPartnerAccounts()) {
				if (role == null) { // without checking role
					if (appid.equals(account.getAppId())) {
						result = account;
						break;
					}
				} else {
					if (appid.equals(account.getAppId()) && role.equals(account.getRole())) {
						result = account;
						break;
					}
				}
			}
			session.close();
			return result;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		}
		if (session != null) session.close();
		return null;
	}

	@Override
	public boolean addAccount(PartnerAccount account) {
		if (account == null) return false;
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			session.saveOrUpdate(account);
			session.getTransaction().commit();
			session.close();
			return true;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		}
		if (session != null) session.close();
		return false;
	}
	
	private PartnerImpl() {
	}

	@Override
	public List<PartnerAccount> getPartnerAccounts(String uid) {
		List <PartnerAccount> partnerAccounts = new LinkedList <PartnerAccount>();
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			String hql = "FROM PartnerAccount P WHERE P.partnerUser.uid = ?";
			Query query = session.createQuery(hql);
			List <?> results = query.setString(0, uid).list();
			if (results != null) {
				for (Object obj: results) {
					partnerAccounts.add((PartnerAccount) obj);
				}
			}
			session.close();
			return partnerAccounts;
		} catch (HibernateException e) {
			LOGGER.error(e.getMessage());
		}
		if (session != null) session.close();
		return null;
	}
}
