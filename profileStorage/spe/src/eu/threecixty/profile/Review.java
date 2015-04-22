package eu.threecixty.profile;

public class Review {
	private String text;
	private boolean translated;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isTranslated() {
		return translated;
	}
	public void setTranslated(boolean translated) {
		this.translated = translated;
	}
	
	public int hashCode() {
		if (text == null) return -1;
		return text.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Review)) return false;
		if (obj == this) return true;
		Review review = (Review) obj;
		if (text == null) {
			if (review.text != null) return false;
		} else {
			if (!text.equals(review.text)) return false;
		}
		return true;
	}
}
