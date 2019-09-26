package labvision.services;

import javax.persistence.EntityManagerFactory;

public class InstructorService extends JpaService {

	public InstructorService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}

}
