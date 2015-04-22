package eu.threecixty.profile;

public class RestTrayObject {

	private String action;
	private String key;
	private Boolean delete;
	private Integer offset;
	private Integer limit;
	private String orderType;
	private Boolean show_past_events;
	private String junk_token;
	private String three_cixty_token;
	
	private String element_id;
	private String element_type;
	private String element_title;
	private long timestamp;

	private String token;
	private String source;
	
	private Boolean attend;
	private String attend_datetime;
	private int rating = -1;
	private String image_url;
	private String language;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Boolean getDelete() {
		return delete;
	}
	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public Boolean getShow_past_events() {
		return show_past_events;
	}
	public void setShow_past_events(Boolean show_past_events) {
		this.show_past_events = show_past_events;
	}
	public String getJunk_token() {
		return junk_token;
	}
	public void setJunk_token(String junk_token) {
		this.junk_token = junk_token;
	}

	public String getThree_cixty_token() {
		return three_cixty_token;
	}
	public void setThree_cixty_token(String three_cixty_token) {
		this.three_cixty_token = three_cixty_token;
	}
	public String getElement_id() {
		return element_id;
	}
	public void setElement_id(String element_id) {
		this.element_id = element_id;
	}
	public String getElement_type() {
		return element_type;
	}
	public void setElement_type(String element_type) {
		this.element_type = element_type;
	}
	public String getElement_title() {
		return element_title;
	}
	public void setElement_title(String element_title) {
		this.element_title = element_title;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Boolean getAttend() {
		return attend;
	}
	public void setAttend(Boolean attend) {
		this.attend = attend;
	}
	public String getAttend_datetime() {
		return attend_datetime;
	}
	public void setAttend_datetime(String attend_datetime) {
		this.attend_datetime = attend_datetime;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
}
