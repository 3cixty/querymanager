package eu.threecixty.querymanager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;

public class HTTPCall {
	
	private static final String USER_AGENT = "Mozilla/5.0";

	protected void sendPost(String url, String params) throws Exception {
		sendPost(url, params, null);
	}
	
	protected void sendPost(String url, String params, StringBuffer response) throws Exception {
		 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		
		Assert.assertEquals(responseCode, 200);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		if (response != null) {

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		in.close();
	}
	
	protected void sendGET(String url, String params) throws Exception {
		sendGET(url, params, null);
	}

	protected void sendGET(String url, String params, StringBuffer response) throws Exception {
		 
		URL obj = new URL(url + "/" + params);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		int responseCode = con.getResponseCode();
		
		Assert.assertEquals(responseCode, 200);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		if (response != null) {

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		in.close();
	}
}
