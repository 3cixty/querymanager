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
 * Source Class: DefaultTemporalDetails <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */
public class DefaultTemporalDetails extends WrappedIndividualImpl implements TemporalDetails {

    public DefaultTemporalDetails(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasDateFrom
     */
     
    public Collection<? extends Object> getHasDateFrom() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATEFROM, Object.class);
    }

    public boolean hasHasDateFrom() {
		return !getHasDateFrom().isEmpty();
    }

    public void addHasDateFrom(Object newHasDateFrom) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATEFROM, newHasDateFrom);
    }

    public void removeHasDateFrom(Object oldHasDateFrom) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATEFROM, oldHasDateFrom);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasDateUntil
     */
     
    public Collection<? extends Object> getHasDateUntil() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATEUNTIL, Object.class);
    }

    public boolean hasHasDateUntil() {
		return !getHasDateUntil().isEmpty();
    }

    public void addHasDateUntil(Object newHasDateUntil) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATEUNTIL, newHasDateUntil);
    }

    public void removeHasDateUntil(Object oldHasDateUntil) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATEUNTIL, oldHasDateUntil);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasDetail
     */
     
    public Collection<? extends String> getHasDetail() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDETAIL, String.class);
    }

    public boolean hasHasDetail() {
		return !getHasDetail().isEmpty();
    }

    public void addHasDetail(String newHasDetail) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDETAIL, newHasDetail);
    }

    public void removeHasDetail(String oldHasDetail) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDETAIL, oldHasDetail);
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


}
