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
 * Source Class: DefaultLanguageDetails <br>
 * @version generated on Thu Jun 05 16:06:07 CEST 2014 by ragarwal
 */
public class DefaultLanguageDetails extends WrappedIndividualImpl implements LanguageDetails {

    public DefaultLanguageDetails(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
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


}
