package eu.threecixty.logs;

import eu.threecixty.keys.AppKey;

/**
 * This class is to represent information about 3cixty App statistics.
 * @author Rachit@Inria
 *
 */
public class CallLoggingDisplay {

	private CallLogging callLogging;

	private int numberOfCalls;
	

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
}
