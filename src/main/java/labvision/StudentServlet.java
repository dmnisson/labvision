package labvision;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.jboss.logging.Logger;

import labvision.dto.experiment.ExperimentInfo;
import labvision.dto.experiment.MeasurementForExperimentView;
import labvision.dto.experiment.MeasurementValueForExperimentView;
import labvision.dto.experiment.report.ReportForReportView;
import labvision.dto.experiment.report.ResultInfo;
import labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard;
import labvision.dto.student.dashboard.ExperimentForStudentDashboard;
import labvision.dto.student.dashboard.RecentCourseForStudentDashboard;
import labvision.dto.student.dashboard.RecentExperimentForStudentDashboard;
import labvision.dto.student.experiment.CurrentExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.PastExperimentForStudentExperimentTable;
import labvision.dto.student.experiment.ReportedResultForStudentExperimentView;
import labvision.dto.student.reports.ReportForStudentReportsTable;
import labvision.entities.CourseClass;
import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.Parameter;
import labvision.entities.ReportDocumentType;
import labvision.entities.ReportedResult;
import labvision.entities.Student;
import labvision.measure.Amount;
import labvision.measure.SI;
import labvision.models.NavbarModel;
import labvision.services.DashboardService;
import labvision.services.ExperimentService;
import labvision.services.ReportService;
import labvision.services.StudentService;
import labvision.utils.StringUtils;
import labvision.utils.ThrowingWrappers;

@MultipartConfig(fileSizeThreshold = 1024 * 1024,
	maxFileSize = 1024 * 1024 * 5, 
	maxRequestSize = 1024 * 1024 * 5 * 5)
public class StudentServlet extends AbstractLabVisionServlet {
	/**
	 * Version 0.0.1
	 */
	private static final long serialVersionUID = -4488832460194220512L;

	public static final String STUDENT_SERVLET_NAME = "labvision-student";
	
	/**
	 * Get experiment detail view for an experiment ID
	 * @param id the experiment ID
	 * @return the detail view path
	 * @throws ServletMappingNotFoundException 
	 * @throws ServletNotFoundException 
	 */
	private String getExperimentPath(int id) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor()
				.getPathFor(STUDENT_SERVLET_NAME, "/experiment/" + id);
	}
	
	/**
	 * Get a path for creating a new measurement value for a given measurement
	 * @param id the measurement ID
	 * @return the path for creating a new measurement value
	 * @throws ServletNotFoundException
	 * @throws ServletMappingNotFoundException
	 */
	public String getNewMeasurementValuePath(int id) throws ServletNotFoundException,ServletMappingNotFoundException{
		return getPathConstructor()
				.getPathFor(STUDENT_SERVLET_NAME, "/measurement/newvalue/" + id);
	}
	
	/**
	 * Get the detail path for the given report ID
	 * @param id the report ID
	 * @return the detail view path
	 * @throws ServletNotFoundException
	 * @throws ServletMappingNotFoundException
	 */
	private String getReportPath(int id) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor().getPathFor(STUDENT_SERVLET_NAME, "/report/" + id);
	}
	
	/**
	 * Get the path for creating a new report
	 * @return the path
	 * @throws ServletNotFoundException
	 * @throws ServletMappingNotFoundException
	 */
	private String getNewReportPath(int experimentId) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor().getPathFor(STUDENT_SERVLET_NAME, "/report/new/" + experimentId);
	}
	
	/**
	 * Get the path for editing a report
	 * @param id the report ID
	 * @return the path
	 * @throws ServletNotFoundException
	 * @throws ServletMappingNotFoundException
	 */
	private String getEditReportPath(int id) throws ServletNotFoundException, ServletMappingNotFoundException {
		return getPathConstructor().getPathFor(STUDENT_SERVLET_NAME, "/report/edit/" + id);
	}
	
	public String getCoursePath(int id) throws ServletNotFoundException,ServletMappingNotFoundException{
		return getPathConstructor()
				.getPathFor(STUDENT_SERVLET_NAME, "/course/" + id);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			response.sendRedirect("/student/dashboard");
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "dashboard":
				doGetDashboard(request, response, session);
				break;
			case "experiments":
				try {
					doGetExperiments(request, response, session);
				} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
					handleURLComputationError(response, e);
				}
				break;
			case "experiment":
				try {
					doGetExperiment(request, response, session, pathParts[2]);
				} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
					handleURLComputationError(response, e);
				}
				break;
			case "measurement":
				doGetMeasurement(request, response, session, pathParts[2]);
				break;
			case "reports":
				try {
					doGetReports(request, response, session);
				} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
					Logger.getLogger(this.getClass())
						.error(URL_COMPUTATION_ERROR_MESSAGE, e);
					response.sendError(500, URL_COMPUTATION_ERROR_MESSAGE);
				}
				break;
			case "report":
				try {
					doGetReport(request, response, session, pathParts[2],
							pathParts.length > 3 ? pathParts[3] : null);
				} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
					Logger.getLogger(this.getClass())
						.error(URL_COMPUTATION_ERROR_MESSAGE, e);
					response.sendError(500, URL_COMPUTATION_ERROR_MESSAGE);
				}
				break;
			case "courses":
				doGetCourses(request, response, session);
				break;
			case "course":
				doGetCourse(request, response, session, pathParts[2]);
				break;
			case "errors":
				doGetErrors(request, response, session);
				break;
			case "profile":
				doGetProfile(request, response, session);
				break;
			default:
				response.sendRedirect("/student/dashboard");
			}
		}
	}



	private void doGetMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourse(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doGetCourses(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doGetErrors(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}
	
	private void doGetReport(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String action, String arg) throws ServletException, IOException, ServletNotFoundException, ServletMappingNotFoundException {		
		
		ReportService reportService = (ReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.REPORT_SERVICE_ATTR);
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		int studentId = student.getId();
		
		int reportId, experimentId;
		List<ResultInfo> acceptedResults;
		String reportDocumentURL;
		String actionURL;
		
		if (action.equals("new")) {
			experimentId = Integer.parseInt(arg);
			acceptedResults = experimentService.getAcceptedResults(experimentId);
			actionURL = getNewReportPath(experimentId);
			
		} else {
			// action is to be performed on the specified report
			// only id with no action name means show report details
			reportId = Integer.parseInt(arg == null ? action : arg);
			
			// ensure student is authorized to access report
			if (!reportService.getReportStudentId(reportId).equals(studentId)) {
				// early exit
				response.sendError(403, "You are not authorized to access this resource.");
				return;
			}
			
			ReportForReportView reportData = reportService.getReport(reportId, ReportForReportView.class);
			acceptedResults = reportService.getAcceptedResults(reportId);
			reportDocumentURL = reportService.getDocumentURL(
					reportId,
					getServletContext(),
					request.getServerName(),
					request.getServerPort()
					);
			experimentId = reportData.getExperimentId();
			actionURL = action.equals("edit") ? getEditReportPath(reportId) : getReportPath(reportId);
			
			request.setAttribute("name", reportData.getName());
			request.setAttribute("documentFileType", reportData.getDocumentFileType());
			request.setAttribute("documentType", reportData.getDocumentType());
			request.setAttribute("filename", reportData.getFilename());
			request.setAttribute("documentLastUpdated", reportData.getDocumentLastUpdated());
			request.setAttribute("reportDocumentURL", reportDocumentURL);
			request.setAttribute("score", reportData.getScore());
			if (!action.equals("edit")) {
				request.setAttribute("editPath", getEditReportPath(reportId));
			}
		}
		
		if (action.matches("new|edit")) {
			// ensure that the deadline has not passed
			LocalDateTime reportDueDate = experimentService.getReportDueDate(experimentId);
			if (reportDueDate.isBefore(LocalDateTime.now())) {
				// early exit
				response.sendError(403, "The deadline to submit reports has passed for this experiment.");
				return;
			}
		}
		
		ExperimentInfo experimentInfo = experimentService.getExperimentInfo(experimentId);
		
		request.setAttribute("experiment", experimentInfo);
		request.setAttribute("acceptedResults", acceptedResults);
		
		if (action.matches("new|edit")) {
			request.setAttribute("actionUrl", actionURL);			
			request.setAttribute(
					"changeReportFilesystemDocumentPath",
					actionURL + "?uploadfile=true"
					);
			request.getRequestDispatcher("/WEB-INF/student/editreport.jsp")
				.forward(request, response);
		} else {
			request.getRequestDispatcher("/WEB-INF/student/report.jsp")
				.forward(request, response);
		}
	}

	private void doGetReports(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletNotFoundException, ServletMappingNotFoundException, ServletException, IOException {
		ReportService reportService = (ReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.REPORT_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		int studentId = student.getId();
		
		List<ReportForStudentReportsTable> reports = reportService.getStudentReports(studentId);
		List<Integer> reportIds = reports.stream().map(r -> r.getId())
				.collect(Collectors.toList());
		List<Integer> experimentIds = reports.stream().map(r -> r.getExperimentId())
				.distinct().collect(Collectors.toList());
		List<Integer> courseIds = reports.stream().map(r -> r.getCourseId())
				.distinct().collect(Collectors.toList());
		
		request.setAttribute("reports", reports);
		request.setAttribute("reportPaths", ThrowingWrappers.collectionToMap(reportIds, id1 -> getReportPath(id1)));
		request.setAttribute("editReportPaths", ThrowingWrappers.collectionToMap(reportIds, id3 -> getEditReportPath(id3)));
		request.setAttribute("coursePaths", ThrowingWrappers.collectionToMap(courseIds, id2 -> getCoursePath(id2)));
		request.setAttribute("experimentPaths", ThrowingWrappers.collectionToMap(experimentIds, id -> getExperimentPath(id)));
		
		request.getRequestDispatcher("/WEB-INF/student/reports.jsp").forward(request, response);
	}

	private void doGetExperiment(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String experimentIdString) throws ServletException, IOException, ServletNotFoundException, ServletMappingNotFoundException {
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		int studentId = student.getId();
		int experimentId = Integer.parseInt(experimentIdString);
		Experiment experiment = experimentService.getExperiment(experimentId, ExperimentPrefetch.PREFETCH_NO_VALUES);
		
		List<MeasurementForExperimentView> measurements = experimentService.getMeasurements(experimentId);
		Map<Integer, List<MeasurementValueForExperimentView>> measurementValues = experimentService.getMeasurementValues(experimentId, studentId);
		List<ReportedResultForStudentExperimentView> reportedResults = experimentService.getReportedResults(experimentId, studentId);
		
		request.setAttribute("experiment", experiment);
		request.setAttribute("measurements", measurements);
		request.setAttribute("measurementValues", measurementValues);
		// measurement ID -> parameters
		request.setAttribute("parameters", measurements.stream()
				.collect(Collectors.toMap(
						MeasurementForExperimentView::getId,
						m -> experimentService.getParameters(m.getId()))));
		// measurement ID -> measurement value ID -> parameter ID -> parameter value
		request.setAttribute("parameterValues", measurements.stream()
				.map(MeasurementForExperimentView::getId)
				.collect(Collectors.toMap(
						Function.identity(),
						id -> measurementValues.get(id).stream()
							.map(MeasurementValueForExperimentView::getId)
							.collect(Collectors.toMap(
									Function.identity(),
									vid -> experimentService.getParameterValues(vid))))));
		request.setAttribute("reportedResults", reportedResults);
		request.setAttribute("reportPaths", ThrowingWrappers.collectionToMap(reportedResults.stream()
		.map(ReportedResultForStudentExperimentView::getId).collect(Collectors.toList()), id2 -> getReportPath(id2)));
		request.setAttribute("reportEditPaths", ThrowingWrappers.collectionToMap(reportedResults.stream()
		.map(ReportedResultForStudentExperimentView::getId).collect(Collectors.toList()), id3 -> getEditReportPath(id3)));
		request.setAttribute("newReportPath", getNewReportPath(experimentId));
		request.setAttribute("newMeasurementValuePaths", ThrowingWrappers.collectionToMap(measurements.stream()
		.collect(Collectors.mapping(
				MeasurementForExperimentView::getId,
				Collectors.toList())), id1 -> getNewMeasurementValuePath(id1)));
		
		request.getRequestDispatcher("/WEB-INF/student/experiment.jsp").forward(request, response);
	}

	private void doGetExperiments(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException, ServletNotFoundException, ServletMappingNotFoundException {
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		
		int studentId = ((Student) session.getAttribute("user")).getId();
		
		List<CurrentExperimentForStudentExperimentTable> currentExperiments = experimentService.getCurrentExperiments(studentId);
		request.setAttribute("currentExperiments", currentExperiments);
		
		List<PastExperimentForStudentExperimentTable> pastExperiments = experimentService.getPastExperiments(studentId);
		request.setAttribute("pastExperiments", pastExperiments);
		
		List<Integer> pathMapKeys = Stream.concat(currentExperiments.stream(), pastExperiments.stream())
			.map(ExperimentForStudentExperimentTable::getId)
			.collect(Collectors.toList());
		
		request.setAttribute("experimentPaths", ThrowingWrappers.collectionToMap(pathMapKeys, id -> getExperimentPath(id)));
		
		request.setAttribute("newReportPaths", ThrowingWrappers.collectionToMap(pathMapKeys, id1 -> getNewReportPath(id1)));
		
		request.getRequestDispatcher("/WEB-INF/student/experiments.jsp").forward(request, response);
	}

	private void doGetDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
		DashboardService dashboardService = (DashboardService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_DASHBOARD_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		int studentId = student.getId();
		
		request.setAttribute("student", student);
		
		List<CurrentExperimentForStudentDashboard> currentExperiments = dashboardService.getCurrentExperiments(studentId);
		request.setAttribute("currentExperiments", currentExperiments);
		
		List<RecentExperimentForStudentDashboard> recentExperiments = dashboardService.getRecentExperiments(studentId);
		request.setAttribute("recentExperiments", recentExperiments);
		
		List<RecentCourseForStudentDashboard> recentCourses = dashboardService.getRecentCourses(studentId);
		request.setAttribute("recentCourses", recentCourses);
		
		request.setAttribute("experimentPaths", 
				ThrowingWrappers.collectionToMap(Stream.concat(currentExperiments.stream(), recentExperiments.stream())
				.map(ExperimentForStudentDashboard::getId)
				.collect(Collectors.toList()), id -> getExperimentPath(id)));
		
		request.setAttribute("coursePaths", 
				ThrowingWrappers.collectionToMap(recentCourses.stream()
				.map(RecentCourseForStudentDashboard::getId)
				.collect(Collectors.toList()), id1 -> getCoursePath(id1)));
		
		request.getRequestDispatcher("/WEB-INF/student/dashboard.jsp").forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			doPost404(request, response);
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "measurement":
				doPostMeasurement(request, response, session, Arrays.copyOfRange(pathParts, 2, pathParts.length));
				break;
			case "report":
				try {
					doPostReport(request, response, session, pathParts[2],
							pathParts.length > 3 ? pathParts[3] : null);
				} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
					handleURLComputationError(response, e);
				}
				break;
			default:
				doPost404(request, response);
			}
		}
	}

	private void doPostReport(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String action, String arg) throws IOException, ServletException, ServletNotFoundException, ServletMappingNotFoundException {
		Student student = (Student) session.getAttribute("user");
		
		ReportService reportService = (ReportService) getServletContext()
				.getAttribute(LabVisionServletContextListener.REPORT_SERVICE_ATTR);
		
		String reportName = request.getParameter("reportName");
		
		ReportDocumentType documentType = ReportDocumentType
				.valueOf(request.getParameter("documentType"));
		String externalDocumentURL = request.getParameter("externalDocumentURL");
		
		List<Part> fileParts = request.getParts().stream()
				.filter(part -> part.getContentType() != null)
				.collect(Collectors.toList());
		
		switch (action) {
		case "new":
			int experimentId = Integer.parseInt(arg);
			int studentId = student.getId();
			
			ReportedResult report;
			
			if (StringUtils.isNullOrEmpty(request.getParameter("documentType"))) {
				report = reportService.createBasicReport(experimentId, studentId, reportName);
			} else {
				switch (documentType) {
				case EXTERNAL:
					if (externalDocumentURL == null) {
						report = reportService.createBasicReport(experimentId, studentId, reportName);
					} else {
						report = reportService.createExternalReport(
								experimentId,
								studentId,
								reportName,
								externalDocumentURL
						);
					}
					break;
				case FILESYSTEM:
					if (fileParts.isEmpty()) {
						report = reportService.createBasicReport(
								experimentId,
								studentId,
								reportName
						);
					} else if (fileParts.size() == 1) {
						Part part = fileParts.get(0);
						
						report = reportService.createFilesystemReport(
								experimentId,
								studentId,
								reportName,
								part.getContentType(),
								part.getSubmittedFileName(),
								part.getInputStream()
						);
					} else {
						// early exit
						response.sendError(400, "Multiple documents cannot be uploaded to a single report");
						return;
					}
					break;
				default:
					throw new UnsupportedOperationException("Unsupported document type: " + documentType);
				}
				
				response.sendRedirect(getReportPath(report.getId()));
			}
			break;
		case "edit":
			int reportId = Integer.parseInt(arg);
			
			if (StringUtils.isNullOrEmpty(request.getParameter("documentType"))) {
				reportService.renameReport(reportId, reportName);
			} else {
				switch (documentType) {
				case EXTERNAL:
					if (!StringUtils.isNullOrEmpty(externalDocumentURL)) {
						reportService.updateExternalReport(reportId, reportName, externalDocumentURL);
					} else {
						reportService.renameReport(reportId, reportName);
					}
					break;
				case FILESYSTEM:
					if (fileParts.isEmpty()) {
						reportService.renameReport(reportId, reportName);
						break;
					}
					
					if (fileParts.size() > 1) {
						response.sendError(400, "Multiple documents cannot be uploaded to a single report.");
						break;
					}
					
					Part part = fileParts.get(0);
					
					reportService.updateFilesystemReport(
							reportId,
							reportName,
							part.getContentType(),
							part.getSubmittedFileName(),
							part.getInputStream()
					);
					break;
				default:
					throw new UnsupportedOperationException("Unsupported document type: " + documentType);
				}
			}
			
			response.sendRedirect(getReportPath(reportId));
			break;
		default:
			response.sendError(400, "Unrecognized action: " + action);
		}
	}

	private void doPostMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String[] info) throws IOException {
		ExperimentService experimentService = (ExperimentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.EXPERIMENT_SERVICE_ATTR);
		StudentService studentService = (StudentService) getServletContext()
				.getAttribute(LabVisionServletContextListener.STUDENT_SERVICE_ATTR);
		
		Student student = (Student) session.getAttribute("user");
		
		switch (info[0]) {
		case "newvalue":
			Measurement measurement = experimentService.getMeasurement(Integer.parseInt(info[1]), true);
			
			Amount<?> measurementAmount = new Amount<>(
				Double.parseDouble(request.getParameter("measurementValue")),
				Double.parseDouble(request.getParameter("measurementUncertainty")),
				SI.getInstance().getUnitFor(measurement, measurement.getQuantityTypeId()
						.getQuantityClass().getQuantityType()));
			
			Map<Parameter, Amount<?>> parameterAmounts = measurement.getParameters().stream()
					.collect(Collectors.toMap(Function.identity(), 
							p -> new Amount<>(
									Double.parseDouble(request.getParameter("parameterValue" + p.getId())),
									Double.parseDouble(request.getParameter("parameterUncertainty" + p.getId())),
									SI.getInstance().getUnitFor(p, p.getQuantityTypeId().getQuantityClass()
											.getQuantityType())))
									);
			
			CourseClass courseClass = studentService.getCourseClass(
					measurement.getExperiment().getCourse(),
					student,
					false);
			
			experimentService.addMeasurementValue(
					student,
					measurement,
					measurementAmount,
					parameterAmounts,
					courseClass);
			
			try {
				response.sendRedirect(getExperimentPath(measurement.getExperiment().getId()));
			} catch (ServletNotFoundException | ServletMappingNotFoundException e) {
				Logger.getLogger(StudentServlet.class).error("Could not redirect after measurement data saved", e);
			}
		}
	}

	private void doPost404(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(404, "Cannot POST to " + request.getRequestURI());
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			doPut404(request, response);
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "measurement":
				doPutMeasurement(request, response, session, pathParts[2]);
				break;
			case "report":
				doPutReport(request, response, session, pathParts[2]);
				break;
			case "profile":
				doPutProfile(request, response, session);
				break;
			default:
				doPut404(request, response);
			}
		}
	}

	private void doPutProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// TODO Auto-generated method stub
		
	}

	private void doPutReport(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPutMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doPut404(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		
		initStudentNavbar(request);
		
		if (request.getPathInfo() == null) {
			doDelete404(request, response);
		} else {
			String[] pathParts = request.getPathInfo().split("/");
			switch (pathParts[1]) {
			case "measurement":
				doDeleteMeasurement(request, response, session, pathParts[2]);
				break;
			default:
				doDelete404(request, response);
			}
		}
	}

	private void doDeleteMeasurement(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String string) {
		// TODO Auto-generated method stub
		
	}

	private void doDelete404(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	private void initStudentNavbar(HttpServletRequest req) {
		NavbarModel model = new NavbarModel();
		
		model.addNavLink("Dashboard", "/student/dashboard");
		model.addNavLink("Experiments", "/student/experiments");
		model.addNavLink("Reports",	"/student/reports");
		model.addNavLink("Errors", "/student/errors");
		model.addNavLink(model.new NavLink(
				"Account", 
				"#", 
				new NavbarModel.NavLink[] {
						model.new NavLink("Profile", "/student/profile"),
						model.new NavLink("Courses", "/student/courses")
				}
			));
		
		model.setLogoutLink("/logout");
		
		req.setAttribute("navbarModel", model);
	}
}
