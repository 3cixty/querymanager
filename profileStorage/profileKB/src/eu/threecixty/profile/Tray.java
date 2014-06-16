package eu.threecixty.profile;

/**
 * This class defines tray item information.
 * Question: should we need to check whether or not event information is correct.
 * @author Cong-Kinh NGUYEN
 *
 */
public class Tray {
	
	private String itemId;
	private ItemType itemType;
	private long timestamp;
	// real UID or junkID
	private String uid;
	private String source;
	
	private boolean attended;
	private String dateTimeAttended;
	private int rating = -1;
	
	
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isAttended() {
		return attended;
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	public String getDateTimeAttended() {
		return dateTimeAttended;
	}

	public void setDateTimeAttended(String dateTimeAttended) {
		this.dateTimeAttended = dateTimeAttended;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public enum ItemType {
		Event, PoI, Transportation
	};
	
	public enum OrderType {
		Desc, Asc
	}
}
