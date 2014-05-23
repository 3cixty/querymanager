package eu.threecixty.models.impl;

import eu.threecixty.models.*;

import java.util.Collection;

import org.protege.owl.codegeneration.WrappedIndividual;
import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultEventDetails <br>
 * @version generated on Fri May 23 10:57:54 CEST 2014 by cknguyen
 */
public class DefaultEventDetails extends WrappedIndividualImpl implements EventDetails {

    public DefaultEventDetails(OWLOntology ontology, IRI iri) {
        super(ontology, iri);
    }





    /* ***************************************************
     * Object Property http://linkedevents.org/ontology/atPlace
     */
     
    public Collection<? extends Address> getAt_place() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_AT_PLACE,
                                               DefaultAddress.class);
    }

    public boolean hasAt_place() {
	   return !getAt_place().isEmpty();
    }

    public void addAt_place(Address newAt_place) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_AT_PLACE,
                                       newAt_place);
    }

    public void removeAt_place(Address oldAt_place) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_AT_PLACE,
                                          oldAt_place);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasTemporalDetails
     */
     
    public Collection<? extends TemporalDetails> getHasTemporalDetails() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASTEMPORALDETAILS,
                                               DefaultTemporalDetails.class);
    }

    public boolean hasHasTemporalDetails() {
	   return !getHasTemporalDetails().isEmpty();
    }

    public void addHasTemporalDetails(TemporalDetails newHasTemporalDetails) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASTEMPORALDETAILS,
                                       newHasTemporalDetails);
    }

    public void removeHasTemporalDetails(TemporalDetails oldHasTemporalDetails) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASTEMPORALDETAILS,
                                          oldHasTemporalDetails);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasEventName
     */
     
    public Collection<? extends String> getHasEventName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASEVENTNAME, String.class);
    }

    public boolean hasHasEventName() {
		return !getHasEventName().isEmpty();
    }

    public void addHasEventName(String newHasEventName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASEVENTNAME, newHasEventName);
    }

    public void removeHasEventName(String oldHasEventName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASEVENTNAME, oldHasEventName);
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


}
