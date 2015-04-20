package eu.threecixty.profile;

public class ElementEventDetails extends ElementDetails {

	private String description;
	private String time_beginning;
	private String time_end;
	private boolean translation;


	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getTime_beginning() {
		return time_beginning;
	}
	public void setTime_beginning(String time_beginning) {
		this.time_beginning = time_beginning;
	}
	public String getTime_end() {
		return time_end;
	}
	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}
	public boolean isTranslation() {
		return translation;
	}
	public void setTranslation(boolean translation) {
		this.translation = translation;
	}
}
