package eu.threecixty.privacy.storage;

import eu.threecixty.privacy.semantic.Scope;

/**
 * <p> A proof of qualification, competence, or authority generally issued 
 *     by a third party. Credentials may be necessary to retrieve resources 
 *     value from the {@link Storage}. Simple credentials may hold 
 *     user ID / password, more advanced credentials may include certificates.</p>
 *     
 * <p> Credentials are usually checked when accessing resources from the 
 *    {@link SecureStorage}.</p>
 * 
 * <p> Applications may create unique credentials allowing private access to 
 *     their data. For example:<pre><code>
 *     final Scope myApp = ...;
 *     Credential myCredential = new Credential() {
 *          public Scope getSubject() { return myApp; }
 *     };
 *     Policy myPolicy = new Policy() {
 *         public boolean isReadAuthorized(Resource resource, Credential credential) {
 *             return (credential == myCredential);
 *         }
 *         public boolean isWriteAuthorized(Resource resource, Credential credential) {
 *             return (credential == myCredential);
 *         }
 *     };
 *     secureStorage.setPolicy(myResource, myPolicy, myCredential);
 *     ...
 *     secureStorage.query(myResource, myCredential); // Only myApp can retrieve myResource value.
 *  </code></pre>
 */
public interface Credential {
    
    /**
     * Returns the subject of this credential.
     */
    Scope getSubject();
    
}
