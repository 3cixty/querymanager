package eu.threecixty.querymanager;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class KeyGeneratedServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4603650777246803498L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (session.getAttribute("key") == null) {
			try {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/error.jsp");
				rd.forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/keys/keygenerated.jsp");
				rd.forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		HttpSession session = req.getSession();
//		if (session.getAttribute("key") == null) {
//			try {
//				RequestDispatcher rd = getServletContext().getRequestDispatcher("/error.jsp");
//				rd.forward(req, resp);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			String key = (String) session.getAttribute("key");
//			byte [] keyInBytes = key.getBytes();
//			resp.setContentType("application/octet-stream");
//			resp.setContentLength(keyInBytes.length);
//			resp.setHeader("Content-Disposition", "attachment; filename=\"3cixty.key\"");
//			PrintWriter writer = resp.getWriter();
//			writer.write(key);
//			writer.close();
//		}
//	}
}
