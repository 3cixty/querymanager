package eu.threecixty.profile.elements;

import java.util.List;

import eu.threecixty.profile.Review;

public class ElementPoIDetails extends ElementDetails {
	private String telephone;
	private double aggregate_rating;
	private int review_counts;
	private List <Review> reviews;
	private String description;
	
	private Boolean augmented;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Review> getReviews() {
		return reviews;
	}
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}
