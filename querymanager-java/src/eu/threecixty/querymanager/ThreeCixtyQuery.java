package eu.threecixty.querymanager;


import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.expr.Expr;

import eu.threecixty.ThreeCixtyExpression;

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
	 * Adds expressions and triples found from a given object to a list of expressions and triples.
	 * 
	 * @param object
	 */
	protected void addExprsAndTriples(Object object, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr) {
		QueryUtils.addExprsAndTriples(query, object, exprs, triples, threeCixyExpr);
	}

	/**
	 * Adds expressions and triples found from a given attribute name of a given object and a given
	 * property value to a list of expressions and triples.
	 * @param object
	 * 			The preference instance which contains preference information such as place, country, city, etc.
	 * @param attrName
	 * 			The attribute name in the preference instance.
	 * @param propertyValue
	 * 			The property which defines triple links and filter.
	 */
	protected void addExprsAndTriples(Object object, String attrName, String propertyValue, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr) {
		QueryUtils.addExprsAndTriples(query, object, attrName, propertyValue, exprs, triples, threeCixyExpr);
	}

	/**
	 * Adds expressions and triples found from a given attribute name of a given object and a given
	 * property name in the property file to a list of expressions and triples.
	 * @param object
	 * 				The preference object.
	 * @param attrName
	 * 				The preference object's attribute name. The attribute name containing a value in the instance object which will be used to add a filter.
	 * @param propertyName
	 * 				The property name in the property file. The property name will be used to find triple links in the property file.
	 * @param exprs
	 * 				The list of expressions which is used to contain ones created by this method
	 * @param triples
	 * 				The list of triples which is used to contain ones created by this method
	 * @param threeCixyExpr
	 * 				The expression which is used for filtering results.
	 */
	protected void addExprsAndTriplesFromAttributeNameAndPropertyName(Object object,
			String attrName, String propertyName, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr) {
		QueryUtils.addExprsAndTriplesFromAttributeNameAndPropertyName(query, object, attrName, propertyName, exprs, triples, threeCixyExpr);
	}
}
