package labvision.services;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;

import labvision.utils.ThrowingWrappers;

public class StudentReportService extends ReportService {
	public StudentReportService(EntityManagerFactory entityManagerFactory) {
		super(entityManagerFactory);
	}
}
