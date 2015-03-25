package eu.threecixty.profile;

public class ReportRequest {

	private String clientTimeStamp;
	private String clientVersion;
	private String reason;
	private String userToken;
	private String otherReasonText;
	private String lastPage;
	private String lastElement;
	private String lastPosition;
	private String key;
	
	private String uid;
	private String firstName;
	private String lastName;

	public String getClientTimeStamp() {
		return clientTimeStamp;
	}
	public void setClientTimeStamp(String clientTimeStamp) {
		this.clientTimeStamp = clientTimeStamp;
	}
	public String getClientVersion() {
		return clientVersion;
	}
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getOtherReasonText() {
		return otherReasonText;
	}
	public void setOtherReasonText(String otherReasonText) {
		this.otherReasonText = otherReasonText;
	}
	public String getLastPage() {
		return lastPage;
	}
	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}
	public String getLastElement() {
		return lastElement;
	}
	public void setLastElement(String lastElement) {
		this.lastElement = lastElement;
	}
	public String getLastPosition() {
		return lastPosition;
	}
	public void setLastPosition(String lastPosition) {
		this.lastPosition = lastPosition;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
