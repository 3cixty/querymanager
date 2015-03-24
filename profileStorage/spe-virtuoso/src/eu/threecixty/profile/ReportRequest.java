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
}
