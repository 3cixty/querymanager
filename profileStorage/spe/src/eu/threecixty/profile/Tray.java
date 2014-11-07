package eu.threecixty.profile;

/**
 * This class defines tray item information.
 * Question: should we need to check whether or not event information is correct.
 * @author Cong-Kinh NGUYEN
 *
 */
public class Tray {
	
	private String element_id;
	private String element_type;
	private String element_title;
	private long timestamp;
	// real UID or junkID
	private String token;
	private String source;
	
	private boolean attend;
	private String attend_datetime;
	private int rating = -1;
	
	private String image_url;
	
	public String getItemId() {
		return element_id;
	}

	public void setItemId(String itemId) {
		this.element_id = itemId;
	}

	public String getItemType() {
		return element_type;
	}

	public void setItemType(String itemType) {
		this.element_type = itemType;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUid() {
		return token;
	}

	public void setUid(String uid) {
		this.token = uid;
	}
	
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isAttended() {
		return attend;
	}

	public void setAttended(boolean attended) {
		this.attend = attended;
	}

	public String getDateTimeAttended() {
		return attend_datetime;
	}

	public void setDateTimeAttended(String dateTimeAttended) {
		this.attend_datetime = dateTimeAttended;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getElement_title() {
		return element_title;
	}

	public void setElement_title(String element_title) {
		this.element_title = element_title;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	
	public enum OrderType {
		Desc, Asc
	}
}
