package labvision.services;

import javax.persistence.EntityManagerFactory;

import labvision.entities.Experiment;
import labvision.entities.Instructor;

public class InstructorService extends JpaService {

	public InstructorService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

	public Experiment addExperiment(int instructorId, int experimentId) {
		return withEntityManager(manager -> {
			manager.getTransaction().begin();
			
			Instructor instructor = manager.find(Instructor.class, instructorId);
			Experiment experiment = manager.find(Experiment.class, experimentId);
			instructor.addExperiment(experiment);
			manager.getTransaction().commit();
			
			return experiment;
		});
	}

}
