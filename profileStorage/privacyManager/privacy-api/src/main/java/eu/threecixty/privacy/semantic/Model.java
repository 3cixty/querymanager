package eu.threecixty.privacy.semantic;

import java.util.Set;

/**
 *  A model for the construction of semantics artifacts.
 */
public interface Model {

    /**
     * Returns all the entities in this model.
     */
    Set<Entity> getEntities();
    
    /**
     * Returns a new entity.
     */
    Entity newEntity(String entity);

    /**
     * Returns a new scope.
     */
    Scope newScope(String scope);
    
    /**
     * Returns a new resource.
     */
    <V> Resource<V> newResource(String resource, Class<V> type);
     
    /**
     * Returns a new statement.
     */
    Statement newStatement(String statement);
         
}
