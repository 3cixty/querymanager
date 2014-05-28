package eu.threecixty.privacy.soa;

import eu.threecixty.privacy.semantic.Resource;


/**
 * A parameter to a semantically annotated operation.
 *
 * @param <V> the value type held by this parameter.
 */
public interface Parameter<V> {
    
    /**
     * Returns the name of this parameter.
     */
    String getName();
    
    /**
     * Returns the semantic resource representing this parameter.
     */
    Resource<V> getResource();

    /**
     * Indicates if this parameter is optional or mandatory (default {@code false}).
     */
    boolean isOptional();
    
    /**
     * Sets the optional indicator.
     */
    void setOptional(boolean isOptional);
    
  }
