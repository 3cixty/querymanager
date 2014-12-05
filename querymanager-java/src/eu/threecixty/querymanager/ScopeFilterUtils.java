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
	
	/**
	 * Checks whether or not a given query contains an explicit predicate for User Profile. 
	 * @param query
	 * @return
	 */
	public static boolean containExplicitProfilePredicate(Query query) {
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		for (Element element: body.getElements()) {
			if (element instanceof ElementTriplesBlock) {
				ElementTriplesBlock etb = (ElementTriplesBlock) element;
				Iterator <Triple> triples = etb.patternElts();
				for ( ; triples.hasNext(); ) {
					Triple triple = triples.next();
					if (isProfileUriNode(triple.getPredicate())) return true;
				}
			} else if (element instanceof ElementPathBlock) {
				ElementPathBlock epb = (ElementPathBlock) element;
				Iterator <TriplePath> triplePaths = epb.patternElts();
				for ( ; triplePaths.hasNext(); ) {
					TriplePath tp = triplePaths.next();
					if (isProfileUriNode(tp.getPredicate())) return true;
				}
			}

		}
		return false;
	}
	
	/**
	 * Adds filters to a given query so that the query cannot get information about Profile scope
	 * (familyName & givenName).
	 * @param query
	 */
	public static void addFiltersToRestrictProfile(Query query) {
		List <ElementFilter> elementFilters = new ArrayList <ElementFilter>();
		ElementGroup body = (ElementGroup) query.getQueryPattern();
		for (Element element: body.getElements()) { 
			if (element instanceof ElementTriplesBlock) {
				ElementTriplesBlock etb = (ElementTriplesBlock) element;
				Iterator <Triple> triples = etb.patternElts();
				for ( ; triples.hasNext(); ) {
					Triple triple = triples.next();
					Node predicateNode = triple.getPredicate();
					addFilterToRestrictProfileWhenNecessary(predicateNode, elementFilters);
				}
			} else if (element instanceof ElementPathBlock) {
				ElementPathBlock epb = (ElementPathBlock) element;
				Iterator <TriplePath> triplePaths = epb.patternElts();
				for ( ; triplePaths.hasNext(); ) {
					TriplePath tp = triplePaths.next();
					addFilterToRestrictProfileWhenNecessary(tp.getPredicate(), elementFilters);
				}
			}
		}
		for (ElementFilter ef: elementFilters) {
			body.addElementFilter(ef);;
		}
	}
	
	/**
	 * Adds filer to a given node when necessary to prevent SPARQL query from
	 * getting familyName and givenName.
	 * @param predicateNode
	 * @param elementFilters
	 */
	private static void addFilterToRestrictProfileWhenNecessary(Node predicateNode,
			List <ElementFilter> elementFilters) {
		if (!predicateNode.isVariable()) return; //immediately return if a given node is not a variable
		for (String predicareStr: PROFILE_URIS) {
		    Expr expr = new E_NotEquals(new ExprVar(predicateNode.getName()),
		    		NodeValue.makeNode(NodeFactory.createURI(predicareStr)));
		    ElementFilter filter = new ElementFilter(expr);
		    elementFilters.add(filter);
		}
	}

	private static boolean isProfileUriNode(Node node) {
		if (node.isURI()) {
			String nodeUri = node.toString();
			for (String uri: PROFILE_URIS) {
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
