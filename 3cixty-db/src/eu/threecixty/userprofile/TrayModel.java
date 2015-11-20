package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * This class represents information about WishList items.
 *
 */
@Entity
@Table(name = "3cixty_tray")
public class TrayModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 521449213940842481L;
	private Integer id;
	// don't link to userModel to avoid potentially having issues with references, but not sure
	// should be indexed
	private String uid;
	private String elementId;
	private String title;
	private String type;
	private long timestamp;
	private String source;
	private String attend_dateTime; // need to refactor later
	private boolean attended;
	private String imageUrl;
	private int rating = -1;
	private Long creationTimestamp;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "uid", nullable = false, length = 255)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Column(name = "elementId", nullable = false, length = 255)
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	@Column(name = "title", nullable = true, length = 1000)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name = "type", nullable = false, length = 100)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "timestamp", nullable = false)
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Column(name = "source", nullable = true, length = 100)
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	@Column(name = "attendTime", nullable = true, length = 100)
	public String getAttend_dateTime() {
		return attend_dateTime;
	}
	public void setAttend_dateTime(String attend_dateTime) {
		this.attend_dateTime = attend_dateTime;
	}
	
	@Column(name = "attended")
	public boolean isAttended() {
		return attended;
	}
	public void setAttended(boolean attended) {
		this.attended = attended;
	}
	
	@Column(name = "imageUrl", nullable = true, length = 255)
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Column(name = "rating")
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	@Column(name = "creationTimestamp", nullable = true)
	public Long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
}
