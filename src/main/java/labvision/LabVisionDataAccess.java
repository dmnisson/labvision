package labvision;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import labvision.entities.Experiment;
import labvision.entities.MeasurementValue;
import labvision.entities.Student;
import labvision.viewmodels.Dashboard;

/**
 * Provides access to the database to load and maniuplate entity objects.
 * @author davidnisson
 *
 */
public class LabVisionDataAccess {
	private EntityManagerFactory entityManagerFactory;
	
	public LabVisionDataAccess(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public Dashboard getDashboard(int studentId) {
		EntityManager manager = entityManagerFactory.createEntityManager();
		
		Dashboard dashboard = new Dashboard();
		
		Student student = manager.find(Student.class, studentId);
		
		// set dashboard values from objects in database
		dashboard.setStudent(student);
		
		// sort measurement values by date last taken
		Stream<MeasurementValue<?>> measurementValueStream = student.getMeasurementValues()
				.stream()
				.sorted((m1, m2) -> m2.getTaken().compareTo(m1.getTaken()));
		
		// sort recent courses by date last measurement value was taken
		dashboard.setRecentCourses(measurementValueStream
				.map(m -> m.getCourseClass().getCourse())
				.distinct()
				.collect(Collectors.toList()));
		
		dashboard.setRecentExperiments(measurementValueStream
				.map(m -> m.getMeasurement().getExperiment())
				.distinct()
				.collect(Collectors.toList()));
		
		dashboard.setCurrentExperiments(student.getActiveExperiments().stream()
				.collect(Collectors.toMap(Experiment::getCourse, Function.identity()))
				);
		
		dashboard.setMaxRecentCourses(student.getStudentPreferences()
				.getMaxRecentCourses());
		
		dashboard.setMaxRecentExperiments(student.getStudentPreferences()
				.getMaxRecentExperiments());
		
		return dashboard;
	}
}
