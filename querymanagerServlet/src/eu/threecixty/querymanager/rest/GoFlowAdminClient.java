package eu.threecixty.querymanager.rest;

import java.io.IOException;

import com.google.gson.JsonObject;

public class GoFlowAdminClient extends GoFlowEventClient {

	public GoFlowAdminClient(String goFlowServerUrl) throws IOException {
		super(goFlowServerUrl);
	}

	//
	//
	//


	public void assignAppCreator(String appid, String userid, String op) throws IOException {
		String res;

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("userid", userid);
		jsonParam.addProperty("op", op);
		jsonParam.addProperty("appid", appid);
		res = sendJsonPostRequest("event/assignAppCreator", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to assignAppCreator");
		}
		return;
	}

	public void assignOwner(String appid, String userid, String op) throws IOException {
		String res;

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("userid", userid);
		jsonParam.addProperty("op", op);
		jsonParam.addProperty("appid", appid);
		res = sendJsonPostRequest("event/assignOwner", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to assignOwner");
		}
		return;
	}

	public void assignUser(String appid, String userid, String op) throws IOException {
		String res;

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("userid", userid);
		jsonParam.addProperty("op", op);
		jsonParam.addProperty("appid", appid);
		res = sendJsonPostRequest("event/assignUser", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to assignUser");
		}
		return;
	}

	public void assignDvlp(String appid, String userid, String op) throws IOException {
		String res;

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("userid", userid);
		jsonParam.addProperty("op", op);
		jsonParam.addProperty("appid", appid);
		res = sendJsonPostRequest("event/assignDvlp", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to assignDvlp");
		}
		return;
	}

	public void unregisterUser(String userid) throws IOException {
		String res;

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("nickname", userid);
		res = sendJsonPostRequest("user/unregisterUser", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to unregisterUser");
		}
		return;
	}

	public void registerUser(String userid, String pwd, String appId) throws IOException {
		String res;

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("nickname", userid);
		jsonParam.addProperty("email", userid + "@tt.com");
		jsonParam.addProperty("password", pwd);
		jsonParam.addProperty("appId", appId);
		res = sendJsonPostRequest("user/registerUser", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to registerUser");
		}
		return;
	}

	public void registerUser(String userid, String pwd) throws IOException {

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("nickname", userid);
		jsonParam.addProperty("email", userid + "@tt.com");
		jsonParam.addProperty("password", pwd);
		String res = sendJsonPostRequest("user/registerUser", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to registerUser");
		}
		return;
	}

	public void unregisterApp(String appid) throws IOException {
		//
		if (appid == null || appid.equals("") || !validId(appid)) {
			throw new IOException("Invalid parameter");
		}

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("app", appid);
		String res = sendJsonPostRequest("event/unregisterApp", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to unregisterApp");
		}
		return;
	}

	public void registerApp(String appid) throws IOException {
		//
		if (appid == null || appid.equals("") || !validId(appid)) {
			throw new IOException("Invalid parameter");
		}

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("app", appid);
		jsonParam.addProperty("devid", appid);
		jsonParam.addProperty("description", appid);
		String res = sendJsonPostRequest("event/registerApp", jsonParam.toString());

		if (res == null) {
			throw new IOException("Unable to registerApp");
		}
		return;
	}



}