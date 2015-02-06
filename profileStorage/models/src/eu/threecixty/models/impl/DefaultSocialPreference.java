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
 * Source Class: DefaultSocialPreference <br>
 * @version generated on Wed Jun 25 09:55:40 CEST 2014 by ragarwal
 */
public class DefaultSocialPreference extends WrappedIndividualImpl implements SocialPreference {

    public DefaultSocialPreference(CodeGenerationInference inference, IRI iri) {
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


}
