package eu.threecixty.profile.elements;

public class ElementEventDetails extends ElementDetails {

	
	private String time_beginning;
	private String time_end;

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
	
	public ElementEventDetails export(String language) {
		ElementEventDetails eed = new ElementEventDetails();
		this.cloneTo(eed, language);

		eed.setTime_beginning(this.getTime_beginning());
		eed.setTime_end(this.getTime_end());

		return eed;
	}
	
}
