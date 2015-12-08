/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.logs;

import java.util.List;

/**
 * This interface aims at easily changing class implementation to test.
 *
 */
public interface CallLoggingStorage {

	/**
	 * Saves statistic information.
	 * @param logging
	 * @return <code>true</code> if the method successfully saves statistics, and <code>false</code> otherwise.
	 */
	boolean save(CallLogging logging);
	
	/**
	 * Saves a list of CallLogging: Batch insert
	 * @param loggings
	 * @return
	 */
	boolean save(List <CallLogging> loggings);

	/**
	 * Gets a list of log calls.
	 * @param appkey
	 * @param from
	 * @param to
	 * @param minTimeConsumed
	 * @param maxTimeConsumed
	 * @return
	 */
	List<CallLogging> getCalls(String appkey, long from, long to,
			int minTimeConsumed, int maxTimeConsumed);
	
	List<CallLoggingDisplay> getCallsWithCount(long from, long to,
			int minTimeConsumed, int maxTimeConsumed);
	
	List<CallLoggingDisplay> getCallsWithCountByMonth();
	List<CallLoggingDisplay> getCallsWithCountByDay();
	
	List<RelativeNumberOfUsers> getRelativeNumberofUsers();
}
