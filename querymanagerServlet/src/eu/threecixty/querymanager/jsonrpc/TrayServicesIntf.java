package eu.threecixty.querymanager.jsonrpc;

public interface TrayServicesIntf {

	/**
	 * 
	 * @param key
	 * @param element_id
	 * @param element_type
	 * @param element_title
	 * @param image_url
	 * @param token
	 * @param source
	 * @throws Throwable
	 * @throws InvalidKeyException
	 */
	void add_tray_element(String key, String element_id, String element_type, String element_title, String image_url, String token, String source)
			throws Throwable, InvalidKeyException;
	
	String login_tray(String key, String junk_token, String google_token) throws Throwable, InvalidKeyException;
	
	void empty_tray(String key, String token) throws Throwable, InvalidKeyException;

	// ----------------
	void update_tray_element(String key, String element_id, String element_type, String token,
			String source, boolean delete, boolean attend, String attend_datetime, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;
	
	void update_tray_element1000(String key, String element_id, String element_type, String token,
			String source, boolean delete) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element1100(String key, String element_id, String element_type, String token,
			String source, boolean delete, boolean attend) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element1110(String key, String element_id, String element_type, String token,
			String source, boolean delete, boolean attend, String attend_datetime) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element1101(String key, String element_id, String element_type, String token,
			String source, boolean delete, boolean attend, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element1001(String key, String element_id, String element_type, String token,
			String source, boolean delete, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element1010(String key, String element_id, String element_type, String token,
			String source, boolean delete, String attend_datetime) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element1011(String key, String element_id, String element_type, String token,
			String source, boolean delete, String attend_datetime, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	// -------------------------
	void update_tray_element0001(String key, String element_id, String element_type, String token,
			String source, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element0010(String key, String element_id, String element_type, String token,
			String source, String attend_datetime) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element0000(String key, String element_id, String element_type, String token,
			String source) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element0011(String key, String element_id, String element_type, String token,
			String source, String attend_datetime, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element0100(String key, String element_id, String element_type, String token,
			String source, boolean attend) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element0101(String key, String element_id, String element_type, String token,
			String source, boolean attend, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element0110(String key, String element_id, String element_type, String token,
			String source, boolean attend, String attend_datetime) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;

	void update_tray_element0111(String key, String element_id, String element_type, String token,
			String source, boolean attend, String attend_datetime, int rating) throws Throwable, InvalidKeyException, AttendDateTimeFormatException;
	
	//----------------------
	// 1111
	String get_tray_elements(String key, String token, int offset, int limit, String order_type, boolean show_past_events) throws Throwable, InvalidKeyException;
	
	String get_tray_elements1110(String key, String token, int offset, int limit, String order_type) throws Throwable, InvalidKeyException;
	
	String get_tray_elements1101(String key, String token, int offset, int limit, boolean show_past_events) throws Throwable, InvalidKeyException;
	
	String get_tray_elements1100(String key, String token, int offset, int limit) throws Throwable, InvalidKeyException;

	String get_tray_elements1000(String key, String token, int offset) throws Throwable, InvalidKeyException;
	
	String get_tray_elements1001(String key, String token, int offset, boolean show_past_events) throws Throwable, InvalidKeyException;

	String get_tray_elements1010(String key, String token, int offset, String order_type) throws Throwable, InvalidKeyException;
	
	String get_tray_elements1011(String key, String token, int offset, String order_type, boolean show_past_events) throws Throwable, InvalidKeyException;
	
	//----------------------------
	// 0000
	String get_tray_elements0000(String key, String token) throws Throwable, InvalidKeyException;
	
	String get_tray_elements0001(String key, String token, boolean show_past_events) throws Throwable, InvalidKeyException;
	
	String get_tray_elements0010(String key, String token, String order_type) throws Throwable, InvalidKeyException;
	
	String get_tray_elements0011(String key, String token, String order_type, boolean show_past_events) throws Throwable, InvalidKeyException;
	
	String get_tray_elements0100(String key, String token, int limit) throws Throwable, InvalidKeyException;

	String get_tray_elements0101(String key, String token, int limit, boolean show_past_events) throws Throwable, InvalidKeyException;
	
	String get_tray_elements0110(String key, String token, int limit, String order_type) throws Throwable, InvalidKeyException;

	String get_tray_elements0111(String key, String token, int limit, String order_type, boolean show_past_events) throws Throwable, InvalidKeyException;
}
