package eu.threecixty.privacy.soa;

import java.util.List;

import eu.threecixty.privacy.semantic.Scope;

/**
 * Service with semantic markup allowing it to be adjusted to user profiles and
 * other kind of contextual data.
 * 
 * <p>
 * Instances of this interface should be created with a {@link ServiceFactory}
 * </p>
 */
public interface Service {

	/**
	 * Returns the name of this semantically annotated service.
	 */
	String getName();

	/**
	 * Returns the semantically annotated operations of this service.
	 */
	List<Operation> getOperations();

	/**
	 * Returns a newly created operation for this semantic service.
	 */
	Operation newOperation(String name, Scope description);

}
