package io.github.dmnisson.labvision.dto.experiment;

public class ExperimentForAdmin {
	private final Integer id;
	private final String name;
	private final Long numOfInstructors;
	private final Long numOfActiveStudents;
	private final Long numOfMeasurements;
	private final Long numOfReports;
	
	public ExperimentForAdmin(Integer id, String name, Long numOfInstructors, Long numOfActiveStudents,
			Long numOfMeasurements, Long numOfReports) {
		super();
		this.id = id;
		this.name = name;
		this.numOfInstructors = numOfInstructors;
		this.numOfActiveStudents = numOfActiveStudents;
		this.numOfMeasurements = numOfMeasurements;
		this.numOfReports = numOfReports;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getNumOfInstructors() {
		return numOfInstructors;
	}

	public Long getNumOfActiveStudents() {
		return numOfActiveStudents;
	}

	public Long getNumOfMeasurements() {
		return numOfMeasurements;
	}

	public Long getNumOfReports() {
		return numOfReports;
	}
	
}
