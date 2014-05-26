package eu.threecixty.privacy.soa;

/**
 * A factory to create {@link Service} objects.
 * 
 * <p>
 * Concrete factories could create Services by program or by parsing <a
 * href="http://www.w3.org/2002/ws/sawsdl/">SAWSDL</a> documents.
 * </p>
 */
public interface ServiceFactory {

	/**
	 * Returns a newly created semantic service.
	 * 
	 * @param name
	 *            the name of the service to create
	 */
	Service newService(String name);

}
