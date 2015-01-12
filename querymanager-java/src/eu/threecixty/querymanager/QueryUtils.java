package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;

import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.E_GreaterThan;
import com.hp.hpl.jena.sparql.expr.E_GreaterThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LessThan;
import com.hp.hpl.jena.sparql.expr.E_LessThanOrEqual;
import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.E_Str;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.util.ExprUtils;

import eu.threecixty.ThreeCixtyExpression;

/**
 * Utility class.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class QueryUtils {

	/**Attribute which contains properties files*/
	private static Map<String, Properties> clazzProperties = new HashMap<String, Properties>();

	/**Attribute which maps between a class name and a static method name to create an instance of NodeValue from
	 * an instance of the class name*/
	private static Map<Class<?>, String> clazzNodeValueMethodNames = new HashMap<Class<?>, String>();

	/**Attribute which maps between a combination of predicate and object (fixed within an Ontology) and a variable name by default.
	 * This variable name needs to be replaced by a real variable name used in the query.*/
	private static Map<String, String> predicateObjects = new HashMap <String, String>();

	//initiate the following code once
	static {
		if (clazzNodeValueMethodNames.size() == 0) {
			// integer
			clazzNodeValueMethodNames.put(int.class, "makeInteger");
			clazzNodeValueMethodNames.put(Integer.class, "makeInteger");
			
			// long
			clazzNodeValueMethodNames.put(long.class, "makeInteger");
			clazzNodeValueMethodNames.put(Long.class, "makeInteger");

			// String
			clazzNodeValueMethodNames.put(String.class, "makeString");

			// float
			clazzNodeValueMethodNames.put(float.class, "makeFloat");
			clazzNodeValueMethodNames.put(Float.class, "makeFloat");

			//double
			clazzNodeValueMethodNames.put(double.class, "makeDouble");
			clazzNodeValueMethodNames.put(Double.class, "makeDouble");

			// boolean
			clazzNodeValueMethodNames.put(boolean.class, "makeBoolean");
			clazzNodeValueMethodNames.put(Boolean.class, "makeBoolean");
		}
		if (predicateObjects.size() == 0) {
			predicateObjects.put("event", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://linkedevents.org/ontology/Event");
		}
	}
	
	/**
	 * Converts a given query to string.
	 *
	 * @param query
	 * @return
	 */
	public static String convert2String(Query query) {
		if (query == null) return null;
		return query.toString();
	}

	/**
	 * Creates an expression which is composed of AND for all the given list of expressions.
	 * @param exprs
	 * @return
	 */
	public static Expr createExprWithAndOperandForExprs(List<Expr> exprs) {
		if (exprs.size() == 0) return null;
		removeDoubleExpressions(exprs);
		if (exprs.size() == 1) {
			return exprs.get(0);
		} else {
			Expr tmpExpr = exprs.get(0);
			for (int i = 1; i < exprs.size(); i++) {
				tmpExpr = new E_LogicalAnd(tmpExpr, exprs.get(i));
			}
			return tmpExpr;
		}
	}

	/**
	 * Adds a list of triples into a given query.
	 * @param triples
	 * @param query
	 */
	public static void addTriplesIntoQuery(List <Triple> triples, Query query) {
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		if (body == null)  body = new ElementGroup();
		for (Triple triple: triples) {
			body.addTriplePattern(triple);
		}
		query.setQueryPattern(body);
	}

	/**
	 * Adds expressions and triples from a given object which a part in preferences to a 
	 * list of expressions and a list of triples.
	 * <br><br>
	 * The method checks if there is a configuration file associated with
	 * the class of a given object. If not, the method immediately returns. Otherwise,
	 * the method adds predefined triples in the property file and expressions by
	 * taking the given instance's attributes and a corresponding property configuration
	 * found in the property file.
	 * 
	 * @param object
	 */
	public static void addExprsAndTriples(Query query, Object object, List <Expr> exprs, List<Triple> triples,
			ThreeCixtyExpression threeCixyExpr, boolean isForEvents) {
		if (object == null) return;
		Class <?> clazz = object.getClass();
		String keyName = getNameFromClass(clazz, isForEvents);
		Properties props = clazzProperties.get(keyName);
		if (props == null) {
			props = loadProperties(clazz, isForEvents);
			if (props == null) return;
			clazzProperties.put(keyName, props);
		}
		// For a pair in properties, Key must be the same with a class' attribute,
		// Value must follow the expression [subject,predicate,object]*,variable
		for (Object objKey: props.keySet()) {
			String attrName = (String) objKey;
			String property = (String) props.get(attrName);
			
			addExprsAndTriples(query, object, attrName, property, exprs, triples, threeCixyExpr);
		}
	}

	/**
	 * Adds expressions and triples from a given attribute name of a given object and a given property name in the property file.
	 *
	 * @param query
	 * 			The query needs to be augmented.
	 * @param object
	 * 			The preference instance.
	 * @param attrName
	 * 			The preference instance's attribute name.
	 * @param propertyName
	 * 			The property name in the property file.
	 */
	public static void addExprsAndTriplesFromAttributeNameAndPropertyName(Query query, Object object,
			String attrName, String propertyName, List <Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr, boolean isForEvents) {
		if (object == null || query == null || attrName == null || propertyName == null) return;
		Class <?> clazz = object.getClass();
		String keyName = getNameFromClass(clazz, isForEvents);
		Properties props = clazzProperties.get(keyName);
		if (props == null) {
			props = loadProperties(clazz, isForEvents);
			if (props == null) return;
			clazzProperties.put(keyName, props);
		}
		// Property Value must follow the regular expression [subject,predicate,object]*,variableName
		String property = (String) props.get(propertyName);
		if (property == null) return;
		addExprsAndTriples(query, object, attrName, property, exprs, triples, threeCixyExpr);

	}

	/**
	 * Adds expressions and triples by taking the value from a given attribute name of a given object.
	 * <br><br>
	 * <code>
	 * Here is an example of a given propertyValue:
	 * event,lode:atPlace,_augplace,_augplace,vcard:adr,_augaddress,_augaddress,vcard:country-name,_augcountryname,_augcountryname.
	 * <br><br>
	 * The first element will be replaced by the real variable name used in a given query. Then every three consecutive elements is composed of a triple.
	 * The last element will be a variable name to be used to create expressions.
	 * </code>
	 * @param query
	 * 			The query.
	 * @param object
	 * 			The instance containing preference object.
	 * @param attrName
	 * 			The attribute name in the given instance.
	 * @param propertyValue
	 * 			The property which defines a list of consecutive triples and a variable.
	 */
	public static void addExprsAndTriples(Query query, Object object, String attrName, String propertyValue,
			List<Expr> exprs, List <Triple> triples, ThreeCixtyExpression threeCixyExpr) {
		if (query == null || object == null || attrName == null || propertyValue == null) return;
		// last element is a variable
		// triples correspond with every three elements
		String [] configStrs = propertyValue.split(",");
	
		Class <?> clazz = object.getClass();
		
		if (configStrs.length > 1) {
			String realVarName = findRealVariableName(query, configStrs[0]);
			if (realVarName != null && !realVarName.equals("")) configStrs[0] = realVarName;
		}
		
		String varName = configStrs[configStrs.length - 1];
		
		try {
			Field field = clazz.getDeclaredField(attrName);
			if (field == null) return;
			addTriples(query, configStrs, triples);
			addExprs(object, field, varName, exprs, threeCixyExpr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds ORDER DESC created by a given list of expressions to a given query.
	 * @param exprs
	 * 				List of expressions which is supposed that the more index value is, the less priority is.
	 * @param query
	 * 				The Jena query.
	 */
	public static void addOrderToQuery(List <Expr> exprs, Query query) {
		// TODO: relate to other parts which will use this method
		if (exprs == null || query == null) return;
		for (Expr expr: exprs) {
			query.addOrderBy(expr, Query.ORDER_DESCENDING);
		}
	}

	/**
	 * Adds var names in order to make ORDER DESC when a given query contains GROUP BY.
	 * @param addedVarNameResults
	 * @param query
	 */
	public static void addVarNameResultsToQuery(List<Expr> exprs, Query query) {
		if (exprs == null || query == null) return;
		if (query.hasAggregators()) return;
		for (Expr expr: exprs) {
			query.addResultVar(expr);
		}
	}

	/**
	 * Adds triples described by a given array into a given list of triples.
	 * @param query
	 * 				The query.
	 * @param configStrs
	 * 				Array containing every three consecutive elements which is a triple.
	 * @param result
	 * 				The given list of triples.
	 */
	private static void addTriples(Query query, String[] configStrs, List <Triple> result) {
		if (configStrs.length <= 1) return;
		for (int i = 0; i < configStrs.length / 3; i++) {
			if (i * 3 + 2 >= configStrs.length - 1) return;
			String subject = configStrs[i * 3];
			String predicate = configStrs[i * 3 + 1];
			String predicateURI = replacePrefix(query, predicate);
			//if (predicateURI == null) continue;
			String object = configStrs[i * 3 + 2];
			String objUri = replacePrefix(query, object);
			Triple pattern = Triple.create(Var.alloc(subject),
					predicateURI != null ? NodeFactory.createURI(predicateURI) : Var.alloc(predicate),
			        objUri == null ? Var.alloc(object) : NodeFactory.createURI(objUri));
			if (result.contains(pattern)) continue;
			result.add(pattern);
		}
		
	}

	/**
	 * Returns the full URI for a given predicate.
	 * @param query
	 * 				The query.
	 * @param predicate
	 * 				The predicate.
	 * @return string containing the full predicate's URI.
	 */
	private static String replacePrefix(Query query, String predicate) {
		int index = predicate.indexOf(':');
		if (index < 0) return null;
		String prefix = predicate.substring(0, index);
		String fullUri = query.getPrefix(prefix);
		return fullUri + predicate.substring(index + 1);
	}

	/**
	 * Finds the real variable name used in a given query for the first subject in a property line.
	 * @param query
	 * @param predefinedVarName
	 * @return
	 */
	private static String findRealVariableName(Query query, String predefinedVarName) {
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		if (body == null) return null;
		String predicateObject = predicateObjects.get(predefinedVarName);
		if (predicateObject == null) return null;
		for (Element el: body.getElements()) {
			if (el instanceof ElementPathBlock) {
				ElementPathBlock epb = (ElementPathBlock) el;
				Iterator<TriplePath> triplePaths = epb.getPattern().iterator();
				for ( ; triplePaths.hasNext(); ) {
					TriplePath tp = triplePaths.next();
					if (predicateObject.equals(
							tp.getPredicate().toString() + " " + tp.getObject().toString())) {
						return tp.getSubject().getName();
					}
				}
				triplePaths = epb.patternElts();
				for ( ; triplePaths.hasNext(); ) {
					TriplePath tp = triplePaths.next();
					if (predicateObject.equals(
							tp.getPredicate().toString() + " " + tp.getObject().toString())) {
						return tp.getSubject().getName();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Adds expressions from a given field and a given expression variable name.
	 *
	 * @param object
	 * @param field
	 * @param varName
	 * @param results
	 * @throws Exception
	 */
	private static void addExprs(Object object, Field field, 
			String varName, List <Expr> results, ThreeCixtyExpression threeCixyExpr) throws Exception {
	    boolean accessible = field.isAccessible();
	    if (!accessible) {
	    	field.setAccessible(true);
	    }
	    Class <?> fieldTypeClass = field.getType();
	    Object fieldInstanceValue = field.get(object);

	    if (fieldInstanceValue == null) return;
	    String methodName = clazzNodeValueMethodNames.get(fieldTypeClass);
	    if (methodName != null) {
	    	Method method = NodeValue.class.getMethod(methodName, fieldTypeClass);
	    	NodeValue node = (NodeValue) method.invoke(null, fieldInstanceValue);
	    	Expr expr = createExprForVariableAndNode(varName, node, threeCixyExpr);
	    	results.add(expr);
	    } else {
	    	// for DateTime, List, Set. The other types are not supported
	    	if (fieldTypeClass == Date.class) {
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTime((Date) fieldInstanceValue);
	    		NodeValue node = NodeValue.makeDate(cal);
		    	Expr expr = createExprForVariableAndNode(varName, node, threeCixyExpr);
		    	results.add(expr);
	    	} else {
	    		addExprsForInstanceOfCollection(field, fieldTypeClass,
	    				fieldInstanceValue, varName, results, threeCixyExpr);
	    	}
	    }

		if (!accessible) { // reset accessible flag
			field.setAccessible(accessible);
		}
		return;
	}

	/**
	 * Adds expressions for a collection.
	 * <br><br>
	 * This method only considers a given field is either a list or set
	 * of Integer, Long, Float, Double, Boolean, String.
	 * @param field
	 * @param fieldTypeClass
	 * @param fieldInstanceValue
	 * @param varName
	 * @throws Exception
	 */
	private static void addExprsForInstanceOfCollection(Field field, Class<?> fieldTypeClass,
			Object fieldInstanceValue, String varName, List <Expr> results, ThreeCixtyExpression threeCixyExpr) throws Exception {
		ParameterizedType paramType = (ParameterizedType) field.getGenericType();
		if (paramType == null) return ;
		Class <?> paramClazz = (Class<?>) paramType.getActualTypeArguments()[0];
		String methodNameForCollectionParam = clazzNodeValueMethodNames.get(paramClazz);
		Method methodForCollectionParam = NodeValue.class.getMethod(methodNameForCollectionParam, paramClazz);
		if (methodNameForCollectionParam == null) return ;
		if (fieldTypeClass.isAssignableFrom(List.class)) {
			List <?> list = (List<?>) fieldInstanceValue;
			for (Object tmp: list) {
		    	NodeValue node = (NodeValue) methodForCollectionParam.invoke(null, tmp);
		    	Expr expr = createExprForVariableAndNode(varName, node, threeCixyExpr);
		    	results.add(expr);
			}
		} else if (fieldTypeClass.isAssignableFrom(Set.class)) {
			Set <?> sets = (Set <?>) fieldInstanceValue;
			for (Object tmp: sets) {
		    	NodeValue node = (NodeValue) methodForCollectionParam.invoke(null, tmp);
		    	Expr expr = createExprForVariableAndNode(varName, node, threeCixyExpr);
		    	results.add(expr);
			}
		}
		return ;
	}

	/**
	 * Creates an expression from a given name, node, and 3cixty expression.
	 *
	 * @param varName
	 * @param node
	 */
	private static Expr createExprForVariableAndNode(String varName, NodeValue node, ThreeCixtyExpression threeCixyExpr) {

		if (threeCixyExpr == null) return null;
		
		Expr exprVar = null;
		if (varName.contains("?")) { // is an expression
			exprVar = ExprUtils.parse(varName);
		}

		if (threeCixyExpr == ThreeCixtyExpression.Equal) {
			return new E_Equals(exprVar == null ? new ExprVar(varName) : exprVar, node);
		} else if (threeCixyExpr == ThreeCixtyExpression.GreaterThan) {
			return new E_GreaterThan(exprVar == null ? new ExprVar(varName) : exprVar, node);
		} else if (threeCixyExpr == ThreeCixtyExpression.GreaterThanOrEqual) {
			return new E_GreaterThanOrEqual(exprVar == null ? new ExprVar(varName) : exprVar, node);
		} else if (threeCixyExpr == ThreeCixtyExpression.LessThan) {
			return new E_LessThan(exprVar == null ? new ExprVar(varName) : exprVar, node);
		} else if (threeCixyExpr == ThreeCixtyExpression.LessThanOrEqual) {
			return new E_LessThanOrEqual(exprVar == null ? new ExprVar(varName) : exprVar, node);
		} else if (threeCixyExpr == ThreeCixtyExpression.StringEqual) {
			return new E_Equals(new E_Str(new ExprVar(varName)), node);
		} else {
			return new E_NotEquals(exprVar == null ? new ExprVar(varName) : exprVar, node);
		}
	}

	/**
	 * Removes double expressions.
	 *
	 * @param exprs
	 */
	public static void removeDoubleExpressions(List <Expr> exprs) {
		List <Expr> tmpExprs = new ArrayList <Expr>();
		for (Expr expr: exprs) {
			if (tmpExprs.contains(expr)) continue;
			tmpExprs.add(expr);
		}
		exprs.clear();
		exprs.addAll(tmpExprs);
		tmpExprs.clear();
	}

	/**
	 * Loads properties from a resource associated with the simple name of a given class.
	 * @param clazz
	 * @return
	 */
	private static Properties loadProperties(Class<?> clazz, boolean isForEvents) {
		InputStream input = clazz.getResourceAsStream("/" + getNameFromClass(clazz, isForEvents));
		if (input != null) {
			try {
				Properties props = new Properties();
				props.load(input);
				input.close();
				return props;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String getNameFromClass(Class<?> clazz, boolean isForEvents) {
		String simpleName = clazz.getSimpleName();
		return (isForEvents ? simpleName + "_events" : simpleName + "_pois") + ".properties";
	}
	
	private QueryUtils() {
	}
}
