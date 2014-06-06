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
 * Source Class: DefaultTripMeasurement <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */
public class DefaultTripMeasurement extends WrappedIndividualImpl implements TripMeasurement {

    public DefaultTripMeasurement(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasMeasurement
     */
     
    public Collection<? extends Measurement> getHasMeasurement() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASMEASUREMENT,
                                               DefaultMeasurement.class);
    }

    public boolean hasHasMeasurement() {
	   return !getHasMeasurement().isEmpty();
    }

    public void addHasMeasurement(Measurement newHasMeasurement) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASMEASUREMENT,
                                       newHasMeasurement);
    }

    public void removeHasMeasurement(Measurement oldHasMeasurement) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASMEASUREMENT,
                                          oldHasMeasurement);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasTrip
     */
     
    public Collection<? extends Trip> getHasTrip() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASTRIP,
                                               DefaultTrip.class);
    }

    public boolean hasHasTrip() {
	   return !getHasTrip().isEmpty();
    }

    public void addHasTrip(Trip newHasTrip) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASTRIP,
                                       newHasTrip);
    }

    public void removeHasTrip(Trip oldHasTrip) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASTRIP,
                                          oldHasTrip);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasDataQuality
     */
     
    public Collection<? extends Object> getHasDataQuality() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATAQUALITY, Object.class);
    }

    public boolean hasHasDataQuality() {
		return !getHasDataQuality().isEmpty();
    }

    public void addHasDataQuality(Object newHasDataQuality) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATAQUALITY, newHasDataQuality);
    }

    public void removeHasDataQuality(Object oldHasDataQuality) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDATAQUALITY, oldHasDataQuality);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasMeasurementID
     */
     
    public Collection<? extends Long> getHasMeasurementID() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTID, Long.class);
    }

    public boolean hasHasMeasurementID() {
		return !getHasMeasurementID().isEmpty();
    }

    public void addHasMeasurementID(Long newHasMeasurementID) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTID, newHasMeasurementID);
    }

    public void removeHasMeasurementID(Long oldHasMeasurementID) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTID, oldHasMeasurementID);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasMeasurementTime
     */
     
    public Collection<? extends Long> getHasMeasurementTime() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTTIME, Long.class);
    }

    public boolean hasHasMeasurementTime() {
		return !getHasMeasurementTime().isEmpty();
    }

    public void addHasMeasurementTime(Long newHasMeasurementTime) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTTIME, newHasMeasurementTime);
    }

    public void removeHasMeasurementTime(Long oldHasMeasurementTime) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTTIME, oldHasMeasurementTime);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasMeasurementTimeZone
     */
     
    public Collection<? extends Long> getHasMeasurementTimeZone() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTTIMEZONE, Long.class);
    }

    public boolean hasHasMeasurementTimeZone() {
		return !getHasMeasurementTimeZone().isEmpty();
    }

    public void addHasMeasurementTimeZone(Long newHasMeasurementTimeZone) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTTIMEZONE, newHasMeasurementTimeZone);
    }

    public void removeHasMeasurementTimeZone(Long oldHasMeasurementTimeZone) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTTIMEZONE, oldHasMeasurementTimeZone);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasMeasurementValidity
     */
     
    public Collection<? extends Long> getHasMeasurementValidity() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTVALIDITY, Long.class);
    }

    public boolean hasHasMeasurementValidity() {
		return !getHasMeasurementValidity().isEmpty();
    }

    public void addHasMeasurementValidity(Long newHasMeasurementValidity) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTVALIDITY, newHasMeasurementValidity);
    }

    public void removeHasMeasurementValidity(Long oldHasMeasurementValidity) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMEASUREMENTVALIDITY, oldHasMeasurementValidity);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasUID
     */
     
    public Collection<? extends String> getHasUID() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUID, String.class);
    }

    public boolean hasHasUID() {
		return !getHasUID().isEmpty();
    }

    public void addHasUID(String newHasUID) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUID, newHasUID);
    }

    public void removeHasUID(String oldHasUID) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUID, oldHasUID);
    }


}
