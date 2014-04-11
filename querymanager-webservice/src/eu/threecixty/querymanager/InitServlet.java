package eu.threecixty.querymanager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * This class is for initiating the absolute path of root servlet.
 * @author Cong-Kinh Nguyen
 *
 */
@WebServlet("InitServlet")
public class InitServlet extends HttpServlet {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static String realPath;

	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    realPath = this.getServletContext().getRealPath(".");
	}

	public static String getRealRootPath() {
		return realPath;
	}
}
