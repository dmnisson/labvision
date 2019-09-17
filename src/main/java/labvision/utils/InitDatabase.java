package labvision.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;

import labvision.LabVisionConfig;
import labvision.LabVisionDataAccess;
import labvision.entities.Instructor;
import labvision.entities.Student;

/**
 * Initialize the database with test users
 * @author davidnisson
 *
 */
public class InitDatabase {
	
	public static void main(String[] args) {
		System.out.println("WARNING: This will drop all existing users from the database! Continue? (y/n)");
		Scanner sc = new Scanner(System.in);
		String response = sc.nextLine();
		if (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("yes")) {
			System.out.println("Aborted");
			System.exit(0);
		}
		
		String configPath;
		if (args.length > 0) {
			configPath = args[0];
		} else {
			configPath = "~/.labvision/app.properties";
		}
		
		LabVisionConfig config = new LabVisionConfig(configPath);
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
				config.getPersistenceUnitName());
		
		// clear users
		EntityManager manager = emf.createEntityManager();
		EntityTransaction tx = manager.getTransaction();
		tx.begin();
		manager.createQuery("DELETE FROM User").executeUpdate();
		tx.commit();
		
		LabVisionDataAccess dataAccess = new LabVisionDataAccess(emf);
		
		Student student1 = new Student();
		student1.setUsername("student1");
		Instructor instructor1 = new Instructor();
		instructor1.setUsername("instructor1");
		
		SecureRandom random = new SecureRandom();
		try {
			student1.updatePassword(config, random, "Password123");
			dataAccess.addUser(student1);
			System.out.println("User student1 added");
			
			instructor1.updatePassword(config, random, "Password123");
			dataAccess.addUser(instructor1);
			System.out.println("User instructor1 added");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		emf.close();
	}
}
