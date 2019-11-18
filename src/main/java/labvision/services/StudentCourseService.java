package labvision.services;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import labvision.utils.ThrowingWrappers;

public class StudentCourseService extends CourseService {

	public StudentCourseService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}
}
