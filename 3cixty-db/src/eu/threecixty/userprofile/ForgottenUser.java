package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CollectionOfElements;

/**
 * 
 * This class is to persist mapping between a friend and a user. The friend should be avoided crawling
 * next times.
 *
 */
@Entity
@Table(name = "3cixty_forgotten_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"uid"})})
public class ForgottenUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5896076677298336888L;
	private Integer id;
	private String uid;
	private Set <String> knowsNotToCrawl;
	private boolean needToAvoidBeingCrawled;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "uid", unique = true, nullable = false, length = 100)
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@CollectionOfElements(fetch=FetchType.EAGER)
	public Set<String> getKnowsNotToCrawl() {
		return knowsNotToCrawl;
	}
	public void setKnowsNotToCrawl(Set<String> knowsNotToCrawl) {
		this.knowsNotToCrawl = knowsNotToCrawl;
	}
	
	@Column(name = "needToAvoidBeingCrawled")
	public boolean isNeedToAvoidBeingCrawled() {
		return needToAvoidBeingCrawled;
	}
	public void setNeedToAvoidBeingCrawled(boolean needToAvoidBeingCrawled) {
		this.needToAvoidBeingCrawled = needToAvoidBeingCrawled;
	}

	
}
