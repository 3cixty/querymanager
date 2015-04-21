package eu.threecixty.profile;

import java.util.List;

public class ElementDetails {

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
