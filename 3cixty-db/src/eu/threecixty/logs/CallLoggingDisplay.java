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



/**
 * This class is to represent information about 3cixty App statistics.
 * @author Rachit@Inria
 *
 */
public class CallLoggingDisplay {

	private CallLogging callLogging;

	private int numberOfCalls;
	
	private String dateCall; 

	protected CallLoggingDisplay() {
	}
	
	public CallLogging getCallLogging() {
		return callLogging;
	}

	public void setCallLogging(CallLogging callLogging) {
		this.callLogging = callLogging;
	}

	public int getNumberOfCalls() {
		return numberOfCalls;
	}

	public void setNumberOfCalls(int numberOfCalls) {
		this.numberOfCalls = numberOfCalls;
	}

	public String getDateCall() {
		return dateCall;
	}

	public void setDateCall(String dateCall) {
		this.dateCall = dateCall;
	}
	
}
