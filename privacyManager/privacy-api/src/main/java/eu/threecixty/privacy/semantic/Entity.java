package eu.threecixty.privacy.semantic;

/**
 *  Any kind of entity, whether they be classes (e.g. person), relations 
 *  (e.g. loves) or individuals (e.g. you), and constructing statements 
 *  to describe them (e.g &lt;the moon&gt; &lt;is made of&gt;  &lt;cheese&gt;).
 */
public interface Entity {

    /**
     * The string representation separator in an entity between the entity 
     * itself and a particular instance identifier. 
     * For example in "http://myontology.com/geo#Location|here",
     * "http://myontology.com/time#Date|today" and 
     * "http://myontology.com/person#user|self"
     * the entity-id separator is "|".  
     */
    public static String ENTITY_ID_SEPARATOR = "|";

    /**
     * Returns the ontology URL of this semantic concept. 
     * For example, for the concept represented by 
     * "http://www.w3.org/2003/01/geo/wgs84_pos#Point",
     * the returned URL will be "http://www.w3.org/2003/01/geo/wgs84_pos".
     *
     * @return a String representation of the corresponding ontology URL.
     */
    String getOntologyURL();

    /**
     * Returns a string representation of this entity.
      */
    String getEntityAsString();

    /**
     * Returns the entity ID as a string. For example, if the entity is
     * {@code http://purl.org/goodrelations/v1#PaymentMethodCreditCard|VISA}, 
     * the entity ID is "VISA".
     *
     * @return a string corresponding to the ID represented by this entity
     */
    String getEntityIDAsString();

    /**
     * Returns the entity type as a string. For example, if the entity is
     * {@code http://purl.org/goodrelations/v1#PaymentMethodCreditCard|VISA}, 
     * the entity type is
     * "http://purl.org/goodrelations/v1#PaymentMethodCreditCard".
     *
     * @return a string corresponding to the type of the this entity
     */
    String getEntityTypeAsString();

    /**
     * Returns the entity type as a short string. For example, if the entity is
     * {@code http://purl.org/goodrelations/v1#PaymentMethodCreditCard|VISA}, 
     * the entity type's short string is "PaymentMethodCreditCard".
     *
     * @return a string corresponding to the type of the this entity
     */
    String getEntityTypeAsShortString();

}
