package eu.threecixty.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.threecixty.profile.PartnerUser.PartnerAccount;

public class PartnerImpl implements Partner {

	private String path;
	private String filename;

	protected PartnerImpl(String path, String filename) {
		this.path = path;
		this.filename = filename;
	}
	
	@Override
	public synchronized boolean addUser(PartnerUser user) {
		if (user == null) return false;
		List <PartnerUser> mobidotUsers = getUsers();
		int index = findIndexOfUID(user.getUid(), mobidotUsers);
		if (index >= 0) return false;
		mobidotUsers.add(user);
		return save(mobidotUsers);
	}

	@Override
	public synchronized boolean updateUser(PartnerUser user) {
		if (user == null) return false;
		List <PartnerUser> mobidotUsers = getUsers();
		int index = findIndexOfUID(user.getUid(), mobidotUsers);
		if (index >= 0) {
			mobidotUsers.set(index, user); // update accounts
			return save(mobidotUsers);
		}
		return false;
	}

	@Override
	public synchronized boolean deleteUser(PartnerUser user) {
		if (user == null) return false;
		List <PartnerUser> mobidotUsers = getUsers();
		if (mobidotUsers == null) return false;
		int index = findIndexOfUID(user.getUid(), mobidotUsers);
		if (index < 0) return false;
		mobidotUsers.remove(index);
		return save(mobidotUsers);
	}

	@Override
	public boolean exist(String uid) {
		PartnerUser mobidotUser = getUser(uid);
		return mobidotUser != null;
	}

	@Override
	public PartnerUser getUser(String uid) {
		if (uid == null) return null;
		List <PartnerUser> mobidotUsers = getUsers();
		if (mobidotUsers == null) return null;
		for (PartnerUser mobidotUser: mobidotUsers) {
			if (uid.equals(mobidotUser.getUid())) return mobidotUser;
		}
		return null;
	}

	@Override
	public List<PartnerUser> getUsers() {
		String content = getContent();
		if (content == null) return null;
		Gson gson = new Gson();
		return gson.fromJson(content, new TypeToken<List<PartnerUser>>(){}.getType());
	}

	@Override
	public PartnerAccount findAccount(PartnerUser user, String appkey) {
		if (user == null || appkey == null) return null;
		for (PartnerAccount account: user.getMobidotAccounts()) {
			if (appkey.equals(account.getAppkey())) return account;
		}
		return null;
	}
	
	private int findIndexOfUID(String uid, List <PartnerUser> users) {
		int len = users.size();
		for (int index = 0; index < len; index++) {
			if (uid.equals(users.get(index).getUid())) return index;
		}
		return -1;
	}

	private synchronized boolean save(List<PartnerUser> mobidotUsers) {
		if (path == null) return false;
		File file = new File(path + File.separatorChar + filename);
		if (file.exists()) file.delete();
		try {
			file.createNewFile();
			Gson gson = new Gson();
			FileOutputStream output = new FileOutputStream(file);
			output.write(gson.toJson(mobidotUsers).getBytes("UTF-8"));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	private synchronized String getContent() {
		if (path == null) return "[]";
		StringBuffer buffer = new StringBuffer();
		File file = new File(path + File.separatorChar + filename);
		if (!file.exists()) return "[]";
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			byte [] b = new byte[1024];
			int readBytes = 0;
			while ((readBytes = input.read(b)) >= 0) {
				buffer.append(new String(b, 0, readBytes, "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if (buffer.length() == 0) return "[]";
		return buffer.toString();
	}
}
