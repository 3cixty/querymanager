package eu.threecixty.querymanager;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eu.threecixty.keys.AppKey;
import eu.threecixty.keys.KeyManager;
import eu.threecixty.keys.KeyOwner;
import eu.threecixty.profile.GoogleAccountUtils;
import eu.threecixty.profile.SettingsStorage;
import eu.threecixty.profile.ThreeCixtySettings;

public class KeyRequestServlet extends HttpServlet {

	private static final String ACCESS_TOKEN_PARAM = "accessToken";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		String accessToken = req.getParameter(ACCESS_TOKEN_PARAM);
		String uid = null;
		if (session.getAttribute(accessToken) != null) {
			uid = (String) session.getAttribute("uid");
		} else {
			uid = GoogleAccountUtils.getUID(accessToken);
		}
		if (uid == null || uid.equals("")) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			session.setAttribute(accessToken, true);
			session.setAttribute("uid", uid);
			AppKey appKey = KeyManager.getInstance().getAppKeyFromUID(uid);
			if (appKey != null) {
				req.setAttribute("appkey", appKey);
			}
			req.setAttribute(ACCESS_TOKEN_PARAM, accessToken);
			ThreeCixtySettings settings = SettingsStorage.load(uid);
			req.setAttribute("settings", settings);
			try {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/keyrequest.jsp");
				rd.forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		String uid = (String) session.getAttribute("uid");
		if (uid == null || uid.equals("")) {
			resp.sendRedirect("./error.jsp");
		} else {
			String email = req.getParameter("email");
			String domain = req.getParameter("domain");
			String rawKey = KeyManager.getInstance().generateKey(uid);
			AppKey appKey = new AppKey();
			appKey.setAppName(domain);
			appKey.setValue(rawKey);
			KeyOwner owner = new KeyOwner();
			appKey.setOwner(owner);
			owner.setEmail(email);
			owner.setUid(uid);
			if (KeyManager.getInstance().addOrUpdateAppKey(appKey)) {
				session.setAttribute("key", rawKey);
				resp.sendRedirect("./keyGenerated");
			} else {
				resp.sendRedirect("./unsuccessfulKeyRequest.jsp");
			}
		}
		
	}
}
