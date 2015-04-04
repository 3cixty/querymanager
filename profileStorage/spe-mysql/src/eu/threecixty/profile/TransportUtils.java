package eu.threecixty.profile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import eu.threecixty.profile.oldmodels.Accompanying;
import eu.threecixty.profile.oldmodels.Transport;
import eu.threecixty.userprofile.AccompanyingModel;
import eu.threecixty.userprofile.TransportModel;


class TransportUtils {

	/**
	 * Converts transportModel (in the DB) to transport.
	 * @param transportModel
	 * @param transport
	 */
	protected static void convertTransport(TransportModel transportModel, Transport transport) {
		Set <AccompanyingModel> accompanyingModel = transportModel.getAccompanyings();
		if (accompanyingModel == null || accompanyingModel.size() == 0) return;
		Set <Accompanying> toAccompanying = new HashSet <Accompanying>();
		
		Iterator <AccompanyingModel> iterators = accompanyingModel.iterator();
		
		for ( ; iterators.hasNext(); ){
			AccompanyingModel singleAccompanyingModel=iterators.next();
			if (singleAccompanyingModel!=null){
				if (singleAccompanyingModel.getAccompanyScore()!=null && singleAccompanyingModel.getAccompanyTime()!=null
						&& singleAccompanyingModel.getAccompanyValidity()!=null
						&& !isNullOrEmpty(singleAccompanyingModel.getHasAccompanyUserid1ST())
						&& !isNullOrEmpty(singleAccompanyingModel.getHasAccompanyUserid2ST())) {
					Accompanying accompanying =new Accompanying();
					accompanying.setHasAccompanyScore(singleAccompanyingModel.getAccompanyScore());
					accompanying.setHasAccompanyValidity(singleAccompanyingModel.getAccompanyValidity());
					accompanying.setHasAccompanyTime(singleAccompanyingModel.getAccompanyTime());
					accompanying.setHasAccompanyUserid2ST(singleAccompanyingModel.getHasAccompanyUserid2ST());
					accompanying.setHasAccompanyUserid1ST(singleAccompanyingModel.getHasAccompanyUserid1ST());
					toAccompanying.add(accompanying);
				}
			}
		}
		
		transport.setHasAccompanyings(toAccompanying);
	}
	
	/**
	 * Converts transport to transportModel (in the DB).
	 * @param transport
	 * @param transportModel
	 * @param session
	 * @throws HibernateException
	 */
	protected static void convertTransportForPersistence(Transport transport, TransportModel transportModel, Session session) throws HibernateException {
		// TODO
		Set <Accompanying> accompanying = transport.getHasAccompanyings();
		if (accompanying == null || accompanying.size()==0) {
			Set <AccompanyingModel> accompanyingModel = transportModel.getAccompanyings();
			session.delete(accompanyingModel);
			transportModel.setAccompanyings(null);
			return;
		}
		Set <AccompanyingModel> accompanyingModel = transportModel.getAccompanyings();
		if (accompanyingModel == null) {
			accompanyingModel = new HashSet <AccompanyingModel>();
		}
		
		Iterator <Accompanying> iterators = accompanying.iterator();
		for ( ; iterators.hasNext(); ){
			Accompanying singleAccompanying=iterators.next();
			if (singleAccompanying.getHasAccompanyScore()!=null && singleAccompanying.getHasAccompanyValidity()!=null
					&& singleAccompanying.getHasAccompanyTime()!=null
					&& !isNullOrEmpty(singleAccompanying.getHasAccompanyUserid1ST())
					&& !isNullOrEmpty(singleAccompanying.getHasAccompanyUserid2ST())) {
				AccompanyingModel singleAccompanyingModel=new AccompanyingModel();
				singleAccompanyingModel.setAccompanyScore(singleAccompanying.getHasAccompanyScore());
				singleAccompanyingModel.setAccompanyValidity(singleAccompanying.getHasAccompanyValidity());
				singleAccompanyingModel.setAccompanyTime(singleAccompanying.getHasAccompanyTime());
				singleAccompanyingModel.setHasAccompanyUserid1ST(singleAccompanying.getHasAccompanyUserid1ST());
				singleAccompanyingModel.setHasAccompanyUserid2ST(singleAccompanying.getHasAccompanyUserid2ST());
				
				accompanyingModel.add(singleAccompanyingModel);	
			}
		}
		transportModel.setAccompanyings(accompanyingModel);
	}
	
	private TransportUtils() {
	}
	private static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}
}
