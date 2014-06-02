package eu.threecixty.models;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * 
 * <p>
 * Generated by Protege (http://protege.stanford.edu). <br>
 * Source Class: Name <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */

public interface Name extends WrappedIndividual {

    /* ***************************************************
     * Property http://www.eu.3cixty.org/profile#middleName
     */
     
    /**
     * Gets all property values for the middleName property.<p>
     * 
     * @returns a collection of values for the middleName property.
     */
    Collection<? extends String> getMiddleName();

    /**
     * Checks if the class has a middleName property value.<p>
     * 
     * @return true if there is a middleName property value.
     */
    boolean hasMiddleName();

    /**
     * Adds a middleName property value.<p>
     * 
     * @param newMiddleName the middleName property value to be added
     */
    void addMiddleName(String newMiddleName);

    /**
     * Removes a middleName property value.<p>
     * 
     * @param oldMiddleName the middleName property value to be removed.
     */
    void removeMiddleName(String oldMiddleName);



    /* ***************************************************
     * Property http://www.w3.org/2006/vcard/ns#additional-name
     */
     
    /**
     * Gets all property values for the additional_name property.<p>
     * 
     * @returns a collection of values for the additional_name property.
     */
    Collection<? extends Object> getAdditional_name();

    /**
     * Checks if the class has a additional_name property value.<p>
     * 
     * @return true if there is a additional_name property value.
     */
    boolean hasAdditional_name();

    /**
     * Adds a additional_name property value.<p>
     * 
     * @param newAdditional_name the additional_name property value to be added
     */
    void addAdditional_name(Object newAdditional_name);

    /**
     * Removes a additional_name property value.<p>
     * 
     * @param oldAdditional_name the additional_name property value to be removed.
     */
    void removeAdditional_name(Object oldAdditional_name);



    /* ***************************************************
     * Property http://www.w3.org/2006/vcard/ns#family-name
     */
     
    /**
     * Gets all property values for the family_name property.<p>
     * 
     * @returns a collection of values for the family_name property.
     */
    Collection<? extends Object> getFamily_name();

    /**
     * Checks if the class has a family_name property value.<p>
     * 
     * @return true if there is a family_name property value.
     */
    boolean hasFamily_name();

    /**
     * Adds a family_name property value.<p>
     * 
     * @param newFamily_name the family_name property value to be added
     */
    void addFamily_name(Object newFamily_name);

    /**
     * Removes a family_name property value.<p>
     * 
     * @param oldFamily_name the family_name property value to be removed.
     */
    void removeFamily_name(Object oldFamily_name);



    /* ***************************************************
     * Property http://www.w3.org/2006/vcard/ns#given-name
     */
     
    /**
     * Gets all property values for the given_name property.<p>
     * 
     * @returns a collection of values for the given_name property.
     */
    Collection<? extends Object> getGiven_name();

    /**
     * Checks if the class has a given_name property value.<p>
     * 
     * @return true if there is a given_name property value.
     */
    boolean hasGiven_name();

    /**
     * Adds a given_name property value.<p>
     * 
     * @param newGiven_name the given_name property value to be added
     */
    void addGiven_name(Object newGiven_name);

    /**
     * Removes a given_name property value.<p>
     * 
     * @param oldGiven_name the given_name property value to be removed.
     */
    void removeGiven_name(Object oldGiven_name);



    /* ***************************************************
     * Property http://www.w3.org/2006/vcard/ns#honorific-prefix
     */
     
    /**
     * Gets all property values for the honorific_prefix property.<p>
     * 
     * @returns a collection of values for the honorific_prefix property.
     */
    Collection<? extends Object> getHonorific_prefix();

    /**
     * Checks if the class has a honorific_prefix property value.<p>
     * 
     * @return true if there is a honorific_prefix property value.
     */
    boolean hasHonorific_prefix();

    /**
     * Adds a honorific_prefix property value.<p>
     * 
     * @param newHonorific_prefix the honorific_prefix property value to be added
     */
    void addHonorific_prefix(Object newHonorific_prefix);

    /**
     * Removes a honorific_prefix property value.<p>
     * 
     * @param oldHonorific_prefix the honorific_prefix property value to be removed.
     */
    void removeHonorific_prefix(Object oldHonorific_prefix);



    /* ***************************************************
     * Property http://www.w3.org/2006/vcard/ns#honorific-suffix
     */
     
    /**
     * Gets all property values for the honorific_suffix property.<p>
     * 
     * @returns a collection of values for the honorific_suffix property.
     */
    Collection<? extends Object> getHonorific_suffix();

    /**
     * Checks if the class has a honorific_suffix property value.<p>
     * 
     * @return true if there is a honorific_suffix property value.
     */
    boolean hasHonorific_suffix();

    /**
     * Adds a honorific_suffix property value.<p>
     * 
     * @param newHonorific_suffix the honorific_suffix property value to be added
     */
    void addHonorific_suffix(Object newHonorific_suffix);

    /**
     * Removes a honorific_suffix property value.<p>
     * 
     * @param oldHonorific_suffix the honorific_suffix property value to be removed.
     */
    void removeHonorific_suffix(Object oldHonorific_suffix);



    /* ***************************************************
     * Property http://www.w3.org/2006/vcard/ns#nickname
     */
     
    /**
     * Gets all property values for the nickname property.<p>
     * 
     * @returns a collection of values for the nickname property.
     */
    Collection<? extends Object> getNickname();

    /**
     * Checks if the class has a nickname property value.<p>
     * 
     * @return true if there is a nickname property value.
     */
    boolean hasNickname();

    /**
     * Adds a nickname property value.<p>
     * 
     * @param newNickname the nickname property value to be added
     */
    void addNickname(Object newNickname);

    /**
     * Removes a nickname property value.<p>
     * 
     * @param oldNickname the nickname property value to be removed.
     */
    void removeNickname(Object oldNickname);



    /* ***************************************************
     * Common interfaces
     */

    OWLNamedIndividual getOwlIndividual();

    OWLOntology getOwlOntology();

    void delete();

}
