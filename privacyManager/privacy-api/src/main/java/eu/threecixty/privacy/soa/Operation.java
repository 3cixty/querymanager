package eu.threecixty.privacy.soa;

import java.util.List;
import java.util.Set;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.semantic.Scope;
import eu.threecixty.privacy.semantic.Statement;

/**
 * A semantically annotated operation.
 */
public interface Operation {
    
    /**
     * Returns the name of this operation.
     */
    String getName();
    
    /**
     * Returns a high level description of the operation or <code>null</code>
     * if none.
     */
    Scope getDescription();
    
    /**
     * Returns the set of semantic statements (or expressions represented using
     * the concepts in a semantic model) that are required to be true before 
     * this operation can be successfully invoked.
     */
    Set<Statement> getPrecondition();
    
    /**
     * Returns the set of semantic statements (or expressions represented using
     * the concepts in a semantic model) that must be true after an operation
     * completes execution after being invoked. Different effects can be true 
     * depending on whether the operation completed successfully or unsuccessfully.
     */
    Set<Statement> getEffect();
    
    /**
     * Returns the semantically annotated input parameters of this operation. 
     */
    List<Parameter<?>> getInputParameters();
    
    /**
     * Returns the semantically annotated output parameters of this operation. 
     */
    List<Parameter<?>> getOutputParameters();
    
    /**
     * Adds the specified precondition.
     */
    void addPrecondition(Statement statement);
    
    /**
     * Adds the specified effect.
     */
    void addEffect(Statement statement);
    
    /**
     * Returns a new input parameter. 
     */
    <V> Parameter<V> newInputParameter(String name, Resource<V> resource);
    
    /**
     * Returns a new output parameter. 
     */
    <V> Parameter<V> newOutputParameter(String name, Resource<V> resource);
    
}
