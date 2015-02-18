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
 * Source Class: DefaultTransport <br>
 * @version generated on Wed Jun 25 09:55:40 CEST 2014 by ragarwal
 */
public class DefaultTransport extends WrappedIndividualImpl implements Transport {

    public DefaultTransport(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasAccompany
     */
     
    public Collection<? extends Accompanying> getHasAccompany() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASACCOMPANY,
                                               DefaultAccompanying.class);
    }

    public boolean hasHasAccompany() {
	   return !getHasAccompany().isEmpty();
    }

    public void addHasAccompany(Accompanying newHasAccompany) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASACCOMPANY,
                                       newHasAccompany);
    }

    public void removeHasAccompany(Accompanying oldHasAccompany) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASACCOMPANY,
                                          oldHasAccompany);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasModalityStatistics
     */
     
    public Collection<? extends ModalityStatistics> getHasModalityStatistics() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASMODALITYSTATISTICS,
                                               DefaultModalityStatistics.class);
    }

    public boolean hasHasModalityStatistics() {
	   return !getHasModalityStatistics().isEmpty();
    }

    public void addHasModalityStatistics(ModalityStatistics newHasModalityStatistics) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASMODALITYSTATISTICS,
                                       newHasModalityStatistics);
    }

    public void removeHasModalityStatistics(ModalityStatistics oldHasModalityStatistics) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASMODALITYSTATISTICS,
                                          oldHasModalityStatistics);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasPersonalPlace
     */
     
    public Collection<? extends PersonalPlace> getHasPersonalPlace() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASPERSONALPLACE,
                                               DefaultPersonalPlace.class);
    }

    public boolean hasHasPersonalPlace() {
	   return !getHasPersonalPlace().isEmpty();
    }

    public void addHasPersonalPlace(PersonalPlace newHasPersonalPlace) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASPERSONALPLACE,
                                       newHasPersonalPlace);
    }

    public void removeHasPersonalPlace(PersonalPlace oldHasPersonalPlace) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASPERSONALPLACE,
                                          oldHasPersonalPlace);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasRegularTrip
     */
     
    public Collection<? extends RegularTrip> getHasRegularTrip() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASREGULARTRIP,
                                               DefaultRegularTrip.class);
    }

    public boolean hasHasRegularTrip() {
	   return !getHasRegularTrip().isEmpty();
    }

    public void addHasRegularTrip(RegularTrip newHasRegularTrip) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASREGULARTRIP,
                                       newHasRegularTrip);
    }

    public void removeHasRegularTrip(RegularTrip oldHasRegularTrip) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASREGULARTRIP,
                                          oldHasRegularTrip);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasTripMeasurement
     */
     
    public Collection<? extends TripMeasurement> getHasTripMeasurement() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASTRIPMEASUREMENT,
                                               DefaultTripMeasurement.class);
    }

    public boolean hasHasTripMeasurement() {
	   return !getHasTripMeasurement().isEmpty();
    }

    public void addHasTripMeasurement(TripMeasurement newHasTripMeasurement) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASTRIPMEASUREMENT,
                                       newHasTripMeasurement);
    }

    public void removeHasTripMeasurement(TripMeasurement oldHasTripMeasurement) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASTRIPMEASUREMENT,
                                          oldHasTripMeasurement);
    }


}