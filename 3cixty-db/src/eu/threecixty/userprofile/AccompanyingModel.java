package eu.threecixty.userprofile;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This model seems to be changed in the near future. So, I don't use a common class
 * for sharing code between Know and Accompanying class.
 *
 * @author Cong-Kinh Nguyen
 *
 */

@Entity
@Table(name = "3cixty_accompanying")
public class AccompanyingModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5486497561246201323L;
	private Integer id;
	private UserModel userModel;

	private Long accompanyTime; 
	private Long accompanyValidity; 
	private String hasAccompanyUserid1ST; 
	private String hasAccompanyUserid2ST; 
	private Double accompanyScore; 

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_user_id", nullable = false)
	public UserModel getUserModel() {
		return userModel;
	}
	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}
	
	@Column(name = "accompanyTime", nullable = true)
	public Long getAccompanyTime() {
		return accompanyTime;
	}
	public void setAccompanyTime(Long accompanyTime) {
		this.accompanyTime = accompanyTime;
	}
	
	@Column(name = "accompanyValidity", nullable = true)
	public Long getAccompanyValidity() {
		return accompanyValidity;
	}
	public void setAccompanyValidity(Long accompanyValidity) {
		this.accompanyValidity = accompanyValidity;
	}
	
	@Column(name = "hasAccompanyUserid1ST", nullable = false, length = 255)
	public String getHasAccompanyUserid1ST() {
		return hasAccompanyUserid1ST;
	}
	public void setHasAccompanyUserid1ST(String hasAccompanyUserid1ST) {
		this.hasAccompanyUserid1ST = hasAccompanyUserid1ST;
	}
	
	@Column(name = "hasAccompanyUserid2ST", nullable = false, length = 255)
	public String getHasAccompanyUserid2ST() {
		return hasAccompanyUserid2ST;
	}
	public void setHasAccompanyUserid2ST(String hasAccompanyUserid2ST) {
		this.hasAccompanyUserid2ST = hasAccompanyUserid2ST;
	}
	
	@Column(name = "accompanyScore", nullable = true)
	public Double getAccompanyScore() {
		return accompanyScore;
	}
	public void setAccompanyScore(Double accompanyScore) {
		this.accompanyScore = accompanyScore;
	}
}
