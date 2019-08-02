package labvision;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LabVisionServlet extends HttpServlet {
	/**
	 * Unique identifier for version for serialization
	 */
	private static final long serialVersionUID = 250946325495998185L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException {
		req.getRequestDispatcher("/index.jsp").forward(req, res);
	}
}
