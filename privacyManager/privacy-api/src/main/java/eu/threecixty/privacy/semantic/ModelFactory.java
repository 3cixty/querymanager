package eu.threecixty.privacy.semantic;

/**
 *  OSGi service to create new models.
 */
public interface ModelFactory {

    /**
     * Returns a new empty model for the specified ontology URL
     */
    Model newModel(String ontologyURL);
    
}
