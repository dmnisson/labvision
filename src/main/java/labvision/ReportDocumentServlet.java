package labvision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import labvision.entities.FilesystemReportDocument;
import labvision.entities.User;
import labvision.entities.UserRole;
import labvision.services.ExperimentService;
import labvision.services.ReportService;

public class ReportDocumentServlet extends HttpServlet {
	/**
	 * Version 0.0.1
	 */
	private static final long serialVersionUID = -2531886153651042408L;

	/**
	 * Gets the document file from the request information, validating and authorizing as
	 * necessary, and sets response headers
	 * @param req the request
	 * @param resp the response
	 * @return the document file, or null if unauthorized
	 * @throws IOException if an error occurs sending a 403 response
	 */
	private File getDocumentFileAndHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession(false);
		
		User user = (User) session.getAttribute("user");
		
		if (req.getPathInfo() == null) {
			// early exit
			resp.sendError(400, "No document path specified.");
			return null;
		}
		
		String[] args = req.getPathInfo().substring(1).split("/");
		
		if (args.length != 3) {
			// early exit
			resp.sendError(400, "Invalid path.");
			return null;
		}
		
		int studentId = Integer.parseInt(args[0]);
		int experimentId = Integer.parseInt(args[1]);
		String filename = args[2];
		
		ReportService reportService = (ReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.REPORT_SERVICE_ATTR);
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		
		FilesystemReportDocument reportDocument = reportService.getReportDocument(studentId, experimentId, filename);
		
		if (!(
				user.getRole().equals(UserRole.ADMIN) ||
				studentId == user.getId() ||
				experimentService.getInstructorIdsFor(experimentId).contains(user.getId())
				)) {
			// early exit
			resp.sendError(403, "You are not authorized to access this document.");
			return null;
		}
		
		File file = reportDocument.getReportDocumentFile();
		
		resp.setContentType(reportDocument.getContentType());
		resp.setContentLengthLong(file.length());
		resp.setDateHeader("Date", Instant.now().toEpochMilli());
		resp.setDateHeader("Last-Modified", file.lastModified());
		
		return file;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		File file = getDocumentFileAndHead(req, resp);
		
		try {
			FileInputStream in = new FileInputStream(file);
			ServletOutputStream out = resp.getOutputStream();
			
			in.transferTo(out);
			
			out.flush();
			in.close();
		} catch (FileNotFoundException e) {
			resp.sendError(404, "The specified document was not found.");
		}
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		getDocumentFileAndHead(req, resp);
	}
}
