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
public class Accompanying implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5486497561246201323L;
	private UserModel user;
	private UserModel friend;
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_user_id", nullable = false)
	public UserModel getUser() {
		return user;
	}
	public void setUser(UserModel user) {
		this.user = user;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "3cixty_friend_id", nullable = false)
	public UserModel getFriend() {
		return friend;
	}
	public void setFriend(UserModel friend) {
		this.friend = friend;
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
