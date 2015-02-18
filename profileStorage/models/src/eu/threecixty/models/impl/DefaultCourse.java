package eu.threecixty.models.impl;

import eu.threecixty.models.*;

import java.util.Collection;

import org.protege.owl.codegeneration.impl.WrappedIndividualImpl;

import org.protege.owl.codegeneration.inference.CodeGenerationInference;

import org.semanticweb.owlapi.model.IRI;


/**
 * Generated by Protege (http://protege.stanford.edu).<br>
 * Source Class: DefaultCourse <br>
 * @version generated on Wed Jun 25 09:55:40 CEST 2014 by ragarwal
 */
public class DefaultCourse extends WrappedIndividualImpl implements Course {

    public DefaultCourse(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
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
     * Object Property http://www.eu.3cixty.org/profile#isOfferedBy
     */
     
    public Collection<? extends School> getIsOfferedBy() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_ISOFFEREDBY,
                                               DefaultSchool.class);
    }

    public boolean hasIsOfferedBy() {
	   return !getIsOfferedBy().isEmpty();
    }

    public void addIsOfferedBy(School newIsOfferedBy) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_ISOFFEREDBY,
                                       newIsOfferedBy);
    }

    public void removeIsOfferedBy(School oldIsOfferedBy) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_ISOFFEREDBY,
                                          oldIsOfferedBy);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasCourseDuration
     */
     
    public Collection<? extends String> getHasCourseDuration() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSEDURATION, String.class);
    }

    public boolean hasHasCourseDuration() {
		return !getHasCourseDuration().isEmpty();
    }

    public void addHasCourseDuration(String newHasCourseDuration) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSEDURATION, newHasCourseDuration);
    }

    public void removeHasCourseDuration(String oldHasCourseDuration) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSEDURATION, oldHasCourseDuration);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasCourseInstructor
     */
     
    public Collection<? extends String> getHasCourseInstructor() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSEINSTRUCTOR, String.class);
    }

    public boolean hasHasCourseInstructor() {
		return !getHasCourseInstructor().isEmpty();
    }

    public void addHasCourseInstructor(String newHasCourseInstructor) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSEINSTRUCTOR, newHasCourseInstructor);
    }

    public void removeHasCourseInstructor(String oldHasCourseInstructor) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSEINSTRUCTOR, oldHasCourseInstructor);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasCourseName
     */
     
    public Collection<? extends String> getHasCourseName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSENAME, String.class);
    }

    public boolean hasHasCourseName() {
		return !getHasCourseName().isEmpty();
    }

    public void addHasCourseName(String newHasCourseName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSENAME, newHasCourseName);
    }

    public void removeHasCourseName(String oldHasCourseName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSENAME, oldHasCourseName);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasCourseType
     */
     
    public Collection<? extends Object> getHasCourseType() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSETYPE, Object.class);
    }

    public boolean hasHasCourseType() {
		return !getHasCourseType().isEmpty();
    }

    public void addHasCourseType(Object newHasCourseType) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSETYPE, newHasCourseType);
    }

    public void removeHasCourseType(Object oldHasCourseType) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASCOURSETYPE, oldHasCourseType);
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
     * Data Property http://www.eu.3cixty.org/profile#hasEventName
     */
     
    public Collection<? extends Object> getHasEventName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASEVENTNAME, Object.class);
    }

    public boolean hasHasEventName() {
		return !getHasEventName().isEmpty();
    }

    public void addHasEventName(Object newHasEventName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASEVENTNAME, newHasEventName);
    }

    public void removeHasEventName(Object oldHasEventName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASEVENTNAME, oldHasEventName);
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