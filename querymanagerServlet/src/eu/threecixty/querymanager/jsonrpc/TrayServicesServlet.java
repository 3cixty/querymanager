package eu.threecixty.querymanager.jsonrpc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.jsonrpc4j.JsonRpcServer;



public class TrayServicesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5588980362514395688L;
	private TrayServicesIntf trayServices;
	private JsonRpcServer jsonRpcServer;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			jsonRpcServer.handle(req, resp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init(ServletConfig config) {
		this.trayServices = new TrayServices();
		this.jsonRpcServer = new JsonRpcServer(this.trayServices, TrayServices.class);
	}

}
