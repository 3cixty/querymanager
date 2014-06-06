package eu.threecixty.models;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: RatingPreference <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */

public interface RatingPreference extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasMaxLastRatingTime
     */
     
    /**
     * Gets all property values for the hasMaxLastRatingTime property.<p>
     * 
     * @returns a collection of values for the hasMaxLastRatingTime property.
     */
    Collection<? extends Object> getHasMaxLastRatingTime();

    /**
     * Checks if the class has a hasMaxLastRatingTime property value.<p>
     * 
     * @return true if there is a hasMaxLastRatingTime property value.
     */
    boolean hasHasMaxLastRatingTime();

    /**
     * Adds a hasMaxLastRatingTime property value.<p>
     * 
     * @param newHasMaxLastRatingTime the hasMaxLastRatingTime property value to be added
     */
    void addHasMaxLastRatingTime(Object newHasMaxLastRatingTime);

    /**
     * Removes a hasMaxLastRatingTime property value.<p>
     * 
     * @param oldHasMaxLastRatingTime the hasMaxLastRatingTime property value to be removed.
     */
    void removeHasMaxLastRatingTime(Object oldHasMaxLastRatingTime);



    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasMinRating
     */
     
    /**
     * Gets all property values for the hasMinRating property.<p>
     * 
     * @returns a collection of values for the hasMinRating property.
     */
    Collection<? extends Object> getHasMinRating();

    /**
     * Checks if the class has a hasMinRating property value.<p>
     * 
     * @return true if there is a hasMinRating property value.
     */
    boolean hasHasMinRating();

    /**
     * Adds a hasMinRating property value.<p>
     * 
     * @param newHasMinRating the hasMinRating property value to be added
     */
    void addHasMinRating(Object newHasMinRating);

    /**
     * Removes a hasMinRating property value.<p>
     * 
     * @param oldHasMinRating the hasMinRating property value to be removed.
     */
    void removeHasMinRating(Object oldHasMinRating);



    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
