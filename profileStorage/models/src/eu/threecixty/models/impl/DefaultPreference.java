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
 * Source Class: DefaultPreference <br>
 * @version generated on Wed Jun 25 09:55:40 CEST 2014 by ragarwal
 */
public class DefaultPreference extends WrappedIndividualImpl implements Preference {

    public DefaultPreference(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasEventPreference
     */
     
    public Collection<? extends EventPreference> getHasEventPreference() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASEVENTPREFERENCE,
                                               DefaultEventPreference.class);
    }

    public boolean hasHasEventPreference() {
	   return !getHasEventPreference().isEmpty();
    }

    public void addHasEventPreference(EventPreference newHasEventPreference) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASEVENTPREFERENCE,
                                       newHasEventPreference);
    }

    public void removeHasEventPreference(EventPreference oldHasEventPreference) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASEVENTPREFERENCE,
                                          oldHasEventPreference);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasHotelPreference
     */
     
    public Collection<? extends HotelPreference> getHasHotelPreference() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASHOTELPREFERENCE,
                                               DefaultHotelPreference.class);
    }

    public boolean hasHasHotelPreference() {
	   return !getHasHotelPreference().isEmpty();
    }

    public void addHasHotelPreference(HotelPreference newHasHotelPreference) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASHOTELPREFERENCE,
                                       newHasHotelPreference);
    }

    public void removeHasHotelPreference(HotelPreference oldHasHotelPreference) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASHOTELPREFERENCE,
                                          oldHasHotelPreference);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasLike
     */
     
    public Collection<? extends Like> getHasLike() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASLIKE,
                                               DefaultLike.class);
    }

    public boolean hasHasLike() {
	   return !getHasLike().isEmpty();
    }

    public void addHasLike(Like newHasLike) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASLIKE,
                                       newHasLike);
    }

    public void removeHasLike(Like oldHasLike) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASLIKE,
                                          oldHasLike);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasPlacePreference
     */
     
    public Collection<? extends PlacePreference> getHasPlacePreference() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASPLACEPREFERENCE,
                                               DefaultPlacePreference.class);
    }

    public boolean hasHasPlacePreference() {
	   return !getHasPlacePreference().isEmpty();
    }

    public void addHasPlacePreference(PlacePreference newHasPlacePreference) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASPLACEPREFERENCE,
                                       newHasPlacePreference);
    }

    public void removeHasPlacePreference(PlacePreference oldHasPlacePreference) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASPLACEPREFERENCE,
                                          oldHasPlacePreference);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasSocialPreference
     */
     
    public Collection<? extends SocialPreference> getHasSocialPreference() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASSOCIALPREFERENCE,
                                               DefaultSocialPreference.class);
    }

    public boolean hasHasSocialPreference() {
	   return !getHasSocialPreference().isEmpty();
    }

    public void addHasSocialPreference(SocialPreference newHasSocialPreference) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASSOCIALPREFERENCE,
                                       newHasSocialPreference);
    }

    public void removeHasSocialPreference(SocialPreference oldHasSocialPreference) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASSOCIALPREFERENCE,
                                          oldHasSocialPreference);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasTripPrefernce
     */
     
    public Collection<? extends TripPreference> getHasTripPrefernce() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASTRIPPREFERNCE,
                                               DefaultTripPreference.class);
    }

    public boolean hasHasTripPrefernce() {
	   return !getHasTripPrefernce().isEmpty();
    }

    public void addHasTripPrefernce(TripPreference newHasTripPrefernce) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASTRIPPREFERNCE,
                                       newHasTripPrefernce);
    }

    public void removeHasTripPrefernce(TripPreference oldHasTripPrefernce) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASTRIPPREFERNCE,
                                          oldHasTripPrefernce);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasUserEnteredRatings
     */
     
    public Collection<? extends UserEnteredRatings> getHasUserEnteredRatings() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASUSERENTEREDRATINGS,
                                               DefaultUserEnteredRatings.class);
    }

    public boolean hasHasUserEnteredRatings() {
	   return !getHasUserEnteredRatings().isEmpty();
    }

    public void addHasUserEnteredRatings(UserEnteredRatings newHasUserEnteredRatings) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASUSERENTEREDRATINGS,
                                       newHasUserEnteredRatings);
    }

    public void removeHasUserEnteredRatings(UserEnteredRatings oldHasUserEnteredRatings) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASUSERENTEREDRATINGS,
                                          oldHasUserEnteredRatings);
    }


}
