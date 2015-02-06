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
 * Source Class: DefaultHotelPreference <br>
 * @version generated on Wed Jun 25 09:55:40 CEST 2014 by ragarwal
 */
public class DefaultHotelPreference extends WrappedIndividualImpl implements HotelPreference {

    public DefaultHotelPreference(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasHotelDetailPreference
     */
     
    public Collection<? extends HotelDetailPreference> getHasHotelDetailPreference() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASHOTELDETAILPREFERENCE,
                                               DefaultHotelDetailPreference.class);
    }

    public boolean hasHasHotelDetailPreference() {
	   return !getHasHotelDetailPreference().isEmpty();
    }

    public void addHasHotelDetailPreference(HotelDetailPreference newHasHotelDetailPreference) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASHOTELDETAILPREFERENCE,
                                       newHasHotelDetailPreference);
    }

    public void removeHasHotelDetailPreference(HotelDetailPreference oldHasHotelDetailPreference) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASHOTELDETAILPREFERENCE,
                                          oldHasHotelDetailPreference);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasPreferredHotelRating
     */
     
    public Collection<? extends RatingPreference> getHasPreferredHotelRating() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASPREFERREDHOTELRATING,
                                               DefaultRatingPreference.class);
    }

    public boolean hasHasPreferredHotelRating() {
	   return !getHasPreferredHotelRating().isEmpty();
    }

    public void addHasPreferredHotelRating(RatingPreference newHasPreferredHotelRating) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASPREFERREDHOTELRATING,
                                       newHasPreferredHotelRating);
    }

    public void removeHasPreferredHotelRating(RatingPreference oldHasPreferredHotelRating) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASPREFERREDHOTELRATING,
                                          oldHasPreferredHotelRating);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasNumberOfTimesVisited
     */
     
    public Collection<? extends Integer> getHasNumberOfTimesVisited() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNUMBEROFTIMESVISITED, Integer.class);
    }

    public boolean hasHasNumberOfTimesVisited() {
		return !getHasNumberOfTimesVisited().isEmpty();
    }

    public void addHasNumberOfTimesVisited(Integer newHasNumberOfTimesVisited) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNUMBEROFTIMESVISITED, newHasNumberOfTimesVisited);
    }

    public void removeHasNumberOfTimesVisited(Integer oldHasNumberOfTimesVisited) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNUMBEROFTIMESVISITED, oldHasNumberOfTimesVisited);
    }


}
