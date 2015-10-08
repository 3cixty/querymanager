package eu.threecixty.profile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.json.JSONObject;

import eu.threecixty.Configuration;

public class TrexManager {

	private static final TrexManager SINGLETON = new TrexManager();
	private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
	
	public static TrexManager getInstance() {
		return SINGLETON;
	}
	
	public void publish(String id, String title, String image) {
		if (isNullOrEmpty(id) || isNullOrEmpty(title) || isNullOrEmpty(image)) return;
		final JSONObject json = new JSONObject();
		json.put("evtType", 1111);
		json.put("timeStamp", 0);
		JSONObject attrJson = new JSONObject();
		attrJson.put("image", image);
		attrJson.put("item_id", id);
		attrJson.put("title", title);
		json.put("attr", attrJson);
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				sendData(json);
			}
		};
		ses.execute(runnable);
	}
	
	private void sendData(JSONObject json) {
		OutputStream output = null;
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(Configuration.getTrexServer());
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			output = conn.getOutputStream();
			if (output == null) return;
			output.write(json.toString().getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			conn.disconnect();
		}
	}

	private boolean isNullOrEmpty(String str) {
		if (str == null || str.equals("")) return true;
		return false;
	}
	
	private TrexManager() {
	}
}
