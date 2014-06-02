package eu.threecixty.models;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: TemporalDetails <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */

public interface TemporalDetails extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasDateFrom
     */
     
    /**
     * Gets all property values for the hasDateFrom property.<p>
     * 
     * @returns a collection of values for the hasDateFrom property.
     */
    Collection<? extends Object> getHasDateFrom();

    /**
     * Checks if the class has a hasDateFrom property value.<p>
     * 
     * @return true if there is a hasDateFrom property value.
     */
    boolean hasHasDateFrom();

    /**
     * Adds a hasDateFrom property value.<p>
     * 
     * @param newHasDateFrom the hasDateFrom property value to be added
     */
    void addHasDateFrom(Object newHasDateFrom);

    /**
     * Removes a hasDateFrom property value.<p>
     * 
     * @param oldHasDateFrom the hasDateFrom property value to be removed.
     */
    void removeHasDateFrom(Object oldHasDateFrom);



    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasDateUntil
     */
     
    /**
     * Gets all property values for the hasDateUntil property.<p>
     * 
     * @returns a collection of values for the hasDateUntil property.
     */
    Collection<? extends Object> getHasDateUntil();

    /**
     * Checks if the class has a hasDateUntil property value.<p>
     * 
     * @return true if there is a hasDateUntil property value.
     */
    boolean hasHasDateUntil();

    /**
     * Adds a hasDateUntil property value.<p>
     * 
     * @param newHasDateUntil the hasDateUntil property value to be added
     */
    void addHasDateUntil(Object newHasDateUntil);

    /**
     * Removes a hasDateUntil property value.<p>
     * 
     * @param oldHasDateUntil the hasDateUntil property value to be removed.
     */
    void removeHasDateUntil(Object oldHasDateUntil);



    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasDetail
     */
     
    /**
     * Gets all property values for the hasDetail property.<p>
     * 
     * @returns a collection of values for the hasDetail property.
     */
    Collection<? extends String> getHasDetail();

    /**
     * Checks if the class has a hasDetail property value.<p>
     * 
     * @return true if there is a hasDetail property value.
     */
    boolean hasHasDetail();

    /**
     * Adds a hasDetail property value.<p>
     * 
     * @param newHasDetail the hasDetail property value to be added
     */
    void addHasDetail(String newHasDetail);

    /**
     * Removes a hasDetail property value.<p>
     * 
     * @param oldHasDetail the hasDetail property value to be removed.
     */
    void removeHasDetail(String oldHasDetail);



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
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
