package eu.threecixty.models;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: ProfileIdentities <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */

public interface ProfileIdentities extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasSource
     */
     
    /**
     * Gets all property values for the hasSource property.<p>
     * 
     * @returns a collection of values for the hasSource property.
     */
    Collection<? extends String> getHasSource();

    /**
     * Checks if the class has a hasSource property value.<p>
     * 
     * @return true if there is a hasSource property value.
     */
    boolean hasHasSource();

    /**
     * Adds a hasSource property value.<p>
     * 
     * @param newHasSource the hasSource property value to be added
     */
    void addHasSource(String newHasSource);

    /**
     * Removes a hasSource property value.<p>
     * 
     * @param oldHasSource the hasSource property value to be removed.
     */
    void removeHasSource(String oldHasSource);



    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasUserAccountID
     */
     
    /**
     * Gets all property values for the hasUserAccountID property.<p>
     * 
     * @returns a collection of values for the hasUserAccountID property.
     */
    Collection<? extends String> getHasUserAccountID();

    /**
     * Checks if the class has a hasUserAccountID property value.<p>
     * 
     * @return true if there is a hasUserAccountID property value.
     */
    boolean hasHasUserAccountID();

    /**
     * Adds a hasUserAccountID property value.<p>
     * 
     * @param newHasUserAccountID the hasUserAccountID property value to be added
     */
    void addHasUserAccountID(String newHasUserAccountID);

    /**
     * Removes a hasUserAccountID property value.<p>
     * 
     * @param oldHasUserAccountID the hasUserAccountID property value to be removed.
     */
    void removeHasUserAccountID(String oldHasUserAccountID);



    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasUserInteractionMode
     */
     
    /**
     * Gets all property values for the hasUserInteractionMode property.<p>
     * 
     * @returns a collection of values for the hasUserInteractionMode property.
     */
    Collection<? extends Object> getHasUserInteractionMode();

    /**
     * Checks if the class has a hasUserInteractionMode property value.<p>
     * 
     * @return true if there is a hasUserInteractionMode property value.
     */
    boolean hasHasUserInteractionMode();

    /**
     * Adds a hasUserInteractionMode property value.<p>
     * 
     * @param newHasUserInteractionMode the hasUserInteractionMode property value to be added
     */
    void addHasUserInteractionMode(Object newHasUserInteractionMode);

    /**
     * Removes a hasUserInteractionMode property value.<p>
     * 
     * @param oldHasUserInteractionMode the hasUserInteractionMode property value to be removed.
     */
    void removeHasUserInteractionMode(Object oldHasUserInteractionMode);



    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
