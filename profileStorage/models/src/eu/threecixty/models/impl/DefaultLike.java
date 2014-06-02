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
 * Source Class: DefaultLike <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */
public class DefaultLike extends WrappedIndividualImpl implements Like {

    public DefaultLike(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasLikeName
     */
     
    public Collection<? extends String> getHasLikeName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLIKENAME, String.class);
    }

    public boolean hasHasLikeName() {
		return !getHasLikeName().isEmpty();
    }

    public void addHasLikeName(String newHasLikeName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLIKENAME, newHasLikeName);
    }

    public void removeHasLikeName(String oldHasLikeName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLIKENAME, oldHasLikeName);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasLikeType
     */
     
    public Collection<? extends Object> getHasLikeType() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLIKETYPE, Object.class);
    }

    public boolean hasHasLikeType() {
		return !getHasLikeType().isEmpty();
    }

    public void addHasLikeType(Object newHasLikeType) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLIKETYPE, newHasLikeType);
    }

    public void removeHasLikeType(Object oldHasLikeType) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASLIKETYPE, oldHasLikeType);
    }


}
