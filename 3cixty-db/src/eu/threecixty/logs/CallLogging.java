package eu.threecixty.logs;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * This class is to represent information about 3cixty App statistics.
 * @author Cong-Kinh Nguyen
 *
 */

@Entity
@Table(name = "logcall")
public class CallLogging implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4453661883917543487L;

	private Integer id;
	
	private String key;

	private Date startTime;
	
	private int timeConsumed;

	private String serviceName;
	
	private String description;
	

	protected CallLogging() {
		startTime = Calendar.getInstance().getTime();
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}
 
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "appkey", unique = false, nullable = false, length = 128)
	public String getKey() {
		return key;
	}



	public void setKey(String key) {
		this.key = key;
	}

	@Column(name = "starttime", unique = false, nullable = false)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "timeConsumed", unique = false, nullable = false)
	public int getTimeConsumed() {
		return timeConsumed;
	}

	public void setTimeConsumed(int timeConsumed) {
		this.timeConsumed = timeConsumed;
	}

	@Column(name = "serviceName", unique = false, nullable = false, length = 256)
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Column(name = "description", unique = false, nullable = false, length = 256)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
