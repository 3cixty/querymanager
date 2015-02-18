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
 * Source Class: DefaultPlacePreference <br>
 * @version generated on Wed Jun 25 09:55:40 CEST 2014 by ragarwal
 */
public class DefaultPlacePreference extends WrappedIndividualImpl implements PlacePreference {

    public DefaultPlacePreference(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasPlaceDetailPreference
     */
     
    public Collection<? extends PlaceDetailPreference> getHasPlaceDetailPreference() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASPLACEDETAILPREFERENCE,
                                               DefaultPlaceDetailPreference.class);
    }

    public boolean hasHasPlaceDetailPreference() {
	   return !getHasPlaceDetailPreference().isEmpty();
    }

    public void addHasPlaceDetailPreference(PlaceDetailPreference newHasPlaceDetailPreference) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASPLACEDETAILPREFERENCE,
                                       newHasPlaceDetailPreference);
    }

    public void removeHasPlaceDetailPreference(PlaceDetailPreference oldHasPlaceDetailPreference) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASPLACEDETAILPREFERENCE,
                                          oldHasPlaceDetailPreference);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasPlaceRatingPreference
     */
     
    public Collection<? extends RatingPreference> getHasPlaceRatingPreference() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASPLACERATINGPREFERENCE,
                                               DefaultRatingPreference.class);
    }

    public boolean hasHasPlaceRatingPreference() {
	   return !getHasPlaceRatingPreference().isEmpty();
    }

    public void addHasPlaceRatingPreference(RatingPreference newHasPlaceRatingPreference) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASPLACERATINGPREFERENCE,
                                       newHasPlaceRatingPreference);
    }

    public void removeHasPlaceRatingPreference(RatingPreference oldHasPlaceRatingPreference) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASPLACERATINGPREFERENCE,
                                          oldHasPlaceRatingPreference);
    }


}