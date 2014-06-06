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
 * Source Class: DefaultLanguage <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */
public class DefaultLanguage extends WrappedIndividualImpl implements Language {

    public DefaultLanguage(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
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
     * Data Property http://www.eu.3cixty.org/profile#hasLanguageName
     */
     
    public Collection<? extends String> getHasLanguageName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLANGUAGENAME, String.class);
    }

    public boolean hasHasLanguageName() {
		return !getHasLanguageName().isEmpty();
    }

    public void addHasLanguageName(String newHasLanguageName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLANGUAGENAME, newHasLanguageName);
    }

    public void removeHasLanguageName(String oldHasLanguageName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLANGUAGENAME, oldHasLanguageName);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasLanguageState
     */
     
    public Collection<? extends Object> getHasLanguageState() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLANGUAGESTATE, Object.class);
    }

    public boolean hasHasLanguageState() {
		return !getHasLanguageState().isEmpty();
    }

    public void addHasLanguageState(Object newHasLanguageState) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLANGUAGESTATE, newHasLanguageState);
    }

    public void removeHasLanguageState(Object oldHasLanguageState) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLANGUAGESTATE, oldHasLanguageState);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#wantsLanguageState
     */
     
    public Collection<? extends Object> getWantsLanguageState() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_WANTSLANGUAGESTATE, Object.class);
    }

    public boolean hasWantsLanguageState() {
		return !getWantsLanguageState().isEmpty();
    }

    public void addWantsLanguageState(Object newWantsLanguageState) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_WANTSLANGUAGESTATE, newWantsLanguageState);
    }

    public void removeWantsLanguageState(Object oldWantsLanguageState) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_WANTSLANGUAGESTATE, oldWantsLanguageState);
    }


}
