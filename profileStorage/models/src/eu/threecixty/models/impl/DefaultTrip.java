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
 * Source Class: DefaultTrip <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */
public class DefaultTrip extends WrappedIndividualImpl implements Trip {

    public DefaultTrip(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasInfraRoute
     */
     
    public Collection<? extends InfraRoute> getHasInfraRoute() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASINFRAROUTE,
                                               DefaultInfraRoute.class);
    }

    public boolean hasHasInfraRoute() {
	   return !getHasInfraRoute().isEmpty();
    }

    public void addHasInfraRoute(InfraRoute newHasInfraRoute) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASINFRAROUTE,
                                       newHasInfraRoute);
    }

    public void removeHasInfraRoute(InfraRoute oldHasInfraRoute) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASINFRAROUTE,
                                          oldHasInfraRoute);
    }


    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasMappedLocation
     */
     
    public Collection<? extends MappedLocation> getHasMappedLocation() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASMAPPEDLOCATION,
                                               DefaultMappedLocation.class);
    }

    public boolean hasHasMappedLocation() {
	   return !getHasMappedLocation().isEmpty();
    }

    public void addHasMappedLocation(MappedLocation newHasMappedLocation) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASMAPPEDLOCATION,
                                       newHasMappedLocation);
    }

    public void removeHasMappedLocation(MappedLocation oldHasMappedLocation) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASMAPPEDLOCATION,
                                          oldHasMappedLocation);
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
     * Object Property http://www.eu.3cixty.org/profile#hasWeather
     */
     
    public Collection<? extends Weather> getHasWeather() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASWEATHER,
                                               DefaultWeather.class);
    }

    public boolean hasHasWeather() {
	   return !getHasWeather().isEmpty();
    }

    public void addHasWeather(Weather newHasWeather) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASWEATHER,
                                       newHasWeather);
    }

    public void removeHasWeather(Weather oldHasWeather) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASWEATHER,
                                          oldHasWeather);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasDecisionLevel
     */
     
    public Collection<? extends Object> getHasDecisionLevel() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDECISIONLEVEL, Object.class);
    }

    public boolean hasHasDecisionLevel() {
		return !getHasDecisionLevel().isEmpty();
    }

    public void addHasDecisionLevel(Object newHasDecisionLevel) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDECISIONLEVEL, newHasDecisionLevel);
    }

    public void removeHasDecisionLevel(Object oldHasDecisionLevel) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASDECISIONLEVEL, oldHasDecisionLevel);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasModalityRole
     */
     
    public Collection<? extends Object> getHasModalityRole() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMODALITYROLE, Object.class);
    }

    public boolean hasHasModalityRole() {
		return !getHasModalityRole().isEmpty();
    }

    public void addHasModalityRole(Object newHasModalityRole) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMODALITYROLE, newHasModalityRole);
    }

    public void removeHasModalityRole(Object oldHasModalityRole) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMODALITYROLE, oldHasModalityRole);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasModalityType
     */
     
    public Collection<? extends Object> getHasModalityType() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMODALITYTYPE, Object.class);
    }

    public boolean hasHasModalityType() {
		return !getHasModalityType().isEmpty();
    }

    public void addHasModalityType(Object newHasModalityType) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMODALITYTYPE, newHasModalityType);
    }

    public void removeHasModalityType(Object oldHasModalityType) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASMODALITYTYPE, oldHasModalityType);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTravelObjectiveType
     */
     
    public Collection<? extends Object> getHasTravelObjectiveType() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRAVELOBJECTIVETYPE, Object.class);
    }

    public boolean hasHasTravelObjectiveType() {
		return !getHasTravelObjectiveType().isEmpty();
    }

    public void addHasTravelObjectiveType(Object newHasTravelObjectiveType) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRAVELOBJECTIVETYPE, newHasTravelObjectiveType);
    }

    public void removeHasTravelObjectiveType(Object oldHasTravelObjectiveType) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRAVELOBJECTIVETYPE, oldHasTravelObjectiveType);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripAverageSpeed
     */
     
    public Collection<? extends Object> getHasTripAverageSpeed() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPAVERAGESPEED, Object.class);
    }

    public boolean hasHasTripAverageSpeed() {
		return !getHasTripAverageSpeed().isEmpty();
    }

    public void addHasTripAverageSpeed(Object newHasTripAverageSpeed) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPAVERAGESPEED, newHasTripAverageSpeed);
    }

    public void removeHasTripAverageSpeed(Object oldHasTripAverageSpeed) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPAVERAGESPEED, oldHasTripAverageSpeed);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripGroupID
     */
     
    public Collection<? extends Long> getHasTripGroupID() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPGROUPID, Long.class);
    }

    public boolean hasHasTripGroupID() {
		return !getHasTripGroupID().isEmpty();
    }

    public void addHasTripGroupID(Long newHasTripGroupID) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPGROUPID, newHasTripGroupID);
    }

    public void removeHasTripGroupID(Long oldHasTripGroupID) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPGROUPID, oldHasTripGroupID);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripID
     */
     
    public Collection<? extends Long> getHasTripID() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPID, Long.class);
    }

    public boolean hasHasTripID() {
		return !getHasTripID().isEmpty();
    }

    public void addHasTripID(Long newHasTripID) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPID, newHasTripID);
    }

    public void removeHasTripID(Long oldHasTripID) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPID, oldHasTripID);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripLastAnalyzed
     */
     
    public Collection<? extends Long> getHasTripLastAnalyzed() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPLASTANALYZED, Long.class);
    }

    public boolean hasHasTripLastAnalyzed() {
		return !getHasTripLastAnalyzed().isEmpty();
    }

    public void addHasTripLastAnalyzed(Long newHasTripLastAnalyzed) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPLASTANALYZED, newHasTripLastAnalyzed);
    }

    public void removeHasTripLastAnalyzed(Long oldHasTripLastAnalyzed) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPLASTANALYZED, oldHasTripLastAnalyzed);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripModalityAutomatic
     */
     
    public Collection<? extends Object> getHasTripModalityAutomatic() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPMODALITYAUTOMATIC, Object.class);
    }

    public boolean hasHasTripModalityAutomatic() {
		return !getHasTripModalityAutomatic().isEmpty();
    }

    public void addHasTripModalityAutomatic(Object newHasTripModalityAutomatic) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPMODALITYAUTOMATIC, newHasTripModalityAutomatic);
    }

    public void removeHasTripModalityAutomatic(Object oldHasTripModalityAutomatic) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPMODALITYAUTOMATIC, oldHasTripModalityAutomatic);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripNumberOfPassengers
     */
     
    public Collection<? extends Integer> getHasTripNumberOfPassengers() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPNUMBEROFPASSENGERS, Integer.class);
    }

    public boolean hasHasTripNumberOfPassengers() {
		return !getHasTripNumberOfPassengers().isEmpty();
    }

    public void addHasTripNumberOfPassengers(Integer newHasTripNumberOfPassengers) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPNUMBEROFPASSENGERS, newHasTripNumberOfPassengers);
    }

    public void removeHasTripNumberOfPassengers(Integer oldHasTripNumberOfPassengers) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPNUMBEROFPASSENGERS, oldHasTripNumberOfPassengers);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripRegularTripId
     */
     
    public Collection<? extends Long> getHasTripRegularTripId() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPREGULARTRIPID, Long.class);
    }

    public boolean hasHasTripRegularTripId() {
		return !getHasTripRegularTripId().isEmpty();
    }

    public void addHasTripRegularTripId(Long newHasTripRegularTripId) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPREGULARTRIPID, newHasTripRegularTripId);
    }

    public void removeHasTripRegularTripId(Long oldHasTripRegularTripId) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPREGULARTRIPID, oldHasTripRegularTripId);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripTotalCalories
     */
     
    public Collection<? extends Object> getHasTripTotalCalories() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALCALORIES, Object.class);
    }

    public boolean hasHasTripTotalCalories() {
		return !getHasTripTotalCalories().isEmpty();
    }

    public void addHasTripTotalCalories(Object newHasTripTotalCalories) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALCALORIES, newHasTripTotalCalories);
    }

    public void removeHasTripTotalCalories(Object oldHasTripTotalCalories) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALCALORIES, oldHasTripTotalCalories);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripTotalCost
     */
     
    public Collection<? extends Object> getHasTripTotalCost() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALCOST, Object.class);
    }

    public boolean hasHasTripTotalCost() {
		return !getHasTripTotalCost().isEmpty();
    }

    public void addHasTripTotalCost(Object newHasTripTotalCost) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALCOST, newHasTripTotalCost);
    }

    public void removeHasTripTotalCost(Object oldHasTripTotalCost) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALCOST, oldHasTripTotalCost);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripTotalDistance
     */
     
    public Collection<? extends Object> getHasTripTotalDistance() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALDISTANCE, Object.class);
    }

    public boolean hasHasTripTotalDistance() {
		return !getHasTripTotalDistance().isEmpty();
    }

    public void addHasTripTotalDistance(Object newHasTripTotalDistance) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALDISTANCE, newHasTripTotalDistance);
    }

    public void removeHasTripTotalDistance(Object oldHasTripTotalDistance) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALDISTANCE, oldHasTripTotalDistance);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripTotalInfraSegmentDistance
     */
     
    public Collection<? extends Object> getHasTripTotalInfraSegmentDistance() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALINFRASEGMENTDISTANCE, Object.class);
    }

    public boolean hasHasTripTotalInfraSegmentDistance() {
		return !getHasTripTotalInfraSegmentDistance().isEmpty();
    }

    public void addHasTripTotalInfraSegmentDistance(Object newHasTripTotalInfraSegmentDistance) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALINFRASEGMENTDISTANCE, newHasTripTotalInfraSegmentDistance);
    }

    public void removeHasTripTotalInfraSegmentDistance(Object oldHasTripTotalInfraSegmentDistance) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTOTALINFRASEGMENTDISTANCE, oldHasTripTotalInfraSegmentDistance);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTripType
     */
     
    public Collection<? extends Object> getHasTripType() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTYPE, Object.class);
    }

    public boolean hasHasTripType() {
		return !getHasTripType().isEmpty();
    }

    public void addHasTripType(Object newHasTripType) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTYPE, newHasTripType);
    }

    public void removeHasTripType(Object oldHasTripType) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTRIPTYPE, oldHasTripType);
    }


}
