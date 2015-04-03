package eu.threecixty.profile;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import eu.threecixty.profile.oldmodels.Transport;
import eu.threecixty.userprofile.TransportModel;


class TransportUtils {

	/**
	 * Converts transportModel (in the DB) to transport.
	 * @param transportModel
	 * @param transport
	 */
	protected static void convertTransport(TransportModel transportModel, Transport transport) {
		// TODO
	}
	
	protected static void convertTransport(Transport transport, TransportModel transportModel, Session session) throws HibernateException {
		// TODO
	}
	
	private TransportUtils() {
	}
}
