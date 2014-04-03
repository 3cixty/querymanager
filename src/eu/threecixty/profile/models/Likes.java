package eu.threecixty.profile.models;
import eu.threecixty.profile.annotations.Description;

public class Likes {
	@Description(hasText="Name of the like")
	private String hasName=""; 
	@Description(hasText="type of the entity liked")
	private LikeType hasLikeType;
	
	public String getHasName() {
		return hasName;
	}
	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	public LikeType getHasLikeType() {
		return hasLikeType;
	}
	public void setHasLikeType(LikeType hasLikeType) {
		this.hasLikeType = hasLikeType;
	}
	
}
