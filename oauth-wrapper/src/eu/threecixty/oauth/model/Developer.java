package eu.threecixty.oauth.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("Developer")
public class Developer extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set <App> apps = new HashSet <App>();
	
	public Developer() {
		super();
	}

	public Developer(Integer id, String uid) {
		super(id, uid);
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "developer")
	public Set<App> getApps() {
		return apps;
	}

	public void setApps(Set<App> apps) {
		this.apps = apps;
	}
}
