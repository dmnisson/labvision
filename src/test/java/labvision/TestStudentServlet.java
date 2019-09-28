package labvision;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import labvision.dto.student.dashboard.CurrentExperimentForStudentDashboard;
import labvision.dto.student.dashboard.RecentCourseForStudentDashboard;
import labvision.dto.student.dashboard.RecentExperimentForStudentDashboard;
import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.QuantityTypeId;
import labvision.entities.Student;
import labvision.measure.Amount;
import labvision.services.StudentDashboardService;
import labvision.services.StudentService;
import tec.units.ri.unit.Units;

class TestStudentServlet {

	@Test
	void testDashboard1() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		
		HttpSession session = mock(HttpSession.class);
		Student student = new Student();
		student.setId(17);
		student.setUsername("test_student");
		
		when(session.getAttribute("user")).thenReturn(student);
		when(req.getSession(anyBoolean() )).thenReturn(session);
		
		CurrentExperimentForStudentDashboard activeExperiment1 =
				new CurrentExperimentForStudentDashboard(
						6, 
						"Test Experiment 1", 
						8, 
						"Test Course 1");
		RecentExperimentForStudentDashboard recentExperiment1 = 
				new RecentExperimentForStudentDashboard(
						6, 
						"Test Experiment 1", 
						LocalDateTime.now().minusHours(2));
		RecentCourseForStudentDashboard recentCourse1 =
				new RecentCourseForStudentDashboard(
						8,
						"Test Course 1",
						LocalDateTime.now().minusHours(2));
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		ServletContext servletContext = mock(ServletContext.class);
		LabVisionConfig labVisionConfig = mock(LabVisionConfig.class);
		StudentDashboardService studentDashboardService = mock(StudentDashboardService.class);
		
		when(studentDashboardService.getCurrentExperiments(student.getId()))
			.thenReturn(Arrays.asList(activeExperiment1));
		when(studentDashboardService.getRecentExperiments(student.getId()))
			.thenReturn(Arrays.asList(recentExperiment1));
		when(studentDashboardService.getRecentCourses(student.getId()))
			.thenReturn(Arrays.asList(recentCourse1));
		
		when(servletContext.getAttribute(LabVisionServletContextListener.CONFIG_ATTR))
			.thenReturn(labVisionConfig);
		when(servletContext.getAttribute(LabVisionServletContextListener.STUDENT_DASHBOARD_SERVICE_ATTR))
			.thenReturn(studentDashboardService);
		
		when(servletConfig.getServletContext()).thenReturn(servletContext);
		
		when(req.getPathInfo()).thenReturn("/dashboard");
		when(session.getAttribute("user")).thenReturn(student);
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(req.getRequestDispatcher(anyString() )).thenReturn(dispatcher);
		
		ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
		doNothing().when(req).setAttribute(eq("student"), studentCaptor.capture());
		
		StudentServlet servlet = new StudentServlet();
		servlet.init(servletConfig);
		servlet.doGet(req, resp);
		
		assertEquals(((Student) studentCaptor.getValue()).getId(), 17);
	}

}
