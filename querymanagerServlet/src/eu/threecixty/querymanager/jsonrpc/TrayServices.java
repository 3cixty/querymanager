package eu.threecixty.querymanager.jsonrpc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;


import eu.threecixty.keys.KeyManager;
import eu.threecixty.logs.CallLoggingConstants;
import eu.threecixty.logs.CallLoggingManager;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.Tray;
import eu.threecixty.profile.TrayStorage;
import eu.threecixty.profile.Tray.OrderType;

public class TrayServices implements TrayServicesIntf {

	private static final String INVALID_KEY = "Invalid key";
	private static final String ORDER_TYPE_DEFAULT = "desc";
	
	private static final int OFFSET_DEFAULT = 0;
	private static final int LIMIT_DEFAULT = 100;

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32002, message = "The item is already existed")
    })
	@Override
	public void add_tray_element(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, @JsonRpcParam("element_title") String element_title,
			@JsonRpcParam("image_url") String image_url, @JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source) throws Throwable, InvalidKeyException {

    	checkAppKey(key);

    	long starttime = System.currentTimeMillis();

		Tray tray = new Tray();
		tray.setItemId(element_id);
		tray.setItemType(element_type);
		tray.setSource(source);
		tray.setTimestamp(System.currentTimeMillis());
		tray.setElement_title(element_title);
		tray.setImage_url(image_url);
		
		String uid = GoogleAccountUtils.getUID(token);
		if (uid == null || uid.equals("")) {
			tray.setUid(token);
		} else {
			tray.setUid(uid);
		}
		if (!TrayStorage.addTray(tray)) {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_ADD_SERVICE, CallLoggingConstants.FAILED);
			throw new Throwable();
		}
		CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_ADD_SERVICE, CallLoggingConstants.SUCCESSFUL);
	}

    @JsonRpcErrors({
    	@JsonRpcError(exception = InvalidKeyException.class,
                code = -32001, message = INVALID_KEY),
        @JsonRpcError(exception = Throwable.class,
            code = -32003, message = "Junk token is empty or Google token is invalid")
    })
	@Override
	public String login_tray(@JsonRpcParam("key") String key, @JsonRpcParam("junk_token") String junk_token,
			@JsonRpcParam("google_token") String google_token) throws Throwable, InvalidKeyException {

    	long starttime = System.currentTimeMillis();

    	checkAppKey(key);

    	List <Tray> trays = loginTray(junk_token, google_token);
    	if (trays == null) {
    		CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_LOGIN_SERVICE, CallLoggingConstants.FAILED);
    		throw new Throwable();
    	}
    	Gson gson = new Gson();
    	String content = gson.toJson(trays);
    	CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_LOGIN_SERVICE, CallLoggingConstants.SUCCESSFUL);
        return content;
	}

    @JsonRpcErrors({
    	@JsonRpcError(exception = InvalidKeyException.class,
                code = -32001, message = INVALID_KEY),
        @JsonRpcError(exception = Throwable.class,
            code = -32003, message = "Token is empty")
    })
	@Override
	public void empty_tray(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token) throws Throwable, InvalidKeyException {

    	long starttime = System.currentTimeMillis();

    	checkAppKey(key);

    	if (!cleanTrays(token)) {
    		CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_EMPTY_SERVICE, CallLoggingConstants.FAILED);
    		throw new Throwable();
    	}
    	CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_EMPTY_SERVICE, CallLoggingConstants.SUCCESSFUL);
	}

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete,
			@JsonRpcParam("attend") boolean attend, @JsonRpcParam("attend_datetime") String attend_datetime,
			@JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {

    	long starttime = System.currentTimeMillis();
    	
		checkAppKey(key);
		
		String uid = GoogleAccountUtils.getUID(token);

		Tray tray = TrayStorage.getTray((uid == null || uid.equals("")) ? token : uid, element_id);
		if (tray == null) throw new Throwable();
		
		if (element_type != null && !element_type.equals("")) tray.setItemType(element_type);
		
		if (source != null && !source.equals("")) tray.setSource(source);
		
		
		if (delete) {
			if (!TrayStorage.deleteTray(tray)) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_UPDATE_SERVICE, CallLoggingConstants.FAILED);
				throw new Throwable();
			}
			return;
		}
		
		if (attend_datetime != null && !attend_datetime.equals("")) {
			boolean okDatetime = false;
			try {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				Date d = format.parse(attend_datetime);
				if (d != null) okDatetime = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!okDatetime) {
				CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_UPDATE_SERVICE, CallLoggingConstants.FAILED);
				throw new AttendDateTimeFormatException();
			}

			tray.setDateTimeAttended(attend_datetime);
		}
		
		tray.setAttended(attend);
		
		if (rating > 0) {
			tray.setRating(rating);
		}
		
		if (!TrayStorage.update(tray)) {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_UPDATE_SERVICE, CallLoggingConstants.FAILED);
			throw new Throwable();
		}
		CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_UPDATE_SERVICE, CallLoggingConstants.SUCCESSFUL);
	}

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element1000(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, delete, true, null, -1);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element1100(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete, @JsonRpcParam("attend") boolean attend)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, delete, attend, null, -1);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element1110(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete, @JsonRpcParam("attend") boolean attend,
			@JsonRpcParam("attend_datetime") String attend_datetime)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, delete, attend, attend_datetime, -1);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element1101(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete, @JsonRpcParam("attend") boolean attend,
			@JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, delete, attend, null, rating);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element1001(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete,
			@JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, delete, true, null, rating);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element1010(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete,
			@JsonRpcParam("attend_datetime") String attend_datetime)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, delete, true, attend_datetime, -1);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element1011(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("delete") boolean delete,
			@JsonRpcParam("attend_datetime") String attend_datetime,
			@JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, delete, true, attend_datetime, rating);
    }
    
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0001(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, true, null, rating);
    }
    
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0010(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("attend_datetime") String attend_datetime)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, true, attend_datetime, -1);
    }
    
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0000(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, true, null, -1);
    }
    
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0011(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("attend_datetime") String attend_datetime,
			@JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, true, attend_datetime, rating);
    }
    
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0100(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("attend") boolean attend)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, attend, null, -1);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0101(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source,@JsonRpcParam("attend") boolean attend, @JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, attend, null, rating);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0110(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source, @JsonRpcParam("attend") boolean attend, @JsonRpcParam("attend_datetime") String attend_datetime)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, attend, attend_datetime, -1);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = AttendDateTimeFormatException.class,
            code = -32004, message = "Attend Date Time format is incorrect. The datetime pattern must be 'dd-MM-yyyy HH:mm'"),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("update_tray_element")
	@Override
	public void update_tray_element0111(@JsonRpcParam("key") String key, @JsonRpcParam("element_id") String element_id,
			@JsonRpcParam("element_type") String element_type, 
			@JsonRpcParam("token") String token,
			@JsonRpcParam("source") String source,
			@JsonRpcParam("attend") boolean attend, @JsonRpcParam("attend_datetime") String attend_datetime,
			@JsonRpcParam("rating") int rating)
			throws Throwable, InvalidKeyException,
			AttendDateTimeFormatException {
    	update_tray_element(key, element_id, element_type, token, source, false, attend, attend_datetime, rating);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset, @JsonRpcParam("limit") int limit,
			@JsonRpcParam("order_type") String order_type, @JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {

    	long starttime = System.currentTimeMillis();
    	
    	checkAppKey(key);

		OrderType orderType = (order_type == null) ? OrderType.Desc
				: order_type.equalsIgnoreCase("Desc") ? OrderType.Desc : OrderType.Asc;

		String uid = GoogleAccountUtils.getUID(token);
		
		List <Tray> trays = TrayStorage.getTrays((uid == null || uid.equals("")) ? token : uid,
				offset, limit, orderType, show_past_events);
		if (trays == null) {
			CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_GET_SERVICE, CallLoggingConstants.FAILED);
			throw new Throwable();
		}
		Gson gson = new Gson();
		String content = gson.toJson(trays);
		CallLoggingManager.getInstance().save(key, starttime, CallLoggingConstants.TRAY_GET_SERVICE, CallLoggingConstants.SUCCESSFUL);
    	return content;
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements1110(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset, @JsonRpcParam("limit") int limit,
			@JsonRpcParam("order_type") String order_type)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, offset, limit, order_type, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements1101(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset, @JsonRpcParam("limit") int limit,
			@JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, offset, limit, ORDER_TYPE_DEFAULT, show_past_events);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements1100(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset, @JsonRpcParam("limit") int limit)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, offset, limit, ORDER_TYPE_DEFAULT, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements1000(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, offset, LIMIT_DEFAULT, ORDER_TYPE_DEFAULT, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements1001(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset, @JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, offset, LIMIT_DEFAULT, ORDER_TYPE_DEFAULT, show_past_events);
    }
    
    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements1010(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset, @JsonRpcParam("order_type") String order_type)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, offset, LIMIT_DEFAULT, order_type, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements1011(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("offset") int offset,
			@JsonRpcParam("order_type") String order_type, @JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, offset, LIMIT_DEFAULT, order_type, show_past_events);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0000(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, OFFSET_DEFAULT, LIMIT_DEFAULT, ORDER_TYPE_DEFAULT, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0001(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, OFFSET_DEFAULT, LIMIT_DEFAULT, ORDER_TYPE_DEFAULT, show_past_events);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0010(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("order_type") String order_type)
			throws Throwable, InvalidKeyException {
		return get_tray_elements(key, token, OFFSET_DEFAULT, LIMIT_DEFAULT, order_type, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0011(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("order_type") String order_type, @JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, OFFSET_DEFAULT, LIMIT_DEFAULT, order_type, show_past_events);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0100(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("limit") int limit)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, OFFSET_DEFAULT, limit, ORDER_TYPE_DEFAULT, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0101(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("limit") int limit, @JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, OFFSET_DEFAULT, limit, ORDER_TYPE_DEFAULT, show_past_events);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0110(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("limit") int limit,
			@JsonRpcParam("order_type") String order_type)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, OFFSET_DEFAULT, limit, order_type, true);
    }

    @JsonRpcErrors({
        @JsonRpcError(exception = InvalidKeyException.class,
            code = -32001, message = INVALID_KEY),
            @JsonRpcError(exception = Throwable.class,
            code = -32005, message = "IOException")
    })
    @JsonRpcMethod("get_tray_elements")
	@Override
	public String get_tray_elements0111(@JsonRpcParam("key") String key, @JsonRpcParam("token") String token,
			@JsonRpcParam("limit") int limit,
			@JsonRpcParam("order_type") String order_type, @JsonRpcParam("show_past_events") boolean show_past_events)
			throws Throwable, InvalidKeyException {
    	return get_tray_elements(key, token, OFFSET_DEFAULT, limit, order_type, show_past_events);
    }
    
	/**
	 * Empties tray list.
	 * @param restTray
	 * @return
	 */
	private boolean cleanTrays(String token) {
		if (token == null || token.equals("")) return false;
		String uid = GoogleAccountUtils.getUID(token);
		if (uid == null || uid.equals("")) {
			return TrayStorage.cleanTrays(token);
		}
		return TrayStorage.cleanTrays(uid);
	}
    
	/**
	 * Login
	 * @param restTray
	 * @return List of trays associated with a given junk token
	 */
	private List<Tray> loginTray(String junkToken, String googleToken) {
		if (junkToken == null || junkToken.equals("")) return null;
		String uid = GoogleAccountUtils.getUID(googleToken);
		if (uid == null || uid.equals("")) return null;
		if (!TrayStorage.replaceUID(junkToken, uid)) return null;
		return TrayStorage.getTrays(uid, 0, -1, OrderType.Desc, true);
	}

	private void checkAppKey(String key) throws InvalidKeyException {
    	if (!KeyManager.getInstance().checkAppKey(key)) {
    		CallLoggingManager.getInstance().save(key, System.currentTimeMillis(), CallLoggingConstants.TRAY_SERVICE,
    				CallLoggingConstants.INVALID_APP_KEY + key);
    		throw new InvalidKeyException();
    	}
	}
	
//	@JsonRpcMethod("concat")
//	public String concat(@JsonRpcParam("key")  String key, @JsonRpcParam("accessToken")  String accessToken, @JsonRpcParam("id")  String id) {
//		return key + accessToken + id;
//	}
//	
//	@JsonRpcMethod("concat")
//	public String concat(@JsonRpcParam("key")  String key, @JsonRpcParam("id")  String id) {
//		return concat(key,"accessToken0001",id);
//	}
//	
//	@JsonRpcMethod("concat")
//	public String zConcat(@JsonRpcParam("key")  String key, @JsonRpcParam("accessToken")  String accessToken) {
//		return concat(key,accessToken,"zID1000");
//	}

}
