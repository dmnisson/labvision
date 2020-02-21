package io.github.dmnisson.labvision.dto.experiment;

public class ExperimentForAdminDetail extends ExperimentForAdmin {

	private final String description;
	
	public ExperimentForAdminDetail(Integer id, String name, Long numOfInstructors, Long numOfActiveStudents,
			Long numOfMeasurements, Long numOfReports, String description) {
		super(id, name, numOfInstructors, numOfActiveStudents, numOfMeasurements, numOfReports);
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
