/**
 * @file	ValuedProperty.java
 * @brief 	Description of a valued property
 * @date	Jun 27, 2014
 * @author	Flore Lantheaume
 * @copyright 	THALES 2014. All rights reserved.
 * 				THALES PROPRIETARY/CONFIDENTIAL.
*/

package eu.threecixty.privacy.datastorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represents a property of the profile with all these values
 * depending on the fact that the property is mono-value or multi-values.
 * It is intended that a property could have several identical values
 */
public final class ValuedProperty {

	/** The path of the property in SPARQL1.1 */
	final String	propertyPath;
	/** The values of the property */
	List<String>	values = null;
	
	
	/**
	 * Create a property with no value
	 * @param propertyPath	The SPARQL1.1 property path
	 */
	public ValuedProperty(String propertyPath ) {
		
		this.propertyPath 	= propertyPath;
		this.values 		= null;
	}
	
	
	/**
	 * Create a property with one value
	 * @param propertyPath	The SPARQL1.1 property path
	 * @param value			The value of the property
	 */
	public ValuedProperty(String propertyPath, String value ) {
		
		this.propertyPath 	= propertyPath;
		this.values 		= new ArrayList<String>();
		this.values.add( value );
	}	

	/**
	 * Create a property with one value
	 * @param propertyPath	The SPARQL1.1 property path
	 * @param value			The values of the property
	 */
	public ValuedProperty(String propertyPath, Collection<String> values ) {
		
		this.propertyPath 	= propertyPath;
		this.values 		= new ArrayList<String>();
		this.values.addAll( values );
	}		
	
	/**
	 * Add a value to the list of values of the property
	 * @param value	The value to be added
	 */
	public void addValue(String value) {
		if ( values == null ) {
			values = new ArrayList<String>();
		}
		values.add( value );
	}
	
	/**
	 * Add a set of values to the list of values of the property
	 * @param values	The values to be added
	 */
	public void addValues(Collection<String> values) {
		if ( this.values == null ) {
			this.values = new ArrayList<String>();
		}
		this.values.addAll( values );
	}
	
	
}
