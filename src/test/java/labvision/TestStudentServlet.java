package labvision;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import labvision.entities.Experiment;
import labvision.entities.Measurement;
import labvision.entities.MeasurementValue;
import labvision.entities.PersistableAmount;
import labvision.entities.QuantityTypeId;
import labvision.entities.Student;
import labvision.entities.VariableValue;
import labvision.measure.Amount;
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
		
		Experiment activeExperiment1 = spy(new Experiment());
		activeExperiment1.setName("Test Experiment 1");
		activeExperiment1.setId(19);
		
		Measurement measurement11 = new Measurement();
		measurement11.setQuantityTypeId(QuantityTypeId.ANGLE);
		MeasurementValue measurementValue111 = new MeasurementValue();
		measurementValue111.setAmountValue(new Amount<>(1.3, 0.11, Units.RADIAN));
		measurementValue111.setTaken(LocalDateTime.now().minusHours(3));
		measurement11.addValue(measurementValue111);
		activeExperiment1.addMeasurement(measurement11);
		student.addActiveExperiment(activeExperiment1);
		
		ServletContext servletContext = mock(ServletContext.class);
		LabVisionDataAccess dataAccess = mock(LabVisionDataAccess.class);
		
		when(servletContext.getAttribute(LabVisionServletContextListener.DATA_ACCESS_ATTR))
			.thenReturn(dataAccess);
		
		when(req.getPathInfo()).thenReturn("/dashboard");
		when(session.getAttribute("user")).thenReturn(student);
		
		new StudentServlet().doGet(req, resp);
		
		
	}

}
