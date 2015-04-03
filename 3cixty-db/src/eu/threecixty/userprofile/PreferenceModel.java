package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "3cixty_preference")
public class PreferenceModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -832632094168680764L;

	private Integer id;
	
    @OneToOne
    @JoinColumn(name = "3cixty_user_id")
	private UserModel userModel;
	private Set <TransportModel> transportModels;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public UserModel getUserModel() {
		return userModel;
	}
	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "preferenceModel")
	public Set<TransportModel> getTransportModels() {
		return transportModels;
	}
	public void setTransportModels(Set<TransportModel> transportModels) {
		this.transportModels = transportModels;
	}
}
