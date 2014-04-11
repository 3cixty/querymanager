package eu.threecixty.querymanager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Equals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
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
	 * Add preferences found a given object to the query.
	 * <br><br>
	 * The method checks if there is a configuration file associated with
	 * the class of a given object. If not, the method immediately returns. Otherwise,
	 * the method add filters by taking values in a given instance's attributes which were
	 * defined in the configuration file.
	 * 
	 * @param object
	 */
	public static void addPreference(Query query, Object object) {
		if (object == null) return;
		Class <?> clazz = object.getClass();
		Properties props = clazzProperties.get(clazz);
		if (props == null) {
			props = loadProperties(clazz);
			if (props == null) return;
			clazzProperties.put(clazz, props);
		}
		// For a pair in properties, Key must be the same with a class' attribute,
		// Value must be the same with what is used in RDF model.
		for (Object objKey: props.keySet()) {
			String attrName = (String) objKey;
			String nameInRDFModel = (String) props.get(attrName);
			try {
				Field field = clazz.getDeclaredField(attrName);
				if (field == null) continue;
				addPreference(query, object, field, attrName, nameInRDFModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add preferences wrapped in a given field from an instance Object.
	 *
	 * @param object
	 * @param field
	 * @param attrName
	 * @param nameInRDFModel
	 * @throws Exception
	 */
	private static void addPreference(Query query, Object object, Field field, String attrName,
			String nameInRDFModel) throws Exception {
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
	    	addTripleFilter(query, nameInRDFModel, node);
	    } else {
	    	// for DateTime, List, Set. The other types are not supported
	    	if (fieldTypeClass == Date.class) {
	    		Calendar cal = Calendar.getInstance();
	    		cal.setTime((Date) fieldInstanceValue);
	    		NodeValue node = NodeValue.makeDate(cal);
	    		addTripleFilter(query, nameInRDFModel, node);
	    	} else {
	    		addTripleFilterForCollection(query, field, fieldTypeClass,
	    				fieldInstanceValue, nameInRDFModel);
	    	}
	    }

		if (!accessible) { // reset accessible flag
			field.setAccessible(accessible);
		}
	}

	/**
	 * Add triple filters wrapped by elements' value in a given field.
	 * <br><br>
	 * This method only considers a given field is either a list or set
	 * of Integer, Long, Float, Double, Boolean, String.
	 * @param field
	 * @param fieldTypeClass
	 * @param fieldInstanceValue
	 * @param nameInRDFModel
	 * @throws Exception
	 */
	private static void addTripleFilterForCollection(Query query, Field field, Class<?> fieldTypeClass,
			Object fieldInstanceValue, String nameInRDFModel) throws Exception {
		ParameterizedType paramType = (ParameterizedType) field.getGenericType();
		if (paramType == null) return;
		Class <?> paramClazz = (Class<?>) paramType.getActualTypeArguments()[0];
		String methodNameForCollectionParam = clazzNodeValueMethodNames.get(paramClazz);
		Method methodForCollectionParam = NodeValue.class.getMethod(methodNameForCollectionParam, paramClazz);
		if (methodNameForCollectionParam == null) return;
		if (fieldTypeClass.isAssignableFrom(List.class)) {
			List <?> list = (List<?>) fieldInstanceValue;
			for (Object tmp: list) {
		    	NodeValue node = (NodeValue) methodForCollectionParam.invoke(null, tmp);
		    	addTripleFilter(query, nameInRDFModel, node);
			}
		} else if (fieldTypeClass.isAssignableFrom(Set.class)) {
			Set <?> sets = (Set <?>) fieldInstanceValue;
			for (Object tmp: sets) {
		    	NodeValue node = (NodeValue) methodForCollectionParam.invoke(null, tmp);
		    	addTripleFilter(query, nameInRDFModel, node);
			}
		}
	}

	/**
	 * Add a triple filter composed by a given name and node to the query.
	 *
	 * @param nameInRDFModel
	 * @param node
	 */
	private static void addTripleFilter(Query query, String nameInRDFModel, NodeValue node) {
		Triple pattern = Triple.create(Var.alloc("x"),
		        Var.alloc(":" +nameInRDFModel), Var.alloc(nameInRDFModel));

		ElementTriplesBlock block = new ElementTriplesBlock();
		block.addTriple(pattern);

		Expr expr = new E_Equals(new ExprVar(nameInRDFModel), node);
		ElementFilter filter = new ElementFilter(expr);

		ElementGroup body = (ElementGroup) query.getQueryPattern();
		if (body == null)  body = new ElementGroup();

		body.addElement(block);
		body.addElement(filter);

		query.setQueryPattern(body);
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
