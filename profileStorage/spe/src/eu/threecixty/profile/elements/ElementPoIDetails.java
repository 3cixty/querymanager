package eu.threecixty.profile.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.threecixty.profile.Review;

public class ElementPoIDetails extends ElementDetails {
	private String telephone;
	private double aggregate_rating;
	private int review_counts;
	private List <Review> reviews;
	
	private Boolean augmented;
	private Map <String, List<Review>> reviewsLanguages; 
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public double getAggregate_rating() {
		return aggregate_rating;
	}
	public void setAggregate_rating(double aggregate_rating) {
		this.aggregate_rating = aggregate_rating;
	}
	public int getReview_counts() {
		return review_counts;
	}
	public void setReview_counts(int review_counts) {
		this.review_counts = review_counts;
	}

	public Boolean getAugmented() {
		return augmented;
	}
	public void setAugmented(Boolean augmented) {
		this.augmented = augmented;
	}
	public List<Review> getReviews() {
		return reviews;
	}
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	public void putReviews(String language, List <Review> reviews) {
		if (language == null || reviews == null) return;
		if (reviewsLanguages == null) reviewsLanguages = new HashMap<String, List<Review>>();
		reviewsLanguages.put(language, reviews);
	}
	
	public ElementPoIDetails export(String language) {
		ElementPoIDetails epd = new ElementPoIDetails();
		this.cloneTo(epd, language);
		epd.telephone = this.telephone;
		epd.augmented = this.augmented;
		epd.review_counts = this.review_counts;
		epd.aggregate_rating = this.aggregate_rating;
		if (language != null && reviewsLanguages != null) epd.reviews = reviewsLanguages.get(language);
		return epd;
	}
}
