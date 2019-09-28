package labvision;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.QuantityTypeId;
import labvision.entities.Student;
import labvision.measure.Amount;
import labvision.models.StudentDashboard;
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
		
		Experiment activeExperiment1 = spy(new Experiment());
		activeExperiment1.setName("Test Experiment 1");
		activeExperiment1.setId(19);
		
		Measurement measurement11 = new Measurement();
		measurement11.setQuantityTypeId(QuantityTypeId.ANGLE);
		MeasurementValue measurementValue111 = new MeasurementValue();
		measurementValue111.setVariable(measurement11);
		measurementValue111.setAmountValue(new Amount<>(1.3, 0.11, Units.RADIAN));
		measurementValue111.setTaken(LocalDateTime.now().minusHours(3));
		measurement11.addValue(measurementValue111);
		activeExperiment1.addMeasurement(measurement11);
		student.addActiveExperiment(activeExperiment1);
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		ServletContext servletContext = mock(ServletContext.class);
		LabVisionConfig labVisionConfig = mock(LabVisionConfig.class);
		StudentService studentService = mock(StudentService.class);
		
		when(studentService.getStudent(anyInt(), anyBoolean(), anyBoolean(), anyBoolean()))
			.thenReturn(student);
		
		when(servletContext.getAttribute(LabVisionServletContextListener.CONFIG_ATTR))
			.thenReturn(labVisionConfig);
		when(servletContext.getAttribute(LabVisionServletContextListener.STUDENT_SERVICE_ATTR))
			.thenReturn(studentService);
		
		when(servletConfig.getServletContext()).thenReturn(servletContext);
		
		when(req.getPathInfo()).thenReturn("/dashboard");
		when(session.getAttribute("user")).thenReturn(student);
		
		RequestDispatcher dispatcher = mock(RequestDispatcher.class);
		when(req.getRequestDispatcher(anyString() )).thenReturn(dispatcher);
		
		ArgumentCaptor<StudentDashboard> dashboardModelCaptor = ArgumentCaptor.forClass(StudentDashboard.class);
		doNothing().when(req).setAttribute(eq("dashboardModel"), dashboardModelCaptor.capture());
		
		StudentServlet servlet = new StudentServlet();
		servlet.init(servletConfig);
		servlet.doGet(req, resp);
		
		assertEquals(((StudentDashboard) dashboardModelCaptor.getValue()).getStudent().getId(), 17);
	}

}
