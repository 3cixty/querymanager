/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile.elements;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.threecixty.profile.Review;

/**
 * 
 * This class represents a PoI in detail.
 *
 */
public class ElementPoIDetails extends ElementDetails {
	
	 //private static final Logger LOGGER = Logger.getLogger(
	//		 ElementPoIDetails.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	 //private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private String telephone;
	private double aggregate_rating;
	private int review_counts;
	private List <Review> reviews;
	
	private Boolean augmented;
	private Map <String, List<Review>> reviewsLanguages;
	
	private String topCategory;
	
	private List<String> topCategories; // contain a list of top categories
	
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
	
	public String getTopCategory() {
		return topCategory;
	}
	public void setTopCategory(String topCategory) {
		this.topCategory = topCategory;
	}
	
	public List<String> getTopCategories() {
		return topCategories;
	}
	public void setTopCategories(List<String> topCategories) {
		this.topCategories = topCategories;
	}
	public void putReview(String language, Review review) {
		if (language == null || review == null) return;
		//if (DEBUG_MOD) LOGGER.info("language: " + language + ", review = " + review.getText());
		if (reviewsLanguages == null) reviewsLanguages = new HashMap<String, List<Review>>();
		int index = language.indexOf(TRANSLATION_TAG);
		String tmpLanguage = index >= 0 ? language.substring(0, index) : language;
		List <Review> reviews = reviewsLanguages.get(tmpLanguage);
		if (reviews == null) {
			reviews = new LinkedList <Review>();
			reviewsLanguages.put(tmpLanguage, reviews);
		}
		if (!reviews.contains(review)) reviews.add(review);
	}
	
	public ElementPoIDetails export(String language) {
		ElementPoIDetails epd = new ElementPoIDetails();
		this.cloneTo(epd, language);
		epd.telephone = this.telephone;
		epd.augmented = this.augmented;
		epd.aggregate_rating = this.aggregate_rating;
		epd.topCategory = this.topCategory;
		if (language != null && reviewsLanguages != null) epd.reviews = reviewsLanguages.get(language);
		if (epd.reviews == null) {
			epd.reviews = Collections.emptyList();
		}
		epd.review_counts = epd.reviews.size();
		//if (DEBUG_MOD) LOGGER.info("language: " + language + ", all reviews = " + reviewsLanguages);
		return epd;
	}
}
