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
 * Source Class: DefaultHotelDetail <br>
 * @version generated on Mon Jun 02 16:02:21 CEST 2014 by ragarwal
 */
public class DefaultHotelDetail extends WrappedIndividualImpl implements HotelDetail {

    public DefaultHotelDetail(CodeGenerationInference inference, IRI iri) {
        super(inference, iri);
    }





    /* ***************************************************
     * Object Property http://www.eu.3cixty.org/profile#hasHotelChains
     */
     
    public Collection<? extends Address> getHasHotelChains() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HASHOTELCHAINS,
                                               DefaultAddress.class);
    }

    public boolean hasHasHotelChains() {
	   return !getHasHotelChains().isEmpty();
    }

    public void addHasHotelChains(Address newHasHotelChains) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HASHOTELCHAINS,
                                       newHasHotelChains);
    }

    public void removeHasHotelChains(Address oldHasHotelChains) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HASHOTELCHAINS,
                                          oldHasHotelChains);
    }


    /* ***************************************************
     * Object Property http://www.w3.org/2006/vcard/ns#hasAddress
     */
     
    public Collection<? extends WrappedIndividual> getHas_address() {
        return getDelegate().getPropertyValues(getOwlIndividual(),
                                               Vocabulary.OBJECT_PROPERTY_HAS_ADDRESS,
                                               WrappedIndividualImpl.class);
    }

    public boolean hasHas_address() {
	   return !getHas_address().isEmpty();
    }

    public void addHas_address(WrappedIndividual newHas_address) {
        getDelegate().addPropertyValue(getOwlIndividual(),
                                       Vocabulary.OBJECT_PROPERTY_HAS_ADDRESS,
                                       newHas_address);
    }

    public void removeHas_address(WrappedIndividual oldHas_address) {
        getDelegate().removePropertyValue(getOwlIndividual(),
                                          Vocabulary.OBJECT_PROPERTY_HAS_ADDRESS,
                                          oldHas_address);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasHotelPriceHigh
     */
     
    public Collection<? extends Object> getHasHotelPriceHigh() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELPRICEHIGH, Object.class);
    }

    public boolean hasHasHotelPriceHigh() {
		return !getHasHotelPriceHigh().isEmpty();
    }

    public void addHasHotelPriceHigh(Object newHasHotelPriceHigh) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELPRICEHIGH, newHasHotelPriceHigh);
    }

    public void removeHasHotelPriceHigh(Object oldHasHotelPriceHigh) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELPRICEHIGH, oldHasHotelPriceHigh);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasHotelPriceLow
     */
     
    public Collection<? extends Object> getHasHotelPriceLow() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELPRICELOW, Object.class);
    }

    public boolean hasHasHotelPriceLow() {
		return !getHasHotelPriceLow().isEmpty();
    }

    public void addHasHotelPriceLow(Object newHasHotelPriceLow) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELPRICELOW, newHasHotelPriceLow);
    }

    public void removeHasHotelPriceLow(Object oldHasHotelPriceLow) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELPRICELOW, oldHasHotelPriceLow);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasHotelRoomType
     */
     
    public Collection<? extends Object> getHasHotelRoomType() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELROOMTYPE, Object.class);
    }

    public boolean hasHasHotelRoomType() {
		return !getHasHotelRoomType().isEmpty();
    }

    public void addHasHotelRoomType(Object newHasHotelRoomType) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELROOMTYPE, newHasHotelRoomType);
    }

    public void removeHasHotelRoomType(Object oldHasHotelRoomType) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELROOMTYPE, oldHasHotelRoomType);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasHotelStarCategory
     */
     
    public Collection<? extends Object> getHasHotelStarCategory() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELSTARCATEGORY, Object.class);
    }

    public boolean hasHasHotelStarCategory() {
		return !getHasHotelStarCategory().isEmpty();
    }

    public void addHasHotelStarCategory(Object newHasHotelStarCategory) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELSTARCATEGORY, newHasHotelStarCategory);
    }

    public void removeHasHotelStarCategory(Object oldHasHotelStarCategory) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASHOTELSTARCATEGORY, oldHasHotelStarCategory);
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
     * Data Property http://www.eu.3cixty.org/profile#hasNatureOfPlace
     */
     
    public Collection<? extends Object> getHasNatureOfPlace() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFPLACE, Object.class);
    }

    public boolean hasHasNatureOfPlace() {
		return !getHasNatureOfPlace().isEmpty();
    }

    public void addHasNatureOfPlace(Object newHasNatureOfPlace) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFPLACE, newHasNatureOfPlace);
    }

    public void removeHasNatureOfPlace(Object oldHasNatureOfPlace) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNATUREOFPLACE, oldHasNatureOfPlace);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasNearByTransportMode
     */
     
    public Collection<? extends String> getHasNearByTransportMode() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNEARBYTRANSPORTMODE, String.class);
    }

    public boolean hasHasNearByTransportMode() {
		return !getHasNearByTransportMode().isEmpty();
    }

    public void addHasNearByTransportMode(String newHasNearByTransportMode) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNEARBYTRANSPORTMODE, newHasNearByTransportMode);
    }

    public void removeHasNearByTransportMode(String oldHasNearByTransportMode) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASNEARBYTRANSPORTMODE, oldHasNearByTransportMode);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasPlaceName
     */
     
    public Collection<? extends String> getHasPlaceName() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPLACENAME, String.class);
    }

    public boolean hasHasPlaceName() {
		return !getHasPlaceName().isEmpty();
    }

    public void addHasPlaceName(String newHasPlaceName) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPLACENAME, newHasPlaceName);
    }

    public void removeHasPlaceName(String oldHasPlaceName) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASPLACENAME, oldHasPlaceName);
    }


    /* ***************************************************
     * Data Property http://www.eu.3cixty.org/profile#hasTypeOfFood
     */
     
    public Collection<? extends Object> getHasTypeOfFood() {
		return getDelegate().getPropertyValues(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTYPEOFFOOD, Object.class);
    }

    public boolean hasHasTypeOfFood() {
		return !getHasTypeOfFood().isEmpty();
    }

    public void addHasTypeOfFood(Object newHasTypeOfFood) {
	    getDelegate().addPropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTYPEOFFOOD, newHasTypeOfFood);
    }

    public void removeHasTypeOfFood(Object oldHasTypeOfFood) {
		getDelegate().removePropertyValue(getOwlIndividual(), Vocabulary.DATA_PROPERTY_HASTYPEOFFOOD, oldHasTypeOfFood);
    }


}
