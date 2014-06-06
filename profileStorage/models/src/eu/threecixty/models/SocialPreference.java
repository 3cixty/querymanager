package eu.threecixty.models;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: SocialPreference <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */

public interface SocialPreference extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasAccompany
     */
     
    /**
     * Gets all property values for the hasAccompany property.<p>
     * 
     * @returns a collection of values for the hasAccompany property.
     */
    Collection<? extends Accompanying> getHasAccompany();

    /**
     * Checks if the class has a hasAccompany property value.<p>
     * 
     * @return true if there is a hasAccompany property value.
     */
    boolean hasHasAccompany();

    /**
     * Adds a hasAccompany property value.<p>
     * 
     * @param newHasAccompany the hasAccompany property value to be added
     */
    void addHasAccompany(Accompanying newHasAccompany);

    /**
     * Removes a hasAccompany property value.<p>
     * 
     * @param oldHasAccompany the hasAccompany property value to be removed.
     */
    void removeHasAccompany(Accompanying oldHasAccompany);


    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
