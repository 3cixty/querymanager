package eu.threecixty.profile.models;

import java.util.Set;

import eu.threecixty.profile.annotations.Description;
import eu.threecixty.profile.annotations.Extend;

/**
 * Group Details
 * @author Rachit.Agarwal@inria.fr
 *
 */
@Extend(hasText="http://www.w3.org/2006/vcard/ns#Group")
public class GroupDetail {
	@Description(hasText="Name of the group")
    private String hasName="";
	@Description(hasText="Group details")
    private String hasDetail="";
	@Description(hasText="Moderator name")
    private String hasModerator="";    
	@Description(hasText="comments")
    private Set <String> haskeyTags;
	public String getHasName() {
		return hasName;
	}
	public void setHasName(String hasName) {
		this.hasName = hasName;
	}
	public String getHasDetail() {
		return hasDetail;
	}
	public void setHasDetail(String hasDetail) {
		this.hasDetail = hasDetail;
	}
	public String getHasModerator() {
		return hasModerator;
	}
	public void setHasModerator(String hasModerator) {
		this.hasModerator = hasModerator;
	}
	public Set<String> getHaskeyTags() {
		return haskeyTags;
	}
	public void setHaskeyTags(Set<String> haskeyTags) {
		this.haskeyTags = haskeyTags;
	}

}
