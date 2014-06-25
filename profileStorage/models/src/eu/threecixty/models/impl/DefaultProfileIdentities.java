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
 * Source Class: DefaultProfileIdentities <br>
 * @version generated on Wed Jun 25 09:55:40 CEST 2014 by ragarwal
 */
public class DefaultProfileIdentities extends WrappedIndividualImpl implements ProfileIdentities {

    public DefaultProfileIdentities(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasSource
     */
     
    public Collection<? extends String> getHasSource() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASSOURCE, String.class);
    }

    public boolean hasHasSource() {
		return !getHasSource().isEmpty();
    }

    public void addHasSource(String newHasSource) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASSOURCE, newHasSource);
    }

    public void removeHasSource(String oldHasSource) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASSOURCE, oldHasSource);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasUserAccountID
     */
     
    public Collection<? extends String> getHasUserAccountID() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUSERACCOUNTID, String.class);
    }

    public boolean hasHasUserAccountID() {
		return !getHasUserAccountID().isEmpty();
    }

    public void addHasUserAccountID(String newHasUserAccountID) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUSERACCOUNTID, newHasUserAccountID);
    }

    public void removeHasUserAccountID(String oldHasUserAccountID) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUSERACCOUNTID, oldHasUserAccountID);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasUserInteractionMode
     */
     
    public Collection<? extends Object> getHasUserInteractionMode() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUSERINTERACTIONMODE, Object.class);
    }

    public boolean hasHasUserInteractionMode() {
		return !getHasUserInteractionMode().isEmpty();
    }

    public void addHasUserInteractionMode(Object newHasUserInteractionMode) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUSERINTERACTIONMODE, newHasUserInteractionMode);
    }

    public void removeHasUserInteractionMode(Object oldHasUserInteractionMode) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASUSERINTERACTIONMODE, oldHasUserInteractionMode);
    }


}
