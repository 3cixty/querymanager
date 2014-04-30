package eu.threecixty.privacy.storage;

import java.io.IOException;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.semantic.Scope;

/**
 * <p> OSGi service to store/retrieve sensitive information.
 *     The information stored using this service is regulated by 
 *     access {@link Policy policies}.</p>
 *     
 * <p> Access policies are of course propagated to any semantic subset of the 
 *     policy intended scope of usage (aka subject) as illustrated below:<pre><code>
 *     
 *     Resource&lt;Address> userAddress = ...; // The user home address.
 *     Scope foodDelivery = ...;
 *     Policy readAccessForFoodDelivery = secureStorage.newReadAccessPolicy(foodDelivery);
 *     secureStorage.setPolicy(userAddress, readAccessForFoodDelivery, null);
 *     
 *     Scope dominoPizza = ...; // Within the foodDelivery scope.
 *     Credential dominoPizzaCredential = secureStorage.newCredential(dominoPizza, user, password);
 *     Address address = secureStorage.getValue(userAddress, dominoPizzaCredential); 
 *        // Ok, dominoPizza is implicitly allowed read access.
 *     </code></pre>
 */
public interface Storage<T> {

    /**
     * Returns the specified resource value or <code>null</code> if none. 
     * 
     * @param resource the entity whose current value is returned.
     * @param credential the credential of the subject requesting read access or <code>null</code> for anonymous access.
     * @throws SecurityException if the policy associated to the resource does not allow read access.
     * @throws IOException if an unexpected error occurred while accessing the resource
     */
    <V extends T> V getValue(Resource<V> resource, Credential credential) throws SecurityException, IOException;

	/**
	 * Stores the specified resource value and overwrite its policy at the same
	 * time.
	 * 
	 * @param resource
	 *            the entity whose current value is stored.
	 * @param value
	 *            the new value of the resource.
	 * @param credential
	 *            the credential of the subject requesting write access or
	 *            <code>null</code> for anonymous access.
	 * @param the
	 *            new policy defining the access modes to the resource
	 * @throws SecurityException
	 *             if the policy associated to the resource does not allow write
	 *             access.
     * @throws IOException if an unexpected error occurred while accessing the resource
	 */
	<V extends T, Y> void setValue(Resource<V> resource, Y value,
			Credential credential) throws SecurityException, IOException;

   /**
    * Returns the policy attached to a resource or <code>null</code> if none.
    * This method is typically used to retrieve current policies in order to 
    * combine them with new ones. 
    * 
    * @param resource the entity for which the attached policy is returned.
    * @param credential the credential of the subject requesting the policy.
    * @throws SecurityException if the entity current policy does not allow read access.
    * @throws IOException if an unexpected error occurred while accessing the resource
    */
   Policy getPolicy(Resource<? extends T> resource, Credential credential) throws SecurityException, IOException;
 
   /**
    * Attaches a policy to an entity replacing the previous one if any.
    * 
    * @param resource the entity to which the specified policy is attached.
    * @param policy the new policy associated to the entity or <code>null</code> 
    *        to delete the policy (resulting in public access to the entity).
    * @param credential the credential of the subject requesting the policy update.
    * @throws SecurityException if the entity current policy does not allow write access. 
    * @throws IOException if an unexpected error occurred while accessing the resource
    */
   void setPolicy(Resource<? extends T> resource, Policy policy, Credential credential) throws SecurityException, IOException;
   
   /**
    * Returns a simple credential from a user/password combination. 
    *  
    * @param scope the scope of the credential.
    * @param user the user. 
    * @param password the user password
    * @throws SecurityException if the specified user/password does not match.
    */
   Credential newCredential(Scope scope, String user, String password) throws SecurityException, IOException;
   
   /**
    * Returns a simple read access policy limited to the specified scope. 
    *  
    * @param scope the scope of the returned policy.
    */
   Policy newReadAccessPolicy(Scope scope) throws IOException;
   
   /**
    * Returns a simple write access policy limited to the specified scope. 
    *  
    * @param scope the scope of the returned policy.
    */
   Policy newWriteAccessPolicy(Scope scope) throws IOException;
}
