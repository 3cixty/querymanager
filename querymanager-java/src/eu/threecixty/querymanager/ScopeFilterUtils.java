package eu.threecixty.querymanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.expr.E_NotEquals;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;

import eu.threecixty.Configuration;

/**
 * This utility class is to add filters to a given query to limit content the query can get based on a given scope.
 * @author Cong-Kinh NGUYEN
 *
 */
public class ScopeFilterUtils {

	private static final String [] PROFILE_URIS = {Configuration.SCHEMA_URI + "familyName",
		Configuration.SCHEMA_URI + "givenName"};

	private static final String [] KNOWS_URIS = {Configuration.SCHEMA_URI + "knows", Configuration.SCHEMA_URI + "follows"};
	
	/**
	 * Checks whether or not a given query contains an explicit predicate for User Profile. 
	 * @param query
	 * @return
	 */
	public static boolean containExplicitProfilePredicate(Query query) {
		return findExplicitPredicateInUris(query, PROFILE_URIS);
	}

	/**
	 * Checks whether or not a given query contains an explicit predicate for KNOWS or FOLLOWS. 
	 * @param query
	 * @return
	 */
	public static boolean containExplicitKnowsOrFollowsPredicate(Query query) {
		return findExplicitPredicateInUris(query, KNOWS_URIS);
	}
	
	/**
	 * Adds filters to a given query so that the query cannot get information about Profile scope
	 * (familyName & givenName).
	 * @param query
	 */
	public static void addFilterToRestrictProfile(Query query) {
		addFilterToRestrictUriPredicates(query, PROFILE_URIS);
	}
	
	public static void addFilterToRestrictKnows(Query query) {
		addFilterToRestrictUriPredicates(query, KNOWS_URIS);
	}
	
	/**
	 * Adds filters to a given query so that the query cannot get information about Profile scope
	 * (familyName & givenName).
	 * @param query
	 */
	private static void addFilterToRestrictUriPredicates(Query query, String [] uriPredicates) {
		List <ElementFilter> elementFilters = new ArrayList <ElementFilter>();
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		for (Element element: body.getElements()) { 
			if (element instanceof ElementTriplesBlock) {
				ElementTriplesBlock etb = (ElementTriplesBlock) element;
				Iterator <Triple> triples = etb.patternElts();
				for ( ; triples.hasNext(); ) {
					Triple triple = triples.next();
					Node predicateNode = triple.getPredicate();
					addFilterToRestrictUriPredicates(predicateNode, uriPredicates, elementFilters);
				}
			} else if (element instanceof ElementPathBlock) {
				ElementPathBlock epb = (ElementPathBlock) element;
				Iterator <TriplePath> triplePaths = epb.patternElts();
				for ( ; triplePaths.hasNext(); ) {
					TriplePath tp = triplePaths.next();
					addFilterToRestrictUriPredicates(tp.getPredicate(), uriPredicates, elementFilters);
				}
			}
		}
		for (ElementFilter ef: elementFilters) {
			body.addElementFilter(ef);;
		}
	}
	
	/**
	 * Adds filer to a given node when necessary to prevent SPARQL query from
	 * a given list of URI predicates.
	 * @param predicateNode
	 * @param results
	 */
	private static void addFilterToRestrictUriPredicates(Node predicateNode, String [] uriPredicates,
			List <ElementFilter> results) {
		if (!predicateNode.isVariable()) return; //immediately return if a given node is not a variable
		for (String predicareStr: uriPredicates) {
		    Expr expr = new E_NotEquals(new ExprVar(predicateNode.getName()),
		    		NodeValue.makeNode(NodeFactory.createURI(predicareStr)));
		    ElementFilter filter = new ElementFilter(expr);
		    results.add(filter);
		}
	}
	
	/**
	 * Checks whether or not a given query contains a predicate found in a given list of URIs.
	 * @param query
	 * @param inUris
	 * @return
	 */
	private static boolean findExplicitPredicateInUris(Query query, String [] inUris) {
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		for (Element element: body.getElements()) {
			if (element instanceof ElementTriplesBlock) {
				ElementTriplesBlock etb = (ElementTriplesBlock) element;
				Iterator <Triple> triples = etb.patternElts();
				for ( ; triples.hasNext(); ) {
					Triple triple = triples.next();
					if (isNodeFromUris(triple.getPredicate(), inUris)) return true;
				}
			} else if (element instanceof ElementPathBlock) {
				ElementPathBlock epb = (ElementPathBlock) element;
				Iterator <TriplePath> triplePaths = epb.patternElts();
				for ( ; triplePaths.hasNext(); ) {
					TriplePath tp = triplePaths.next();
					if (isNodeFromUris(tp.getPredicate(), inUris)) return true;
				}
			}

		}
		return false;
	}

	private static boolean isNodeFromUris(Node node, String[] uris) {
		if (node.isURI()) {
			String nodeUri = node.toString();
			for (String uri: uris) {
				if (uri.equals(nodeUri)) return true;
			}
		}
		return false;
	}

	/**
	 * Prohibit instantiations.
	 */
	private ScopeFilterUtils() {
	}
}
