package io.github.dmnisson.labvision.dto.course;

public class CourseForAdminTable {
	private final Integer id;
	private final String name;
	private final Long numOfCourseClasses;
	private final Long numOfExperiments;
	
	public CourseForAdminTable(Integer id, String name, Long numOfCourseClasses, Long numOfExperiments) {
		super();
		this.id = id;
		this.name = name;
		this.numOfCourseClasses = numOfCourseClasses;
		this.numOfExperiments = numOfExperiments;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getNumOfCourseClasses() {
		return numOfCourseClasses;
	}

	public Long getNumOfExperiments() {
		return numOfExperiments;
	}

}
