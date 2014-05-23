package eu.threecixty.models.impl;

import eu.threecixty.models.*;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultUserHotelRating <br>
 * @version generated on Fri May 23 10:57:54 CEST 2014 by cknguyen
 */
public class DefaultUserHotelRating extends WrappedIndividualImpl implements UserHotelRating {

    public DefaultUserHotelRating(OWLOntology ontology, IRI iri) {
        super(ontology, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasHotelDetail
     */
     
    public Collection<? extends HotelDetail> getHasHotelDetail() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASHOTELDETAIL,
                                               DefaultHotelDetail.class);
    }

    public boolean hasHasHotelDetail() {
	   return !getHasHotelDetail().isEmpty();
    }

    public void addHasHotelDetail(HotelDetail newHasHotelDetail) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASHOTELDETAIL,
                                       newHasHotelDetail);
    }

    public void removeHasHotelDetail(HotelDetail oldHasHotelDetail) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASHOTELDETAIL,
                                          oldHasHotelDetail);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasRating
     */
     
    public Collection<? extends Rating> getHasRating() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASRATING,
                                               DefaultRating.class);
    }

    public boolean hasHasRating() {
	   return !getHasRating().isEmpty();
    }

    public void addHasRating(Rating newHasRating) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASRATING,
                                       newHasRating);
    }

    public void removeHasRating(Rating oldHasRating) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASRATING,
                                          oldHasRating);
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


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasNumberofTimesVisited
     */
     
    public Collection<? extends Integer> getHasNumberofTimesVisited() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNUMBEROFTIMESVISITED, Integer.class);
    }

    public boolean hasHasNumberofTimesVisited() {
		return !getHasNumberofTimesVisited().isEmpty();
    }

    public void addHasNumberofTimesVisited(Integer newHasNumberofTimesVisited) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNUMBEROFTIMESVISITED, newHasNumberofTimesVisited);
    }

    public void removeHasNumberofTimesVisited(Integer oldHasNumberofTimesVisited) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNUMBEROFTIMESVISITED, oldHasNumberofTimesVisited);
    }


}
