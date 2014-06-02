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
 * Source Class: DefaultRatingPreference <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */
public class DefaultRatingPreference extends WrappedIndividualImpl implements RatingPreference {

    public DefaultRatingPreference(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasMaxLastRatingTime
     */
     
    public Collection<? extends Object> getHasMaxLastRatingTime() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMAXLASTRATINGTIME, Object.class);
    }

    public boolean hasHasMaxLastRatingTime() {
		return !getHasMaxLastRatingTime().isEmpty();
    }

    public void addHasMaxLastRatingTime(Object newHasMaxLastRatingTime) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMAXLASTRATINGTIME, newHasMaxLastRatingTime);
    }

    public void removeHasMaxLastRatingTime(Object oldHasMaxLastRatingTime) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMAXLASTRATINGTIME, oldHasMaxLastRatingTime);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasMinRating
     */
     
    public Collection<? extends Object> getHasMinRating() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMINRATING, Object.class);
    }

    public boolean hasHasMinRating() {
		return !getHasMinRating().isEmpty();
    }

    public void addHasMinRating(Object newHasMinRating) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMINRATING, newHasMinRating);
    }

    public void removeHasMinRating(Object oldHasMinRating) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMINRATING, oldHasMinRating);
    }


}
