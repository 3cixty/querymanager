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
 * Source Class: DefaultEventDetailPreference <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */
public class DefaultEventDetailPreference extends WrappedIndividualImpl implements EventDetailPreference {

    public DefaultEventDetailPreference(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasNatureOfEvent
     */
     
    public Collection<? extends Object> getHasNatureOfEvent() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFEVENT, Object.class);
    }

    public boolean hasHasNatureOfEvent() {
		return !getHasNatureOfEvent().isEmpty();
    }

    public void addHasNatureOfEvent(Object newHasNatureOfEvent) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFEVENT, newHasNatureOfEvent);
    }

    public void removeHasNatureOfEvent(Object oldHasNatureOfEvent) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFEVENT, oldHasNatureOfEvent);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPreferredEndDate
     */
     
    public Collection<? extends Object> getHasPreferredEndDate() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDENDDATE, Object.class);
    }

    public boolean hasHasPreferredEndDate() {
		return !getHasPreferredEndDate().isEmpty();
    }

    public void addHasPreferredEndDate(Object newHasPreferredEndDate) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDENDDATE, newHasPreferredEndDate);
    }

    public void removeHasPreferredEndDate(Object oldHasPreferredEndDate) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDENDDATE, oldHasPreferredEndDate);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPreferredEventKeyTags
     */
     
    public Collection<? extends String> getHasPreferredEventKeyTags() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDEVENTKEYTAGS, String.class);
    }

    public boolean hasHasPreferredEventKeyTags() {
		return !getHasPreferredEventKeyTags().isEmpty();
    }

    public void addHasPreferredEventKeyTags(String newHasPreferredEventKeyTags) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDEVENTKEYTAGS, newHasPreferredEventKeyTags);
    }

    public void removeHasPreferredEventKeyTags(String oldHasPreferredEventKeyTags) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDEVENTKEYTAGS, oldHasPreferredEventKeyTags);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPreferredStartDate
     */
     
    public Collection<? extends Object> getHasPreferredStartDate() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDSTARTDATE, Object.class);
    }

    public boolean hasHasPreferredStartDate() {
		return !getHasPreferredStartDate().isEmpty();
    }

    public void addHasPreferredStartDate(Object newHasPreferredStartDate) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDSTARTDATE, newHasPreferredStartDate);
    }

    public void removeHasPreferredStartDate(Object oldHasPreferredStartDate) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPREFERREDSTARTDATE, oldHasPreferredStartDate);
    }


}
