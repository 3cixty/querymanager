package eu.threecixty.querymanager;


import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.Expr;

/**
 * Abstract class to represent a query.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public abstract class ThreeCixtyQuery {
	
	protected Query query;

	/**
	 * Clones query.
	 * @return
	 */
	public abstract ThreeCixtyQuery cloneQuery();

	/**
	 * Gets Jena's query.
	 *
	 * @return
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Converts the query to string.
	 * @return
	 */
	public String convert2String() {
		return QueryUtils.convert2String(query);
	}

	/**
	 * Creates expressions for filter to add to query.
	 *
	 * <br><br>
	 * The method checks if there is a configuration file associated with
	 * the class of a given object. If not, the method immediately returns. Otherwise,
	 * the method add filters by taking values in a given instance's attributes which were
	 * defined in the configuration file.
	 * 
	 * @param object
	 */
	protected List<Expr> createExprs(Object object) {
		return QueryUtils.createExprs(query, object);
	}

	/**
	 * Adds preference found in a given instance object into the query.
	 * @param object
	 * 			The preference instance which contains preference information such as place, country, city, etc.
	 * @param attrName
	 * 			The attribute name in the preference instance.
	 * @param propertyValue
	 * 			The property which defines triple links and filter.
	 */
	protected List <Expr> createExprs(Object object, String attrName, String propertyValue) {
		return QueryUtils.createExprs(query, object, attrName, propertyValue);
	}

	/**
	 * Adds preference found in a given instance object with a given attribute name and a given property name in the property file.
	 * @param object
	 * 				The preference object.
	 * @param attrName
	 * 				The preference object's attribute name. The attribute name containing a value in the instance object which will be used to add a filter.
	 * @param propertyName
	 * 				The property name in the property file. The property name will be used to find triple links in the property file.
	 */
	protected List <Expr> createExprsFromAttributeNameAndPropertyName(Object object,
			String attrName, String propertyName) {
		return QueryUtils.createExprsFromAttributeNameAndPropertyName(query, object, attrName, propertyName);
	}
}
