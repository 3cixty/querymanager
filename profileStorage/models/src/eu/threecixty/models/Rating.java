package eu.threecixty.models;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: Rating <br>
 * @version generated on Fri May 23 10:57:54 CEST 2014 by cknguyen
 */

public interface Rating extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasKeyTags
     */
     
    /**
     * Gets all property values for the hasKeyTags property.<p>
     * 
     * @returns a collection of values for the hasKeyTags property.
     */
    Collection<? extends String> getHasKeyTags();

    /**
     * Checks if the class has a hasKeyTags property value.<p>
     * 
     * @return true if there is a hasKeyTags property value.
     */
    boolean hasHasKeyTags();

    /**
     * Adds a hasKeyTags property value.<p>
     * 
     * @param newHasKeyTags the hasKeyTags property value to be added
     */
    void addHasKeyTags(String newHasKeyTags);

    /**
     * Removes a hasKeyTags property value.<p>
     * 
     * @param oldHasKeyTags the hasKeyTags property value to be removed.
     */
    void removeHasKeyTags(String oldHasKeyTags);



    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasUserDefinedRating
     */
     
    /**
     * Gets all property values for the hasUserDefinedRating property.<p>
     * 
     * @returns a collection of values for the hasUserDefinedRating property.
     */
    Collection<? extends Object> getHasUserDefinedRating();

    /**
     * Checks if the class has a hasUserDefinedRating property value.<p>
     * 
     * @return true if there is a hasUserDefinedRating property value.
     */
    boolean hasHasUserDefinedRating();

    /**
     * Adds a hasUserDefinedRating property value.<p>
     * 
     * @param newHasUserDefinedRating the hasUserDefinedRating property value to be added
     */
    void addHasUserDefinedRating(Object newHasUserDefinedRating);

    /**
     * Removes a hasUserDefinedRating property value.<p>
     * 
     * @param oldHasUserDefinedRating the hasUserDefinedRating property value to be removed.
     */
    void removeHasUserDefinedRating(Object oldHasUserDefinedRating);



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
