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
import com.hp.hpl.jena.sparql.expr.E_LogicalOr;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

/**
 * Utility class.
 *
 * @author Cong-Kinh NGUYEN
 *
 */
public class QueryUtils {

	/**Attribute which contains properties files*/
	private static Map<Class<?>, Properties> clazzProperties = new HashMap<Class<?>, Properties>();

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

	public static void createFiltersWithOrOperandForExprs(Query query, List<Expr> exprs) {
		if (exprs.size() == 0) return;
		removeDoubleExpressions(exprs);
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		if (body == null)  body = new ElementGroup();
		if (exprs.size() == 1) {
			ElementFilter filter = new ElementFilter(exprs.get(0));
			body.addElementFilter(filter);
		} else {
			Expr tmpExpr = exprs.get(0);
			for (int i = 1; i < exprs.size(); i++) {
				tmpExpr = new E_LogicalOr(tmpExpr, exprs.get(i));
			}
			ElementFilter filter = new ElementFilter(tmpExpr);
			body.addElementFilter(filter);
		}
		query.setQueryPattern(body);
	}

	/**
	 * Add preferences found in the attributes of a given object to the query.
	 * <br><br>
	 * The method checks if there is a configuration file associated with
	 * the class of a given object. If not, the method immediately returns. Otherwise,
	 * the method add filters by taking values in a given instance's attributes which were
	 * defined in the configuration file.
	 * 
	 * @param object
	 */
	public static List <Expr> createExprs(Query query, Object object) {
		List <Expr> exprs = new ArrayList <Expr>();
		if (object == null) return exprs;
		Class <?> clazz = object.getClass();
		Properties props = clazzProperties.get(clazz);
		if (props == null) {
			props = loadProperties(clazz);
			if (props == null) return exprs;
			clazzProperties.put(clazz, props);
		}
		// For a pair in properties, Key must be the same with a class' attribute,
		// Value must follow the expression [subject,predicate,object]*,filterVariable
		for (Object objKey: props.keySet()) {
			String attrName = (String) objKey;
			String property = (String) props.get(attrName);
			
			exprs.addAll(createExprs(query, object, attrName, property));
		}
		return exprs;
	}

	/**
	 * Adds preference which is the value of a given attribute name (attName) in a given object instance (object)
	 * into the query (query).
	 * <br><br>
	 * The triple links and filter are defined in the corresponding property file.
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
	public static List <Expr> createExprsFromAttributeNameAndPropertyName(Query query, Object object,
			String attrName, String propertyName) {
		List <Expr> exprs = new ArrayList <Expr>();
		if (object == null || query == null || attrName == null || propertyName == null) return exprs;
		Class <?> clazz = object.getClass();
		Properties props = clazzProperties.get(clazz);
		if (props == null) {
			props = loadProperties(clazz);
			if (props == null) return exprs;
			clazzProperties.put(clazz, props);
		}
		// Property Value must follow the regular expression [subject,predicate,object]*,filterVariableName
		String property = (String) props.get(propertyName);
		if (property == null) return exprs;
		return createExprs(query, object, attrName, property);

	}

	/**
	 * Adds preferences into a given query.
	 * <br><br>
	 * <code>
	 * Here is an example of a given propertyValue:
	 * event,lode:atPlace,_augplace,_augplace,vcard:adr,_augaddress,_augaddress,vcard:country-name,_augcountryname,_augcountryname.
	 * <br><br>
	 * The first element will be replaced by the real variable name used in a given query. Then every three consecutive elements is composed of a triple.
	 * The last element will be a filter variable name to be added into the query.
	 * </code>
	 * @param query
	 * 			The query.
	 * @param object
	 * 			The instance containing preference object.
	 * @param attrName
	 * 			The attribute name in the given instance.
	 * @param propertyValue
	 * 			The property which defines a list of consecutive triples and filter.
	 */
	public static List<Expr> createExprs(Query query, Object object, String attrName, String propertyValue) {
		List <Expr> exprs = new ArrayList <Expr>();
		if (query == null || object == null || attrName == null || propertyValue == null) return exprs;
		// last element is a filter variable
		// triples correspond with every three elements
		String [] configStrs = propertyValue.split(",");
		String filterVarName = configStrs[configStrs.length - 1];
	
		Class <?> clazz = object.getClass();
		
		if (configStrs.length > 1) {
			String realVarName = findRealVariableName(query, configStrs[0]);
			if (realVarName != null && !realVarName.equals("")) configStrs[0] = realVarName;
		}
		
		try {
			Field field = clazz.getDeclaredField(attrName);
			if (field == null) return exprs;
			addTriples(query, configStrs);
			exprs = createExprs(object, field, filterVarName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exprs;
	}

	/**
	 * Adds triples described by a given array to the query.
	 * @param query
	 * 				The query.
	 * @param configStrs
	 * 				Array containing every three consecutive elements which is a triple.
	 */
	private static void addTriples(Query query, String[] configStrs) {
		if (configStrs.length <= 1) return;
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		for (int i = 0; i < configStrs.length / 3; i++) {
			if (i * 3 + 2 >= configStrs.length - 1) return;
			String subject = configStrs[i * 3];
			String predicate = configStrs[i * 3 + 1];
			String predicateURI = replacePrefix(query, predicate);
			if (predicateURI == null) continue;
			String object = configStrs[i * 3 + 2];
			Triple pattern = Triple.create(Var.alloc(subject),
			        NodeFactory.createURI(predicateURI), Var.alloc(object));
			if (existTriple(pattern, body)) continue;
			body.addTriplePattern(pattern);
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
	 * Add preferences wrapped in a given field from an instance Object.
	 *
	 * @param object
	 * @param field
	 * @param attrName
	 * @param filterVarName
	 * @throws Exception
	 */
	private static List <Expr> createExprs(Object object, Field field, 
			String filterVarName) throws Exception {
		List <Expr> exprs = new ArrayList <Expr>();
	    boolean accessible = field.isAccessible();
	    if (!accessible) {
	    	field.setAccessible(true);
	    }
	    Class <?> fieldTypeClass = field.getType();
	    Object fieldInstanceValue = field.get(object);
	    if (fieldInstanceValue == null) return exprs;
	    String methodName = clazzNodeValueMethodNames.get(fieldTypeClass);
	    if (methodName != null) {
	    	Method method = NodeValue.class.getMethod(methodName, fieldTypeClass);
	    	NodeValue node = (NodeValue) method.invoke(null, fieldInstanceValue);
	    	Expr expr = createExprForFilter(filterVarName, node);
	    	exprs.add(expr);
	    } else {
	    	// for DateTime, List, Set. The other types are not supported
	    	if (fieldTypeClass == Date.class) {
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTime((Date) fieldInstanceValue);
	    		NodeValue node = NodeValue.makeDate(cal);
		    	Expr expr = createExprForFilter(filterVarName, node);
		    	exprs.add(expr);
	    	} else {
	    		exprs.addAll(createExprsForFilterOfCollection(field, fieldTypeClass,
	    				fieldInstanceValue, filterVarName));
	    	}
	    }

		if (!accessible) { // reset accessible flag
			field.setAccessible(accessible);
		}
		return exprs;
	}

	/**
	 * Add triple filters wrapped by elements' value in a given field.
	 * <br><br>
	 * This method only considers a given field is either a list or set
	 * of Integer, Long, Float, Double, Boolean, String.
	 * @param field
	 * @param fieldTypeClass
	 * @param fieldInstanceValue
	 * @param filterVarName
	 * @throws Exception
	 */
	private static List<Expr> createExprsForFilterOfCollection(Field field, Class<?> fieldTypeClass,
			Object fieldInstanceValue, String filterVarName) throws Exception {
		List <Expr> exprs = new ArrayList <Expr>();
		ParameterizedType paramType = (ParameterizedType) field.getGenericType();
		if (paramType == null) return exprs;
		Class <?> paramClazz = (Class<?>) paramType.getActualTypeArguments()[0];
		String methodNameForCollectionParam = clazzNodeValueMethodNames.get(paramClazz);
		Method methodForCollectionParam = NodeValue.class.getMethod(methodNameForCollectionParam, paramClazz);
		if (methodNameForCollectionParam == null) return exprs;
		if (fieldTypeClass.isAssignableFrom(List.class)) {
			List <?> list = (List<?>) fieldInstanceValue;
			for (Object tmp: list) {
		    	NodeValue node = (NodeValue) methodForCollectionParam.invoke(null, tmp);
		    	Expr expr = createExprForFilter(filterVarName, node);
		    	exprs.add(expr);
			}
		} else if (fieldTypeClass.isAssignableFrom(Set.class)) {
			Set <?> sets = (Set <?>) fieldInstanceValue;
			for (Object tmp: sets) {
		    	NodeValue node = (NodeValue) methodForCollectionParam.invoke(null, tmp);
		    	Expr expr = createExprForFilter(filterVarName, node);
		    	exprs.add(expr);
			}
		}
		return exprs;
	}

	/**
	 * Add a triple filter composed by a given name and node to the query.
	 *
	 * @param filtervarName
	 * @param node
	 */
	private static Expr createExprForFilter(String filtervarName, NodeValue node) {

		Expr expr = new E_Equals(new ExprVar(filtervarName), node);
		return expr;
	}

	/**
	 * Removes double expressions.
	 *
	 * @param exprs
	 */
	private static void removeDoubleExpressions(List <Expr> exprs) {
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
	 * Checks whether or not a given triple exists in a given element group.
	 * @param triple
	 * @param body
	 * @return
	 */
	private static boolean existTriple(Triple triple, ElementGroup body) {
		for (Element element: body.getElements()) {
			if (element instanceof ElementTriplesBlock) {
				ElementTriplesBlock etb = (ElementTriplesBlock) element;
				
				Iterator <Triple> triples = etb.patternElts();
				for ( ; triples.hasNext(); ) {
					Triple tmp = triples.next();
					if (triple.equals(tmp)) return true;
				}
			}
		}
		return false;
	}

	/**
	 * Loads properties from a resource associated with the simple name of a given class.
	 * @param clazz
	 * @return
	 */
	private static Properties loadProperties(Class<?> clazz) {
		String simpleName = clazz.getSimpleName();
		InputStream input = clazz.getResourceAsStream("/" + simpleName + ".properties");
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
	
	private QueryUtils() {
	}
}
