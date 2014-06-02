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
 * Source Class: DefaultUserEnteredRatings <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */
public class DefaultUserEnteredRatings extends WrappedIndividualImpl implements UserEnteredRatings {

    public DefaultUserEnteredRatings(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasUserEventRating
     */
     
    public Collection<? extends UserEventRating> getHasUserEventRating() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASUSEREVENTRATING,
                                               DefaultUserEventRating.class);
    }

    public boolean hasHasUserEventRating() {
	   return !getHasUserEventRating().isEmpty();
    }

    public void addHasUserEventRating(UserEventRating newHasUserEventRating) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASUSEREVENTRATING,
                                       newHasUserEventRating);
    }

    public void removeHasUserEventRating(UserEventRating oldHasUserEventRating) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASUSEREVENTRATING,
                                          oldHasUserEventRating);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasUserHotelRating
     */
     
    public Collection<? extends UserHotelRating> getHasUserHotelRating() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASUSERHOTELRATING,
                                               DefaultUserHotelRating.class);
    }

    public boolean hasHasUserHotelRating() {
	   return !getHasUserHotelRating().isEmpty();
    }

    public void addHasUserHotelRating(UserHotelRating newHasUserHotelRating) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASUSERHOTELRATING,
                                       newHasUserHotelRating);
    }

    public void removeHasUserHotelRating(UserHotelRating oldHasUserHotelRating) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASUSERHOTELRATING,
                                          oldHasUserHotelRating);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasUserPlaceRating
     */
     
    public Collection<? extends UserPlaceRating> getHasUserPlaceRating() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASUSERPLACERATING,
                                               DefaultUserPlaceRating.class);
    }

    public boolean hasHasUserPlaceRating() {
	   return !getHasUserPlaceRating().isEmpty();
    }

    public void addHasUserPlaceRating(UserPlaceRating newHasUserPlaceRating) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASUSERPLACERATING,
                                       newHasUserPlaceRating);
    }

    public void removeHasUserPlaceRating(UserPlaceRating oldHasUserPlaceRating) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASUSERPLACERATING,
                                          oldHasUserPlaceRating);
    }


}
