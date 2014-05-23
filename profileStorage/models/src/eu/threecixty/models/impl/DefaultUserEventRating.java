package eu.threecixty.models.impl;

import eu.threecixty.models.*;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultUserEventRating <br>
 * @version generated on Fri May 23 10:57:54 CEST 2014 by cknguyen
 */
public class DefaultUserEventRating extends WrappedIndividualImpl implements UserEventRating {

    public DefaultUserEventRating(OWLOntology ontology, IRI iri) {
        super(ontology, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasEventDetail
     */
     
    public Collection<? extends EventDetails> getHasEventDetail() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASEVENTDETAIL,
                                               DefaultEventDetails.class);
    }

    public boolean hasHasEventDetail() {
	   return !getHasEventDetail().isEmpty();
    }

    public void addHasEventDetail(EventDetails newHasEventDetail) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASEVENTDETAIL,
                                       newHasEventDetail);
    }

    public void removeHasEventDetail(EventDetails oldHasEventDetail) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASEVENTDETAIL,
                                          oldHasEventDetail);
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
