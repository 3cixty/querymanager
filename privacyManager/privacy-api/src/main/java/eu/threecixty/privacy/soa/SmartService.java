package eu.threecixty.privacy.soa;

import java.util.Map;

import eu.threecixty.privacy.storage.Credential;

/**
 * Provides the parameters to be used when calling a service.
 * 
 * <p>
 * Given the semantic description of any operation, it is able to determine the
 * most appropriate values for the parameters of that operation. Any required
 * data should be accessed through a
 * {@link eu.threecixty.privacy.storage.Storage} companion, typically to get or
 * set private information.
 * </p>
 * 
 * <p>
 * Implementations of this interface should not perform service composition or
 * call new services (systems or others) to retrieve parameters values not
 * directly available from the data set. However, if a mandatory parameter is
 * not available, it should be input by the user (if in an interactive
 * communication) to be stored with the proper access policy afterwards.
 * </p>
 * 
 * @see eu.threecixty.privacy.soa.Service
 */
public interface SmartService {

	/**
	 * Returns the most appropriate parameters values to call the specified
	 * operation.
	 * 
	 * @param operation
	 *            the semantically annotated operation for which the parameter
	 *            values are retrieved.
	 * @param credential
	 *            the credential of the subject requesting the parameters values
	 *            or <code>null</code> for anonymous access.
	 * @throws SecurityException
	 *             if the policy associated to mandatory parameter does not
	 *             allow read access.
	 */
	Map<Parameter<?>, ?> getParameterValues(Operation operation,
			Credential credential) throws SecurityException;

}
