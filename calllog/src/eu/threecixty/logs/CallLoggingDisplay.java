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
