package eu.threecixty.privacy.storage;

import eu.threecixty.privacy.semantic.Resource;

/**
 * A policy to control access to secured resources.
 * Policies may supports the enforcement of a number of obligations, which are 
 * bound tightly to data. For instance, one can impose a specific retention 
 * period, as well as the production of user's notifications and/or logging 
 * under certain conditions.
 * Complex policies may be constructed from 
 * <a href="http://en.wikipedia.org/wiki/XACML">XACML</a> documents. 
 */
public interface Policy {
    
    /**
     * Indicates if this policy allows read access of the specified resource
     * using the given credential. 
     * 
     * @param resource the data, service or system component whose read access is checked.
     * @param credential the credential of the subject requesting access or <code>null</code> if anonymous.
     */
    boolean isReadAuthorized(Resource<?> resource, Credential credential);
    
    /**
     * Indicates if this policy allows write access of the specified resource
     * using the given credential. 
     * 
     * @param resource the data, service or system component whose write access is checked.
     * @param credential the credential of the subject requesting access or <code>null</code> if anonymous.
     */
    boolean isWriteAuthorized(Resource<?> resource, Credential credential);
          
}
