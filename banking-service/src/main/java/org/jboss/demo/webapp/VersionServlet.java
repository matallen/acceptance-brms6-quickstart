package org.jboss.demo.webapp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class VersionServlet extends HttpServlet {
	private static final long serialVersionUID = -1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		
		try{
			resp.getWriter().println(IOUtils.toString(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF")));
		}catch(Exception e){
			resp.getWriter().append("Version information unknown");
		}
	}

}