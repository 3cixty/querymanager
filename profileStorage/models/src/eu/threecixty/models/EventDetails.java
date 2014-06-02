package eu.threecixty.models;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: EventDetails <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */

public interface EventDetails extends WrappedIndividual {

    /* ***************************************************
     * Property http://linkedevents.org/ontology/atPlace
     */
     
    /**
     * Gets all property values for the at_place property.<p>
     * 
     * @returns a collection of values for the at_place property.
     */
    Collection<? extends Address> getAt_place();

    /**
     * Checks if the class has a at_place property value.<p>
     * 
     * @return true if there is a at_place property value.
     */
    boolean hasAt_place();

    /**
     * Adds a at_place property value.<p>
     * 
     * @param newAt_place the at_place property value to be added
     */
    void addAt_place(Address newAt_place);

    /**
     * Removes a at_place property value.<p>
     * 
     * @param oldAt_place the at_place property value to be removed.
     */
    void removeAt_place(Address oldAt_place);


    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasTemporalDetails
     */
     
    /**
     * Gets all property values for the hasTemporalDetails property.<p>
     * 
     * @returns a collection of values for the hasTemporalDetails property.
     */
    Collection<? extends TemporalDetails> getHasTemporalDetails();

    /**
     * Checks if the class has a hasTemporalDetails property value.<p>
     * 
     * @return true if there is a hasTemporalDetails property value.
     */
    boolean hasHasTemporalDetails();

    /**
     * Adds a hasTemporalDetails property value.<p>
     * 
     * @param newHasTemporalDetails the hasTemporalDetails property value to be added
     */
    void addHasTemporalDetails(TemporalDetails newHasTemporalDetails);

    /**
     * Removes a hasTemporalDetails property value.<p>
     * 
     * @param oldHasTemporalDetails the hasTemporalDetails property value to be removed.
     */
    void removeHasTemporalDetails(TemporalDetails oldHasTemporalDetails);


    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasEventName
     */
     
    /**
     * Gets all property values for the hasEventName property.<p>
     * 
     * @returns a collection of values for the hasEventName property.
     */
    Collection<? extends String> getHasEventName();

    /**
     * Checks if the class has a hasEventName property value.<p>
     * 
     * @return true if there is a hasEventName property value.
     */
    boolean hasHasEventName();

    /**
     * Adds a hasEventName property value.<p>
     * 
     * @param newHasEventName the hasEventName property value to be added
     */
    void addHasEventName(String newHasEventName);

    /**
     * Removes a hasEventName property value.<p>
     * 
     * @param oldHasEventName the hasEventName property value to be removed.
     */
    void removeHasEventName(String oldHasEventName);



    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#hasNatureOfEvent
     */
     
    /**
     * Gets all property values for the hasNatureOfEvent property.<p>
     * 
     * @returns a collection of values for the hasNatureOfEvent property.
     */
    Collection<? extends Object> getHasNatureOfEvent();

    /**
     * Checks if the class has a hasNatureOfEvent property value.<p>
     * 
     * @return true if there is a hasNatureOfEvent property value.
     */
    boolean hasHasNatureOfEvent();

    /**
     * Adds a hasNatureOfEvent property value.<p>
     * 
     * @param newHasNatureOfEvent the hasNatureOfEvent property value to be added
     */
    void addHasNatureOfEvent(Object newHasNatureOfEvent);

    /**
     * Removes a hasNatureOfEvent property value.<p>
     * 
     * @param oldHasNatureOfEvent the hasNatureOfEvent property value to be removed.
     */
    void removeHasNatureOfEvent(Object oldHasNatureOfEvent);



    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
