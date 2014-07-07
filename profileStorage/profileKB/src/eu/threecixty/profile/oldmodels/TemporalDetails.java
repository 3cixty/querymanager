package eu.threecixty.profile.oldmodels;

import java.util.Set;
import java.util.Date;

import eu.threecixty.profile.annotations.Description;

/**
 * 
 * @author Rachit.Agarwal@inria.fr
 *
 */
public class TemporalDetails {
	@Description(hasText = "start date")
	private Date hasDateFrom;
	@Description(hasText = "end date. can be empty")
	private Date hasDateUntil;
	@Description(hasText = "comments")
	private Set <String> hasKeyTags;
	
	public Date getHasDateFrom() {
		return hasDateFrom;
	}
	public void setHasDateFrom(Date hasDateFrom) {
		this.hasDateFrom = hasDateFrom;
	}
	public Date getHasDateUntil() {
		return hasDateUntil;
	}
	public void setHasDateUntil(Date hasDateUntil) {
		this.hasDateUntil = hasDateUntil;
	}
	public Set<String> getHasKeyTags() {
		return hasKeyTags;
	}
	public void setHasKeyTags(Set<String> hasKeyTags) {
		this.hasKeyTags = hasKeyTags;
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (this == object) return true;
		if (!(object instanceof TemporalDetails)) return false;
		TemporalDetails td = (TemporalDetails) object;
		if (hasDateFrom == null) {
			if (td.hasDateFrom != null) return false;
		} else if (!hasDateFrom.equals(td.hasDateFrom)) return false;
		if (hasDateUntil == null) {
			if (td.hasDateUntil != null) return false;
		} else if (!hasDateUntil.equals(td.hasDateUntil)) return false;
		return true;
	}
}
