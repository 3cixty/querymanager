/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.querymanager;

/**
 * This is the interface to augment a query. This interface is just to create a new query which is augmented
 * from a given original query.
 *
 */
public interface QueryAugmenter {

	/**
	 * Creates a query augmented in string format.
	 * <br>
	 * Note that if the given filter is improper, the augmented query will be the same with the original one.
	 *
	 * @param original
	 * 			The original SPARQL query.
	 * @param filter
	 * 			The filter to augment the query.
	 * @param uid
	 * 			The 3cixty UID.
	 * @throws InvalidSparqlQuery
	 * @return
	 */
	String createQueryAugmented(String original, QueryAugmenterFilter filter,
			String uid, double coef, String endPointUrl) throws InvalidSparqlQuery;
}
