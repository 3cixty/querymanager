package eu.threecixty.models.impl;

import eu.threecixty.models.*;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultPlaceDetail <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */
public class DefaultPlaceDetail extends WrappedIndividualImpl implements PlaceDetail {

    public DefaultPlaceDetail(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.w3.org/2006/vcard/ns#hasAddress
     */
     
    public Collection<? extends WrappedIndividual> getHas_address() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HAS_ADDRESS,
                                               WrappedIndividualImpl.class);
    }

    public boolean hasHas_address() {
	   return !getHas_address().isEmpty();
    }

    public void addHas_address(WrappedIndividual newHas_address) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HAS_ADDRESS,
                                       newHas_address);
    }

    public void removeHas_address(WrappedIndividual oldHas_address) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HAS_ADDRESS,
                                          oldHas_address);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasKeyTags
     */
     
    public Collection<? extends String> getHasKeyTags() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASKEYTAGS, String.class);
    }

    public boolean hasHasKeyTags() {
		return !getHasKeyTags().isEmpty();
    }

    public void addHasKeyTags(String newHasKeyTags) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASKEYTAGS, newHasKeyTags);
    }

    public void removeHasKeyTags(String oldHasKeyTags) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASKEYTAGS, oldHasKeyTags);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasNatureOfPlace
     */
     
    public Collection<? extends Object> getHasNatureOfPlace() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFPLACE, Object.class);
    }

    public boolean hasHasNatureOfPlace() {
		return !getHasNatureOfPlace().isEmpty();
    }

    public void addHasNatureOfPlace(Object newHasNatureOfPlace) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFPLACE, newHasNatureOfPlace);
    }

    public void removeHasNatureOfPlace(Object oldHasNatureOfPlace) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFPLACE, oldHasNatureOfPlace);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPlaceName
     */
     
    public Collection<? extends String> getHasPlaceName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPLACENAME, String.class);
    }

    public boolean hasHasPlaceName() {
		return !getHasPlaceName().isEmpty();
    }

    public void addHasPlaceName(String newHasPlaceName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPLACENAME, newHasPlaceName);
    }

    public void removeHasPlaceName(String oldHasPlaceName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPLACENAME, oldHasPlaceName);
    }


}
