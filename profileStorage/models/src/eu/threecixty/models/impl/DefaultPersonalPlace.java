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
 * Source Class: DefaultPersonalPlace <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */
public class DefaultPersonalPlace extends WrappedIndividualImpl implements PersonalPlace {

    public DefaultPersonalPlace(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
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
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceAccuracy
     */
     
    public Collection<? extends Object> getHasPersonalPlaceAccuracy() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEACCURACY, Object.class);
    }

    public boolean hasHasPersonalPlaceAccuracy() {
		return !getHasPersonalPlaceAccuracy().isEmpty();
    }

    public void addHasPersonalPlaceAccuracy(Object newHasPersonalPlaceAccuracy) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEACCURACY, newHasPersonalPlaceAccuracy);
    }

    public void removeHasPersonalPlaceAccuracy(Object oldHasPersonalPlaceAccuracy) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEACCURACY, oldHasPersonalPlaceAccuracy);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceDayHourPattern
     */
     
    public Collection<? extends String> getHasPersonalPlaceDayHourPattern() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEDAYHOURPATTERN, String.class);
    }

    public boolean hasHasPersonalPlaceDayHourPattern() {
		return !getHasPersonalPlaceDayHourPattern().isEmpty();
    }

    public void addHasPersonalPlaceDayHourPattern(String newHasPersonalPlaceDayHourPattern) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEDAYHOURPATTERN, newHasPersonalPlaceDayHourPattern);
    }

    public void removeHasPersonalPlaceDayHourPattern(String oldHasPersonalPlaceDayHourPattern) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEDAYHOURPATTERN, oldHasPersonalPlaceDayHourPattern);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceExternalIds
     */
     
    public Collection<? extends String> getHasPersonalPlaceExternalIds() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEEXTERNALIDS, String.class);
    }

    public boolean hasHasPersonalPlaceExternalIds() {
		return !getHasPersonalPlaceExternalIds().isEmpty();
    }

    public void addHasPersonalPlaceExternalIds(String newHasPersonalPlaceExternalIds) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEEXTERNALIDS, newHasPersonalPlaceExternalIds);
    }

    public void removeHasPersonalPlaceExternalIds(String oldHasPersonalPlaceExternalIds) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEEXTERNALIDS, oldHasPersonalPlaceExternalIds);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceID
     */
     
    public Collection<? extends Long> getHasPersonalPlaceID() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEID, Long.class);
    }

    public boolean hasHasPersonalPlaceID() {
		return !getHasPersonalPlaceID().isEmpty();
    }

    public void addHasPersonalPlaceID(Long newHasPersonalPlaceID) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEID, newHasPersonalPlaceID);
    }

    public void removeHasPersonalPlaceID(Long oldHasPersonalPlaceID) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEID, oldHasPersonalPlaceID);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceName
     */
     
    public Collection<? extends String> getHasPersonalPlaceName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACENAME, String.class);
    }

    public boolean hasHasPersonalPlaceName() {
		return !getHasPersonalPlaceName().isEmpty();
    }

    public void addHasPersonalPlaceName(String newHasPersonalPlaceName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACENAME, newHasPersonalPlaceName);
    }

    public void removeHasPersonalPlaceName(String oldHasPersonalPlaceName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACENAME, oldHasPersonalPlaceName);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceStayDuration
     */
     
    public Collection<? extends Long> getHasPersonalPlaceStayDuration() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACESTAYDURATION, Long.class);
    }

    public boolean hasHasPersonalPlaceStayDuration() {
		return !getHasPersonalPlaceStayDuration().isEmpty();
    }

    public void addHasPersonalPlaceStayDuration(Long newHasPersonalPlaceStayDuration) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACESTAYDURATION, newHasPersonalPlaceStayDuration);
    }

    public void removeHasPersonalPlaceStayDuration(Long oldHasPersonalPlaceStayDuration) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACESTAYDURATION, oldHasPersonalPlaceStayDuration);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceStayPercentage
     */
     
    public Collection<? extends Object> getHasPersonalPlaceStayPercentage() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACESTAYPERCENTAGE, Object.class);
    }

    public boolean hasHasPersonalPlaceStayPercentage() {
		return !getHasPersonalPlaceStayPercentage().isEmpty();
    }

    public void addHasPersonalPlaceStayPercentage(Object newHasPersonalPlaceStayPercentage) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACESTAYPERCENTAGE, newHasPersonalPlaceStayPercentage);
    }

    public void removeHasPersonalPlaceStayPercentage(Object oldHasPersonalPlaceStayPercentage) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACESTAYPERCENTAGE, oldHasPersonalPlaceStayPercentage);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceType
     */
     
    public Collection<? extends String> getHasPersonalPlaceType() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACETYPE, String.class);
    }

    public boolean hasHasPersonalPlaceType() {
		return !getHasPersonalPlaceType().isEmpty();
    }

    public void addHasPersonalPlaceType(String newHasPersonalPlaceType) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACETYPE, newHasPersonalPlaceType);
    }

    public void removeHasPersonalPlaceType(String oldHasPersonalPlaceType) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACETYPE, oldHasPersonalPlaceType);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPersonalPlaceWeekDayPattern
     */
     
    public Collection<? extends String> getHasPersonalPlaceWeekDayPattern() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEWEEKDAYPATTERN, String.class);
    }

    public boolean hasHasPersonalPlaceWeekDayPattern() {
		return !getHasPersonalPlaceWeekDayPattern().isEmpty();
    }

    public void addHasPersonalPlaceWeekDayPattern(String newHasPersonalPlaceWeekDayPattern) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEWEEKDAYPATTERN, newHasPersonalPlaceWeekDayPattern);
    }

    public void removeHasPersonalPlaceWeekDayPattern(String oldHasPersonalPlaceWeekDayPattern) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPERSONALPLACEWEEKDAYPATTERN, oldHasPersonalPlaceWeekDayPattern);
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


    /* ***************************************************
     * Data Property http://www.w3.org/2006/vcard/ns#latitude
     */
     
    public Collection<? extends Object> getLatitude() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_LATITUDE, Object.class);
    }

    public boolean hasLatitude() {
		return !getLatitude().isEmpty();
    }

    public void addLatitude(Object newLatitude) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_LATITUDE, newLatitude);
    }

    public void removeLatitude(Object oldLatitude) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_LATITUDE, oldLatitude);
    }


    /* ***************************************************
     * Data Property http://www.w3.org/2006/vcard/ns#longitude
     */
     
    public Collection<? extends Object> getLongitude() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_LONGITUDE, Object.class);
    }

    public boolean hasLongitude() {
		return !getLongitude().isEmpty();
    }

    public void addLongitude(Object newLongitude) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_LONGITUDE, newLongitude);
    }

    public void removeLongitude(Object oldLongitude) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_LONGITUDE, oldLongitude);
    }


    /* ***************************************************
     * Data Property http://www.w3.org/2006/vcard/ns#postal-code
     */
     
    public Collection<? extends Object> getPostal_code() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_POSTAL_CODE, Object.class);
    }

    public boolean hasPostal_code() {
		return !getPostal_code().isEmpty();
    }

    public void addPostal_code(Object newPostal_code) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_POSTAL_CODE, newPostal_code);
    }

    public void removePostal_code(Object oldPostal_code) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_POSTAL_CODE, oldPostal_code);
    }


}
