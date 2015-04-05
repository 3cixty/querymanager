package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "3cixty_transport")
public class TransportModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4926882465262059416L;

	private PreferenceModel preferenceModel;
	private Set <AccompanyingModel> accompanyings;
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_preference_id", nullable = false)
	public PreferenceModel getPreferenceModel() {
		return preferenceModel;
	}
	public void setPreferenceModel(PreferenceModel preferenceModel) {
		this.preferenceModel = preferenceModel;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transportModel")
	public Set<AccompanyingModel> getAccompanyings() {
		return accompanyings;
	}
	public void setAccompanyings(Set<AccompanyingModel> accompanyings) {
		this.accompanyings = accompanyings;
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
