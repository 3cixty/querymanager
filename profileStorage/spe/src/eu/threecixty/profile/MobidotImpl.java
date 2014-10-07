package eu.threecixty.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.threecixty.profile.MobidotUser.MobidotAccount;

/**
 * This class is to store all the 3Cixty's mobidot users in a file. 
 * @author Cong-Kinh NGUYEN
 *
 */
public class MobidotImpl implements Mobidot {
	
	private static final Object _sync = new Object();
	private static final String MOBIDOT_FILE = "mobidot.json";
	
	private static Mobidot instance;
	
	private static String path;

	public static Mobidot getInstance() {
		if (instance == null) {
			synchronized (_sync) {
				if (instance == null) instance = new MobidotImpl();
			}
		}
		return instance;
	}
	
	public static void setPath(String path) {
		MobidotImpl.path = path;
	}

	@Override
	public synchronized boolean addUser(MobidotUser user) {
		if (user == null) return false;
		List <MobidotUser> mobidotUsers = getUsers();
		int index = findIndexOfUID(user.getUid(), mobidotUsers);
		if (index >= 0) return false;
		mobidotUsers.add(user);
		return save(mobidotUsers);
	}

	@Override
	public synchronized boolean updateUser(MobidotUser user) {
		if (user == null) return false;
		List <MobidotUser> mobidotUsers = getUsers();
		int index = findIndexOfUID(user.getUid(), mobidotUsers);
		if (index >= 0) {
			mobidotUsers.set(index, user); // update accounts
			return save(mobidotUsers);
		}
		return false;
	}

	@Override
	public synchronized boolean deleteUser(MobidotUser user) {
		if (user == null) return false;
		List <MobidotUser> mobidotUsers = getUsers();
		if (mobidotUsers == null) return false;
		int index = findIndexOfUID(user.getUid(), mobidotUsers);
		if (index < 0) return false;
		mobidotUsers.remove(index);
		return save(mobidotUsers);
	}

	@Override
	public boolean exist(String uid) {
		MobidotUser mobidotUser = getUser(uid);
		return mobidotUser != null;
	}

	@Override
	public MobidotUser getUser(String uid) {
		if (uid == null) return null;
		List <MobidotUser> mobidotUsers = getUsers();
		if (mobidotUsers == null) return null;
		for (MobidotUser mobidotUser: mobidotUsers) {
			if (uid.equals(mobidotUser.getUid())) return mobidotUser;
		}
		return null;
	}

	@Override
	public List<MobidotUser> getUsers() {
		String content = getContent();
		if (content == null) return null;
		Gson gson = new Gson();
		return gson.fromJson(content, new TypeToken<List<MobidotUser>>(){}.getType());
	}

	@Override
	public MobidotAccount findAccount(MobidotUser user, String appkey) {
		if (user == null || appkey == null) return null;
		for (MobidotAccount account: user.getMobidotAccounts()) {
			if (appkey.equals(account.getAppkey())) return account;
		}
		return null;
	}
	
	private int findIndexOfUID(String uid, List <MobidotUser> users) {
		int len = users.size();
		for (int index = 0; index < len; index++) {
			if (uid.equals(users.get(index).getUid())) return index;
		}
		return -1;
	}

	private synchronized boolean save(List<MobidotUser> mobidotUsers) {
		if (path == null) return false;
		File file = new File(path + File.separatorChar + MOBIDOT_FILE);
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
		File file = new File(path + File.separatorChar + MOBIDOT_FILE);
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
	
	private MobidotImpl() {
	}
}
