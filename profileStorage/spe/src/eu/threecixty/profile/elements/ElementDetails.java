package eu.threecixty.profile.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class is used to represent an item which can be an Event or a PoI so that
 * it's easy to export to JSON format. All results to the call from Mobile Guide apps
 * should be represented by this class.
 *
 */
public class ElementDetails {
	
	protected static final String TRANSLATION_TAG = "-tr";

	private String id;
	private String name;
	private String category;
	private String lat;
	private String lon;
	private String address;
	private String locality;
	private String image_url;
	private String source;
	private String type;
	private boolean translation;
	
	private Double distance; // this attribute is used for nearby elements (Event/PoI)
	
	private List<String> categories; // This contains a list of categories
	private String url;
	
	private String description;
	
	// asked by Christian to avoid calling API twice to get trays list in detail
	private Long creationTimestamp;
	private String attend_datetime; // for WishList purpose
	private Integer rating; // for WishList purpose
	
	// for highlighting results
	private Boolean highlighted;
	
	protected Map <String, String> descriptions;
	protected Map <String, Boolean> translateds;
	
	// additional URLs
	private List <String> additionalUrls;

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	public boolean isTranslation() {
		return translation;
	}
	public void setTranslation(boolean translation) {
		this.translation = translation;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getAttend_datetime() {
		return attend_datetime;
	}
	public void setAttend_datetime(String attend_datetime) {
		this.attend_datetime = attend_datetime;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
	public Boolean getHighlighted() {
		return highlighted;
	}
	public void setHighlighted(Boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean containsDescIn(String language) {
		if (language == null) return false;
		if (descriptions == null) return false;
		return descriptions.containsKey(language);
	}
	
	public void putDescription(String language, String description) {
		if (descriptions == null) {
			descriptions = new HashMap<String, String>();
		}
		if (translateds == null) translateds = new HashMap<String, Boolean>();
		if (language == null || description == null) return;
		int index = language.indexOf(TRANSLATION_TAG);
		if (index >= 0) {
			String tmpLang = language.substring(0, index);
		    descriptions.put(tmpLang, description);
		    translateds.put(tmpLang, true);
		} else {
			descriptions.put(language, description);
			translateds.put(language, false);
		}
	}
	
	public void cloneTo(ElementDetails element, String language) {
		if (element == null) return;
		element.setAddress(this.getAddress());
		element.setCategory(this.getCategory());
		if (language != null) {
			if (descriptions != null) element.setDescription(descriptions.get(language));
			if (translateds != null) {
				Boolean tmpTranslated = translateds.get(language);
				element.setTranslation(tmpTranslated == null ? false : tmpTranslated.booleanValue());
			}
		}
		element.setDistance(this.getDistance());
		element.setId(this.getId());
		element.setImage_url(this.getImage_url());
		element.setLat(this.getLat());
		element.setLon(this.getLon());
		element.setLocality(this.getLocality());
		element.setName(this.getName());
		element.setSource(this.getSource());
		element.setUrl(this.getUrl());
		element.setAdditionalUrls(additionalUrls);
	}
	
	public List<String> getAdditionalUrls() {
		return additionalUrls;
	}
	public void setAdditionalUrls(List<String> additionalUrls) {
		this.additionalUrls = additionalUrls;
	}

	public int hashCode() {
		if (id == null) return -1;
		return id.hashCode();
	}
	
	/**
	 * This method only considers ID
	 */
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof ElementDetails)) {
			return false;
		}
		ElementDetails tmp = (ElementDetails) obj;
		if (tmp.id == null) {
			if (id != null) return false;
		}
		if (!tmp.id.equals(id)) return false;
		return true;
	}
}
